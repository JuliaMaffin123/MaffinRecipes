package com.maffin.recipes.ui.cart;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.maffin.recipes.App;
import com.maffin.recipes.db.AppDatabase;
import com.maffin.recipes.db.dao.CartDao;
import com.maffin.recipes.db.dao.CartReceiptDao;
import com.maffin.recipes.db.entity.Cart;
import com.maffin.recipes.db.entity.CartReceipt;

import java.util.List;

/**
 * Модель данных для отображения на фрагменте СПИСОК ПОКУПОК.
 */
public class CartViewModel extends ViewModel {

    /** TAG для логирования. */
    private static final String TAG = "HomeViewModel";
    /** Список рецептов. */
    private final MutableLiveData<List<CartReceipt>> mList;
    /** Список покупок. */
    private List<Cart> mCart;
    /** База данных. */
    private final AppDatabase db;

    public CartViewModel() {
        // Инциализируем переменные, через которые будут передаваться данные в активность
        mList = new MutableLiveData<>();
        // Инициализируем соединение с базой данных
        db = App.getInstance().getDatabase();
    }

    /**
     * Возвращает объект LiveData со списком реуептов.
     * @return LiveData
     */
    public LiveData<List<CartReceipt>> getList() {
        return mList;
    }

    /**
     * Возвращает список покупок.
     * @return List<Cart>
     */
    public List<Cart> getCart() {
        return mCart;
    }

    /**
     * Загрузка списка рецептов из базы данных.
     */
    public void loadData() {
        // Запрос в базу списка ингредиентов
        CartDao cartDao = db.cartDao();
        mCart = cartDao.getCart();
        Log.d(TAG, "Отобрано " + mCart.size() + " ингредиентов в корзине");

        // Запрос в базу списка рецептов
        CartReceiptDao cartReceiptDao = db.cartReceiptDao();
        List<CartReceipt> cartReceipts = cartReceiptDao.getCartReceipt();
        Log.d(TAG, "Отобрано " + cartReceipts.size() + " рецептов в корзине");
        // Запоминаем отобранный список
        mList.setValue(cartReceipts);
    }

    /**
     * Переключает выделение элемента в базе.
     * @param itemId    ID элемента списка
     * @param chk       true/false
     */
    public void toggleChk(long itemId, boolean chk) {
        CartDao cartDao = db.cartDao();
        Cart cart = cartDao.getById(itemId);
        cart.itemChk = chk;
        cartDao.update(cart);
        //cartDao.toggleChk(itemId, chk);
    }
}