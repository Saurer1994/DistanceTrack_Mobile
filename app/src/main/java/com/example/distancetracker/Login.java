package com.example.distancetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.distancetracker.Utilities.NetworkUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

//https://doctorcodetutorial.blogspot.com/2019/09/make-login-activity-in-android-studio.html

public class Login extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    public EditText user;
    public EditText password;
    public Button btn_Login;

    public URL SearchUrl;

    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int SEARCH_LOADER = 22;
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //portrait mode

        user = findViewById(R.id.editText_user);
        password = findViewById(R.id.editText_pass);
        btn_Login = findViewById(R.id.btn_Login);

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setText("");
                password.setText("");
                makeSearchQuery();
            }
        });

        LoaderManager.getInstance(this).initLoader(SEARCH_LOADER, null, this);
    }

    private void makeSearchQuery() {

        SearchUrl = NetworkUtils.buildUrl();

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, SearchUrl.toString());

        //Call getSupportLoaderManager and store it in a LoaderManager variable
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        //Get our Loader by calling getLoader and passing the ID we specified
        Loader<String> githubSearchLoader = loaderManager.getLoader(SEARCH_LOADER);
        //If the Loader was null, initialize it. Else, restart it.
        if (githubSearchLoader == null) {
            loaderManager.initLoader(SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(SEARCH_LOADER, queryBundle, this);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Nullable
            @Override
            public String loadInBackground() {

                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString))
                {
                    return null;
                }

                try {
                    URL url = new URL(searchQueryUrlString);
                    return NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "No response From Http URL");
                    return null;
                }
            }

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null)
                {
                    return;
                }

                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Intent StartActivity = new Intent(Login.this, StartActivity.class);
        startActivity(StartActivity);
        //response.setText(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

}
