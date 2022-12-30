package com.maffin.recipes.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_receipt")
public class CartReceipt {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "receipt_id")
    public long receiptId;

    @ColumnInfo(name = "receipt_name")
    public String receiptName;
}
