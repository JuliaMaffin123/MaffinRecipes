package com.maffin.recipes.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.maffin.recipes.network.AsyncRequest;

import org.json.JSONObject;

public class HomeViewModel extends ViewModel {
    /** TAG для логирования. */
    private static final String TAG = "HomeViewModel";
    /** Список рецептов. */
    //private final MutableLiveData<List<Receipt>> mList;
    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        // Инциализируем переменные, через которые будут передаваться данные в активность
        mText = new MutableLiveData<>();
    }

    /**
     * Возвращает объект LiveData со списком записей.
     * @return LiveData
     */
    public LiveData<String> getText() {
        //  return mList;
        return mText;
    }

    /**
     * Загрузка списка рецептов из базы данных.
     */
    public void loadData() {
        new RequestList().execute("https://my-school2070.ru/api/v1/?controller=Recipes&action=getList");
    }

    /**
     * Асинхронный запрос списка рецептов.
     */
    private class RequestList extends AsyncRequest {
        @Override
        public void onPostExecute(JSONObject data) {
            mText.setValue(data.toString());
        }
    }
}