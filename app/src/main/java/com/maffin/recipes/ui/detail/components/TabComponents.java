package com.maffin.recipes.ui.detail.components;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.databinding.TabComponentsBinding;
import com.maffin.recipes.network.Component;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;
import com.maffin.recipes.ui.detail.DetailActivity;

/**
 * Фрагмент для вкладки ИНГРЕДИЕНТЫ.
 */
public class TabComponents extends Fragment {

    /** TAG для логирования. */
    private static final String TAG = "TabComponents";
    /** Разметка фрагмента. */
    private TabComponentsBinding binding;
    /** Модель данных фрагмента. */
    private ComponentsViewModel componentsViewModel;
    /** ID рецепта. */
    private long id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Получаем ID рецепта
        id = ((DetailActivity) getActivity()).getReceiptId();

        // Инициализируем разметку фрагмента
        binding = TabComponentsBinding.inflate(inflater, container, false);

        // Инициализируем модель данных
        componentsViewModel = new ViewModelProvider(this).get(ComponentsViewModel.class);
        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в ListView
        final ListView listView = binding.list;
        listView.setEmptyView(binding.empty);
        componentsViewModel.getList().observe(getViewLifecycleOwner(), components -> {
            ArrayAdapter<Component> adapter = new ComponentsAdapter(getContext(), components);
            listView.setAdapter(adapter);
        });
        // Навешиваем прослушку на нажатие по элементу списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Зная view - элемент списка, получим из него холдер с данными
                final AbstractListAdapter.ViewHolder holder = (AbstractListAdapter.ViewHolder) view.getTag();
                // Запускаем активность и передаем в нее ID рецепта
                switchInCart(position, holder.getId());
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
        componentsViewModel.loadData(id);
    }

    /**
     * Добавляет или удаляет ингредиент из списка покупок.
     * @param position
     * @param id
     */
    private void switchInCart(int position, long id) {
        Log.d(TAG, "Выбрана позиция: " + position + ", id: " + id);
    }
}
