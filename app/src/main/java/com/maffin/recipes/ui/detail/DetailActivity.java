package com.maffin.recipes.ui.detail;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maffin.recipes.App;
import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.databinding.ActivityDetailBinding;
import com.maffin.recipes.db.AppDatabase;
import com.maffin.recipes.db.dao.FavoriteDao;
import com.maffin.recipes.db.entity.Favorite;
import com.maffin.recipes.network.ImageManager;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.ui.draw.DrawUtils;
import com.maffin.recipes.ui.draw.VerticalImageSpan;
import com.maffin.recipes.ui.home.HomeAdapter;
import com.maffin.recipes.ui.home.HomeViewModel;

/**
 * Активность для отображения детальной информации о рецепте.
 *
 * См. как добавить кнопку back в заголовок: https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar
 */
public class DetailActivity extends AppCompatActivity {
    /** TAG для логирования. */
    private static final String TAG = "DetailActivity";

    /** Шаблон URL-а для загрузки изображений. */
    private static final String URL_TEMPLATE = Config.BASE_URL + "/images/receipt-%d-image.png";

    /** Разметка активности. */
    private ActivityDetailBinding binding;
    /** ID рецепта. */
    private long id;
    /** Информация о рецепте. */
    private Receipt receipt;
    /** База данных. */
    private AppDatabase db;
    /** Запись в таблице ИЗБРАННОЕ. */
    private Favorite favorite;
    /** Кнопка меню ИЗБРАННОЕ. */
    private MenuItem favoriteMenuItem;
    /** Модель данных фрагмента. */
    private DetailViewModel detailViewModel;

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
        // Инициализируем соединение с базой данных
        db = App.getInstance().getDatabase();
        // Определим, является ли рецепт избранным
        favorite = getFavorite(id);
        // Инициализируем модель данных
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в ListView
        detailViewModel.getReceipt().observe(this, r -> {
            receipt = r;
            // Название рецепта
            binding.receiptName.setText(receipt.getName());
            // Время приготовления
            if (receipt.getTime() != 0) {
                binding.receiptTime.setVisibility(View.VISIBLE);
                binding.receiptTime.setText(getString(R.string.template_time, receipt.getTime()));
                DrawUtils.spanImageIntoText(DetailActivity.this, binding.receiptTime,
                        getString(R.string.holder_time),
                        R.drawable.ic_baseline_access_time_24,
                        getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                        getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
            } else {
                binding.receiptTime.setVisibility(View.GONE);
            }
            // Каллорийность
            if (receipt.getEnergy() != 0) {
                binding.receiptEnergy.setVisibility(View.VISIBLE);
                binding.receiptEnergy.setText(getString(R.string.template_energy, receipt.getEnergy()));
                DrawUtils.spanImageIntoText(DetailActivity.this, binding.receiptEnergy,
                        getString(R.string.holder_energy),
                        R.drawable.ic_baseline_fastfood_24,
                        getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                        getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
            } else {
                binding.receiptEnergy.setVisibility(View.GONE);
            }
        });

//        final ListView listView = binding.list;
//        listView.setEmptyView(binding.empty);
//        homeViewModel.getList().observe(getViewLifecycleOwner(), receipts -> {
//            ArrayAdapter<Receipt> adapter = new HomeAdapter(getContext(), receipts);
//            listView.setAdapter(adapter);
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Загружаем данные из сети
        detailViewModel.loadReceipt(id);
        //detailViewModel.loadComponents(id);
        //detailViewModel.loadSteps(id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
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
                toggleFavorite();
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
        // Инициируем разметку
        getMenuInflater().inflate(R.menu.detail, menu);
        // Сохраняем ссылки на пункты меню, которыми хотим управлять программно
        favoriteMenuItem = menu.findItem(R.id.action_favorite);
        // Отрисуем правильный цвет у кнопки меню
        changeColorMenuFavorite(favorite);
        return true;
    }

    /**
     * Определяет по БД, является ли рецепт избранным?
     * @param id    ID рецепта
     * @return
     */
    private Favorite getFavorite(long id) {
        // Запрос в базу
        FavoriteDao favoriteDao = db.favoriteDao();
        Favorite favorite = favoriteDao.getById(id);
        Log.d(TAG, "Отобрано " + (favorite == null ? "0" : "1") + " записей");
        return favorite;
    }

    /**
     * Меняет цвет иконки Избранное в зависимости от того добавлен ли рецепт в избранное или нет.
     * @param favorite  запись из таблицы Избранное
     */
    private void changeColorMenuFavorite(Favorite favorite) {
        if (favorite == null) {
            // Записи нет в избранном: белый цвет
            tintMenuIcon(DetailActivity.this, favoriteMenuItem, android.R.color.white);
        } else {
            // Записи в избранном: другой цвет
            tintMenuIcon(DetailActivity.this, favoriteMenuItem, android.R.color.holo_orange_dark);
        }
    }

    /**
     * Меняет цвет иконок в меню.
     * @param context   контекст
     * @param item      элемент меню
     * @param color     цвет
     */
    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        if (item != null) {
            Drawable normalDrawable = item.getIcon();
            Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
            DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));
            item.setIcon(wrapDrawable);
        }
    }

    /**
     * Переключает состояние рецепта Избранное.
     */
    private void toggleFavorite() {
        FavoriteDao favoriteDao = db.favoriteDao();
        if (favorite == null) {
            // Не был в Избранном. Добавляем!
            favorite = new Favorite();
            favorite.receiptId = id;
            favorite.receiptName = receipt.getName();
            favorite.receiptKkal = receipt.getEnergy();
            favorite.receiptTime = receipt.getTime();
            long rowId = favoriteDao.insert(favorite);
            favorite.id = rowId;
        } else {
            // Рецепт в избранном. Надо удалить!
            favoriteDao.delete(favorite);
            favorite = null;
        }
        // Отрисовываем текущий статус
        changeColorMenuFavorite(favorite);
    }

}