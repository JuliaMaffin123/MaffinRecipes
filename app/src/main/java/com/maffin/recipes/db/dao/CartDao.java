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

    @Query("SELECT * FROM cart WHERE receipt_id = :id")
    List<Cart> getById(long id);

    @Query("DELETE FROM cart WHERE receipt_id = :id")
    void removeAll(long id);

    @Query("DELETE FROM cart WHERE receipt_id = :id and item_id = :itemId")
    void removeById(long id, long itemId);

    @Insert
    void insert(Cart favorite);

    @Update
    void update(Cart favorite);

    @Delete
    void delete(Cart favorite);
}
