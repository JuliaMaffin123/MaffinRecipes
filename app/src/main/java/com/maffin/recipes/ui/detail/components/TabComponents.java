package com.maffin.recipes.ui.detail.components;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.R;
import com.maffin.recipes.databinding.TabComponentsBinding;
import com.maffin.recipes.db.entity.Cart;
import com.maffin.recipes.network.Component;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;
import com.maffin.recipes.ui.detail.DetailFragment;

import java.util.List;

/**
 * Фрагмент для вкладки ИНГРЕДИЕНТЫ.
 */
public class TabComponents extends Fragment {

    /** TAG для логирования. */
    private static final String TAG = "TabComponents";
    /** Разметка фрагмента. */
    private TabComponentsBinding binding;
    /** Модель данных фрагмента. */
    private ComponentsViewModel componentsViewModel;
    /** ID рецепта. */
    private long id;
    /** Рецепт. */
    private Receipt receipt;
    /** Список ингредиентов, добавленных в корзину. */
    private boolean[] itemToggled;
    /** Список ингридентов, полученных с сервера. */
    private List<Component> componentList;
    /** Родительский фрагмент. */
    private DetailFragment root;
    /** Кнопка "Выбрать все". */
    private AppCompatButton btnSelectAll;
    /** Статус кнопки "Выбрать все". */
    private boolean isCheckedAll;

    /**
     * Возвращает ссылку на родительский фрагмент.
     * @return
     */
    public DetailFragment getRoot() {
        List<Fragment> fragmentList = getParentFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof DetailFragment) {
                return (DetailFragment) fragment;
            }
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Получаем ID рецепта
        root = getRoot();
        id = root.getReceiptId();

        // Инициализируем разметку фрагмента
        binding = TabComponentsBinding.inflate(inflater, container, false);

