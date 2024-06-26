package nl.lucashuls.kentekencheck.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RDWApiUtils {
    private static final String RDW_API_URL = "https://opendata.rdw.nl/resource/m9d7-ebf2.json?kenteken=";
    private static final String RDW_LATEST_KENTEKENS_URL = "https://opendata.rdw.nl/resource/m9d7-ebf2.json?$where=datum_eerste_tenaamstelling_in_nederland IS NOT NULL&$order=datum_eerste_tenaamstelling_in_nederland DESC&$limit=20";

    // Fetch data for a specific kenteken from the RDW API
    public static void fetchKentekenData(Context context, String kenteken, RDWApiCallback callback) {
        String url = RDW_API_URL + kenteken;
        makeApiRequest(context, url, callback);
    }

    // Fetch the latest kentekens data from the RDW API
    public static void fetchLatestKentekensData(Activity activity, RDWApiCallback callback) {
        String url = RDW_LATEST_KENTEKENS_URL;
        makeApiRequest(activity, url, callback);
    }

    // Make an API request to the RDW API with the given URL and callback
    private static void makeApiRequest(Context context, String url, RDWApiCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                String errorMessage = "Failed to fetch data from RDW API";
                runOnUiThread(context, () -> {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    callback.onFailure(errorMessage);
                });
            }

            // Parse the response and call the callback on the UI thread
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonData = response.body().string();
                        JSONArray jsonArray = new JSONArray(jsonData);
                        runOnUiThread(context, () -> callback.onSuccess(jsonArray));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        String errorMessage = "Failed to parse RDW API response";
                        runOnUiThread(context, () -> {
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            callback.onFailure(errorMessage);
                        });
                    }
                } else {
                    String errorMessage = "Failed to fetch data from RDW API";
                    runOnUiThread(context, () -> {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                        callback.onFailure(errorMessage);
                    });
                }
            }
        });
    }

    private static void runOnUiThread(Context context, Runnable runnable) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(runnable);
        } else {
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }

    public interface RDWApiCallback {
        void onSuccess(JSONArray data);

        void onFailure(String errorMessage);
    }
}
