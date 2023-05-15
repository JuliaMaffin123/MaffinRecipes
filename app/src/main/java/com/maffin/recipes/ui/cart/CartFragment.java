package com.maffin.recipes.ui.cart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.databinding.FragmentCartBinding;
import com.maffin.recipes.db.entity.Cart;
import com.maffin.recipes.db.entity.CartReceipt;
import com.maffin.recipes.network.ImageManager;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Фрагмент для отображения списка покупок.
 */
public class CartFragment extends Fragment {

    /** TAG для логирования. */
    private static final String TAG = "CartFragment";
    /** Разметка фрагмента. */
    private FragmentCartBinding binding;
    /** Модель данных фрагмента. */
    private CartViewModel cartViewModel;
    /** Отметки ингредиентов (куплено/не куплено) в корзине. */
    private boolean[] itemToggled;
    /** Список отображаемых элементов в корзине. */
    List<Model> modelList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Разрешаем свое меню
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализируем разметку фрагмента
        binding = FragmentCartBinding.inflate(inflater, container, false);

        // Инициализируем модель данных
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в ListView
        final ListView listView = binding.list;
        listView.setEmptyView(binding.empty);
        cartViewModel.getList().observe(getViewLifecycleOwner(), receipts -> {
            // Загружаем из модели список компонентов
            List<Cart> cartList = cartViewModel.getCart();
            // Конвертируем список рецептов и компонентов в единую модель
            modelList = magicConversion(receipts, cartList);
            // Определяем: какие записи были отмечены
            int size = modelList.size();
            itemToggled = new boolean[size];
            for (int i = 0; i < size; i++) {
                Model model = modelList.get(i);
                itemToggled[i] = model.check;
            }
            // Инициализируем адаптер и список
            ArrayAdapter<Receipt> adapter = new LocalCartAdapter(getContext(), modelList);
            listView.setAdapter(adapter);
        });
        // Навешиваем прослушку на нажатие по элементу списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Зная view - элемент списка, получим из него холдер с данными
                final AbstractListAdapter.ViewHolder holder = (AbstractListAdapter.ViewHolder) view.getTag();
                // Если нажат элемент, соответствующий рецепту (itemId=-1), то ничего не произойдет
                if (holder.getId() > -1) {
                    // Переключаем состояние элемента списка КУПЛЕН
                    toggleItem(position, holder);
                }
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
        // Загружаем данные из БД
        cartViewModel.loadData();
    }

    /**
     * Срабатывает при создании меню.
     * @param menu  мею
     * @return
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        // Инициируем разметку
        inflater.inflate(R.menu.cart, menu);
    }

    /**
     * Срабатывает при клик на пункт меню (в нашем случае на кнопки в ActionBar).
     * @param item  пункт меню
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        switch (item.getItemId()) {
            case R.id.action_share:
                // Нажата кнопка ПОДЕЛИТЬСЯ
                shareCart();
                return true;
            case R.id.action_clear:
                // Нажата кнопка ОЧИСТИТЬ
                clearCart();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Очистка корзины.
     */
    private void clearCart() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        cartViewModel.clearCart();
                        final ListView listView = binding.list;
                        ArrayAdapter<Receipt> adapter = (ArrayAdapter<Receipt>) listView.getAdapter();
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Вы уверены, что хотите очистить корзину?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();
    }

    /**
     * Конвертирует список рецептов и список покупок в список для адаптера.
     * @param receipts  список рецептов
     * @param carts     список покупок
     * @return  список для адаптера
     */
    private List<Model> magicConversion(List<CartReceipt> receipts, List<Cart> carts) {
        List<Model> list = new ArrayList<>(receipts.size() + carts.size());
        for (CartReceipt receipt : receipts) {
            long id = receipt.receiptId;
            list.add(new Model(receipt));
            for (Cart cart : carts) {
                if (id == cart.receiptId && cart.itemId > 0) {
                    list.add(new Model(cart));
                }
            }
        }
        return list;
    }

    /**
     * Переключает выделение элемента в списке.
     * @param position  позиция элемента
     * @param holder    холдер
     */
    private void toggleItem(int position, AbstractListAdapter.ViewHolder holder) {
        long itemId = holder.getId();
        Log.d(TAG, "Выбрана позиция: " + position + ", id: " + itemId);
        // Переключаем элемент в кэше и в таблице
        boolean toggle = !itemToggled[position];
        itemToggled[position] = toggle;
        cartViewModel.toggleChk(itemId, toggle);
        // Отрисовываем отметки
        showCheckers();
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
            ImageView thumbnail = holder.getThumbnail();
            if (thumbnail != null) {
                if (itemToggled[i]) {
                    thumbnail.setImageResource(R.drawable.ic_baseline_check_box_24);
                } else {
                    thumbnail.setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24);
                }
            }
        }
        // Сообщим адаптеру, что данные в списке изменились и его надо обновить
        adapter.notifyDataSetChanged();
    }

    /**
     * Публикует корзину.
     */
    public void shareCart() {
        StringBuilder sb = new StringBuilder();
        sb.append("Список покупок:\n");
        if (modelList != null) {
            for (Model m : modelList) {
                if (m.itemId == -1) {
                    // Заголовок рецепта
                    sb.append("\n" + m.name + "\n");
                } else {
                    // Ингредиент
                    sb.append("• " + m.name + " " + m.desc + "\n");
                }
            }
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        sendIntent.setType("text/html");
        startActivity(sendIntent);
    }

    /**
     * Модель данных для элемента списка.
     */
    public class Model {
        public long id;
        public long itemId;
        public String name;
        public String desc;
        public boolean check;

        /**
         * Конструктор для группирующй записи с именем рецепта.
         * @param receipt   рецепта
         */
        public Model(CartReceipt receipt) {
            this.id = receipt.receiptId;
            this.itemId = -1;
            this.name = receipt.receiptName;
            this.check = false;
        }

        /**
         * Конструктор для ингредиентов.
         * @param cart  ингредиент
         */
        public Model(Cart cart) {
            this.id = cart.receiptId;
            this.itemId = cart.itemId;
            this.name = cart.itemName;
            this.desc = cart.itemCnt;
            this.check = cart.itemChk;
        }
    }

    /**
     * Адаптер для списка.
     * Объявлен внутри фрагмента чтобы иметь доступ к массиву отмеченных элементов для правильной
     * отрисовки "купленных" элементов списка.
     */
    public class LocalCartAdapter extends AbstractListAdapter {
        /** TAG для логирования. */
        private static final String TAG = "LocalCartAdapter";
        /** Шаблон URL-а для загрузки изображений. */
        private static final String URL_TEMPLATE = Config.BASE_URL + "/images/receipt-%d-thumbnail.png";

        /**
         * Конструктор.
         *
         * @param context      интерфейс к глобальной информации о среде приложений.
         * @param list         список рецептов и ингредиентов
         */
        public LocalCartAdapter(Context context, List<CartFragment.Model> list) {
            super(context, R.layout.cart_receipt_list_item, R.layout.cart_list_item, list);
        }

        @Override
        public void bindView(int position, View view) {
            Context context = getContext();
            // Получим ссылки на составные части шаблона
            ViewHolder holder = (ViewHolder) view.getTag();
            // Получим ссылку на запись (это элемент массива в соответствующей позиции)
            final CartFragment.Model model = (CartFragment.Model) getData().get(position);
            // Наполним строку данными
            holder.setId(model.itemId);
            // Наименование
            TextView name = holder.getName();
            if (name != null) {
                name.setText(model.name);
                if (itemToggled[position]) {
                    // Помеченные записи считам купленными и зачеркиваем
                    name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    // Флаг зачеркнтого текста можно убрать
                    name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
            // Количество
            TextView desc = holder.getDescription1();
            if (desc != null) {
                desc.setText(model.desc);
            }
            // Миниатюра
            ImageView thumb = holder.getThumbnail();
            if (thumb != null) {
                if (model.itemId == -1) {
                    // Это группирующая строка с названием рецепта, загружаем картинку
                    if (model.id == -1) {
                        thumb.setImageResource(R.drawable.ic_cook_thumb);
                    } else {
                        String url = String.format(URL_TEMPLATE, model.id);
                        ImageManager.fetchImage(context, url, thumb, R.drawable.ic_cook_thumb);
                    }
                } else {
                    // Это элемент списка, рисуем имитацию чекбокса
                    thumb.setImageResource(itemToggled[position]
                            ? R.drawable.ic_baseline_check_box_24
                            : R.drawable.ic_baseline_check_box_outline_blank_24);
                }
            }
            // Отключаем нажатие в группах
            if (model.itemId == -1) {
                view.setEnabled(false);
                view.setClickable(false);
            }
        }

        @Override
        public int getResource(int position) {
            // Получим ссылку на запись (это элемент массива в соответствующей позиции)
            final CartFragment.Model model = (CartFragment.Model) getData().get(position);
            // Если ID элемента списка == -1, значит это строка для группирующего названия рецепта
            return (model.itemId == -1 ? mGroupResource : mResource);
        }
    }
}