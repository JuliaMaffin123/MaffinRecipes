package com.maffin.recipes.ui.detail.components;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.maffin.recipes.Config;
import com.maffin.recipes.network.Component;
import com.maffin.recipes.network.ReceiptService;
import com.maffin.recipes.network.ResponseSuccess;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Модель данных для отображения на фрагменте ИНГРЕДИЕНТЫ.
 */
public class ComponentsViewModel extends ViewModel {
    /** TAG для логирования. */
    private static final String TAG = "ComponentsViewModel";
    /** Список ингредиентов. */
    private final MutableLiveData<List<Component>> mList;

    public ComponentsViewModel() {
        // Инциализируем переменные, через которые будут передаваться данные в активность
        mList = new MutableLiveData<>();
    }

    /**
     * Возвращает объект LiveData со списком записей.
     * @return LiveData
     */
    public LiveData<List<Component>> getList() {
        return mList;
    }

    /**
     * Загрузка списка ингредиентов рецепта с сервера.
     * @param id    ID рецепта
     */
    public void loadData(long id) {
        // Реализация запроса через Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ReceiptService service = retrofit.create(ReceiptService.class);
        Call<ResponseSuccess<Component>> call = service.fetchComponents(id);
        call.enqueue(new Callback<ResponseSuccess<Component>>() {

            @Override
            public void onResponse(Call<ResponseSuccess<Component>> call, Response<ResponseSuccess<Component>> response) {
                if (response.isSuccessful()) {
                    // При успешном запросе извлекаем массив ингредиентов из ответа и передаем их на фрагмент через LiveData
                    ResponseSuccess<Component> body = response.body();
                    mList.setValue(body.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseSuccess<Component>> call, Throwable t) {
                Log.e(TAG, "Что-то пошло не так!", t);
            }
        });
    }

}
