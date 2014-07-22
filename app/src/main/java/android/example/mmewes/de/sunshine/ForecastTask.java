package android.example.mmewes.de.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Chrissi&Maik on 20.07.2014.
 */
public class ForecastTask extends AsyncTask<String, Void, ArrayList<String>> {

    private  static final String LOG_TAG = ForecastTask.class.getSimpleName ();
    private final ArrayAdapter<String> adapter;
    private final boolean metrical;

    public ForecastTask(ArrayAdapter<String> adapter, boolean metrical) {
        this.adapter = adapter;
        this.metrical = metrical;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {

        // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

// Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            Uri uri = Uri.parse("http://api.openweathermap.org/data/2.5/forecast/daily").buildUpon()
                    .appendQueryParameter("q", params[0])
                    .appendQueryParameter("mode", "json")
                    .appendQueryParameter("units", "metric")
                    .appendQueryParameter("cnt", "7")
                    .build();
            Log.v(LOG_TAG, uri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) new URL (uri.toString()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                forecastJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                forecastJsonStr = null;
            }
            forecastJsonStr = buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            forecastJsonStr = null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        Log.v(LOG_TAG, forecastJsonStr);
        ArrayList<String> res = new ArrayList<String>(7);
        DateFormat df = new SimpleDateFormat("dd.MM.");
        try {
            JSONObject jsonObject = new JSONObject(forecastJsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject day = jsonArray.getJSONObject(i);
                JSONObject tmp = day.getJSONObject("temp");
                Date date = new Date(day.getLong("dt")*1000);
                double min = this.convertIfNeeded(tmp.getDouble("min")),
                       max = this.convertIfNeeded(tmp.getDouble("max"));
                JSONArray weather = day.getJSONArray("weather");
                JSONObject weatherObj = weather.getJSONObject(0);
                String condition = weatherObj.get("main") + " - " + weatherObj.get("description");
                String result = df.format(date) + " - " + min + "/" + max +
                        " -> " + condition;
                res.add(result);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "määp", e);
        }

        return res;
    }

    private double convertIfNeeded(double temp) {
        if (this.metrical)return temp;
        // convert to fahrenheit
        return ((temp*9)/5) + 32;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        for (String string : strings) {
            this.adapter.add(string);
        }
    }
}
