package android.example.mmewes.de.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ForecastDetailsActivity extends ActionBarActivity {

    android.support.v7.widget.ShareActionProvider shareProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_details);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailsFragment())
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailsFragment extends Fragment {

        private ShareActionProvider shareProvider;
        private String foreCast;

        public DetailsFragment() {
            this.setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);
            MenuItem share = menu.findItem(R.id.action_share);
            this.shareProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(share);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            sendIntent.putExtra(Intent.EXTRA_TEXT, foreCast + "#SunshineApp");
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
            View rootView = inflater.inflate(R.layout.fragment_forecast_details, container, false);
            this.foreCast = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
            ((TextView)rootView.findViewById(R.id.textView_forcast_details_fullTxt)).setText(foreCast);
            return rootView;
        }
    }
}
