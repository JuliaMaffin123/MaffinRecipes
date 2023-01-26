package com.maffin.recipes.ui.home;

import android.content.Context;
import android.view.View;

import com.maffin.recipes.R;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;

import java.util.List;

public class HomeAdapter extends AbstractListAdapter {

    /**
     * Конструктор.
     *
     * @param context      интерфейс к глобальной информации о среде приложений.
     * @param list         список с данными для адаптера
     */
    public HomeAdapter(Context context, List<Receipt> list) {
        super(context, R.layout.home_list_item, list);
    }

    @Override
    public void bindView(int position, View view) {
        Context context = getContext();
        // Получим ссылки на составные части шаблона
        ViewHolder holder = (ViewHolder) view.getTag();
        // Получим ссылку на рецепт (это элемент массива в соответствующей позиции)
        final Receipt receipt = (Receipt) getData().get(position);
        // Наполним строку данными
        holder.setId(receipt.getId());
        // Наименование рецепта
        if (holder.getName() != null) {
            holder.getName().setText(receipt.getName());
        }
        // Время приготовления рецепта
        if (holder.getTime() != null) {
            int receiptTime = receipt.getTime();
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
            int receiptKkal = receipt.getEnergy();
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
    }
}
