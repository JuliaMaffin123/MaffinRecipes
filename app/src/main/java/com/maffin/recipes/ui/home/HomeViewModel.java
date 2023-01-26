package com.maffin.recipes.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.network.ReceiptService;
import com.maffin.recipes.network.ResponseSuccess;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Модель данных для отображения на фрагменте ГЛАВНАЯ.
 */
public class HomeViewModel extends ViewModel {
    /** TAG для логирования. */
    private static final String TAG = "HomeViewModel";
    /** Адрес сервера. */
    private final static String BASE_URL = "https://testoligon.ru";
    /** Список рецептов. */
    private final MutableLiveData<List<Receipt>> mList;

    public HomeViewModel() {
        // Инциализируем переменные, через которые будут передаваться данные в активность
        mList = new MutableLiveData<>();
    }

    /**
     * Возвращает объект LiveData со списком записей.
     * @return LiveData
     */
    public LiveData<List<Receipt>> getList() {
        return mList;
    }

    /**
     * Загрузка списка рецептов из базы данных.
     */
    public void loadData() {
        // Реализация запроса через Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ReceiptService service = retrofit.create(ReceiptService.class);
        Call<ResponseSuccess<Receipt>> call = service.fetchReceiptList();
        call.enqueue(new Callback<ResponseSuccess<Receipt>>() {

            @Override
            public void onResponse(Call<ResponseSuccess<Receipt>> call, Response<ResponseSuccess<Receipt>> response) {
                if (response.isSuccessful()) {
                    // При успешном запросе извлекаем массив рецептов из ответа и передаем их на фрагмент через LiveData
                    ResponseSuccess<Receipt> body = response.body();
                    mList.setValue(body.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseSuccess<Receipt>> call, Throwable t) {
                Log.e(TAG, "Что-то пошло не так!", t);
            }
        });
    }

}