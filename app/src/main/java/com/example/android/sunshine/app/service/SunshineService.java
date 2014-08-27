package com.example.android.sunshine.app.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.sunshine.app.FetchWeatherTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by mmewes on 27.08.2014.
 */
public class SunshineService extends IntentService {

    public SunshineService() {
        super("SunshineService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String location = intent.getStringExtra("location");
        try {
            new FetchWeatherTask(getApplicationContext()).execute(location).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String location = intent.getStringExtra("location");
            try {
                new FetchWeatherTask(context).execute(location).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
