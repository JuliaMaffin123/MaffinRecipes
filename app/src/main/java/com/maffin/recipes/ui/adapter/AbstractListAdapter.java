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
    protected final LayoutInflater mInflater;
    /** интерфейс к глобальной информации о среде приложений. */
    protected Context mContext;
    /** Список с данными для адаптера. */
    protected List mData;
    /** ID ресурса с шаблоном элемента списка. */
    protected int mResource;
    /** ID ресурса с шаблоном группы элементов. */
    protected int mGroupResource;

    /**
     * Конструктор.
     *
     * @param context   интерфейс к глобальной информации о среде приложений.
     * @param group     ID макета группы элементов
     * @param resource  ID макета элемента списка
     * @param list      список с данными для адаптера
     */
    public AbstractListAdapter(final Context context, int group, int resource, final List list) {
        super(context, resource, R.id.name, list);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = list;
        mGroupResource = group;
        mResource = resource;
    }

    /**
     * Конструктор.
     *
     * @param context   интерфейс к глобальной информации о среде приложений.
     * @param resource  ID макета элемента списка
     * @param list      список с данными для адаптера
     */
    public AbstractListAdapter(final Context context, int resource, final List list) {
        super(context, resource, R.id.name, list);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = list;
        mResource = resource;
        mGroupResource = resource;
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
            convertView = newView(mContext, parent, position);
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
     * @param context    интерфейс к глобальной информации о среде приложений
     * @param parent     ссылка на контейнер, в котором создается элемент списка, т.е. сам список
     * @param position   позиция в списке
     * @return созданный объект (элемент списка)
     */
    public View newView(final Context context, final ViewGroup parent, final int position) {
        // Создаем объект для хранения ссылок на составные части элемента списка
        final ViewHolder holder = new ViewHolder();
        // Создаем представление элемента списка по разметке
        final View view = mInflater.inflate(getResource(position), parent, false);
        // Получаем ссылки на составные части элемента списка и сохранеям их в холдере
        holder.mName = view.findViewById(R.id.name);
        holder.mDescription1 = view.findViewById(R.id.description1);
        holder.mDescription2 = view.findViewById(R.id.description2);
        holder.mThumbnail = view.findViewById(R.id.thumbnail);
        holder.mAction = view.findViewById(R.id.action);

        // Навешиваем обработчик нажатия кнопки действия
        if (holder.mAction != null) {
            holder.mAction.setOnClickListener(v -> onActionClick(v));
        }

        // Сохраняем ссылку на холдер в тэге представления
        view.setTag(holder);
        return view;
    }

    /**
     * Возвращает ID шаблона в ресурсном файле.
     * Если шаблон зависит от данных, например, рецепт как группа ингредиентов должен быть другого
     * вида, тогда этот метод надо переопределить. Иначе для всех элементов будет использоваться
     * mResource.
     *
     * @param position  позиция в списке
     * @return
     */
    public int getResource(int position) {
        return mResource;
    }

    /**
     * Абстрактный метод. Вызывается при нажатии на кнопку действия из списка.
     * @param v ссылка на View кнопки
     */
    public void onActionClick(View v) {
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
        /** Поле вывода дополнительного описания 1. */
        private TextView 	mDescription1;
        /** Поле вывода дополнительного описания 2. */
        private TextView 	mDescription2;
        /** Миниатюра с картинкой рецепта. */
        private ImageView   mThumbnail;
        /** Иконка дополнительного действия. */
        private ImageView   mAction;

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
}
