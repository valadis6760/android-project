package com.example.mdpproject.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mdpproject.MainActivity;
import com.example.mdpproject.R;
import com.example.mdpproject.databinding.ActivityMapsBinding;
import com.example.mdpproject.db.DBHelper;
import com.example.mdpproject.db.DailyInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DBHelper dbHelper;
    private GoogleMap googleMap;
    private ActivityMapsBinding binding;

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final ArrayList<DailyInfo> allArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Location History");

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dbHelper = new DBHelper(this);
        try {
            allArrayList.addAll(dbHelper.getAllRecords());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng last = null;
        if (allArrayList.size() > 0) {
            for (DailyInfo dailyInfo : allArrayList) {
                if (dailyInfo.getLatitude() != null && dailyInfo.getLongitude() != null) {
                    LatLng latlng = new LatLng(Double.parseDouble(dailyInfo.getLatitude()), Double.parseDouble(dailyInfo.getLongitude()));
                    String title = dateFormat.format(dailyInfo.getDate()) + " | Total steps: " + dailyInfo.getSteps();
                    this.googleMap.addMarker(new MarkerOptions().position(latlng).title(title));
                    last = latlng;
                    Log.d("TABLE", "onCreate: content " + dailyInfo.getLatitude() + " - " + dailyInfo.getLongitude());
                }
            }
            if (last != null) {
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(last));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            } else {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("No Goals")
                        .setMessage("You haven't completed any of the Goals Set")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                Intent myIntent = new Intent(MapsActivity.this, MainActivity.class);
                                startActivity(myIntent);
                            }
                        });
            }
        }
    }
}