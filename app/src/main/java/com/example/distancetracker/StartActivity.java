package com.example.distancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    public Button btnStart;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnStart = findViewById(R.id.btn_stop);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            username = extras.getString("USERNAME");
            password = extras.getString("PASSWORD");
        }

        final Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        bundle.putString("PASSWORD", password);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent CarSelectionIntent =  new Intent(StartActivity.this, CarSelectionActivity.class);
                CarSelectionIntent.putExtras(bundle);
                startActivity(CarSelectionIntent);
                finish();
            }
        });
    }
}
