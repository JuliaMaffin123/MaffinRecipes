package com.maffin.recipes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.maffin.recipes.databinding.ActivityDetailBinding;
import com.maffin.recipes.network.ImageManager;

/**
 * Активность для отображения детальной информации о рецепте.
 *
 * См. как добавить кнопку back в заголовок: https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar
 */
public class DetailActivity extends AppCompatActivity {

    /** Шаблон URL-а для загрузки изображений. */
    private static final String URL_TEMPLATE = Config.BASE_URL + "/images/receipt-%d-image.png";

    /** Разметка активности. */
    private ActivityDetailBinding binding;
    /** ID рецепта. */
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализируем разметку
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Добавляем кнопку возврата на главный экран
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Извлекаем ID рецепта из Extras
        id = getIntent().getLongExtra(Config.RECEIPT_ID, -1);
        // Устанавливаем фоново изображение
        if (id > 0) {
            // Запускаем загрузку картинки
            String url = String.format(URL_TEMPLATE, id);
            ImageManager.fetchImage(getApplicationContext(), url, binding.detailBackgroundImage, R.drawable.ic_cooking_chef_opacity);
        }
    }

    @Override
    public boolean  onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Нажата кнопка НАЗАД
                finish();
                return true;
            case R.id.action_share:
                // Нажата кнопка ПОДЕЛИТЬСЯ
                Toast.makeText(getApplicationContext(), "ПОДЕЛИТЬСЯ", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_favorite:
                // Нажата кнопка ИЗБРАННОЕ
                Toast.makeText(getApplicationContext(), "ИЗБРАННОЕ", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_cart:
                // Нажата кнопка КОРЗИНА
                Toast.makeText(getApplicationContext(), "КОРЗИНА", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

}