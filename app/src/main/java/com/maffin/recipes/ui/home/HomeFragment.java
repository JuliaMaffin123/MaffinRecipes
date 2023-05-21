package com.maffin.recipes.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.databinding.FragmentHomeBinding;
import com.maffin.recipes.network.ImageManager;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.ui.draw.DrawUtils;

import java.util.List;

/**
 * Фрагмент для отображения списка рецептов на базе RecycledView.
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

        // Инициализируем модель данных
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // Навешиваем прослушку на изменение данных в модели данных. Когда модель получит данные из БД,
        // прослушка через адаптер загрузит список в RecycledView
        final RecyclerView recyclerView = binding.recyclerView;
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());

        homeViewModel.getList().observe(getViewLifecycleOwner(), receipts -> {
            CustomAdapter adapter = new CustomAdapter(getContext(), receipts);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
            binding.progressBar.setVisibility(View.GONE);
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
     * Срабатывает при создании меню.
     * @param menu  мею
     * @return
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    /**
     * Переключаемся на фрагмент с деталями рецепта.
     * @param view      элемент, который породил событие
     * @param id        ID рецепта
     */
    private void startDetailFragment(View view, long id) {
        Log.d(TAG, "Clicked list item with id: " + id);
        // Передаем параметр с ID рецепта
        Bundle bundle = new Bundle();
        bundle.putLong(Config.RECEIPT_ID, id);
        // Переключаем фрагмент
        Navigation.findNavController(view).navigate(R.id.nav_detail, bundle);
    }

    /**
     * Адаптер, который содержит, обрабатывает и связывает данные со списком.
     */
    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        /** Шаблон URL-а для загрузки изображений. */
        private static final String URL_TEMPLATE = Config.BASE_URL + "/images/receipt-%d-image.png";

        /** Интерфейс к глобальной информации о среде приложений. */
        protected Context context;
        /** Список рецептов. */
        private List<Receipt> receipts;

        /**
         * Контейнер для всех элементов, входящих в элемент списка.
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            /** ID рецепта. */
            private long 		mId;
            /** Поле вывода наименования рецепта. */
            private TextView mName;
            /** Поле вывода дополнительного описания 1. */
            private TextView 	mDescription1;
            /** Поле вывода дополнительного описания 2. */
            private TextView 	mDescription2;
            /** Миниатюра с картинкой рецепта. */
            private ImageView mThumbnail;
            /** Иконка дополнительного действия. */
            private ImageView   mAction;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // Получаем ссылки на составные части элемента списка и сохранеям их в холдере
                mName = itemView.findViewById(R.id.name);
                mDescription1 = itemView.findViewById(R.id.description1);
                mDescription2 = itemView.findViewById(R.id.description2);
                mThumbnail = itemView.findViewById(R.id.thumbnail);
                mAction = itemView.findViewById(R.id.action);
            }

            /**
             * Возвращает ID рецепта.
             *
             * @return ID рецепта
             */
            public long getId() {
                return mId;
            }

            /**
             * Устанавливает ID рецепта.
             *
             * @param id ID рецепта
             */
            public void setId(long id) {
                mId = id;
            }

            public TextView getName() {
                return mName;
            }

            public void setName(TextView name) {
                mName = name;
            }

            public TextView getDescription1() {
                return mDescription1;
            }

            public TextView getDescription2() {
                return mDescription2;
            }

            public ImageView getThumbnail() {
                return mThumbnail;
            }

            public ImageView getAction() {
                return mAction;
            }

        }

        /**
         * Инициализирует набор данных в адаптере.
         *
         * @param receipts список рецептов
         */
        public CustomAdapter(Context context, List<Receipt> receipts) {
            this.context = context;
            this.receipts = receipts;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Создаем новый объект, для отрисовки элемента списка
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.home_list_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Заполняем элемент списка данными
            final Receipt receipt = receipts.get(position);
            // Наполним строку данными
            holder.setId(receipt.getId());
            // Наименование рецепта
            if (holder.getName() != null) {
                holder.getName().setText(receipt.getName());
            }
            // Время приготовления рецепта
            if (holder.getDescription1() != null) {
                int receiptTime = receipt.getTime();
                if (receiptTime > 0) {
                    holder.getDescription1().setVisibility(View.VISIBLE);
                    holder.getDescription1().setText(context.getString(R.string.template_time, receiptTime));
                    DrawUtils.spanImageIntoText(context, holder.getDescription1(), context.getString(R.string.holder_time),
                            R.drawable.ic_baseline_access_time_24,
                            context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                            context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
                } else {
                    holder.getDescription1().setVisibility(View.GONE);
                }
            }
            // Калорийность рецепта
            if (holder.getDescription2() != null) {
                int receiptKkal = receipt.getEnergy();
                if (receiptKkal > 0) {
                    holder.getDescription2().setVisibility(View.VISIBLE);
                    holder.getDescription2().setText(context.getString(R.string.template_energy, receiptKkal));
                    DrawUtils.spanImageIntoText(context, holder.getDescription2(), context.getString(R.string.holder_energy),
                            R.drawable.ic_outline_fastfood_24,
                            context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                            context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
                } else {
                    holder.getDescription2().setVisibility(View.GONE);
                }
            }
            // Запускаем загрузку картинки
            String url = String.format(URL_TEMPLATE, receipt.getId());
            ImageManager.fetchImage(context, url, holder.getThumbnail(), R.drawable.ic_cooking_chef_opacity);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "CLICKED: " + holder.getId());
                    startDetailFragment(view, holder.getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return receipts.size();
        }

    }
}
