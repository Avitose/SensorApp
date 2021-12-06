package com.avito.sensorapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SensorActivity extends AppCompatActivity {
    public static final String SENSOR_DETAILS_TAG = "details";
    public static final String SUBTITLE_KEY = "sensorCount";

    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private SensorAdapter adapter;
    private RecyclerView recyclerView;
    private boolean subtitleVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(SUBTITLE_KEY);
        }

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        if (adapter == null) {
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }

        for (Sensor sensor : sensorList) {
            Log.d("SensorApp", "Name: " + sensor.getName() + "   " + "Vendor: " + sensor.getVendor() + "   " + "Max range: " + sensor.getMaximumRange());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SUBTITLE_KEY, subtitleVisible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensor_menu, menu);
        MenuItem subtitleItem = menu.findItem(R.id.menu_sensor_count);
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        subtitleVisible = ! subtitleVisible;
        invalidateOptionsMenu();
        String countText = getString(R.string.sensors_count, sensorList.size());

        if (! subtitleVisible) {
            countText = null;
        }
        getSupportActionBar().setSubtitle(countText);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (subtitleVisible) {
            String countText = getString(R.string.sensors_count, sensorList.size());
            getSupportActionBar().setSubtitle(countText);
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        private final TextView sensorNameTextView;


        public Holder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.sensor_list_item, parent, false));

            sensorNameTextView = itemView.findViewById(R.id.sensorNameText);
        }

        public void bind(Sensor sensor) {
            sensorNameTextView.setText(sensor.getName());

            View itemContainer = itemView.findViewById(R.id.list_item_sensor);

            int sensorType = sensor.getType();
            if (sensorType == Sensor.TYPE_PROXIMITY || sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                itemContainer.setBackgroundColor(Color.YELLOW);
                itemContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                    intent.putExtra(SensorDetailsActivity.SENSOR_DETAILS_KEY, sensor.getType());
                    startActivity(intent);
                });
            }

        }
    }

    private class SensorAdapter extends RecyclerView.Adapter<Holder> {
        private final List<Sensor> sensorList;

        public SensorAdapter(List<Sensor> sensorList) {

            this.sensorList = sensorList;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflate = LayoutInflater.from(parent.getContext());
            return new Holder(inflate, parent);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Sensor sensor = sensorList.get(position);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return sensorList.size();
        }
    }
}