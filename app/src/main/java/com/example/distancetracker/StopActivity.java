package com.example.distancetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.makeText;

public class StopActivity extends AppCompatActivity {

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;

    private DateFormat df = new SimpleDateFormat("yyyyMMdd");
    private String fromDate = "20120607";
    private String toDate = "20120913";
    private Calendar c1;

    private String start_Address = null;
    private String end_Address = null;

    private double longOld = 0;
    private double latOld = 0;
    private float distancecalc = 0;

    private String car = "";
    private String TypeOfDrive = "";

    private boolean startLocationAssigned = false;

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
            Toast.makeText(StopActivity.this, car, Toast.LENGTH_LONG).show();
        }

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapsActivity = new Intent(StopActivity.this, MapsActivity.class);
                startActivity(mapsActivity);
                finish();
            }
        });

        initGpsTracker();

    }

    public void initGpsTracker(){
        //Check if GPS is enabled
        locationListener = new GPSTracker();

        if (ContextCompat.checkSelfPermission(StopActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //v_gps_status.setText("Wait for signal");
            //v_gps_status.setTextColor(Color.parseColor("#0066ff"));
            locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else {
            //v_gps_status.setText("No GPS-Access!!!");
            //v_gps_status.setTextColor(Color.parseColor("#ff0000"));
        }
    }
    private class GPSTracker implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            //Convert to Date
            Date startDate = null;
            try {

                startDate = df.parse(fromDate);

            } catch (ParseException e) {

                e.printStackTrace();
            }

            c1 = Calendar.getInstance();
            //Change to Calendar Date
            c1.setTime(startDate);

            try {
                for (int j = 0; j <= 10; j++)
                {
                    addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        if (start_Address == null) {
                            start_Address = address.getAddressLine(0);
                        }
                        else {
                            end_Address = address.getAddressLine(0);
                        }

                    }
                }
            } catch (IOException e) {
                //end_Address = "unknown";
                e.printStackTrace();
            }

            if(latOld == 0 || longOld == 0)
            {
                latOld = loc.getLatitude();
                longOld = loc.getLongitude();
            }

            if(!startLocationAssigned)
            {
                //v_oldLatitude.setText(String.valueOf(latOld));
                //v_oldLongitude.setText(String.valueOf(longOld));
                startLocationAssigned = true;
            }

            //v_oldLocation.setText(start_Address);
            //v_latestLatitude.setText(String.valueOf(loc.getLatitude()));
            //v_latestLongitude.setText(String.valueOf(loc.getLongitude()));
            //v_latestLocation.setText(end_Address);

            if(calculateDistance(latOld, longOld, loc.getLatitude(),loc.getLongitude()) < 1000)
            {
                double m = calculateDistance(latOld, longOld, loc.getLatitude(),loc.getLongitude());
                textViewDistance.setText(String.format("%.2f", m) + " m");
            }else{
                double km = calculateDistance(latOld, longOld, loc.getLatitude(),loc.getLongitude()) /1000;
                textViewDistance.setText(String.format("%.2f", km) + " km");
            }


            //v_gps_status.setText("GPS working");
            //v_gps_status.setTextColor(Color.parseColor("#33cc33"));
            Calendar c = Calendar.getInstance(); //Get time on system
            //v_update_status.setText("Last update: " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
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
