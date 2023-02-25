package com.maffin.recipes.ui.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
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

/**
 * Активность для отображения детальной информации о рецепте.
 *
 * См. как добавить кнопку back в заголовок: https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar
 */
public class DetailActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
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
    /** Кнопка меню КОРЗИНА. */
    private View cartMenuItem;
    /** Счетчик выбранных элементов в корзине. */
    private TextView txtViewCount;
    /** Модель данных фрагмента. */
    private DetailViewModel detailViewModel;
    /** Ссылка на разметку вкладок. */
    private TabLayout tabLayout;
    /** Компонент управления вкладками. */
    private ViewPager viewPager;

    /**
     * Срабатывает при создании активности.
     * @param savedInstanceState    окружение с сохраненным состоянием активности
     */
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
        // Навешиваем прослушку на изменение данных в модели данных.
        // Когда модель получит данные с сервера, прослушка заменит описание рецепта
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

        // Инициализируем вкладки
        tabLayout = binding.tabLayout;
        viewPager = binding.pager;
        // Добавляем на разметку две вкладки
        tabLayout.addTab(tabLayout.newTab().setText("Ингредиенты"));
        tabLayout.addTab(tabLayout.newTab().setText("Приготовление"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        // Инициализируем адаптер
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        // Передаем адаптер компоненту для управления
        viewPager.setAdapter(adapter);
        // Добавляем прослушку onTabSelectedListener
        tabLayout.setOnTabSelectedListener(this);
    }

    /**
     * Срабатывает при восстановлении активности.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Загружаем данные
        detailViewModel.loadReceipt(id);
    }

    /**
     * Срабатывает при уничтожении активности.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    /**
     * Срабатывает при клик на пункт меню (в нашем случае на кнопки в ActionBar).
     * @param item  пункт меню
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    /**
     * Срабатывает при создании меню.
     * @param menu  мею
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Инициируем разметку
        getMenuInflater().inflate(R.menu.detail, menu);
        // Сохраняем ссылки на пункты меню, которыми хотим управлять программно
        favoriteMenuItem = menu.findItem(R.id.action_favorite);
        cartMenuItem = menu.findItem(R.id.action_cart).getActionView();
        txtViewCount = (TextView) cartMenuItem.findViewById(R.id.txtCount);
        // Отрисуем правильный цвет у кнопки меню
        changeColorMenuFavorite(favorite);
        return true;
    }

    /**
     * отображает на бейдже число отмеченных ингредиентов.
     * @param count число
     */
    public void showComponentsCount(int count) {
        // Подсчитаем, сколько выбрано ингредиентов
        if (count < 0) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (count == 0)
                    txtViewCount.setVisibility(View.GONE);
                else {
                    txtViewCount.setVisibility(View.VISIBLE);
                    txtViewCount.setText(Integer.toString(count));
                }
            }
        });
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
            DrawUtils.tintMenuIcon(DetailActivity.this, favoriteMenuItem, R.color.white);
        } else {
            // Записи в избранном: другой цвет
            DrawUtils.tintMenuIcon(DetailActivity.this, favoriteMenuItem, R.color.orange);
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

    /**
     * Срабатывает при выборе вкладки.
     * См. TabLayout.OnTabSelectedListener
     * @param tab   вкладка
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * Срабатывает, когда вкладка теряет фокус.
     * См. TabLayout.OnTabSelectedListener
     * @param tab   вкладка
     */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    /**
     * Срабатывает при повторном выборе вкладки.
     * См. TabLayout.OnTabSelectedListener
     * @param tab   вкладка
     */
    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * Возвращает ID рецепта.
     * @return
     */
    public long getReceiptId() {
        return id;
    }

    /**
     * Возвращает информацию о рецепте.
     * @return
     */
    public Receipt getReceipt() {
        return receipt;
    }
}