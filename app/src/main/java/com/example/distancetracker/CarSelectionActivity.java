package com.example.distancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class CarSelectionActivity extends AppCompatActivity {

    public Button selectCar;
    public Button scanQr;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_selection);

        selectCar = (Button) findViewById(R.id.btn_select_car);
        scanQr = (Button) findViewById(R.id.btn_scan_qr);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            username = extras.getString("USERNAME");
            password = extras.getString("PASSWORD");
        }

        final Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        bundle.putString("PASSWORD", password);

        selectCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ChooseCar =  new Intent(CarSelectionActivity.this, ChooseCarActivity.class);
                ChooseCar.putExtras(bundle);
                startActivity(ChooseCar);
            }
        });

        scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent QRIntent =  new Intent(CarSelectionActivity.this, QRScannerActivity.class);
                QRIntent.putExtras(bundle);
                startActivity(QRIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.add:
            Toast.makeText(this, "ADD", Toast.LENGTH_LONG).show();
            return(true);
        case R.id.reset:
            Toast.makeText(this, "RESET", Toast.LENGTH_LONG).show();
            return(true);
        case R.id.logout:
            Intent loginActivity  = new Intent(CarSelectionActivity.this, UserLoginActivity.class);
            startActivity(loginActivity);
            finish();
            return(true);
        case R.id.exit:
            finish();
    }
        return(super.onOptionsItemSelected(item));
    }
}
