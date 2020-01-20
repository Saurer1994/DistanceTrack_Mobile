package com.example.distancetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        actionBar.setTitle("LOGDRIVER");
        actionBar.setSubtitle("Press to start your ride");

        btnStart = findViewById(R.id.btn_stop);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            username = extras.getString("USERNAME");
            password = extras.getString("PASSWORD");
        }

        final Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        bundle.putString("PASSWORD", password);

        Log.i("Bundle", bundle.toString());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent loginActivity  = new Intent(StartActivity.this, UserLoginActivity.class);
        switch(item.getItemId()) {

        case R.id.back:
            loginActivity  = new Intent(StartActivity.this, UserLoginActivity.class);
            startActivity(loginActivity);
            finish();
            return(true);
        case R.id.logout:
            loginActivity  = new Intent(StartActivity.this, UserLoginActivity.class);
            startActivity(loginActivity);
            finish();
            return(true);
        case R.id.exit:
            finish();
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }
}
