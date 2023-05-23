package com.maffin.recipes.ui.detail;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.maffin.recipes.App;
import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.databinding.FragmentDetailBinding;
import com.maffin.recipes.db.AppDatabase;
import com.maffin.recipes.db.dao.FavoriteDao;
import com.maffin.recipes.db.entity.Favorite;
import com.maffin.recipes.network.Component;
import com.maffin.recipes.network.ImageManager;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.network.Step;
import com.maffin.recipes.ui.draw.DrawUtils;

import java.util.List;

/**
 * Фрагмент для отображения детальной информации о рецепте.
 *
 * См. как добавить кнопку back в заголовок: https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar
 */
public class DetailFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    /** TAG для логирования. */
    private static final String TAG = "DetailActivity";

    /** Шаблон URL-а для загрузки изображений. */
    private static final String FILE_TEMPLATE = "receipt-%d-image.png";
    private static final String URL_TEMPLATE = Config.BASE_URL + "/images/" + FILE_TEMPLATE;

    /** Разметка активности. */
    private FragmentDetailBinding binding;
    /** ID рецепта. */
    private long id;
    /** Информация о рецепте. */
    private Receipt receipt;
    private List<Component> components;
    private List<Step> steps;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Получим аргументы вызова
        id = getArguments().getLong(Config.RECEIPT_ID, 0);
        // Разрешаем свое меню
        setHasOptionsMenu(true);
    }

    /**
     * Срабатывает при создании активности.
     * @param savedInstanceState    окружение с сохраненным состоянием активности
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализируем разметку
        binding = FragmentDetailBinding.inflate(inflater, container, false);

        // Устанавливаем фоновое изображение
        if (id > 0) {
            // Запускаем загрузку картинки
            String url = String.format(URL_TEMPLATE, id);
            ImageManager.fetchImage(getContext(), url, binding.detailBackgroundImage, R.drawable.ic_cooking_chef_opacity);
        }
        // Инициализируем соединение с базой данных
        db = App.getInstance().getDatabase();
        // Определим, является ли рецепт избранным
        favorite = getFavorite(id);
        // Инициализируем модель данных
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        // Навешиваем прослушку на изменение данных в модели данных.
        // Когда модель получит данные с сервера, прослушка заменит описание рецепта
        detailViewModel.getReceipt().observe(getViewLifecycleOwner(), r -> {
            receipt = r;
            // Название рецепта
            binding.receiptName.setText(receipt.getName());
            // Время приготовления
            if (receipt.getTime() != 0) {
                binding.receiptTime.setVisibility(View.VISIBLE);
                binding.receiptTime.setText(getString(R.string.template_time, receipt.getTime()));
                DrawUtils.spanImageIntoText(getContext(), binding.receiptTime,
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
                DrawUtils.spanImageIntoText(getContext(), binding.receiptEnergy,
                        getString(R.string.holder_energy),
                        R.drawable.ic_outline_room_service_24,
                        getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                        getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
            } else {
                binding.receiptEnergy.setVisibility(View.GONE);
            }
        });
        detailViewModel.getComponents().observe(getViewLifecycleOwner(), c -> {
            components = c;
        });
        detailViewModel.getSteps().observe(getViewLifecycleOwner(), s -> {
            steps = s;
        });
        // Инициализируем вкладки
        tabLayout = binding.tabLayout;
        viewPager = binding.pager;
        // Добавляем на разметку две вкладки
        tabLayout.addTab(tabLayout.newTab().setText("Ингредиенты"));
        tabLayout.addTab(tabLayout.newTab().setText("Приготовление"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        // Инициализируем адаптер
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        // Передаем адаптер компоненту для управления
        viewPager.setAdapter(adapter);
        // Добавляем прослушку onTabSelectedListener
        tabLayout.setOnTabSelectedListener(this);
        return binding.getRoot();
    }

    /**
     * Срабатывает при восстановлении активности.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Загружаем данные
        detailViewModel.loadReceipt(id);
        detailViewModel.loadComponents(id);
        detailViewModel.loadSteps(id);

        // Меняем заголовок
        replaceToolbar(true);

        // При активации фрагмента всегда переключаемся на первую вкладку
        viewPager.setCurrentItem(0);
    }

    private void replaceToolbar(boolean isOnOff) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (isOnOff) {
            // Добавляем кнопку возврата на главный экран
            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
            // Удаляем заголовок
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            // Восстанавливаем кнопку меню
            toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
            // Восстанавливаем видимость заголовка
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
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
     * Срабатывает при подготовке меню для отображения.
     * @param menu  меню
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Решаем проблему: пункты меню с переопределенным шаблоном не кликабельны
        final MenuItem cartMenuItem = menu.findItem(R.id.action_cart);
        RelativeLayout rootView = (RelativeLayout) cartMenuItem.getActionView();
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(cartMenuItem);
            }
        });
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * Срабатывает при клик на пункт меню (в нашем случае на кнопки в ActionBar).
     * @param item  пункт меню
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        switch (item.getItemId()) {
            case android.R.id.home:
                // Передаем управление прошлому фрагменту в стеке
                replaceToolbar(false);
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            case R.id.action_share:
                // Нажата кнопка ПОДЕЛИТЬСЯ
                shareReceipt();
                return true;
            case R.id.action_favorite:
                // Нажата кнопка ИЗБРАННОЕ
                toggleFavorite();
                return true;
            case R.id.action_cart:
                // Нажата кнопка КОРЗИНА.
                // Восстанавливаем видимость заголовка
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
//                Navigation.findNavController(getActivity().findViewById(R.id.detail_backgroundImage))
//                        .navigate(R.id.nav_cart);

                // Manually build the NavOptions that manually do
                // what NavigationUI.onNavDestinationSelected does for you
                Bundle bundle = new Bundle();
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_home, false, true)
                        .setRestoreState(true)
                        .build();

                NavController navController = Navigation.findNavController(getActivity(),
                        R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_cart, bundle, navOptions);
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Передаем управление прошлому фрагменту в стеке
                replaceToolbar(false);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Срабатывает при создании меню.
     * @param menu  мею
     * @return
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        // Инициируем разметку
        inflater.inflate(R.menu.detail, menu);
        // Сохраняем ссылки на пункты меню, которыми хотим управлять программно
        favoriteMenuItem = menu.findItem(R.id.action_favorite);
        cartMenuItem = menu.findItem(R.id.action_cart).getActionView();
        txtViewCount = (TextView) cartMenuItem.findViewById(R.id.txtCount);
        // Отрисуем правильный цвет у кнопки меню
        changeColorMenuFavorite(favorite);
    }

    /**
     * Отображает на бейдже число отмеченных ингредиентов.
     * @param count число
     */
    public void showComponentsCount(int count) {
        // Подсчитаем, сколько выбрано ингредиентов
        if (count < 0) {
            return;
        }
        if (txtViewCount != null) {
            getActivity().runOnUiThread(new Runnable() {
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
            //DrawUtils.tintMenuIcon(getContext(), favoriteMenuItem, R.color.md_theme_light_scrim);
            favoriteMenuItem.setIcon(R.drawable.ic_baseline_favorite_border_24);
        } else {
            // Записи в избранном: другой цвет
            //DrawUtils.tintMenuIcon(getContext(), favoriteMenuItem, R.color.md_theme_light_primary);
            favoriteMenuItem.setIcon(R.drawable.ic_baseline_favorite_24);
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

    /**
     * Публикует рецепт.
     */
    public void shareReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("*" + receipt.getName() + "*\n");
        if (components != null) {
            for (Component c : components) {
                sb.append("• " + c.getName() + " " + c.getCount() + "\n");
            }
            sb.append("\n");
        }
        if (steps != null) {
            int i = 0;
            for (Step s : steps) {
                i++;
                sb.append("Шаг " + i + ": " + s.getDescription() + "\n");
            }
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        sendIntent.setType("text/html");
        startActivity(sendIntent);
    }
}