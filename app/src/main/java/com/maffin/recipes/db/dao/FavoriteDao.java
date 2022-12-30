package com.maffin.recipes.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.maffin.recipes.db.entity.Favorite;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    List<Favorite> getAll();

    @Insert
    void insert(Favorite favorite);

    @Update
    void update(Favorite favorite);

    @Delete
    void delete(Favorite favorite);
}
