package com.maffin.recipes.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Класс для работы с изображениями.
 * См.: https://habr.com/ru/post/78747/
 */
public class ImageManager {
    /** Тег для логирования. */
    private final static String TAG = "ImageManager";
    /** Кэш загруженных изображений. */
    private final static HashMap<String, Bitmap> BITMAP_HASH_MAP = new HashMap<>();
    /** Очередь загрузки изображений. */
    private final static HashMap<ImageView, String> IMAGE_QUEUE = new HashMap<>();


    /** Приватный конструктор для предотвращения создания инстанса. */
    private ImageManager () {}

    /**
     * Загрузка изображения.
     * @param context   контекст приложения
     * @param iUrl      адрес
     * @param iView     ImageView, получатель изображения
     * @param stub      ID ресурса, заглушка, на случай, если изображние не удалось загрузить
     *                  -1, если надо скрыть ImageView при отсутствии изображения
     */
    public static void fetchImage(final Context context, final String iUrl, final ImageView iView, final int stub) {
        // Если URL или ImageView не определены,
        if ( iUrl == null || iView == null )
            return;

        // Сохраним пару в очереди
        IMAGE_QUEUE.put(iView, iUrl);

        // Создаем поток загрузки изображения
        final Thread thread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                // Создадим обработчик, который заменит картинку в ImageView при завршении загрузки
                final Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        // Проверяем какой URL последним был заявлен на загрузку в ImageView
                        String currentUrl = IMAGE_QUEUE.get(iView);
                        if (!iUrl.equals(currentUrl)) {
                            Log.v(TAG, "ImageView переиспользован в другой загрузке: " + currentUrl);
                            return;
                        }
                        // Извлекаем из сообщения картинку
                        final Bitmap image = (Bitmap) message.obj;
                        // Вставляем картинку в ImageView и делаем его видимым
                        iView.setImageBitmap(image);
                        iView.setVisibility(View.VISIBLE);
                        // Убираем ImageView из очереди
                        IMAGE_QUEUE.remove(iView);
                    }
                };

                // Проверяем, может наша картинка уже а кэше?
                if (BITMAP_HASH_MAP.containsKey(iUrl)) {
                    Log.v(TAG, "Изображени найдено в кэше. URL: " + iUrl);
                    // Передаем изображение а handler для отрисовки в UI
                    final Message message = handler.obtainMessage(1, BITMAP_HASH_MAP.get(iUrl));
                    handler.sendMessage(message);
                } else {
                    // Проверяем, может наша картинка ране была сохранена в файловой системе телефона?
                    Bitmap image = loadBitmap(context, iUrl);
                    if (image != null) {
                        // Добавляем картинку в кэш
                        BITMAP_HASH_MAP.put(iUrl, image);
                        // Передаем изображение а handler для отрисовки в UI
                        final Message message = handler.obtainMessage(1, image);
                        handler.sendMessage(message);
                    } else {
                        // Картинка не известная, надо загружать из сети
                        image = downloadImage(iUrl);
                        if (image != null) {
                            Log.v(TAG, "Изображение загружено по URL: " + iUrl);
                            // Добавляем картинку в кэш
                            BITMAP_HASH_MAP.put(iUrl, image);
                            // Передаем изображение а handler для отрисовки в UI
                            final Message message = handler.obtainMessage(1, image);
                            handler.sendMessage(message);
                            // Сохраняем картинку в файловой системе для переиспользования в будущем
                            saveBitmap(context, image, iUrl);
                        }
                    }
                }
                Looper.loop();
            }
        };
        // Пока картинка грузится в отдельном потоке, подложим вместо нее заглушку или скроем
        if (stub == -1) {
            iView.setVisibility(View.GONE);
        } else {
            iView.setVisibility(View.VISIBLE);
            iView.setImageResource(stub);
        }
        // Устанавливаем потоку загрузки низкий приоритет и запускаем его на выполнение
        thread.setPriority(3);
        thread.start();
    }

    /**
     * Загрузка изображения с сервера.
     * @param iUrl  адрес картинки
     * @return  картинка
     */
    public static Bitmap downloadImage(String iUrl) {
        Bitmap bitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream buf_stream = null;
        try {
            Log.v(TAG, "Starting loading image by URL: " + iUrl);
            conn = (HttpURLConnection) new URL(iUrl).openConnection();
            conn.setDoInput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            buf_stream = new BufferedInputStream(conn.getInputStream(), 8192);
            bitmap = BitmapFactory.decodeStream(buf_stream);
            buf_stream.close();
            conn.disconnect();
            buf_stream = null;
            conn = null;
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Url parsing was failed: " + iUrl);
        } catch (IOException ex) {
            Log.d(TAG, iUrl + " does not exists");
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "Out of memory!!!");
            return null;
        } finally {
            if ( buf_stream != null )
                try { buf_stream.close(); } catch (IOException ex) {}
            if ( conn != null )
                conn.disconnect();
        }
        return bitmap;
    }

    public static void saveBitmap(final Context context, Bitmap bitmap, String url) {
        String[] parts = url.split("/");
        String fileName = parts[parts.length - 1];
        try {
            //create a file to write bitmap data
            File file = new File(context.getExternalCacheDir(), fileName);
            file.createNewFile();

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            Log.v(TAG, "Файл сохранен на телефон: " + fileName);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка сохранения файла на телефон: " + fileName, e);
        }
    }

    public static Bitmap loadBitmap(final Context context, String url) {
        String[] parts = url.split("/");
        String fileName = parts[parts.length - 1];
        try {
            File file = new File(context.getExternalCacheDir(), fileName);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Log.v(TAG, "Файл загружен с телефона: " + fileName);
                return bitmap;
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка загрузки файла с телефона: " + fileName, e);
        }
        return null;
    }
}
