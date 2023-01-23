package com.maffin.recipes.ui.adapter;

import android.content.Context;
import com.maffin.recipes.db.entity.Favorite;

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

}
