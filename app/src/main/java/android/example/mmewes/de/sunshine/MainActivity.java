package android.example.mmewes.de.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements Callback {

    private boolean twoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            if (findViewById(R.id.weather_detail_container) == null) {
                // phone layout
                this.twoPaneLayout = false;
            } else {
                this.twoPaneLayout = true;
                // tablet two pane layout --> add details fragment
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container, new DetailsFragment())
                        .commit();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public static final String WEATHER_DATE = "WEATHER_DATE";

    @Override
    public void onItemSelected(String date) {
        if (this.twoPaneLayout){
            final DetailsFragment detailsFragment = new DetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString(WEATHER_DATE, date);
            detailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, detailsFragment)
                    .commit();
        } else {
            startActivity(new Intent(this,ForecastDetailsActivity.class ).putExtra(Intent.EXTRA_TEXT, date));
        }

    }
}
