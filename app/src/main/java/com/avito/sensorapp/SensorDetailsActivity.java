package com.avito.sensorapp;

import static java.lang.Math.abs;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {
    public static final String SENSOR_DETAILS_KEY = "SENSOR_DETAILS";
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView sensorNameTextView;
    private TextView sensorValueTextView;
    private View detailsView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_details_activity);

        sensorNameTextView = findViewById(R.id.details_sensor_name);
        sensorValueTextView = findViewById(R.id.details_sensor_value);

        int sensorType = getIntent().getIntExtra(SENSOR_DETAILS_KEY, 0);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);

        if (sensor == null) {
            sensorNameTextView.setText(R.string.missing_sensor);
        }
        else {
            sensorNameTextView.setText(sensor.getName());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        float value = event.values[0];

        int g = Math.min(abs((int)(sensor.getMaximumRange()-value)), 220);
        sensorValueTextView.setTextColor(Color.rgb(255, g, 0));

        switch (type) {
            case Sensor.TYPE_PROXIMITY:
                sensorValueTextView.setText(String.valueOf(value));
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorValueTextView.setText(String.valueOf(value));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        System.out.println("onAccuracyChanged");
    }
}