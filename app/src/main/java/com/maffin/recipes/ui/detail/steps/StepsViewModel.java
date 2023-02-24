package com.maffin.recipes.ui.detail.steps;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.maffin.recipes.Config;
import com.maffin.recipes.network.ReceiptService;
import com.maffin.recipes.network.ResponseSuccess;
import com.maffin.recipes.network.Step;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Модель данных для отображения на фрагменте ПРИГОТОВЛЕНИЕ.
 */
public class StepsViewModel extends ViewModel {
    /** TAG для логирования. */
    private static final String TAG = "ComponentsViewModel";
    /** Список ингредиентов. */
    private final MutableLiveData<List<Step>> mList;

    /**
     * Конструктор.
     */
    public StepsViewModel() {
        // Инциализируем переменные, через которые будут передаваться данные в активность
        mList = new MutableLiveData<>();
    }

    /**
     * Возвращает объект LiveData со списком записей.
     * @return LiveData
     */
    public LiveData<List<Step>> getList() {
        return mList;
    }

    /**
     * Загрузка списка ингредиентов рецепта с сервера.
     * @param id    ID рецепта
     */
    public void loadData(long id) {
        // Запрашиваем данные о шагах приготовления через Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ReceiptService service = retrofit.create(ReceiptService.class);
        Call<ResponseSuccess<Step>> call = service.fetchSteps(id);
        call.enqueue(new Callback<ResponseSuccess<Step>>() {

            @Override
            public void onResponse(Call<ResponseSuccess<Step>> call, Response<ResponseSuccess<Step>> response) {
                if (response.isSuccessful()) {
                    // При успешном запросе извлекаем массив шагов из ответа и передаем их на фрагмент через LiveData
                    ResponseSuccess<Step> body = response.body();
                    List<Step> list = body.getData();
                    // Передаем данные в активность
                    mList.setValue(list);
                }
            }

            @Override
            public void onFailure(Call<ResponseSuccess<Step>> call, Throwable t) {
                Log.e(TAG, "Что-то пошло не так!", t);
            }
        });
    }

}
