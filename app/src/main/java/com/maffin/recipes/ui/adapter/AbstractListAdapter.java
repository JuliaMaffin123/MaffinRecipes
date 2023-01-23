package com.maffin.recipes.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.maffin.recipes.R;
import com.maffin.recipes.adapter.IGetItemPosition;
import com.maffin.recipes.db.entity.Favorite;
import com.maffin.recipes.ui.draw.VerticalImageSpan;

import java.util.List;

/**
 * Абстрактный адаптер для заполнения данными элемента списков.
 * Для переопределения вывода надо переопределить метод bindView().
 */
public abstract class AbstractListAdapter extends ArrayAdapter implements IGetItemPosition {
    /** Ссылка на объек, конвертирующий шаблон в отображаемые элементы. */
    private final LayoutInflater mInflater;
    /** интерфейс к глобальной информации о среде приложений. */
    private Context mContext;
    /** Список с данными для адаптера. */
    private List mData;

    /**
     * Конструктор.
     *
     * @param context интерфейс к глобальной информации о среде приложений.
     * @param list    список с данными для адаптера
     */
    public AbstractListAdapter(final Context context, final List list) {
        super(context, R.layout.list_item, R.id.receiptName, list);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = list;
    }

    /**
     * Возвращает контекст.
     * @return  контекст
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Возвращает позицию элемента по его id.
     *
     * @param id идентификатор записи
     * @return позиция элемента
     */
    @Override
    public int getItemPosition(long id) {
        return Long.valueOf(id).intValue();
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
    public final View newView(final Context context, final ViewGroup parent) {
        // Создаем объект для хранения ссылок на составные части элемента списка
        final ViewHolder holder = new ViewHolder();
        // Создаем представление элемента списка по разметке
        final View view = mInflater.inflate(R.layout.list_item, parent, false);
        // Получаем ссылки на составные части элемента списка и сохранеям их в холдере
        holder.mName = view.findViewById(R.id.receiptName);
        holder.mTime = view.findViewById(R.id.receiptTime);
        holder.mEnergy = view.findViewById(R.id.receiptEnergy);
        holder.mThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        holder.mDeleteFromFavorite = view.findViewById(R.id.deleteFromFavorite);
        // Сохраняем ссылку на холдер в тэге представления
        view.setTag(holder);
        return view;
    }

    protected void spanImageIntoText(TextView textView, String atText, int imageId, int imgWidth, int imgHeight) {
        String text = textView.getText().toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        Drawable drawable = ContextCompat.getDrawable(mContext, imageId);
        drawable.mutate();
        drawable.setBounds(0, 0, imgWidth, imgHeight);
        int start = text.indexOf(atText);
        VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
        ssb.setSpan(imageSpan, start, start + atText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    /**
     * Биндит в шаблон элемента списка данные из массива.
     *
     * @param position позиция в списке
     * @param view     Объект, соответствующий элементу списка
     */
    public void bindView(int position, View view) {
        Context context = getContext();
        // Получим ссылки на составные части шаблона
        ViewHolder holder = (ViewHolder) view.getTag();
        // Получим ссылку на рецепт (это элемент массива в соответствующей позиции)
        final Favorite receipt = (Favorite) getData().get(position);
        // Наполним строку данными
        holder.setId(receipt.receiptId);
        // Наименование рецепта
        holder.getName().setText(receipt.receiptName);
        // Время приготовления рецепта
        int receiptTime = receipt.receiptTime;
        if (receiptTime > 0) {
            holder.getTime().setVisibility(View.VISIBLE);
            holder.getTime().setText(context.getString(R.string.template_time, receiptTime));
            spanImageIntoText(holder.getTime(), "[time-icon]",
                    R.drawable.ic_baseline_access_time_24,
                    context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                    context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
        } else {
            holder.getTime().setVisibility(View.GONE);
        }
        // Калорийность рецепта
        int receiptKkal = receipt.receiptKkal;
        if (receiptKkal > 0) {
            holder.getEnergy().setVisibility(View.VISIBLE);
            holder.getEnergy().setText(context.getString(R.string.template_energy, receiptKkal));
            spanImageIntoText(holder.getEnergy(), "[energy-icon]",
                    R.drawable.ic_baseline_fastfood_24,
                    context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                    context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
        } else {
            holder.getEnergy().setVisibility(View.GONE);
        }
    }


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
