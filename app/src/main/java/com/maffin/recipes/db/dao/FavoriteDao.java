package com.maffin.recipes.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.maffin.recipes.db.entity.Favorite;

import java.util.List;

/**
 * DAO-класс (Data Access Object) для доступа к локальной таблице Favorite (избранное).
 */
@Dao
public interface FavoriteDao {
    /**
     * Возвращает список рецептов в Избранном.
     * @return список рецептов
     */
    @Query("SELECT * FROM favorite")
    List<Favorite> getAll();

    /**
     * Возвращает одну строку по ID рецепта.
     * @param id    ID рецепта
     * @return одна строка
     */
    @Query("SELECT * FROM favorite WHERE receipt_id = :id")
    Favorite getById(long id);

    /**
     * Добавление строки.
     * @param favorite строка
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Favorite favorite);

    /**
     * Обновление строки
     * @param favorite строка
     */
    @Update
    void update(Favorite favorite);

    /**
     * Удаление строки.
     * @param favorite строка
     */
    @Delete
    void delete(Favorite favorite);
}
