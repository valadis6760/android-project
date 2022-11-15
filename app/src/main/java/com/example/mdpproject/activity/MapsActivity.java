package com.example.mdpproject.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mdpproject.MainActivity;
import com.example.mdpproject.R;
import com.example.mdpproject.SettingsActivity;
import com.example.mdpproject.databinding.ActivityMapsBinding;
import com.example.mdpproject.db.DBHelper;
import com.example.mdpproject.db.DailyInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DBHelper db;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private ArrayList<DailyInfo> allArrayList  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Location History");

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = new DBHelper(this);
        try {
            allArrayList.addAll(db.getAllRecords());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng last = null;
        if (allArrayList.size() > 0) {
            for (DailyInfo x : allArrayList) {
                if (x.getLatitude() != null && x.getLongitude() != null) {
                    LatLng latlng = new LatLng(Double.parseDouble(x.getLatitude()), Double.parseDouble(x.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(latlng).title(Integer.toString(x.getSteps())));
                    last = latlng;
                    Log.d("TABLE", "onCreate: content " + x.getLatitude() + " - " + x.getLongitude());
                }
            }
            if (last != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(last));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));
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