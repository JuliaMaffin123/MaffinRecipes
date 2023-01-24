package com.maffin.recipes.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Класс, который умеет выполнять запросы по адресу URL.
 *
 * См. пример тут: https://stackoverflow.com/questions/62438770/android-asynctask-deprecated-need-substitute-examples/62438889#62438889
 * См. про Looper-ы и Handler-ы тут: http://javaway.info/mnogopotochnost-v-android-looper-handler-handlerthread-chast-1/
 * См. про postDelay: https://stackoverflow.com/questions/42379301/how-to-use-postdelayed-correctly-in-android-studio
 */
public abstract class AsyncRequest {
    /**
     * GET запрос по адресу.
     * @param url   адрес
     */
    public void execute(String url) {
        new Thread(() -> {
            final JSONObject[] mainData = {new JSONObject()};
            // background work here ...
            String data = "";
            String error_data = "";

            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                int status = httpURLConnection.getResponseCode();
                Log.d("GET RX", " status=> " + status);

                try {
                    InputStream in = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(in);

                    int inputStreamData = inputStreamReader.read();
                    while (inputStreamData != -1) {
                        char current = (char) inputStreamData;
                        inputStreamData = inputStreamReader.read();
                        data += current;
                    }
                    Log.d("GET RX =>", " " + data);
                    mainData[0] = new JSONObject(data);
                } catch (Exception exx) {
                    InputStream error = httpURLConnection.getErrorStream();
                    InputStreamReader inputStreamReader2 = new InputStreamReader(error);

                    int inputStreamData2 = inputStreamReader2.read();
                    while (inputStreamData2 != -1) {
                        char current = (char) inputStreamData2;
                        inputStreamData2 = inputStreamReader2.read();
                        error_data += current;
                    }
                    Log.e("TX", "error => " + error_data);
                }
            } catch (Exception e) {
                Log.e("TX", " error => " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            // Передаем результат запроса
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Update UI here ...
                onPostExecute(mainData[0]);
            }, 2000);
        }).start();
    }

    /**
     * Пост-обработка результатов запроса
     * @param data  JSON-объект, вернувшийся в ответе
     */
    public abstract void onPostExecute(JSONObject data);
}
