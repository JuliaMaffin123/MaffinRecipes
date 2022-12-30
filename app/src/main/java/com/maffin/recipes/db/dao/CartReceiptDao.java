package com.maffin.recipes.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.maffin.recipes.db.entity.CartReceipt;

import java.util.List;

@Dao
public interface CartReceiptDao {

    @Query("SELECT * FROM cart_receipt ORDER BY receipt_name")
    List<CartReceipt> getCartReceipt();

    @Insert
    void insert(CartReceipt favorite);

    @Update
    void update(CartReceipt favorite);

    @Delete
    void delete(CartReceipt favorite);
}
