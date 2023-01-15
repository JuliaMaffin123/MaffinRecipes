package com.maffin.recipes.ui.favorite;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.maffin.recipes.App;
import com.maffin.recipes.db.AppDatabase;
import com.maffin.recipes.db.dao.FavoriteDao;
import com.maffin.recipes.db.entity.Favorite;

import java.util.List;

/**
 * Модель данных для отображения на фрагменте ИЗБРАННОЕ.
 */
public class FavoriteViewModel extends ViewModel {

    private final MutableLiveData<List<Favorite>> mList;
    private final AppDatabase db;

    public FavoriteViewModel() {
        // Инциализируем переменные, через которые будут передаваться данные в активность
        mList = new MutableLiveData<>();
        // Инициализируем соединение с базой данных
        db = App.getInstance().getDatabase();
    }

    /**
     * Возвращает объект LiveData со списком записей.
     * @return LiveData
     */
    public LiveData<List<Favorite>> getList() {
        return mList;
    }

    /**
     * Загрузка списка рецептов из базы данных.
     */
    public void loadData() {
        // Запрос в базу
        FavoriteDao favoriteDao = db.favoriteDao();
        List<Favorite> favoriteList = favoriteDao.getAll();
        Log.d("FVM", "Отобрано " + favoriteList.size() + " записей");
        // Запоминаем отобранный список
        mList.setValue(favoriteList);
    }
}