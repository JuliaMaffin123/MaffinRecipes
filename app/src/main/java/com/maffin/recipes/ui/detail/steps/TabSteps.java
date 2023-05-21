package com.maffin.recipes.ui.detail.steps;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.databinding.TabStepsBinding;
import com.maffin.recipes.network.ImageManager;
import com.maffin.recipes.network.Step;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;
import com.maffin.recipes.ui.detail.DetailFragment;
import com.maffin.recipes.ui.draw.DrawUtils;

import java.util.List;

/**
 * Фрагмент для вкладки ПРИГОТОВЛЕНИЕ.
 */
public class TabSteps extends Fragment {
    /** TAG для логирования. */
    private static final String TAG = "TabSteps";
    /** Разметка фрагмента. */
    private TabStepsBinding binding;
    /** Модель данных фрагмента. */
    private StepsViewModel stepsViewModel;
    /** ID рецепта. */
    private long id;
    /** Родительский фрагмент. */
    private DetailFragment root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Получаем ID рецепта
        root = (DetailFragment) getParentFragment();
        id = root.getReceiptId();

        // Инициализируем разметку фрагмента
        binding = TabStepsBinding.inflate(inflater, container, false);

        // Инициализируем модель данных
        stepsViewModel = new ViewModelProvider(this).get(StepsViewModel.class);
        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в ListView
        final ListView listView = binding.list;
        listView.setEmptyView(binding.empty);
        stepsViewModel.getList().observe(getViewLifecycleOwner(), steps -> {
            // Инициализируем адаптер и список
            ArrayAdapter<Step> adapter = new TabSteps.LocalAdapter(getContext(), steps);
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
        // Загружаем данные из сети
        stepsViewModel.loadData(id);
    }

    /**
     * Адаптер для наполнения списка шагов приготовления.
     */
    private class LocalAdapter extends AbstractListAdapter {
        /** Шаблон URL-а для загрузки изображений. */
        private static final String URL_TEMPLATE = Config.BASE_URL + "/images/receipt-%d-step-%d.png";
        /** Шаблон описания. */
        private static final String DESC_TEMPLATE = "Шаг %d. %s";

        /**
         * Конструктор.
         *
         * @param context      интерфейс к глобальной информации о среде приложений.
         * @param list         список с данными для адаптера
         */
        public LocalAdapter(Context context, List<Step> list) {
            super(context, R.layout.steps_list_item, list);
        }

        @Override
        public void bindView(int position, View view) {
            Context context = getContext();
            // Получим ссылки на составные части шаблона
            ViewHolder holder = (ViewHolder) view.getTag();
            // Получим ссылку на шаг приготовления (это элемент массива в соответствующей позиции)
            final Step step = (Step) getData().get(position);
            // Наполним строку данными
            holder.setId(step.getId());
            // Описание шага
            if (holder.getName() != null) {
                String text = String.format(DESC_TEMPLATE, position + 1, step.getDescription());
                SpannableStringBuilder sb = DrawUtils.emboldenKeywords(text, new String[] {"Шаг " + (position + 1)});
                holder.getName().setText(sb);
            }
            // Изображение
            if (holder.getThumbnail() != null) {
                if (step.getPhoto() == 0) {
                    holder.getThumbnail().setVisibility(View.GONE);
                } else {
                    holder.getThumbnail().setVisibility(View.VISIBLE);
                    // Запускаем загрузку картинки
                    String url = String.format(URL_TEMPLATE, id, position + 1);
                    ImageManager.fetchImage(context, url, holder.getThumbnail(), R.drawable.ic_cooking_chef_opacity);
                }
            }
        }
    }
}
