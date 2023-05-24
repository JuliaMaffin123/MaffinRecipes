package com.maffin.recipes.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.maffin.recipes.db.entity.CartReceipt;

import java.util.List;

/**
 * DAO-класс (Data Access Object) для доступа к локальной таблице CartReceipt (рецепты в корзине покупок).
 */
@Dao
public interface CartReceiptDao {

    /**
     * Возвращает список рецептов в корзине, отсортированный по ID рецепта.
     * @return  список рецептов
     */
    @Query("SELECT * FROM cart_receipt ORDER BY receipt_id")
    List<CartReceipt> getCartReceipt();

    /**
     * Возвращает одну строку по ID рецепта.
     * @param id    ID рецепта
     * @return  одна строка
     */
    @Query("SELECT * FROM cart_receipt WHERE receipt_id = :id")
    CartReceipt getCartReceiptById(long id);

    /**
     * Удаляет одну строку по ID рецепта.
     * @param id    ID рецепта
     */
    @Query("DELETE FROM cart_receipt WHERE receipt_id = :id")
    void removeById(long id);

    /**
     * Удаляет все строки (очистка корзины).
     */
    @Query("DELETE FROM cart_receipt")
    void removeAll();

    /**
     * Добавление строки.
     * @param cartReceipt строка
     */
    @Insert
    void insert(CartReceipt cartReceipt);

    /**
     * Обновление строки
     * @param cartReceipt строка
     */
    @Update
    void update(CartReceipt cartReceipt);

    /**
     * Удаление строки.
     * @param cartReceipt строка
     */
    @Delete
    void delete(CartReceipt cartReceipt);
}
