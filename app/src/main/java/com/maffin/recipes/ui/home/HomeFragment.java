package com.maffin.recipes.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.databinding.FragmentHomeBinding;
import com.maffin.recipes.db.entity.Favorite;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.ui.favorite.FavoriteAdapter;
import com.maffin.recipes.ui.favorite.FavoriteViewModel;

/**
 * Фрагмент для отображения списка рецептов.
 */
public class HomeFragment extends Fragment {
    /** TAG для логирования. */
    private static final String TAG = "HomeFragment";
    /** Разметка фрагмента. */
    private FragmentHomeBinding binding;
    /** Модель данных фрагмента. */
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Инициализируем разметку фрагмента
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Инициализируем модель данных
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в ListView
        final ListView listView = binding.list;
        listView.setEmptyView(binding.empty);
        homeViewModel.getList().observe(getViewLifecycleOwner(), receipts -> {
            ArrayAdapter<Receipt> adapter = new HomeAdapter(getContext(), receipts);
            listView.setAdapter(adapter);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Загружаем данные из базы
        homeViewModel.loadData();
    }
}