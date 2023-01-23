package com.maffin.recipes.ui.favorite;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.maffin.recipes.db.entity.Favorite;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;

import java.util.List;

public class FavoriteAdapter extends AbstractListAdapter {

    /**
     * Конструктор.
     *
     * @param context      интерфейс к глобальной информации о среде приложений.
     * @param list         список с данными для адаптера
     */
    public FavoriteAdapter(Context context, List<Favorite> list) {
        super(context, list);
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
}
