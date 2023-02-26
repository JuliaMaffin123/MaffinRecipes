package com.maffin.recipes.ui.home;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.R;
import com.maffin.recipes.ui.detail.DetailFragment;
import com.maffin.recipes.databinding.FragmentHomeBinding;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;
import com.maffin.recipes.ui.search.SearchFragment;

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
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Разрешаем показ заголовка
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
//        // Явно задам кнопку меню и заголовок
//        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
//        toolbar.setTitle(R.string.menu_home);

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
                startDetailFragment(position, holder.getId());
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
        // Принудительно обновляем тулбар
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
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
     * Переключаемся на фрагмент с деталями рецепта.
     * @param position  позиция в списке
     * @param id        ID рецепта
     */
    private void startDetailFragment(int position, long id) {
        Log.d(TAG, "position: " + position + " id: " + id);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        DetailFragment detailFragment = DetailFragment.newInstance(id);
        ft.replace(R.id.nav_host_fragment_content_main, detailFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}