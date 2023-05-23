package com.maffin.recipes.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Строка в таблице Favorite (избранное).
 */
@Entity(tableName = "favorite")
public class Favorite {

    /** Уникальный идентификатор строки. */
    @PrimaryKey(autoGenerate = true)
    public long id;

    /** ID рецепта. */
    @ColumnInfo(name = "receipt_id")
    public long receiptId;

    /** Наименование рецепта. */
    @ColumnInfo(name = "receipt_name")
    public String receiptName;

    /** Время приготовления. */
    @ColumnInfo(name = "receipt_time")
    public int receiptTime;

    /** Количество порций. */
    @ColumnInfo(name = "receipt_portion")
    public int receiptPortion;
}
