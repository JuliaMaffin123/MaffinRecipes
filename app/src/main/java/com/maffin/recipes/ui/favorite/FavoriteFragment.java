package com.maffin.recipes.ui.favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.databinding.FragmentFavoriteBinding;
import com.maffin.recipes.db.entity.Favorite;
import com.maffin.recipes.ui.adapter.FavoriteAdapter;

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
    private static final String TAG = "FF";
    /** Разметка фрагмента. */
    private FragmentFavoriteBinding binding;
    /** Модель данных фрагмента. */
    private FavoriteViewModel favoriteViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Инициализируем разметку фрагмента
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);

        // Инициализируем модель данных
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в ListView
        final ListView listView = binding.listFavorite;
        favoriteViewModel.getList().observe(getViewLifecycleOwner(), favorites -> {
            ArrayAdapter<Favorite> adapter = new FavoriteAdapter(getContext(), favorites);
            listView.setAdapter(adapter);
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
}