package com.maffin.recipes.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Строка в таблице Cart (корзина покупок).
 */
@Entity(tableName = "cart")
public class Cart {

    /** Уникальный идентификатор строки. */
    @PrimaryKey(autoGenerate = true)
    public long id;

    /** ID рецепта. */
    @ColumnInfo(name = "receipt_id")
    public long receiptId;

    /** ID ингредиента. */
    @ColumnInfo(name = "item_id")
    public long itemId;

    /** Наименование ингредиента. */
    @ColumnInfo(name = "item_name")
    public String itemName;

    /** Количество. */
    @ColumnInfo(name = "item_cnt")
    public String itemCnt;

    /** Отметка "куплен / не куплен. */
    @ColumnInfo(name = "item_chk")
    public boolean itemChk;

}
