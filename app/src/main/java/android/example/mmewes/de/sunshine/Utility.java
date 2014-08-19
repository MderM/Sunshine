package android.example.mmewes.de.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.example.mmewes.de.sunshine.data.WeatherContract;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

public class Utility {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.location_settings_key),
                context.getString(R.string.default_location_settings_value));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.unit_settings_key);
        String defval = context.getString(R.string.default_unit_settings_value);
        return prefs.getString(key,defval).equals("1");
    }

    static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }

    static String formatDate(String dateString) {
        Date date = WeatherContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }
}