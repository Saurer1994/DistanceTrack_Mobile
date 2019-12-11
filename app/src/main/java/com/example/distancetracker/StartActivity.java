package com.example.distancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    public Button btnStart;

    public String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnStart = findViewById(R.id.btn_stop);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            Intent StopActivity = new Intent(StartActivity.this, StopActivity.class);
            StopActivity.putExtra("QRCODE", extras.getString("QR"));
            startActivity(StopActivity);
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent QRIntent =  new Intent(StartActivity.this, QRScannerActivity.class);
                startActivity(QRIntent);
            }
        });
    }
}
