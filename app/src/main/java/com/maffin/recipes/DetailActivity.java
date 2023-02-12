package com.maffin.recipes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.maffin.recipes.databinding.ActivityDetailBinding;

/**
 * Активность для отображения детальной информации о рецепте.
 *
 * См. как добавить кнопку back в заголовок: https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar
 */
public class DetailActivity extends AppCompatActivity {

    /** Разметка активности. */
    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализируем разметку
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Добавляем кнопку возврата на главный экран
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean  onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // При нажатии на кнопку возврата закрываем активность и возвращаемся на главный экран
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}