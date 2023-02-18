package com.maffin.recipes.ui.draw;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

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


}
