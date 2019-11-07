package com.example.distancetracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private Button BtnStart = null;
    private Button BtnStop = null;
    private TextView v_longitude = null;
    private TextView v_latitude = null;
    private TextView v_location = null;
    private TextView v_distance = null;
    //private TextView v_update_status = null;
    private TextView v_gps_status = null;

    String longitude = "";
    String latitude = "";
    String location = "";
    String altitude = "";


    double longOld = 0;
    double latOld = 0;
    float distancecalc = 0;

    DateFormat df = new SimpleDateFormat("yyyyMMdd");
    String fromDate = "20120607";
    String toDate = "20120913";
    Calendar c1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //portrait mode

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        v_longitude = (TextView) findViewById(R.id.Lon_view);
        v_latitude = (TextView) findViewById(R.id.Lat_view);
        v_location = (TextView) findViewById(R.id.Loc_view);
        v_distance = (TextView) findViewById(R.id.Distance_view);
        v_gps_status = (TextView) findViewById(R.id.Gps_status);
        BtnStart = (Button) findViewById(R.id.btnStart);
        BtnStop = (Button) findViewById(R.id.btnStop);

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                //Check if GPS is enabled
                locationListener = new GPSTracker();

                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    v_gps_status.setText("Wait for signal");
                    v_gps_status.setTextColor(Color.parseColor("#0066ff"));
                    locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
                else {
                    v_gps_status.setText("No GPS-Access!!!");
                    v_gps_status.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        });
        locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private class GPSTracker implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            //Try to get city name
            String city_name = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;

            try {

                for (int j = 0; j <= 10; j++)
                {
                    addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        city_name = address.getAddressLine(0);
                    }
                }
            } catch (IOException e) {
                city_name = "unknown";
                e.printStackTrace();
            }

            latitude = "" + loc.getLatitude(); //Get latitude
            longitude = "" + loc.getLongitude(); //Get longitude

            if(latOld == 0 || longOld == 0)
            {
                latOld = loc.getLatitude();
                longOld = loc.getLongitude();

            }

            location = "" + city_name; //Get city name
            altitude = "" + loc.getAltitude() + " m"; //Get height in meters
            v_latitude.setText(latitude);
            v_longitude.setText(longitude);
            v_location.setText(location);
            v_distance.setText(calculateDistance(latOld, longOld, loc.getLatitude(),loc.getLongitude()) + " m");
            v_gps_status.setText("GPS working");
            v_gps_status.setTextColor(Color.parseColor("#33cc33"));
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