package com.maffin.recipes.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite")
public class Favorite {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "receipt_id")
    public long receiptId;

    @ColumnInfo(name = "receipt_name")
    public String receiptName;

    @ColumnInfo(name = "receipt_time")
    public int receiptTime;

    @ColumnInfo(name = "receipt_kkal")
    public int receiptKkal;
}
