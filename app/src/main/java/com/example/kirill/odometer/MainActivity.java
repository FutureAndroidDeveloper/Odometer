package com.example.kirill.odometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {
    private OdometerService odometer;
    private boolean bundle = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) service;
            odometer = odometerBinder.getOdometer();
            bundle = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bundle = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayDistance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bundle) {
            unbindService(connection);
            bundle = false;
        }
    }

    public void displayDistance() {
        final TextView distanceView = (TextView) findViewById(R.id.distance);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;

                if (bundle && connection != null) {
                    distance = odometer.getDistance();
                }

                String distanceStr = String.format(Locale.getDefault(),
                        "%1$,.2f miles", distance);
                distanceView.setText(distanceStr);
                handler.postDelayed(this, 1000);
            }
        });
    }
}
