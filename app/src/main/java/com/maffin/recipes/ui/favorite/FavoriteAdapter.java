package com.maffin.recipes.ui.favorite;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.maffin.recipes.App;
import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.db.AppDatabase;
import com.maffin.recipes.db.dao.FavoriteDao;
import com.maffin.recipes.db.entity.Favorite;
import com.maffin.recipes.network.ImageManager;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;
import com.maffin.recipes.ui.draw.DrawUtils;

import java.util.List;

public class FavoriteAdapter extends AbstractListAdapter {
    /** TAG для логирования. */
    private static final String TAG = "FavoriteAdapter";
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
    public void onActionClick(View v) {
        View listItem = (View) v.getParent();
        ListView listView = (ListView) listItem.getParent();
        int position = listView.getPositionForView(listItem);
        ViewHolder holder = (ViewHolder) listItem.getTag();
        long id = holder.getId();
        Log.d(TAG, "Тапнули удаление на элементе: position=" + position + ", id=" + id);
        // Удаляем в базе фаворитов
        AppDatabase db = App.getInstance().getDatabase();
        FavoriteDao dao = db.favoriteDao();
        Favorite favorite = dao.getById(id);
        dao.delete(favorite);
        // Перерисовываем адаптер
        remove(getItem(position));
        notifyDataSetChanged();
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
        if (holder.getDescription1() != null) {
            int receiptTime = receipt.receiptTime;
            if (receiptTime > 0) {
                holder.getDescription1().setVisibility(View.VISIBLE);
                holder.getDescription1().setText(context.getString(R.string.template_time, receiptTime));
                DrawUtils.spanImageIntoText(context, holder.getDescription1(),
                        context.getString(R.string.holder_time),
                        R.drawable.ic_baseline_access_time_24,
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
            } else {
                holder.getDescription1().setVisibility(View.GONE);
            }
        }
        // Калорийность рецепта
        if (holder.getDescription2() != null) {
            int receiptKkal = receipt.receiptKkal;
            if (receiptKkal > 0) {
                holder.getDescription2().setVisibility(View.VISIBLE);
                holder.getDescription2().setText(context.getString(R.string.template_energy, receiptKkal));
                DrawUtils.spanImageIntoText(context, holder.getDescription2(),
                        context.getString(R.string.holder_energy),
                        R.drawable.ic_outline_room_service_24,
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
            } else {
                holder.getDescription2().setVisibility(View.GONE);
            }
        }
        // Запускаем загрузку картинки
        String url = String.format(URL_TEMPLATE, receipt.receiptId);
        ImageManager.fetchImage(context, url, holder.getThumbnail(), R.drawable.ic_cook_thumb);
    }
}
