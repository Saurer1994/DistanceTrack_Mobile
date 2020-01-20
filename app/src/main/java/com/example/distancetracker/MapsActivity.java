package com.example.distancetracker;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.distancetracker.Utilities.DirectionJSONParser;
import com.example.distancetracker.Web_API.ApiAuthenticationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//https://www.journaldev.com/10365/android-google-maps-api
//https://www.journaldev.com/13373/android-google-map-drawing-route-two-points
//https://medium.com/@sraju432/drawing-route-between-two-points-using-google-map-ab85f4906035

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    public Button btnsave;
    public Button btncancel;

    private GoogleMap mMap;
    private ArrayList markerPoints= new ArrayList();
    private TextView distanceView;
    private TextView timeView;
    private String startLng;
    private String startlat;
    private String stopLng;
    private String stopLat;
    private String distance;
    private String startDate;
    private String endDate;
    private String starAddress;
    private String endAddress;
    private String carId;
    private float km;
    private JSONObject jsonObject;
    private  String username;
    private String password;
    private final static String BASEURL = "https://logdriverwebapi20200102075926.azurewebsites.net/trip/create";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        distanceView = (TextView) findViewById(R.id.distance_lbl);
        timeView = (TextView) findViewById(R.id.time_lbl);
        btncancel = (Button) findViewById(R.id.btn_cancel);
        btnsave = (Button) findViewById(R.id.btn_save);

        final Intent startActivity = new Intent(MapsActivity.this, StartActivity.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            //replace comma with a point
            distance = extras.getStringArray("DATA")[0];

            km = Float.valueOf(distance)/1000;
            String convert = String.format("%.2f", km);
            distanceView.setText(convert + " km");

            startlat = extras.getStringArray("DATA")[1];
            startLng = extras.getStringArray("DATA")[2];
            stopLat = extras.getStringArray("DATA")[3];
            stopLng = extras.getStringArray("DATA")[4];

            long mills = Long.valueOf(extras.getStringArray("DATA")[5]);
            int seconds = (int) (mills / 1000) % 60 ;
            int minutes = (int) ((mills / (1000*60)) % 60);
            int hours   = (int) ((mills / (1000*60*60)) % 24);
            timeView.setText(hours+":" +minutes + ":" + seconds );

            endDate = extras.getStringArray("DATA")[6];
            startDate = extras.getStringArray("DATA")[7];

            starAddress = extras.getStringArray("DATA")[8];
            endAddress = extras.getStringArray("DATA")[9];

            carId = extras.getStringArray("DATA")[10];

            username = extras.getStringArray("DATA")[11];
            password = extras.getStringArray("DATA")[12];

            jsonObject = new JSONObject();
            try {
                jsonObject.put("carId", carId);
                jsonObject.put("startAddress", starAddress);
                jsonObject.put("endAddress", endAddress);
                jsonObject.put("startTime", startDate);
                jsonObject.put("endTime", endDate);
                jsonObject.put("startLongitude", startLng);
                jsonObject.put("endLongitude", stopLng);
                jsonObject.put("endLatitude", stopLat);
                jsonObject.put("startLatitude", startlat);


                jsonObject.put("distance", convert.replace(",", "."));

                Log.i("JSON", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        final Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        bundle.putString("PASSWORD", password);

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity.putExtras(bundle);
                startActivity(startActivity);
                finish();
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    //sendPost();
                    ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(BASEURL, username, password);
                    apiAuthenticationClient.setHttpMethod("POST");
                    apiAuthenticationClient.setJsonObject(jsonObject);

                    AsyncTask<Void, Void, String> execute = new MapsActivity.ExecuteNetworkOperations(apiAuthenticationClient);
                    execute.execute();
                } catch (Exception ex) {
                }
                startActivity.putExtras(bundle);
                startActivity(startActivity);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        LatLng startPosition = new LatLng(Double.valueOf(startlat), Double.valueOf(startLng));
        mMap.addMarker(new MarkerOptions().position(startPosition).title("Startposition"));
        LatLng endPosition = new LatLng(Double.valueOf(stopLat),Double.valueOf(stopLng));
        mMap.addMarker(new MarkerOptions().position(endPosition).title("Endposition"));

        //Map focus on route
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startPosition);
        builder.include(endPosition);
        LatLngBounds bounds = builder.build();
        int padding = 200; // padding around start and end marker
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);


        String url = getDirectionsUrl(startPosition, endPosition);

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();



            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble((String) point.get("lat"));
                    double lng = Double.parseDouble((String) point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyC0S4ZsYDsCM21PCivCdyGYqIIz2WupUjQ";
        //Url for more Locations
        //https://maps.googleapis.com/maps/api/directions/json?origin=40.722543,-73.998585&destination=40.7057,-73.9964&waypoints=40.7064,-74.0094|40.722543,-73.998585&key=AIzaSyC0S4ZsYDsCM21PCivCdyGYqIIz2WupUjQ
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public class ExecuteNetworkOperations extends AsyncTask<Void, Void, String> {

        private ApiAuthenticationClient apiAuthenticationClient;
        private String responseServer;

        public ExecuteNetworkOperations (ApiAuthenticationClient apiAuthenticationClient) {
            this.apiAuthenticationClient = apiAuthenticationClient;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                responseServer = apiAuthenticationClient.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseServer;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(MapsActivity.this, "Saved", Toast.LENGTH_LONG).show();
        }
    }
}