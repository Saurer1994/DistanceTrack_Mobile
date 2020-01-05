package com.example.distancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class CarSelectionActivity extends AppCompatActivity {

    public Button selectCar;
    public Button scanQr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_selection);

        selectCar = (Button) findViewById(R.id.btn_select_car);
        scanQr = (Button) findViewById(R.id.btn_scan_qr);

        //final Spinner dropdown = findViewById(R.id.spinner);
        //String[] items = new String[]{"Skoda Octavia", "BMW 320", "Audi A7"};
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //dropdown.setAdapter(adapter);

        selectCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ChooseCar =  new Intent(CarSelectionActivity.this, ChooseCarActivity.class);
                //QRIntent.putExtra("CAR", dropdown.getSelectedItem().toString());
                startActivity(ChooseCar);
            }
        });

        scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent QRIntent =  new Intent(CarSelectionActivity.this, QRScannerActivity.class);
                startActivity(QRIntent);
            }
        });
    }
}
