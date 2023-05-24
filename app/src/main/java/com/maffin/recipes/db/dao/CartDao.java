package com.maffin.recipes.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.maffin.recipes.db.entity.Cart;

import java.util.List;

/**
 * DAO-класс (Data Access Object) для доступа к локальной таблице Cart (корзина покупок).
 */
@Dao
public interface CartDao {

    /**
     * Получить полный список покупок, отсортированный по ID рецепта и ID ингредиента.
     * @return  список строк
     */
    @Query("SELECT * FROM cart ORDER BY receipt_id, item_id")
    List<Cart> getCart();

    /**
     * Получить список покупок для одного рецепта по его ID.
     * @param id    ID рецепта
     * @return  список строк
     */
    @Query("SELECT * FROM cart WHERE receipt_id = :id")
    List<Cart> getByReceiptId(long id);

    /**
     * Получить одну строку по ID ингредиента.
     * @param itemId    ID ингредиента
     * @return  одна строка
     */
    @Query("SELECT * FROM cart WHERE item_id = :itemId")
    Cart getByItemId(long itemId);

    /**
     * Удаляет все строки из таблицы (очистка корзины).
     */
    @Query("DELETE FROM cart")
    void removeAll();

    /**
     * Удаляет ингредиенты одного рецепта.
     * @param id    ID рецепта
     */
    @Query("DELETE FROM cart WHERE receipt_id = :id")
    void removeAll(long id);

    /**
     * Удаляет одну строку по ID рецепта и ID ингредиента.
     * @param id        ID рецепта
     * @param itemId    ID ингредиента
     */
    @Query("DELETE FROM cart WHERE receipt_id = :id and item_id = :itemId")
    void removeById(long id, long itemId);

    /**
     * Добавление строки.
     * @param cart строка
     */
    @Insert
    void insert(Cart cart);

    /**
     * Обновление строки
     * @param cart строка
     */
    @Update
    void update(Cart cart);

    /**
     * Удаление строки.
     * @param cart строка
     */
    @Delete
    void delete(Cart cart);
}
