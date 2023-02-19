package com.maffin.recipes.ui.detail.components;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.R;
import com.maffin.recipes.databinding.TabComponentsBinding;
import com.maffin.recipes.db.entity.Cart;
import com.maffin.recipes.network.Component;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;
import com.maffin.recipes.ui.detail.DetailActivity;

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
    /** Список ингредиентов, добавленных в корзину. */
    private boolean[] itemToggled;
    /** Список ингридентов, полученных с сервера. */
    private List<Component> componentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Получаем ID рецепта
        id = ((DetailActivity) getActivity()).getReceiptId();

        // Инициализируем разметку фрагмента
        binding = TabComponentsBinding.inflate(inflater, container, false);

        // Инициализируем модель данных
        componentsViewModel = new ViewModelProvider(this).get(ComponentsViewModel.class);
        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в ListView
        final ListView listView = binding.list;
        listView.setEmptyView(binding.empty);
        componentsViewModel.getList().observe(getViewLifecycleOwner(), components -> {
            // Запоминаем список ингредиентов
            componentList = components;
            // Определяем: какие записи были отмечены
            int size = componentList.size();
            itemToggled = new boolean[size];
            List<Cart> cartList = componentsViewModel.getCart();
            for (int i = 0; i < size; i++) {
                itemToggled[i] = false;
                Component component = componentList.get(i);
                int id = component.getId();
                for (Cart cart : cartList) {
                    if (id == cart.id) {
                        itemToggled[i] = true;
                    }
                }
            }

            // Инициализируем адаптер и список
            ArrayAdapter<Component> adapter = new LocalAdapter(getContext(), components);
            listView.setAdapter(adapter);
        });
        // Навешиваем прослушку на нажатие по элементу списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Зная view - элемент списка, получим из него холдер с данными
                final AbstractListAdapter.ViewHolder holder = (AbstractListAdapter.ViewHolder) view.getTag();
                // Добавляем или удаляем ингредиент из корзины
                switchInCart(position, holder);
            }
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
        long id = holder.getId();
        Log.d(TAG, "Выбрана позиция: " + position + ", id: " + id);
        final ListView listView = binding.list;
        // Проверим, присутствует ли элемент списка в кэше?
        if (itemToggled[position]) {
            // Удаляем из списка
            if (id == -1) {
                // Нажат элемент "Выбрать все", удаляем все элементы
                itemToggled = new boolean[componentList.size()];
            } else {
                // Удаляем один элемент и "Выбрать все"
                itemToggled[position] = false;
                itemToggled[componentList.size() - 1] = false;
            }
        } else {
            // Добавляем в списка
            if (id == -1) {
                // Нажат элемент "Выбрать все", добавляем все элементы
                for (int i = 0; i < componentList.size(); i++) {
                    itemToggled[i] = true;
                }
            } else {
                // Добавляем один элемент и "Выбрать все", если список наполнился
                Component component = (Component) listView.getItemAtPosition(position);
                itemToggled[position] = true;
                boolean success = true;
                for (int i = 0; i < componentList.size() - 1; i++) {
                    success = success && itemToggled[i];
                }
                itemToggled[componentList.size() - 1] = success;
            }
        }
        showCheckers();
    }

    private Cart componentToCart(Component component) {
        Cart cart = new Cart();
        cart.itemId = component.getId();
        cart.receiptId = component.getReceipt_id();
        cart.itemChk = false;
        cart.itemCnt = component.getCount();
        cart.itemName = component.getName();
        return cart;
    }

    /**
     * Отображает изменение выделенных элементов списка.
     */
    private void showCheckers() {
        // Получим ссылку на список и адаптер
        final ListView listView = binding.list;
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        // Пробежимся по всему списку и изменим картинку выделения
        for (int i = 0; i < adapter.getCount(); i++) {
            AbstractListAdapter.ViewHolder holder = (AbstractListAdapter.ViewHolder) adapter.getView(i, null, listView).getTag();
            ImageView action = holder.getAction();
            action.setImageResource(itemToggled[i]
                    ? R.drawable.ic_baseline_radio_button_checked_24
                    : R.drawable.ic_baseline_radio_button_unchecked_24);
        }
        // Сообщим адаптеру, что данные в списке изменились и его надо обновить
        adapter.notifyDataSetChanged();
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
                // Для "Выбрать всё" изменим текст на жирный
                if (component.getId() == -1) {
                    holder.getName().setTypeface(null, Typeface.BOLD);
                }
            }
            // Количество
            if (holder.getDescription1() != null) {
                holder.getDescription1().setText(component.getCount());
            }
            // Отметка о добавлении в корзину
            if (holder.getAction() != null) {
                holder.getAction().setImageResource(itemToggled[position]
                        ? R.drawable.ic_baseline_radio_button_checked_24
                        : R.drawable.ic_baseline_radio_button_unchecked_24);
            }
        }

    }
}
