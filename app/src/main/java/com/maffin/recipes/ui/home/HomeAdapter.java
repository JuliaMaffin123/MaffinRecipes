package com.maffin.recipes.ui.home;

import android.content.Context;
import android.view.View;

import com.maffin.recipes.Config;
import com.maffin.recipes.R;
import com.maffin.recipes.network.ImageManager;
import com.maffin.recipes.network.Receipt;
import com.maffin.recipes.ui.adapter.AbstractListAdapter;
import com.maffin.recipes.ui.draw.DrawUtils;

import java.util.List;

public class HomeAdapter extends AbstractListAdapter {

    /** Шаблон URL-а для загрузки изображений. */
    private static final String URL_TEMPLATE = Config.BASE_URL + "/images/receipt-%d-image.png";

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
        if (holder.getDescription1() != null) {
            int receiptTime = receipt.getTime();
            if (receiptTime > 0) {
                holder.getDescription1().setVisibility(View.VISIBLE);
                holder.getDescription1().setText(context.getString(R.string.template_time, receiptTime));
                DrawUtils.spanImageIntoText(context, holder.getDescription1(), context.getString(R.string.holder_time),
                        R.drawable.ic_baseline_access_time_24,
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
            } else {
                holder.getDescription1().setVisibility(View.GONE);
            }
        }
        // Калорийность рецепта
        if (holder.getDescription2() != null) {
            int receiptKkal = receipt.getEnergy();
            if (receiptKkal > 0) {
                holder.getDescription2().setVisibility(View.VISIBLE);
                holder.getDescription2().setText(context.getString(R.string.template_energy, receiptKkal));
                DrawUtils.spanImageIntoText(context, holder.getDescription2(), context.getString(R.string.holder_energy),
                        R.drawable.ic_baseline_fastfood_24,
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item),
                        context.getResources().getDimensionPixelOffset(R.dimen.icon_for_list_item));
            } else {
                holder.getDescription2().setVisibility(View.GONE);
            }
        }
        // Запускаем загрузку картинки
        String url = String.format(URL_TEMPLATE, receipt.getId());
        ImageManager.fetchImage(context, url, holder.getThumbnail(), R.drawable.ic_cooking_chef_opacity);
    }
}
