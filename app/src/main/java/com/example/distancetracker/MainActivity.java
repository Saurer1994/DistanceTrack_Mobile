package com.example.distancetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

public class MainActivity extends AppCompatActivity  {

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private Button BtnStart = null;
    private Button BtnStop = null;
    private Button BtnQR = null;
    private TextView v_oldLongitude = null;
    private TextView v_oldLatitude = null;
    private TextView v_oldLocation = null;

    private TextView v_latestLongitude = null;
    private TextView v_latestLatitude = null;
    private TextView v_latestLocation = null;

    private TextView v_distance = null;
    private TextView v_licencePlate = null;

    //private TextView v_update_status = null;
    private TextView v_gps_status = null;

    double longOld = 0;
    double latOld = 0;
    float distancecalc = 0;

    String start_Address = null;
    String end_Address = null;

    boolean startLocationAssigned = false;

    DateFormat df = new SimpleDateFormat("yyyyMMdd");
    String fromDate = "20120607";
    String toDate = "20120913";
    Calendar c1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //portrait mode

        //Intent LoginActivity = new Intent(MainActivity.this, StopActivity.class);
        //startActivity(LoginActivity);
        Intent LoginActivity = new Intent(MainActivity.this, UserLoginActivity.class);
        startActivity(LoginActivity);
        finish();


        v_oldLongitude = (TextView) findViewById(R.id.First_Lon_view);
        v_oldLatitude = (TextView) findViewById(R.id.First_Lat_view);
        v_oldLocation = (TextView) findViewById(R.id.First_Loc_view);

        v_latestLongitude = (TextView) findViewById(R.id.Latest_Lon_view);
        v_latestLatitude = (TextView) findViewById(R.id.Latest_Lat_view);
        v_latestLocation = (TextView) findViewById(R.id.Latest_Loc_view);

        v_distance = (TextView) findViewById(R.id.Distance_view);
        v_gps_status = (TextView) findViewById(R.id.Gps_status);
        v_licencePlate = (TextView) findViewById(R.id.LicencePlate_view);

        BtnStart = (Button) findViewById(R.id.btnStart);
        BtnStop = (Button) findViewById(R.id.btnStop);
        BtnQR = (Button) findViewById(R.id.btnQR);

        //Waiting for car licence plate
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            v_licencePlate.setText(extras.getString("QR"));
        }

        BtnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent QRIntent =  new Intent(MainActivity.this, QRScannerActivity.class);
                //startActivity(QRIntent);

                /*Intent MapIntent =  new Intent(MainActivity.this, MapsActivity.class);
                MapIntent.putExtra("LAT", latOld);
                MapIntent.putExtra("LON", longOld);
                startActivity(MapIntent);*/

                Intent Loginintent = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivity(Loginintent);
            }
        });

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                v_oldLatitude.setText(String.valueOf(latOld));
                v_oldLongitude.setText(String.valueOf(longOld));
                startLocationAssigned = true;
            }

            v_oldLocation.setText(start_Address);
            v_latestLatitude.setText(String.valueOf(loc.getLatitude()));
            v_latestLongitude.setText(String.valueOf(loc.getLongitude()));
            v_latestLocation.setText(end_Address);

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