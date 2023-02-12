package com.maffin.recipes.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.Config;
import com.maffin.recipes.ui.detail.DetailActivity;
import com.maffin.recipes.databinding.FragmentHomeBinding;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;

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
        // Навешиваем прослушку на нажатие по элементу списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
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
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Загружаем данные из сети
        homeViewModel.loadData();
    }

    /**
     * Запуск активности с деталями рецепта.
     * @param position  позиция в списке
     * @param id        ID рецепта
     */
    private void startDetailActivity(int position, long id) {
        Log.d(TAG, "position: " + position + " id: " + id);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(Config.RECEIPT_ID, id);
        startActivity(intent);
    }
}