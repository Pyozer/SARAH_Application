package com.pyozer.sarah;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SpeakActivity extends AppCompatActivity {

    protected EditText text4Speak;
    protected TextInputLayout input_layout_text4Speak;
    protected ImageView icon_mic;
    protected Button button4Speak;

    private final int RESULT_SPEECH = 100;

    protected SharedPreferences preferences;

    HttpRequest HttpRequest;

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

        HttpRequest = new HttpRequest(this);

        input_layout_text4Speak = (TextInputLayout) findViewById(R.id.input_layout_text4Speak);
        text4Speak = (EditText) findViewById(R.id.text4Speak);
        button4Speak = (Button) findViewById(R.id.button4Speak);
        icon_mic = (ImageView) findViewById(R.id.icon_mic) ;

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
                String text2Speak = text4Speak.getText().toString().trim();

                if(text2Speak.isEmpty() || text2Speak.length() < 1) {
                    input_layout_text4Speak.setError(getString(R.string.error_field_required));
                } else {
                    sendTTS(text2Speak);
                    input_layout_text4Speak.setErrorEnabled(false);
                    button4Speak.setEnabled(false);

                    new Timer().schedule(new TimerTask() {
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

        icon_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
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

    // On prépare l'url avant la requete
    protected void sendTTS(String query) {

        String clientIP = preferences.getString("clientIp", "192.168.0.11");
        String clientPORT = preferences.getString("clientPort", "8888");

        // Requete au client
        String url = "http://" + clientIP + ":" + clientPORT + "?tts=" + Uri.encode(query);
        HttpRequest.new DownloadWebpageTask().execute(url, "5000", "5000");
    }

    // On lance la reconnaissance vocal
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // On défini la langue
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        // On met un petit message sympa
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Snackbar.make(layoutView, getString(R.string.speech_not_supported), Snackbar.LENGTH_LONG)
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
                    String SpeechToText = text.get(0);
                    // On met le texte dans le Input
                    text4Speak.setText(SpeechToText);
                }
                break;
            }
        }
    }
}
