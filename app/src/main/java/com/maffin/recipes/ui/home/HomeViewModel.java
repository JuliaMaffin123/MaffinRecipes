package com.maffin.recipes.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.maffin.recipes.network.AsyncRequest;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.network.ReceiptService;
import com.maffin.recipes.network.ResponseSuccess;

import org.json.JSONObject;

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
        // Реализация запроса черз отдельный поток
        // new RequestList().execute("https://testoligon.ru/recipes/list");

        // Реализация запроса через Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://testoligon.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ReceiptService service = retrofit.create(ReceiptService.class);
        Call<ResponseSuccess<Receipt>> call = service.fetchReceiptList();
        call.enqueue(new Callback<ResponseSuccess<Receipt>>() {

            @Override
            public void onResponse(Call<ResponseSuccess<Receipt>> call, Response<ResponseSuccess<Receipt>> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    mText.setValue("Retrofit: " + json);
                }
            }

            @Override
            public void onFailure(Call<ResponseSuccess<Receipt>> call, Throwable t) {
                mText.setValue("Что-то пошло не так: " + t.getMessage());
            }
        });
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