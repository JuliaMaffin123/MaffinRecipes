package com.maffin.recipes.ui.detail.components;

import android.content.Context;
import android.view.View;

import com.maffin.recipes.R;
import com.maffin.recipes.network.Component;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;

import java.util.List;

public class ComponentsAdapter extends AbstractListAdapter {

    /**
     * Конструктор.
     *
     * @param context      интерфейс к глобальной информации о среде приложений.
     * @param list         список с данными для адаптера
     */
    public ComponentsAdapter(Context context, List<Component> list) {
        super(context, R.layout.components_list_item, list);
    }

    @Override
    public void bindView(int position, View view) {
        Context context = getContext();
        // Получим ссылки на составные части шаблона
        ViewHolder holder = (ViewHolder) view.getTag();
        // Получим ссылку на ингредиент (это элемент массива в соответствующей позиции)
        final Component component = (Component) getData().get(position);
        // Наполним строку данными
        holder.setId(component.getId());
        // Наименование ингредиента
        if (holder.getName() != null) {
            holder.getName().setText(component.getName());
        }
        // Количество
        if (holder.getDescription1() != null) {
            holder.getDescription1().setText(component.getCount());
        }
        // Ингредиент добавлен в корзину покупок
        if (holder.getAction() != null) {
            // ToDo:
        }
    }

}

