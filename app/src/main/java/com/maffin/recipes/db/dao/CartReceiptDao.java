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

    @Query("SELECT * FROM cart_receipt ORDER BY receipt_id")
    List<CartReceipt> getCartReceipt();

    @Query("SELECT * FROM cart_receipt WHERE receipt_id = :id")
    CartReceipt getCartReceiptById(long id);

    @Query("DELETE FROM cart_receipt WHERE receipt_id = :id")
    void removeById(long id);

    @Insert
    void insert(CartReceipt cartReceipt);

    @Update
    void update(CartReceipt cartReceipt);

    @Delete
    void delete(CartReceipt cartReceipt);
}
