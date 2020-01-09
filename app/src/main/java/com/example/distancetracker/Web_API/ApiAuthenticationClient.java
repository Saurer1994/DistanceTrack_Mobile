package com.example.distancetracker.Web_API;

import android.util.Log;

import com.example.distancetracker.Utilities.Base64Encoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApiAuthenticationClient {

    private String baseUrl;
    private String username;
    private String password;
    private String httpMethod; // GET, POST, PUT, DELETE
    private JSONObject jsonObject;

    /**
     *
     * @param baseUrl String
     * @param username String
     * @param password String
     */
    public ApiAuthenticationClient(String  baseUrl, String username, String password) {
        setBaseUrl(baseUrl);
        this.username = username;
        this.password = password;
        // This is important. The application may break without this line.
        System.setProperty("jsse.enableSNIExtension", "false");
    }

    /**
     * --&gt;http://BASE_URL.COM&lt;--/resource/path
     * @param baseUrl the root part of the URL
     * @return this
     */
    public ApiAuthenticationClient setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        if (!baseUrl.substring(baseUrl.length() - 1).equals("/")) {
            this.baseUrl += "/";
        }
        return this;
    }

    /**
     * Sets the HTTP method used for the Rest API.
     * GET, PUT, POST, or DELETE
     * @return this
     */
    public ApiAuthenticationClient setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public ApiAuthenticationClient setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    /**
     * Make the call to the Rest API and return its response as a string.
     * @return String
     */
    public String execute() {
        String line;
        StringBuilder outputStringBuilder = new StringBuilder();

        try {
            StringBuilder urlString = new StringBuilder(baseUrl);

            URL url = new URL(urlString.toString());

            String encoding = Base64Encoder.encode(username + ":" + password);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(httpMethod);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.setRequestProperty("Accept", "application/json");


            // Make the network connection and retrieve the output from the server.
            if (httpMethod.equals("POST") || httpMethod.equals("PUT")) {

                connection.setDoInput(true);
                connection.setDoOutput(true);

                try {
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                    Log.i("JSON", jsonObject.toString());
                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());

                    os.writeBytes(jsonObject.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(connection.getResponseCode()));
                    Log.i("MSG", connection.getResponseMessage());

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        outputStringBuilder.append(line);
                    }

                    }catch (IOException ex) { }
                connection.disconnect();

            } else {

                connection.setRequestProperty("Content-Type", "text/plain");

                InputStream content = (InputStream) connection.getInputStream();

                //connection.
                BufferedReader in = new BufferedReader(new InputStreamReader(content));

                while ((line = in.readLine()) != null) {
                    outputStringBuilder.append(line);
                }

                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStringBuilder.toString();
    }
}


