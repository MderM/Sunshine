package android.example.mmewes.de.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.example.mmewes.de.sunshine.data.WeatherContract;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FORECAST_LOADER = 0;
    private String mLocation;


    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ENTRY_ID = 0;
    public static final int COL_LOCATION_SETTING = 1;
    public static final int COL_DEGREES = 2;
    public static final int COL_WEATHER_ID = 3;
    public static final int COL_DATETEXT = 4;
    public static final int COL_WEATHER_DESC = 5;
    public static final int COL_WEATHER_MAX_TEMP = 6;
    public static final int COL_WEATHER_MIN_TEMP = 7;
    public static final int COL_HUMIDITY = 8;
    public static final int COL_PRESSURE = 9;
    public static final int COL_WINDSPEED = 10;


    private ShareActionProvider shareProvider;
    private String shareForecast;
    private String queryDate;
    private View rootView;

    public DetailsFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem share = menu.findItem(R.id.action_share);
        this.shareProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareForecast + "#SunshineApp");
        sendIntent.setType("text/plain");
        this.setShareIntent(sendIntent);
    }

    // Call to update the share intent
    void setShareIntent(Intent sendIntent) {
        if (this.shareProvider != null) {
            this.shareProvider.setShareIntent(sendIntent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_forecast_details, container, false);
        this.queryDate = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                mLocation, this.queryDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) return;
        // fill the view items
        shareForecast = data.getString(COL_LOCATION_SETTING) + " - ";
        String date = data.getString(COL_DATETEXT);
        ImageView iView = (ImageView) this.rootView.findViewById(R.id.details_icon_imageView);
        iView.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_ID)));
        TextView view = (TextView) this.rootView.findViewById(R.id.details_date1_textView);
        view.setText(Utility.getFriendlyDayString(getActivity(), Utility.prepareDate(date)));
        view = (TextView) this.rootView.findViewById(R.id.details_date2_textView);
        view.setText(Utility.getFormattedMonthDay(getActivity(), Utility.prepareDate(date)));
        shareForecast += Utility.formatDate(date) + " - ";
        view = (TextView) this.rootView.findViewById(R.id.details_wind_textView);
        final boolean metric = Utility.isMetric(getActivity());
        String wind = Utility.getFormattedWind(getActivity(), data.getFloat(COL_WINDSPEED), data.getFloat(COL_DEGREES));
        view.setText(wind);
        view = (TextView) this.rootView.findViewById(R.id.details_description_textView);
        view.setText(data.getString(COL_WEATHER_DESC));
        shareForecast += view.getText() + " - ";
        view = (TextView) this.rootView.findViewById(R.id.details_humidity_textView);
        view.setText(data.getString(COL_HUMIDITY) + " %");
        view = (TextView) this.rootView.findViewById(R.id.details_max_temp_textView);
        view.setText(Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), metric));
        shareForecast += view.getText() + "/";
        view = (TextView) this.rootView.findViewById(R.id.details_min_temp_textView);
        view.setText(Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), metric));
        shareForecast += view.getText();
        view = (TextView) this.rootView.findViewById(R.id.details_pressure_textView);
        view.setText(data.getString(COL_PRESSURE));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }
}
