package com.maffin.recipes.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maffin.recipes.R;

import java.util.List;

/**
 * Абстрактный адаптер для заполнения данными элемента списков.
 * Для переопределения вывода надо переопределить метод bindView().
 * Предпологается, что шаблон состоит из следующих элементов:
 *  - thumbnail - ImageView, изображение рецепта
 *  - receiptName - TextView, наименование рецепта
 *  - receiptTime - TextView, время приготовления
 *  - receiptEnergy - TextView, каллорийность
 *  - deleteFromFavorite - ImageView, кнопка удаления из избранного (опционально)
 */
public abstract class AbstractListAdapter extends ArrayAdapter {
    /** Ссылка на объек, конвертирующий шаблон в отображаемые элементы. */
    private final LayoutInflater mInflater;
    /** интерфейс к глобальной информации о среде приложений. */
    private Context mContext;
    /** Список с данными для адаптера. */
    private List mData;
    /** ID ресурса с шаблоном элемента списка. */
    private int mResource;

    /**
     * Конструктор.
     *
     * @param context   интерфейс к глобальной информации о среде приложений.
     * @param resource  ID макета элемента списка
     * @param list      список с данными для адаптера
     */
    public AbstractListAdapter(final Context context, int resource, final List list) {
        super(context, resource, R.id.receiptName, list);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = list;
        mResource = resource;
    }

    /**
     * Возвращает контекст.
     * @return  контекст
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Создает элементы списка.
     * @param position  позиция в списке
     * @param view      представление элемента
     * @param parent    ссылка на родительский элемент
     * @return  представление элемента
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
     * Возвращает список с данными.
     * @return  список
     */
    public List getData() {
        return mData;
    }

    /**
     * Создает новый объект (элемент списка) для просмотра данных.
     *
     * @param context интерфейс к глобальной информации о среде приложений
     * @param parent  ссылка на контейнер, в котором создается элемент списка, т.е. сам список
     * @return созданный объект (элемент списка)
     */
    public View newView(final Context context, final ViewGroup parent) {
        // Создаем объект для хранения ссылок на составные части элемента списка
        final ViewHolder holder = new ViewHolder();
        // Создаем представление элемента списка по разметке
        final View view = mInflater.inflate(mResource, parent, false);
        // Получаем ссылки на составные части элемента списка и сохранеям их в холдере
        holder.mName = view.findViewById(R.id.receiptName);
        holder.mTime = view.findViewById(R.id.receiptTime);
        holder.mEnergy = view.findViewById(R.id.receiptEnergy);
        holder.mThumbnail = view.findViewById(R.id.thumbnail);
        holder.mDeleteFromFavorite = view.findViewById(R.id.deleteFromFavorite);

        // Навешиваем обработчик нажатия кнопки удаления
        if (holder.mDeleteFromFavorite != null) {
            holder.mDeleteFromFavorite.setOnClickListener(v -> onDeleteClick(v));
        }

        // Сохраняем ссылку на холдер в тэге представления
        view.setTag(holder);
        return view;
    }

    /**
     * Абстрактный метод. Вызывается при нажатии на кнопку удаления из списка.
     * @param v ссылка на View кнопки
     */
    public void onDeleteClick(View v) {
        // По умолчанию ничего не выполняется
    };

    /**
     * Биндит в шаблон элемента списка данные из массива.
     *
     * @param position позиция в списке
     * @param view     Объект, соответствующий элементу списка
     */
    public abstract void bindView(int position, View view);

    /**
     * Вспомогательный класс для хранения ссылок на элементы шаблона.
     */
    public class ViewHolder {
        /** ID рецепта. */
        private long 		mId;
        /** Поле вывода наименования рецепта. */
        private TextView 	mName;
        /** Поле вывода времени приготовления рецепта. */
        private TextView 	mTime;
        /** Поле вывода кКал рецепта. */
        private TextView 	mEnergy;
        /** Миниатюра с картинкой рецепта. */
        private ImageView   mThumbnail;
        /** Иконка удаления из избранного. */
        private ImageView   mDeleteFromFavorite;

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

        public TextView getTime() {
            return mTime;
        }

        public TextView getEnergy() {
            return mEnergy;
        }

        public ImageView getThumbnail() {
            return mThumbnail;
        }

        public ImageView getDeleteFromFavorite() {
            return mDeleteFromFavorite;
        }
    }
}
