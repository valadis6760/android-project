package com.example.mdpproject.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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
        allArrayList.addAll(db.getAllRecords());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng last = null;
        if(allArrayList.size()>0){
            for(DailyInfo x : allArrayList) {
                LatLng latlng = new LatLng(Double.parseDouble(x.getLatitude()), Double.parseDouble(x.getLongitude()));
                mMap.addMarker(new MarkerOptions().position(latlng).title(x.getSteps()));
                last = latlng;
                Log.d("TABLEEEE", "onCreate: content "+x.getLatitude()+" - "+x.getLongitude());
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(last));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));
        }
    }
}