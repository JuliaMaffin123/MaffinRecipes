package com.maffin.recipes.ui.favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.databinding.FragmentFavoriteBinding;
import com.maffin.recipes.db.entity.Favorite;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
     * Вызывается при нажатии на элемент списка удаления из избранного.
     *
     * @param view Объект, который был нажат
     */
    public void onDeleteClick(final View view) {
        View parent = (View) view.getParent();
        final int position = getListView().getPositionForView(parent);
        Toast.makeText(view.getContext(), "Тапнули удаление на элементе " + position, Toast.LENGTH_LONG);
    }
}