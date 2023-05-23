package com.maffin.recipes.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Строка в таблице CartReceipt (рецепты в корзине покупок).
 */
@Entity(tableName = "cart_receipt")
public class CartReceipt {

    /** Уникальный идентификатор строки. */
    @PrimaryKey(autoGenerate = true)
    public long id;

    /** ID рецепта. */
    @ColumnInfo(name = "receipt_id")
    public long receiptId;

    /** Наименование рецепта. */
    @ColumnInfo(name = "receipt_name")
    public String receiptName;
}
