package com.maffin.recipes.ui.draw;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.Locale;

/**
 * Вспомогательный класс для управления визуальными элементами.
 */
public class DrawUtils {

    private DrawUtils() {}

    /**
     * Умеет добавлять иконку вместо placeholder-а в TextView.
     * @param context   контекст
     * @param textView  ссылка на TextView
     * @param atText    placeholder, который надо заменить на иконку
     * @param imageId   ссылка на ресурс иконки
     * @param imgWidth  ширина иконки
     * @param imgHeight высота иконки
     */
    public static void spanImageIntoText(Context context, TextView textView, String atText, int imageId, int imgWidth, int imgHeight) {
        String text = textView.getText().toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        Drawable drawable = ContextCompat.getDrawable(context, imageId);
        drawable.mutate();
        drawable.setBounds(0, 0, imgWidth, imgHeight);
        int start = text.indexOf(atText);
        VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
        ssb.setSpan(imageSpan, start, start + atText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    /**
     * Меняет цвет иконок в меню.
     * @param context   контекст
     * @param item      элемент меню
     * @param color     цвет
     */
    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        if (item != null) {
            Drawable normalDrawable = item.getIcon();
            Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
            DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));
            item.setIcon(wrapDrawable);
        }
    }

    /**
     * Выделение ключевых слов жирным.
     * @param text              Исходный текст
     * @param searchKeywords    Список ключевых слов
     * @return
     */
    public static SpannableStringBuilder emboldenKeywords(final String text,
                                                           final String[] searchKeywords) {
        // searching in the lower case text to make sure we catch all cases
        final String loweredMasterText = text.toLowerCase();
        final SpannableStringBuilder span = new SpannableStringBuilder(text);

        // for each keyword
        for (final String keyword : searchKeywords) {
            // lower the keyword to catch both lower and upper case chars
            final String loweredKeyword = keyword.toLowerCase();

            // start at the beginning of the master text
            int offset = 0;
            int start;
            final int len = keyword.length(); // let's calculate this outside the 'while'

            while ((start = loweredMasterText.indexOf(loweredKeyword, offset)) >= 0) {
                // make it bold
                span.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                // move your offset pointer
                offset = start + len;
            }
        }

        // put it in your TextView and smoke it!
        return span;
    }
}
