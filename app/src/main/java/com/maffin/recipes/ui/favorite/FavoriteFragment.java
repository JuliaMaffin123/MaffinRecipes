package com.maffin.recipes.ui.favorite;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.databinding.FragmentFavoriteBinding;
import com.maffin.recipes.db.entity.Favorite;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;
import com.maffin.recipes.ui.detail.DetailFragment;
import com.maffin.recipes.ui.search.SearchFragment;

/**
 * Фрагмент для отображения избранных рецептов.
 *
 * См. статья про обсерверы и LiveData: https://habr.com/ru/post/577482/
 * См. статья про ListView: https://developer.alexanderklimov.ru/android/views/listview.php
 * См. статью про RelativeLayoute: https://developer.alexanderklimov.ru/android/layout/relativelayout.php
 * См. статью про кастомизацию элементов ListView: https://www.vogella.com/tutorials/AndroidListView/article.html
 */
public class FavoriteFragment extends ListFragment {

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

        // Разрешаем показ заголовка
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        // Явно задам кнопку меню и заголовок
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        toolbar.setTitle(R.string.menu_favorite);

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
                Toast.makeText(getContext(), TAG + ": onItemClick", Toast.LENGTH_LONG).show();
                // Зная view - элемент списка, получим из него холдер с данными
                final AbstractListAdapter.ViewHolder holder = (AbstractListAdapter.ViewHolder) view.getTag();
                // Запускаем активность и передаем в нее ID рецепта
                startDetailActivity(position, holder.getId());
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
        inflater.inflate(R.menu.main, menu);
    }

    /**
     * Срабатывает при клик на пункт меню (в нашем случае на кнопки в ActionBar).
     * @param item  пункт меню
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Нажата кнопка ПОИСК
                Toast.makeText(getContext(), TAG + ": ПОИСК", Toast.LENGTH_LONG).show();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                SearchFragment searchFragment = new SearchFragment();
                ft.replace(R.id.nav_host_fragment_content_main, searchFragment);
                ft.commit();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Запуск активности с деталями рецепта.
     * @param position  позиция в списке
     * @param id        ID рецепта
     */
    private void startDetailActivity(int position, long id) {
        Log.d(TAG, "position: " + position + " id: " + id);
        Intent intent = new Intent(getContext(), DetailFragment.class);
        intent.putExtra(Config.RECEIPT_ID, id);
        startActivity(intent);
    }
}