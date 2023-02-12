package com.maffin.recipes.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.maffin.recipes.db.entity.Favorite;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    List<Favorite> getAll();

    @Query("SELECT * FROM favorite WHERE receipt_id = :id")
    Favorite getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Favorite favorite);

    @Update
    void update(Favorite favorite);

    @Delete
    void delete(Favorite favorite);
}
