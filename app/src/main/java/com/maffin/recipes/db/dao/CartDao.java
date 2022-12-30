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

    @Query("SELECT * FROM cart WHERE receipt_id = :id ORDER BY item_name")
    List<Cart> getById(long id);

    @Insert
    void insert(Cart favorite);

    @Update
    void update(Cart favorite);

    @Delete
    void delete(Cart favorite);
}
