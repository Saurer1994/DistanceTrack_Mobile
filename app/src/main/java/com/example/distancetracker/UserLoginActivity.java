package com.example.distancetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.distancetracker.Web_API.ApiAuthenticationClient;

public class UserLoginActivity extends AppCompatActivity {

    private Button button_login;
    private EditText editText_login_username;
    private EditText editText_login_password;
    private String username;
    private String password;
    private final static String BASEURL = "https://logdriverwebapi20200102075926.azurewebsites.net/car/getall";;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        editText_login_username = (EditText) findViewById(R.id.editText_user);
        editText_login_password = (EditText) findViewById(R.id.editText_pass);

        button_login = findViewById(R.id.btn_LoginUser);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    username = editText_login_username.getText().toString();
                    password = editText_login_password.getText().toString();

                    ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(BASEURL,username,password);
                    apiAuthenticationClient.setHttpMethod("GET");

                    AsyncTask<Void, Void, String> execute = new UserLoginActivity.ExecuteNetworkOperation(apiAuthenticationClient);
                    execute.execute();
                } catch (Exception ex) {
                }
            }
        });
    }

    /**
     * This subclass handles the network operations in a new thread.
     * It starts the progress bar, makes the API call, and ends the progress bar.
     */
    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ApiAuthenticationClient apiAuthenticationClient;
        private String isValidCredentials;

        /**
         * Overload the constructor to pass objects to this class.
         */
        public ExecuteNetworkOperation(ApiAuthenticationClient apiAuthenticationClient) {
            this.apiAuthenticationClient = apiAuthenticationClient;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Display the progress bar.
            //findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                isValidCredentials = apiAuthenticationClient.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Hide the progress bar.
            //findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            // LoginActivity Success
            if (!isValidCredentials.equals("")) {
                goToNextActivity();
            }
            // LoginActivity Failure
            else {
                Toast.makeText(getApplicationContext(), "LoginActivity Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Open a new activity window.
     */
    private void goToNextActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        bundle.putString("PASSWORD", password);

        Intent intent = new Intent(UserLoginActivity.this, StartActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
