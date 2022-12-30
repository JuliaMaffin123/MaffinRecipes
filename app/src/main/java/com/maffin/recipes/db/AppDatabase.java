package com.maffin.recipes.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.maffin.recipes.db.dao.CartDao;
import com.maffin.recipes.db.dao.CartReceiptDao;
import com.maffin.recipes.db.dao.FavoriteDao;
import com.maffin.recipes.db.entity.Cart;
import com.maffin.recipes.db.entity.CartReceipt;
import com.maffin.recipes.db.entity.Favorite;

/**
 * База данных.
 *
 * См. https://startandroid.ru/ru/courses/architecture-components/27-course/architecture-components/529-urok-5-room-osnovy.html
 * См. https://startandroid.ru/ru/courses/architecture-components/27-course/architecture-components/530-urok-6-room-entity.html
 */
@Database(entities = {Favorite.class, Cart.class, CartReceipt.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();
    public abstract CartDao cartDao();
    public abstract CartReceiptDao cartReceiptDao();

}
