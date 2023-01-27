package com.maffin.recipes.ui.favorite;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.db.entity.Favorite;
import com.maffin.recipes.network.ImageManager;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;

import java.util.List;

public class FavoriteAdapter extends AbstractListAdapter {
    /** Шаблон URL-а для загрузки изображений. */
    private static final String URL_TEMPLATE = Config.BASE_URL + "/images/receipt-%d-thumbnail.png";
    /**
     * Конструктор.
     *
     * @param context      интерфейс к глобальной информации о среде приложений.
     * @param list         список с данными для адаптера
     */
    public FavoriteAdapter(Context context, List<Favorite> list) {
        super(context, R.layout.favorite_list_item, list);
    }

    @Override
    public void onDeleteClick(View v) {
        View listItem = (View) v.getParent();
        ListView listView = (ListView) listItem.getParent();
        int position = listView.getPositionForView(listItem);
        ViewHolder holder = (ViewHolder) listItem.getTag();
        long id = holder.getId();
        Log.d("FavoriteAdapter", "Тапнули удаление на элементе: position=" + position + ", id=" + id);
    }

    @Override
    public void bindView(int position, View view) {
        Context context = getContext();
        // Получим ссылки на составные части шаблона
        ViewHolder holder = (ViewHolder) view.getTag();
        // Получим ссылку на рецепт (это элемент массива в соответствующей позиции)
        final Favorite receipt = (Favorite) getData().get(position);
        // Наполним строку данными
        holder.setId(receipt.receiptId);
        // Наименование рецепта
        if (holder.getName() != null) {
            holder.getName().setText(receipt.receiptName);
        }
        // Время приготовления рецепта
        if (holder.getTime() != null) {
            int receiptTime = receipt.receiptTime;
            if (receiptTime > 0) {
                holder.getTime().setVisibility(View.VISIBLE);
                holder.getTime().setText(context.getString(R.string.template_time, receiptTime));
                spanImageIntoText(holder.getTime(), context.getString(R.string.holder_time),
                        R.drawable.ic_baseline_access_time_24,
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
            } else {
                holder.getTime().setVisibility(View.GONE);
            }
        }
        // Калорийность рецепта
        if (holder.getEnergy() != null) {
            int receiptKkal = receipt.receiptKkal;
            if (receiptKkal > 0) {
                holder.getEnergy().setVisibility(View.VISIBLE);
                holder.getEnergy().setText(context.getString(R.string.template_energy, receiptKkal));
                spanImageIntoText(holder.getEnergy(), context.getString(R.string.holder_energy),
                        R.drawable.ic_baseline_fastfood_24,
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
            } else {
                holder.getEnergy().setVisibility(View.GONE);
            }
        }
        // Запускаем загрузку картинки
        String url = String.format(URL_TEMPLATE, receipt.receiptId);
        ImageManager.fetchImage(context, url, holder.getThumbnail(), R.drawable.ic_cook_thumb);
    }
}
