package com.example.distancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.distancetracker.Utilities.Car;
import com.example.distancetracker.Web_API.ApiAuthenticationClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class ChooseCarActivity extends AppCompatActivity{

    private NumberPicker picker;
    private Button btnChoose;
    private List<Car> cars;
    private String[] carsArray;
    private String username;
    private String password;
    private final static String BASEURL = "https://logdriverwebapi20200102075926.azurewebsites.net/car/getall";
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_car);
        picker = (NumberPicker) findViewById(R.id.picker);
        btnChoose = (Button) findViewById(R.id.btn_choose);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("USERNAME");
            password = extras.getString("PASSWORD");
        }

        try {
            ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(BASEURL, username, password);
            apiAuthenticationClient.setHttpMethod("GET");

            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(apiAuthenticationClient);
            execute.execute();
        } catch (Exception ex) {
        }

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bundle = new Bundle();
                bundle.putString("USERNAME", username);
                bundle.putString("PASSWORD", password);
                bundle.putString("CARID",cars.get(picker.getValue()).getId());

                Intent StopActivity = new Intent(ChooseCarActivity.this, StopActivity.class);
                StopActivity.putExtras(bundle);
                startActivity(StopActivity);
                finish();
            }
        });


    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ApiAuthenticationClient apiAuthenticationClient;
        private String carsFromDb;

        public ExecuteNetworkOperation(ApiAuthenticationClient apiAuthenticationClient) {
            this.apiAuthenticationClient = apiAuthenticationClient;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                carsFromDb = apiAuthenticationClient.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return carsFromDb;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parseJson(result);
        }
    }

    public void parseJson(String json)
    {
        try {
            cars = new LinkedList<>();

            JSONArray jsonArray = new JSONArray(json);

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject  = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String model = jsonObject.getString("model");
                String carNickname = jsonObject.getString("carNickname");

                cars.add(new Car(id,name,model,carNickname));

            }

            carsArray = new String[cars.size()];
            for (int i=0; i<cars.size(); i++) {
                carsArray[i] = cars.get(i).getName() + " " + cars.get(i).getModel()+ " " + cars.get(i).getCarNickname();
            }

            picker.setMinValue(0);
            picker.setMaxValue(carsArray.length-1);
            picker.setDisplayedValues(carsArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            Intent ChooseCar =  new Intent(ChooseCarActivity.this, CarSelectionActivity.class);
            ChooseCar.putExtras(bundle);
            startActivity(ChooseCar);
        case R.id.logout:
            Intent loginActivity  = new Intent(ChooseCarActivity.this, UserLoginActivity.class);
            startActivity(loginActivity);
            finish();
            return(true);
        case R.id.exit:
            finish();
    }
        return(super.onOptionsItemSelected(item));
    }
}
