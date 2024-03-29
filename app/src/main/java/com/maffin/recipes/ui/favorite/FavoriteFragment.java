package com.maffin.recipes.ui.favorite;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.databinding.FragmentFavoriteBinding;
import com.maffin.recipes.db.entity.Favorite;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;

/**
 * Фрагмент для отображения избранных рецептов.
 *
 * См. статья про обсерверы и LiveData: https://habr.com/ru/post/577482/
 * См. статья про ListView: https://developer.alexanderklimov.ru/android/views/listview.php
 * См. статью про RelativeLayoute: https://developer.alexanderklimov.ru/android/layout/relativelayout.php
 * См. статью про кастомизацию элементов ListView: https://www.vogella.com/tutorials/AndroidListView/article.html
 */
public class FavoriteFragment extends Fragment {

    /** TAG для логирования. */
    private static final String TAG = "FavoriteFragment";
    /** Разметка фрагмента. */
    private FragmentFavoriteBinding binding;
    /** Модель данных фрагмента. */
    private FavoriteViewModel favoriteViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Разрешаем свое меню
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализируем разметку фрагмента
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);

        // Инициализируем модель данных
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в ListView
        final ListView listView = binding.list;
        listView.setEmptyView(binding.empty);
        favoriteViewModel.getList().observe(getViewLifecycleOwner(), favorites -> {
            ArrayAdapter<Favorite> adapter = new FavoriteAdapter(getContext(), favorites);
            listView.setAdapter(adapter);
        });
        // Навешиваем прослушку на нажатие по элементу списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Зная view - элемент списка, получим из него холдер с данными
                final AbstractListAdapter.ViewHolder holder = (AbstractListAdapter.ViewHolder) view.getTag();
                // Запускаем активность и передаем в нее ID рецепта
                startDetailActivity(view, position, holder.getId());
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        // Освобождаем ресурсы
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Загружаем данные из базы
        favoriteViewModel.loadData();
        // Разрешаем показ заголовка
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        // Явно задам кнопку меню и заголовок
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        toolbar.setTitle(R.string.menu_favorite);
    }

    /**
     * Срабатывает при создании меню.
     * @param menu  мею
     * @return
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    /**
     * Запуск активности с деталями рецепта.
     * @param view      элемент, который породил событие
     * @param position  позиция в списке
     * @param id        ID рецепта
     */
    private void startDetailActivity(View view, int position, long id) {
        Log.d(TAG, "position: " + position + " id: " + id);
        // Передаем параметр с ID рецепта
        Bundle bundle = new Bundle();
        bundle.putLong(Config.RECEIPT_ID, id);
        // Переключаем фрагмент
        Navigation.findNavController(view).navigate(R.id.nav_detail, bundle);
    }
}