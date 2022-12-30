package com.maffin.recipes;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.maffin.recipes.db.AppDatabase;
import com.maffin.recipes.db.entity.Favorite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class FavoriteDaoTest {

    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private AppDatabase mDatabase;

    @Before
    public void initDb() throws Exception {
        mDatabase = Room.inMemoryDatabaseBuilder(
                        appContext,
                        AppDatabase.class)
                .build();
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    @Test
    public void insertAndGetFavorite() {
        Favorite favorite = new Favorite();
        favorite.receiptId = 1000;
        // Добавление в базу данных
        mDatabase.favoriteDao().insert(favorite);

        // Проверка возможности получения данных из базы данных
        List<Favorite> favorites = mDatabase.favoriteDao().getAll();
        assertThat(favorites.size(), is(1));
        Favorite dbFavorite = favorites.get(0);
        assertEquals(dbFavorite.receiptId, favorite.receiptId);
    }
}
