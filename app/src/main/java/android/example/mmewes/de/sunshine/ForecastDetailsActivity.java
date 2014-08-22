package android.example.mmewes.de.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ForecastDetailsActivity extends ActionBarActivity {

    android.support.v7.widget.ShareActionProvider shareProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_details);
        if (savedInstanceState == null) {
            String date = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            final DetailsFragment detailsFragment = new DetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.WEATHER_DATE, date);
            detailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, detailsFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forecast_details, menu);
        MenuItem share = menu.findItem(R.id.action_share);
        this.shareProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(share);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
