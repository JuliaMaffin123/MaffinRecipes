package com.maffin.recipes.ui.detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.maffin.recipes.Config;
import com.maffin.recipes.network.Component;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.network.ReceiptService;
import com.maffin.recipes.network.ResponseSuccess;
import com.maffin.recipes.network.Step;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DetailViewModel extends ViewModel {
    /** TAG для логирования. */
    private static final String TAG = "HomeViewModel";
    /** Рецепт. */
    private final MutableLiveData<Receipt> mReceipt;
    /** Список ингридиентов. */
    private final MutableLiveData<List<Component>> mComponents;
    /** Список шагов. */
    private final MutableLiveData<List<Step>> mSteps;

    public DetailViewModel() {
        // Инциализируем переменные, через которые будут передаваться данные в активность
        mReceipt = new MutableLiveData<>();
        mComponents = new MutableLiveData<>();
        mSteps = new MutableLiveData<>();
    }

    /**
     * Возвращает объект LiveData с рецептом.
     * @return LiveData
     */
    public LiveData<Receipt> getReceipt() {
        return mReceipt;
    }

    /**
     * Возвращает объект LiveData со списком ингридиентов.
     * @return LiveData
     */
    public LiveData<List<Component>> getComponents() {
        return mComponents;
    }

    /**
     * Возвращает объект LiveData со списком шагов приготовления.
     * @return LiveData
     */
    public LiveData<List<Step>> getSteps() {
        return mSteps;
    }

    /**
     * Загрузка рецепта из базы данных.
     * @param id    ID рецепта
     */
    public void loadReceipt(long id) {
        // Реализация запроса через Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ReceiptService service = retrofit.create(ReceiptService.class);
        Call<ResponseSuccess<Receipt>> call = service.fetchReceipt(id);
        call.enqueue(new Callback<ResponseSuccess<Receipt>>() {

            @Override
            public void onResponse(Call<ResponseSuccess<Receipt>> call, Response<ResponseSuccess<Receipt>> response) {
                if (response.isSuccessful()) {
                    // При успешном запросе извлекаем массив рецептов из ответа и передаем первый элемент списка через LiveData
                    ResponseSuccess<Receipt> body = response.body();
                    mReceipt.setValue(body.getData().get(0));
                }
            }

            @Override
            public void onFailure(Call<ResponseSuccess<Receipt>> call, Throwable t) {
                Log.e(TAG, "Что-то пошло не так!", t);
            }
        });
    }

    /**
     * Загрузка ингридиентов рецепта из базы данных.
     */
    public void loadComponents(long id) {
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
                    // При успешном запросе извлекаем массив ингридиентов из ответа и передаем его через LiveData
                    ResponseSuccess<Component> body = response.body();
                    mComponents.setValue(body.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseSuccess<Component>> call, Throwable t) {
                Log.e(TAG, "Что-то пошло не так!", t);
            }
        });
    }

    /**
     * Загрузка шагов рецепта из базы данных.
     */
    public void loadSteps(long id) {
        // Реализация запроса через Retrofit
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
                    // При успешном запросе извлекаем массив шагов из ответа и передаем его через LiveData
                    ResponseSuccess<Step> body = response.body();
                    mSteps.setValue(body.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseSuccess<Step>> call, Throwable t) {
                Log.e(TAG, "Что-то пошло не так!", t);
            }
        });
    }
}
