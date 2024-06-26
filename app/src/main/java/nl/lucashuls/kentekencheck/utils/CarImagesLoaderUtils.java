package nl.lucashuls.kentekencheck.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.jetbrains.annotations.Nullable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import nl.lucashuls.kentekencheck.R;

public class CarImagesLoaderUtils {
    private static final String API_URL = "https://www.carimagery.com/api.asmx/GetImageUrl?searchTerm=";

    // Load the logo of a car brand by its name
    public static void loadLogo(Context context, String merk, ImageView imageView) {
        String logoUrl = getLogoUrl(merk);
        Glide.with(context)
                .load(logoUrl)
                .error(R.drawable.sharp_car_crash_24) // Placeholder image in case of an error
                .into(imageView);
    }

    // Get the URL of the logo of a car brand by its name
    public static String getLogoUrl(String merk) {
        return "URL" + merk.toLowerCase() + ".png";
    }


    // Load an image of a car by its brand, model and year
    public static void loadCarImage(Context context, String brand, String model, String year, ImageView imageView) {
        new LoadCarImageTask(context, imageView).execute(brand, model, year);
    }


    // Get the URL of an image of a car by its brand, model and year
    @Nullable
    private static String getCarImageUrl(String brand, String model, String year) throws IOException, XmlPullParserException {
        String encodedBrand = URLEncoder.encode(brand, StandardCharsets.UTF_8.toString());
        String encodedModel = URLEncoder.encode(model, StandardCharsets.UTF_8.toString());
        String encodedYear = URLEncoder.encode(year, StandardCharsets.UTF_8.toString());
        String searchUrl = API_URL + encodedBrand + "+" + encodedModel + "+" + encodedYear;
        Log.d("CarImageLoaderUtil", "Search URL: " + searchUrl);

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(searchUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            inputStream = connection.getInputStream();
            return parseImageUrlFromXml(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Parse the image URL from the XML response
    @Nullable
    private static String parseImageUrlFromXml(InputStream inputStream) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.TEXT) {
                return parser.getText();
            }
            eventType = parser.next();
        }
        return null;
    }

    // AsyncTask to load the image of a car
    private static class LoadCarImageTask extends AsyncTask<String, Void, String> {
        private final Context context;
        private final ImageView imageView;

        LoadCarImageTask(Context context, ImageView imageView) {
            this.context = context;
            this.imageView = imageView;
        }

        // Load the image of a car in the background
        @Override
        protected String doInBackground(String... params) {
            try {
                return getCarImageUrl(params[0], params[1], params[2]);
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Load the image of a car in the UI thread
        @Override
        protected void onPostExecute(String imageUrl) {
            if (imageUrl != null) {
                Glide.with(context)
                        .load(imageUrl)
                        .apply(new RequestOptions().error(R.drawable.sharp_car_crash_24)) // Placeholder image in case of an error
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.sharp_car_crash_24);
            }
        }
    }
}
