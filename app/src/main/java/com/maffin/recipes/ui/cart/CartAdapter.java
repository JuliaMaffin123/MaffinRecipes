package com.maffin.recipes.ui.cart;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.network.ImageManager;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;

import java.util.List;

public class CartAdapter extends AbstractListAdapter {
    /** TAG для логирования. */
    private static final String TAG = "CartAdapter";
    /** Шаблон URL-а для загрузки изображений. */
    private static final String URL_TEMPLATE = Config.BASE_URL + "/images/receipt-%d-thumbnail.png";

    /**
     * Конструктор.
     *
     * @param context      интерфейс к глобальной информации о среде приложений.
     * @param list         список рецептов и ингредиентов
     */
    public CartAdapter(Context context, List<CartFragment.Model> list) {
        super(context, R.layout.cart_receipt_list_item, R.layout.cart_list_item, list);
    }

    @Override
    public void bindView(int position, View view) {
        Context context = getContext();
        // Получим ссылки на составные части шаблона
        ViewHolder holder = (ViewHolder) view.getTag();
        // Получим ссылку на запись (это элемент массива в соответствующей позиции)
        final CartFragment.Model model = (CartFragment.Model) getData().get(position);
        // Наполним строку данными
        holder.setId(model.itemId);
        // Наименование
        TextView name = holder.getName();
        if (name != null) {
            name.setText(model.name);
            if (model.check) {
                // Помеченные записи считам купленными и зачеркиваем
                name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                // Флаг зачеркнтого текста можно убрать
                name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
        // Количество
        TextView desc = holder.getDescription1();
        if (desc != null) {
            desc.setText(model.desc);
        }
        // Миниатюра
        ImageView thumb = holder.getThumbnail();
        if (thumb != null) {
            if (model.itemId == -1) {
                // Это группирующая строка с названием рецепта, загружаем картинку
                if (model.id == -1) {
                    thumb.setImageResource(R.drawable.ic_cook_thumb);
                } else {
                    String url = String.format(URL_TEMPLATE, model.id);
                    ImageManager.fetchImage(context, url, thumb, R.drawable.ic_cook_thumb);
                }
            } else {
                // Это элемент списка, рисуем имитацию чекбокса
                if (model.check) {
                    thumb.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
                } else {
                    thumb.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                }
            }
        }
        // Отключаем нажатие в группах
        if (model.itemId == -1) {
            view.setEnabled(false);
            view.setClickable(false);
        }
    }

    @Override
    public int getResource(int position) {
        // Получим ссылку на запись (это элемент массива в соответствующей позиции)
        final CartFragment.Model model = (CartFragment.Model) getData().get(position);
        // Если ID элемента списка == -1, значит это строка для группирующего названия рецепта
        return (model.itemId == -1 ? mGroupResource : mResource);
    }
}
