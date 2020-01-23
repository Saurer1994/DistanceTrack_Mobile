package com.example.distancetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.widget.Toast.makeText;

public class StopActivity extends AppCompatActivity {

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;

    private Long startTimeInMillis;
    private String mills;
    private String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private String startDate;

    private String startAddress = null;
    private String endAddress = null;

    private double longOld = 0;
    private double latOld = 0;
    private float distancecalc = 0;

    private String carId;

    private double startLng;
    private double startlat;
    private double endLng;
    private double endLat;

    private String username;
    private String password;
    private boolean locationFound;
    private boolean checkIfEndAdressIsAssigned;

    public TextView textViewDistance;
    public TextView textViewStatusGps;
    public Button btnStop;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        actionBar.setTitle("LOGDRIVER");
        actionBar.setSubtitle("Press to stop your ride");

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        textViewDistance = findViewById(R.id.textView_distance);
        textViewStatusGps = findViewById(R.id.textview_statusgps);
        btnStop = findViewById(R.id.btn_stop);

        locationFound = false;
        checkIfEndAdressIsAssigned = false;

        locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString("USERNAME");
            password = bundle.getString("PASSWORD");
            carId = bundle.getString("CARID");
        }
        startTimeInMillis = Calendar.getInstance().getTimeInMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
        Date today = Calendar.getInstance().getTime();
        startDate = dateFormat.format(today);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (locationFound){
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
                    Date today = Calendar.getInstance().getTime();
                    String endDate = dateFormat.format(today);

                    Long endTimeInMillis = Calendar.getInstance().getTimeInMillis();
                    mills = Long.toString(endTimeInMillis - startTimeInMillis);

                    String[] data = new String[13];
                    data[0] = Float.toString(distancecalc);
                    data[1] = Double.toString(startlat);
                    data[2] = Double.toString(startLng);
                    data[3] = Double.toString(endLat);
                    data[4] = Double.toString(endLng);
                    data[5] = mills;
                    data[6] = endDate;
                    data[7] = startDate;
                    data[8] = startAddress;
                    data[9] = endAddress;
                    data[10] = carId;
                    data[11] = username;
                    data[12] = password;

                    Intent mapsActivity = new Intent(StopActivity.this, MapsActivity.class);
                    mapsActivity.putExtra("DATA", data );
                    startActivity(mapsActivity);
                    finish();
                }
            }
        });

        initGpsTracker();

    }

    public void initGpsTracker(){

        locationListener = new GPSTracker();

        if (ContextCompat.checkSelfPermission(StopActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            textViewStatusGps.setTextColor(Color.RED);
            textViewStatusGps.setText("Waiting for signal");
            locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else {
            textViewStatusGps.setTextColor(Color.RED);
            textViewStatusGps.setText("No GPS-Access");
        }
    }

    private class GPSTracker implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            textViewStatusGps.setText("");

            locationFound = true;

            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    if (startAddress == null) {
                        startAddress = address.getAddressLine(0);
                        startlat = loc.getLatitude();
                        startLng = loc.getLongitude();

                        //if no endAddress is assigned it will be the same as the startAddress
                        if(checkIfEndAdressIsAssigned == false){
                            endAddress = startAddress;
                            endLat = startlat;
                            endLng = startLng;
                        }
                    }
                    else {
                        //if no endAddress is assigned it will be the same as the startAddress
                        checkIfEndAdressIsAssigned = true;

                        endAddress = address.getAddressLine(0);
                        endLat = loc.getLatitude();
                        endLng = loc.getLongitude();
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
                textViewDistance.setText(String.format("%.0f", m) + " m");
            }else{
                double km = calculateDistance(latOld, longOld, loc.getLatitude(),loc.getLongitude()) /1000;
                String kmOutput = String.format("%.2f", km).replace(",", ".");
                textViewDistance.setText(kmOutput + " km");
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
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.back:
            Intent ChooseCar =  new Intent(StopActivity.this, CarSelectionActivity.class);
            ChooseCar.putExtras(bundle);
            startActivity(ChooseCar);
            return(true);
        case R.id.logout:
            Intent loginActivity  = new Intent(StopActivity.this, UserLoginActivity.class);
            loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginActivity);
            finish();
            return(true);
        case R.id.exit:
            finish();
    }
        return(super.onOptionsItemSelected(item));
    }
}
