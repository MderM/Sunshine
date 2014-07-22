package android.example.mmewes.de.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.widget.AdapterView.OnItemClickListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayList<String> data;
    private ArrayAdapter<String> adapter;

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        this.setHasOptionsMenu(true);
        this.data = new ArrayList<String>();
        this.adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textView, data);
        ListView listView = (ListView) rootView.findViewById(R.id.listView_forecast);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity().getApplicationContext(), adapter.getItem(position), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(),ForecastDetailsActivity.class ).putExtra(Intent.EXTRA_TEXT, adapter.getItem(position)));
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh){
            updateWeather();
            return true;
        }
        if (item.getItemId() == R.id.action_map) {
            this.showMap ();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMap() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locationPreference = prefs.getString(getString(R.string.location_settings_key), getString(R.string.default_location_settings_value));
        int zoomLevel = 15;
        Uri geo = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", locationPreference)
                .appendQueryParameter("zoom", "" + zoomLevel)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(geo);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.updateWeather();
    }

    private void updateWeather() {
        this.data.clear();
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            String locationPreference = preferences.getString(getString(R.string.location_settings_key), getString(R.string.default_location_settings_value));
            String def = getString(R.string.default_unit_settings_value);
            String unit = preferences.getString(getString(R.string.unit_settings_key), def);
            this.data.addAll(new ForecastTask(this.adapter, unit.equals(def)).execute(locationPreference).get());
        } catch (InterruptedException e) {
            Log.e("forecast fragment", "interrupted", e);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
