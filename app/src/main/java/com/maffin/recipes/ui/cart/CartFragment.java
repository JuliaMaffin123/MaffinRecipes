package com.maffin.recipes.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.databinding.FragmentCartBinding;
import com.maffin.recipes.db.entity.Cart;
import com.maffin.recipes.db.entity.CartReceipt;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    /** TAG для логирования. */
    private static final String TAG = "CartFragment";
    /** Разметка фрагмента. */
    private FragmentCartBinding binding;
    /** Модель данных фрагмента. */
    private CartViewModel cartViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Инициализируем разметку фрагмента
        binding = FragmentCartBinding.inflate(inflater, container, false);

        // Инициализируем модель данных
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в ListView
        final ListView listView = binding.list;
        listView.setEmptyView(binding.empty);
        cartViewModel.getList().observe(getViewLifecycleOwner(), receipts -> {
            List<Cart> cart = cartViewModel.getCart();
            ArrayAdapter<Receipt> adapter = new CartAdapter(getContext(), magicConversion(receipts, cart));
            listView.setAdapter(adapter);
        });
        // Навешиваем прослушку на нажатие по элементу списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Зная view - элемент списка, получим из него холдер с данными
                final AbstractListAdapter.ViewHolder holder = (AbstractListAdapter.ViewHolder) view.getTag();
                if (holder.getId() > -1) {
                    // Переключаем состояние элемента списка КУПЛЕН
                    //toggleItem(position, holder.getId());
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
            this.itemId = cart.id;
            this.name = cart.itemName;
            this.desc = cart.itemCnt;
            this.check = cart.itemChk;
        }
    }
}