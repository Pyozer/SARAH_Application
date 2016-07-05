package com.pyozer.sarah;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TextToSpeech.OnInitListener {

    protected TextView home_text;
    protected TextView text_response;
    protected ImageView speakEqualizer;
    protected AnimationDrawable rocketAnimation;
    protected FloatingActionButton button_mic;
    protected View drawer_layout;
    private final int RESULT_SPEECH = 100;

    protected TextToSpeech tts;

    protected SharedPreferences preferences;

    HttpRequest HttpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HttpRequest = new HttpRequest(this);

        home_text = (TextView) findViewById(R.id.home_text);
        text_response = (TextView) findViewById(R.id.text_response);
        button_mic = (FloatingActionButton) findViewById(R.id.fab);

        speakEqualizer = (ImageView) findViewById(R.id.speakEqualizer);
        speakEqualizer.setBackgroundResource(R.drawable.speaking_animation);
        rocketAnimation = (AnimationDrawable) speakEqualizer.getBackground();

        drawer_layout = findViewById(R.id.appbar_main);

        tts = new TextToSpeech(this, this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // On vérifie la connexion internet
        if(!checkInternet()){
            // Si pas internet, on met un message et désactive le bouton micro
            Snackbar snackbar = Snackbar
                    .make(drawer_layout, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction("Rafraichir", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();

            //home_text.setText(R.string.no_internet);
            button_mic.setEnabled(false);
        } else {
            // Si internet, on active le bouton micro
            button_mic.setEnabled(true);
        }

        button_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Au clique sur le bouton micro, on lance la reco vocal
                promptSpeechInput();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String state_home_text = home_text.getText().toString().trim();
        String state_textLog = text_response.getText().toString().trim();

        outState.putString("home_text", state_home_text);
        outState.putString("textLog", state_textLog);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String saved_home_text = savedInstanceState.getString("home_text");
        String saved_textLog = savedInstanceState.getString("textLog");

        if(saved_home_text != null) {
            home_text.setText(saved_home_text);
        }
        if(saved_textLog != null) {
            text_response.setText(saved_textLog);
        }
    }

    // Vérification de la connexion internet
    public boolean checkInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
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
            Snackbar.make(drawer_layout, getString(R.string.speech_not_supported), Snackbar.LENGTH_LONG)
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
                    // On lance la requete http
                    HttpRequest.prepareUrlRequest(text.get(0));
                } else {
                    home_text.setText(getString(R.string.say_again));
                }
                break;
            }
        }
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            //Log.d("TTS", "Initilization Success!");
            tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                @Override
                public void onUtteranceCompleted(String utteranceId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button_mic.setEnabled(true);
                            rocketAnimation.stop();
                            speakEqualizer.setVisibility(View.GONE);
                        }
                    });
                }
            });

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                tts.speak(" ", TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    // Vocalise le résultat
    protected void speechText(String textToSpeech) {
        if(!tts.isSpeaking()) {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SpeechID");
            tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, params);
            button_mic.setEnabled(false);
            speakEqualizer.setVisibility(View.VISIBLE);
            rocketAnimation.start();
        } else {
            tts.stop();
        }
    }

    @Override
    protected void onDestroy() {
        if(tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroy();
    }

    //
    // Menu de navigation
    //
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_speak) {
            startActivity(new Intent(MainActivity.this, SpeakActivity.class));

        } else if(id == R.id.menu_command) {
            startActivity(new Intent(MainActivity.this, ActionActivity.class));

        } else if (id == R.id.menu_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        } else if (id == R.id.menu_help) {
            startActivity(new Intent(MainActivity.this, HelpActivity.class));

        } else if (id == R.id.menu_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));

        } else if (id == R.id.menu_update) {
            startActivity(new Intent(MainActivity.this, UpdateActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}