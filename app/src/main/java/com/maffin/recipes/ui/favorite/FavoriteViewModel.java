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

public class FavoriteViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FavoriteViewModel() {
        // Запрос в базу
        AppDatabase db = App.getInstance().getDatabase();
        FavoriteDao favoriteDao = db.favoriteDao();
        List<Favorite> favoriteList = favoriteDao.getAll();
        int cnt = favoriteList.size();
        Log.d("FVM", "Отобрано " + cnt + " записей");
        // Наполняем текстданными
        mText = new MutableLiveData<>();
        mText.setValue("This is favorite fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}