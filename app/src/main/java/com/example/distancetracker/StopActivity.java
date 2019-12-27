package com.example.distancetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.makeText;

public class StopActivity extends AppCompatActivity {

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;

    private Calendar calendar;

    Long startTime;
    String mills;

    private String start_Address = null;
    private String end_Address = null;

    private double longOld = 0;
    private double latOld = 0;
    private float distancecalc = 0;

    private String car = "";
    private String TypeOfDrive = "";

    double startLng;
    double startlat;
    double stopLng;
    double stopLat;

    public TextView textViewDistance;
    public Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        ActivityCompat.requestPermissions(StopActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        textViewDistance = findViewById(R.id.textView_distance);
        btnStop = findViewById(R.id.btn_stop);

        locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            car = extras.getString("CAR");
        }
        startTime = Calendar.getInstance().getTimeInMillis();

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Long endDate = Calendar.getInstance().getTimeInMillis();
                mills = Long.toString(endDate - startTime);

                String[] data = new String[6];
                data[0] = textViewDistance.getText().toString();
                data[1] = Double.toString(startlat);
                data[2] = Double.toString(startLng);
                data[3] = Double.toString(stopLat);
                data[4] = Double.toString(stopLng);
                data[5] = mills;

                Intent mapsActivity = new Intent(StopActivity.this, MapsActivity.class);
                mapsActivity.putExtra("DATA", data );
                startActivity(mapsActivity);
                finish();
            }
        });

        initGpsTracker();

    }

    public void initGpsTracker(){

        locationListener = new GPSTracker();

        if (ContextCompat.checkSelfPermission(StopActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //v_gps_status.setText("Wait for signal");
            locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else {
            //v_gps_status.setText("No GPS-Access!!!");
        }
    }
    private class GPSTracker implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    if (start_Address == null) {
                        start_Address = address.getAddressLine(0);
                        startlat = loc.getLatitude();
                        startLng = loc.getLongitude();
                    }
                    else {
                        end_Address = address.getAddressLine(0);
                        stopLat = loc.getLatitude();
                        stopLng = loc.getLongitude();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(latOld == 0 || longOld == 0)
            {
                latOld = loc.getLatitude();
                longOld = loc.getLongitude();
            }

            if(calculateDistance(latOld, longOld, loc.getLatitude(),loc.getLongitude()) < 1000)
            {
                double m = calculateDistance(latOld, longOld, loc.getLatitude(),loc.getLongitude());
                textViewDistance.setText(String.format("%.2f", m) + " m");
            }else{
                double km = calculateDistance(latOld, longOld, loc.getLatitude(),loc.getLongitude()) /1000;
                textViewDistance.setText(String.format("%.2f", km) + " km");
            }
        }


        public float calculateDistance(double lat1 ,double lng1, double lat2, double lng2){
            float[] results = new float[1];
            Location.distanceBetween(lat1, lng1, lat2, lng2, results);

            latOld = lat2;
            longOld = lng2;

            distancecalc = distancecalc + results[0];
            // distance in meter
            return distancecalc;
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}
