package com.pyozer.sarah;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class SpeakActivity extends AppCompatActivity {

    protected EditText text4Speak;
    //protected TextView textSpeakLog;
    protected Button button4Speak;
    private static final String DEBUG_TAG = "HttpTTS";
    protected SharedPreferences preferences;

    protected View layoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //textSpeakLog = (TextView) findViewById(R.id.textSpeakLog);
        text4Speak = (EditText) findViewById(R.id.text4Speak);
        button4Speak = (Button) findViewById(R.id.button4Speak);

        layoutView = findViewById(R.id.layoutView);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // On vérifie la connexion internet
        if(!checkInternet()){
            // Si pas internet, on met un message et désactive le bouton micro
            Snackbar snackbar = Snackbar
                    .make(layoutView, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction("Rafraichir", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(SpeakActivity.this, SpeakActivity.class));
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();

            button4Speak.setEnabled(false);
        } else {
            // Si internet, on active le bouton micro
            button4Speak.setEnabled(true);
        }

        button4Speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text2Speak = text4Speak.getText().toString();

                if(text2Speak.isEmpty() || text2Speak.trim().length() < 1) {
                    text4Speak.setError(getString(R.string.error_field_required));
                } else {
                    sendTTS(text2Speak);

                    button4Speak.setEnabled(false);
                    Timer buttonTimer = new Timer();
                    buttonTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    button4Speak.setEnabled(true);
                                }
                            });
                        }
                    }, 2000);
                }
            }
        });
    }

    // Permet de vérifier la connexion internet
    public boolean checkInternet() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                downloadUrl(urls);
                return "Requête envoyé";
            } catch (IOException e) {
                Log.d(DEBUG_TAG, e.getMessage());
                return getString(R.string.no_connexion_client);
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Snackbar.make(layoutView, result, Snackbar.LENGTH_SHORT).show();
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private void downloadUrl(String... params) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;
        String ReadTimeout = params[1];
        String ConnectTimeout = params[2];

        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(Integer.parseInt(ReadTimeout));
            conn.setConnectTimeout(Integer.parseInt(ConnectTimeout));
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // On prépare l'url avant la requete
    protected void sendTTS(String query) {

        String clientIP = preferences.getString("clientIp", "192.168.0.11");
        String clientPORT = preferences.getString("clientPort", "8888");

        // Requete au client
        String url = "http://" + clientIP + ":" + clientPORT + "?tts=" + Uri.encode(query);
        new DownloadWebpageTask().execute(url, "1000", "1000");
    }
}
