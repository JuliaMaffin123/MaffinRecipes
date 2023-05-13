package com.maffin.recipes.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.maffin.recipes.db.entity.Cart;

import java.util.List;

@Dao
public interface CartDao {

    @Query("SELECT * FROM cart ORDER BY receipt_id, item_id")
    List<Cart> getCart();

    @Query("SELECT * FROM cart WHERE receipt_id = :id")
    List<Cart> getByReceiptId(long id);

    @Query("SELECT * FROM cart WHERE item_id = :itemId")
    Cart getByItemId(long itemId);

    @Query("DELETE FROM cart")
    void removeAll();

    @Query("DELETE FROM cart WHERE receipt_id = :id")
    void removeAll(long id);

    @Query("DELETE FROM cart WHERE receipt_id = :id and item_id = :itemId")
    void removeById(long id, long itemId);

    @Insert
    void insert(Cart cart);

    @Update
    void update(Cart cart);

    @Delete
    void delete(Cart cart);
}
