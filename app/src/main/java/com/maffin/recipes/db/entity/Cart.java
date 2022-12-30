package com.maffin.recipes.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart")
public class Cart {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "receipt_id")
    public long receiptId;

    @ColumnInfo(name = "item_name")
    public String itemName;

    @ColumnInfo(name = "item_cnt")
    public String itemCnt;

    @ColumnInfo(name = "item_chk")
    public boolean itemChk;

}
