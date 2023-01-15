package com.maffin.recipes.ui.favorite;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.maffin.recipes.R;
import com.maffin.recipes.adapter.IGetItemPosition;
import com.maffin.recipes.databinding.FragmentFavoriteBinding;
import com.maffin.recipes.db.entity.Favorite;

import java.time.Duration;
import java.util.List;

/**
 * Фрагмент для отображения избранных рецептов.
 *
 * См. статья про обсерверы и LiveData: https://habr.com/ru/post/577482/
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
        // Навешиваем прослушку на изменение данных в модели данных
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

    private class FavoriteAdapter extends ArrayAdapter<Favorite> implements IGetItemPosition {

        /** Ссылка на объек, конвертирующий шаблон в отображаемые элементы. */
        private final LayoutInflater 	mInflater;
        /** интерфейс к глобальной информации о среде приложений. */
        private Context 				mContext;
        /** Список с данными для адаптера. */
        private List<Favorite>          mData;

        /**
         * Конструктор.
         *
         * @param context
         *            интерфейс к глобальной информации о среде приложений.
         * @param list
         *            список с данными для адаптера
         */
        public FavoriteAdapter(final Context context, final List<Favorite> list) {
            super(context, R.layout.favorite_list_item, R.id.receiptName, list);
            mInflater = LayoutInflater.from(context);
            mContext = context;
            mData = list;
        }

        /**
         * Возвращает позицию элемента по его id.
         *
         * @param id
         *            идентификатор записи
         * @return позиция элемента
         */
        @Override
        public int getItemPosition(long id) {
            return Long.valueOf(id).intValue();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public View getView(final int position, final View view, final ViewGroup parent) {
            View convertView;
            if (view == null) {
                convertView = newView(mContext, parent);
            } else {
                convertView = view;
            }
            bindView(position, convertView);
            return convertView;
        }

        /**
         * Создает новый объект (элемент списка) для просмотра данных.
         *
         * @param context
         *            интерфейс к глобальной информации о среде приложений
         * @param parent
         *            ссылка на контейнер, в котором создается элемент списка, т.е. сам список
         * @return созданный объект (элемент списка)
         */
        public final View newView(final Context context, final ViewGroup parent) {
            final ViewHolder holder = new ViewHolder();
            final View view = mInflater.inflate(R.layout.favorite_list_item, parent, false);
            holder.mName = (TextView) view.findViewById(R.id.receiptName);
//            holder.mAnnotation = (TextView) view.findViewById(R.id.annotation);
//            holder.mThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
//            holder.mCartSection = view.findViewById(R.id.cartSection);
//            holder.mCartCount = (TextView) view.findViewById(R.id.cartCount);
            view.setTag(holder);
            return view;
        }

        /**
         * Биндит в шаблон элемента списка данные из курсора.
         *
         * @param position
         *            позиция в списке
         * @param view
         *            Объект, соответствующий элементу списка
         */
        public void bindView(final int position, final View view) {
            // Получим ссылки на составные части шаблона
            ViewHolder holder = (ViewHolder) view.getTag();
            // Получим ссылку на рецепт
            final Favorite receipt = mData.get(position);
            // Наполним строку данными
            holder.mName.setText(receipt.receiptName);
//            final String annotation = reciept.getAnnotation();
//            if (AdlRoutines.isEmpty(annotation)) {
//                holder.mAnnotation.setVisibility(View.GONE);
//            } else {
//                holder.mAnnotation.setVisibility(View.VISIBLE);
//                holder.mAnnotation.setText(Html.fromHtml(reciept.getAnnotation()));
//            }
//            holder.mId = new Integer(position).longValue();
//            String photo = reciept.getThumbnail();
//            if (AdlRoutines.isNotEmpty(photo)) {
//                final Drawable d = AhLogic.loadImage(AhCategoryList.this, photo);
//                if (d != null) {
//                    holder.mThumbnail.setVisibility(View.VISIBLE);
//                    holder.mThumbnail.setImageDrawable(d);
//                }
//            } else {
//                holder.mThumbnail.setVisibility(View.GONE);
//            }
//            String key = mRecieptList.get(position);
//            int cnt = AhLogic.getCart().getRecieptCount(key);
//            if (cnt == 0) {
//                holder.mCartCount.setVisibility(View.GONE);
//            } else {
//                holder.mCartCount.setVisibility(View.VISIBLE);
//                holder.mCartCount.setText(Integer.toString(cnt));
//            }
        }

        /**
         * Вспомогательный класс для хранения ссылок на элементы шаблона.
         */
        private class ViewHolder {
            /** ID записи. */
            private long 		mId;
            /** Поле вывода наименования рецепта. */
            private TextView 	mName;
//            /** Поле вывода краткого описания рецепта. */
//            private TextView 	mAnnotation;
//            /** Поле вывода краткого описания рецепта. */
//            private ImageView 	mThumbnail;
//            /** Секция добавления рецепта в корзину. */
//            private View		mCartSection;
//            /** Количество рецептов в корзине. */
//            private TextView	mCartCount;

            /**
             * Возвращает ID записи.
             *
             * @return ID записи
             */
            public long getId() {
                return mId;
            }
        }
    }
}