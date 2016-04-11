package com.pyozer.sarah;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements android.speech.tts.TextToSpeech.OnInitListener, android.speech.tts.TextToSpeech.OnUtteranceCompletedListener {

    protected TextView home_text;
    protected TextView textLog;
    protected FloatingActionButton button_mic;
    private final int RESULT_SPEECH = 100;

    protected TextToSpeech tts;

    private static final String DEBUG_TAG = "HttpExample";
    protected SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        home_text = (TextView) findViewById(R.id.home_text);
        textLog = (TextView) findViewById(R.id.textLog);
        button_mic = (FloatingActionButton) findViewById(R.id.fab);

        tts = new TextToSpeech(MainActivity.this, MainActivity.this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // On vérifie la connexion internet
        if(!checkInternet()){
            // Si pas internet, on met un message et désactive le bouton micro
            home_text.setText(R.string.no_internet);
            button_mic.setEnabled(false);
        } else {
            // Si internet, on active le bouton micro
            button_mic.setEnabled(true);
        }

        FloatingActionButton button_mic = (FloatingActionButton) findViewById(R.id.fab);
        button_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Au clique sur le bouton micro, on lance la reco vocal
                promptSpeechInput(view);
            }
        });
    }

    // Permet de véirifier la connexion internet
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
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Impossible de se connecter à SARAH \n Vérifiez les paramètres de connexions";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Boolean return_tts = preferences.getBoolean("return_tts", true);

            if((result.trim().length() < 1 || result.isEmpty()) && result != "NOSPEAK") {
                textLog.setText(R.string.no_result);
                if(return_tts) {
                    speechText("Désolé je n'ai pas compris");
                }

            } else if(result == "NOSPEAK"){
                Toast.makeText(MainActivity.this, "REQUETE SCRIBE FAITE", Toast.LENGTH_SHORT).show();
            } else {
                textLog.setText(result);
                // On vocalise le résultat
                if(return_tts) {
                    speechText(result);
                }
            }
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(2000 /* milliseconds */);
            conn.setConnectTimeout(2100 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            if(myurl.indexOf("sarah/scribe") > 0) {
                contentAsString = "NOSPEAK";
            }
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    // On lance la reconnaissance vocal
    private void promptSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // On défini la langue
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        // On met un petit message sympa
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Snackbar.make(view, getString(R.string.speech_not_supported), Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    // On suit l'activité de la reconnaissance vocale
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    home_text.setText(text.get(0));
                    //textLog.setText(R.string.send_request);
                    Toast.makeText(this, R.string.send_request, Toast.LENGTH_LONG).show();

                    // On lance la requete http
                    prepareUrlRequest(text.get(0));


                } else {
                    home_text.setText(getString(R.string.say_again));
                }
                break;
            }
        }
    }

    // On prépare l'url avant la requete
    protected void prepareUrlRequest(String query) {

        String serverIP = preferences.getString("serverIp", "192.168.0.11");
        String serverPORT = preferences.getString("serverPort", "8080");
        String clientIP = preferences.getString("clientIp", "192.168.0.11");
        String clientPORT = preferences.getString("clientPort", "8888");

        // On vérifie que le prénom est dit
        if(query.indexOf("Jarvis") < 0) {
            query = "Jarvis " + query;
        }
        // Requete au client
        String url = "http://" + clientIP + ":" + clientPORT + "?emulate=" + Uri.encode(query);
        new DownloadWebpageTask().execute(url);
        // Requete au Serveur
        Boolean scribe_on = preferences.getBoolean("scribe", true);
        if(scribe_on) {
            String urlScribe = "http://" + serverIP + ":" + serverPORT + "/sarah/scribe?reco=" + Uri.encode(query) + "&confidence=0.95";
            new DownloadWebpageTask().execute(urlScribe);
        }

    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //Toast.makeText(MainActivity.this, "Utterence complete", Toast.LENGTH_SHORT).show();
                // On réactive le bouton micro après le texte dit
                button_mic.setEnabled(true);
            }
        });

    }

    // Permet de vocaliser le résultat
    protected void speechText(String textToSpeech) {
        if(!tts.isSpeaking()) {
            HashMap<String, String> params = new HashMap<>();
            params.put(android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sampletext");
            tts.speak(textToSpeech, android.speech.tts.TextToSpeech.QUEUE_ADD, params);
            button_mic.setEnabled(false);
        } else {
            tts.stop();
        }
    }

    @Override
    public void onInit(int status) {
        tts.setOnUtteranceCompletedListener(this);

    }

    @Override
    protected void onDestroy() {
        if(tts!=null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;

            case R.id.menu_help:
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                return true;

            case R.id.menu_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}