        // Инициализируем модель данных
        componentsViewModel = new ViewModelProvider(this).get(ComponentsViewModel.class);
        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в ListView
        final ListView listView = binding.list;
        componentsViewModel.getList().observe(getViewLifecycleOwner(), components -> {
            // Запоминаем список ингредиентов
            componentList = components;
            // Определяем: какие записи были отмечены
            int size = componentList.size();
            itemToggled = new boolean[size];
            List<Cart> cartList = componentsViewModel.getCart();
            int checked = 0;
            for (int i = 0; i < size; i++) {
                itemToggled[i] = false;
                Component component = componentList.get(i);
                int id = component.getId();
                for (Cart cart : cartList) {
                    if (id == cart.itemId) {
                        itemToggled[i] = true;
                        checked++;
                    }
                }
            }
            // Отрисовываем счетчик выбранных элементов
            root.showComponentsCount(checked);
            // Инициализируем адаптер и список
            ArrayAdapter<Component> adapter = new LocalAdapter(getContext(), components);
            listView.setAdapter(adapter);
            // Отрисовываем кнопку
            isCheckedAll = checkAllCheckers();
        });
        // Навешиваем прослушку на кнопку "Выбрать все"
        btnSelectAll = binding.actionSelectAll;
        btnSelectAll.setOnClickListener(view -> onClickCheckAll());
        // Навешиваем прослушку на нажатие по элементу списка
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            // Зная view - элемент списка, получим из него холдер с данными
            final AbstractListAdapter.ViewHolder holder = (AbstractListAdapter.ViewHolder) view.getTag();
            // Добавляем или удаляем ингредиент из корзины
            switchInCart(position, holder);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Загружаем данные из сети
        componentsViewModel.loadData(id);
    }

    /**
     * Добавляет или удаляет ингредиент из списка покупок.
     * @param position позиция в списке
     * @param holder   контейнер с элементами представления для строки списка
     */
    private void switchInCart(int position, AbstractListAdapter.ViewHolder holder) {
        long itemId = holder.getId();
        Log.d(TAG, "Выбрана позиция: " + position + ", id: " + itemId);
        final ListView listView = binding.list;
        // Проверим, присутствует ли элемент списка в кэше?
        if (itemToggled[position]) {
            // Удаляем из списка
            itemToggled[position] = false;
            componentsViewModel.removeFromCart(id, itemId);
            // Проверяем остались ли отмеченные элементы
            boolean success = false;
            for (int i = 0; i < componentList.size(); i++) {
                if (itemToggled[i]) {
                    success = true;
                    break;
                }
            }
            // Если это удален последний элемент рецепта, убираем рецепт из списка
            if (!success) {
                componentsViewModel.removeFromCart(id);
            }
        } else {
            // Загружаем рецепт из родительской активности
            receipt = root.getReceipt();
            // Добавляем в список
            Component component = (Component) listView.getItemAtPosition(position);
            itemToggled[position] = true;
            componentsViewModel.appendToCart(id, component);
            // Добавляем рецепт в корзину
            componentsViewModel.appendReceiptToCart(id, receipt);
        }
        showCheckers();
        isCheckedAll = checkAllCheckers();
    }

    /**
     * Изменяет текст кнопки в зависимости от того сколько элементов списка отмечено.
     */
    private boolean checkAllCheckers() {
        boolean success = true;
        for (int i = 0; i < componentList.size(); i++) {
            success = success && itemToggled[i];
        }
        // Меняем текст у кнопки
        btnSelectAll.setText(success ? "Убрать всё" : "Выбрать всё");
        return success;
    }

    private void onClickCheckAll() {
        if (isCheckedAll) {
            // Выделены все элементы, снимаем все выделение
            itemToggled = new boolean[componentList.size()];
            componentsViewModel.removeAllFromCart(id);
            // Удяляем рецепт из списка рецептов в корзине
            componentsViewModel.removeFromCart(id);
        } else {
            // Загружаем рецепт из родительской активности
            receipt = root.getReceipt();
            // Выделены не все элементы, отмечаем все
            for (int i = 0; i < componentList.size(); i++) {
                itemToggled[i] = true;
                Component component = (Component) binding.list.getItemAtPosition(i);
                componentsViewModel.appendToCart(id, component);
            }
            // Добавляем рецепт в корзину
            componentsViewModel.appendReceiptToCart(id, receipt);
        }
        showCheckers();
        isCheckedAll = checkAllCheckers();
    }

    /**
     * Отображает изменение выделенных элементов списка.
     */
    private void showCheckers() {
        // Получим ссылку на список и адаптер
        final ListView listView = binding.list;
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        // Пробежимся по всему списку и изменим картинку выделения
        int checked = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            AbstractListAdapter.ViewHolder holder = (AbstractListAdapter.ViewHolder) adapter.getView(i, null, listView).getTag();
            ImageView thumbnail = holder.getThumbnail();
            if (itemToggled[i]) {
                thumbnail.setImageResource(R.drawable.ic_baseline_check_box_24);
                checked++;
            } else {
                thumbnail.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24);
            }
        }
        // Сообщим адаптеру, что данные в списке изменились и его надо обновить
        adapter.notifyDataSetChanged();
        // Сообщим корневой активности об изменении числа выбранных элементов
        root.showComponentsCount(checked);
    }

    /**
     * Адаптер для наполнения списка с привязкой отображения к массиву выделенных элементов.
     */
    private class LocalAdapter extends AbstractListAdapter {

        /**
         * Конструктор.
         *
         * @param context      интерфейс к глобальной информации о среде приложений.
         * @param list         список с данными для адаптера
         */
        public LocalAdapter(Context context, List<Component> list) {
            super(context, R.layout.components_list_item, list);
        }

        @Override
        public void bindView(int position, View view) {
            Context context = getContext();
            // Получим ссылки на составные части шаблона
            ViewHolder holder = (ViewHolder) view.getTag();
            // Получим ссылку на ингредиент (это элемент массива в соответствующей позиции)
            final Component component = (Component) getData().get(position);
            // Наполним строку данными
            holder.setId(component.getId());
            // Наименование ингредиента
            if (holder.getName() != null) {
                holder.getName().setText(component.getName());
            }
            // Количество
            if (holder.getDescription1() != null) {
                holder.getDescription1().setText(component.getCount());
            }
            // Отметка о добавлении в корзину
            if (holder.getThumbnail() != null) {
                holder.getThumbnail().setImageResource(itemToggled[position]
                        ? R.drawable.ic_baseline_check_box_24
                        : R.drawable.ic_baseline_check_box_outline_blank_24);
            }
        }
    }
}
