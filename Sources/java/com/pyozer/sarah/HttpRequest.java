package com.pyozer.sarah;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
    private MainActivity mainActivity = null;
    private SpeakActivity speakActivity = null;
    private ActionActivity actionActivity = null;
    private UpdateActivity updateActivity = null;

    SharedPreferences preferences;

    public HttpRequest(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
    }

    public HttpRequest(SpeakActivity speakActivity) {
        this.speakActivity = speakActivity;
        preferences = PreferenceManager.getDefaultSharedPreferences(speakActivity);
    }

    public HttpRequest(ActionActivity actionActivity) {
        this.actionActivity = actionActivity;
        preferences = PreferenceManager.getDefaultSharedPreferences(actionActivity);
    }

    public HttpRequest(UpdateActivity updateActivity) {
        this.updateActivity = updateActivity;
        preferences = PreferenceManager.getDefaultSharedPreferences(updateActivity);
    }

    public class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                if(mainActivity != null || actionActivity != null || updateActivity != null) {
                    return downloadUrl(urls);
                } else {
                    downloadUrl(urls);
                    return "Requête envoyé";
                }
            } catch (IOException e) {
                if(mainActivity != null) {
                    return doInBackgroundMainActivity(urls);

                } else if(speakActivity != null) {
                    return speakActivity.getString(R.string.no_connexion_client);

                } else if(actionActivity != null) {
                    doInBackgroundActionActivity(urls);
                    return null;
                } else if(updateActivity != null) {
                    return updateActivity.getString(R.string.no_connexion_github);
                }
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            if (updateActivity != null) {
                updateActivity.checkUpdate.setEnabled(false);
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(mainActivity != null) {
                onExecuteMainActivity(result);
            } else if(speakActivity != null) {
                onExecuteSpeakActivity(result);
            } else if(actionActivity != null) {
                onExecuteActionActivity();
            } else if(updateActivity != null) {
                onExecuteUpdateActivity(result);
            }
        }
    }

    /**
     * On effectue la requête et récupère le contenu
     * @param params
     * @return
     * @throws IOException
     */
    private String downloadUrl(String... params) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 1000;
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
            //int response = conn.getResponseCode();
            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            if(params[0].indexOf("/sarah?reco") > 0) {
                contentAsString = "NOSPEAK";
            }
            return contentAsString;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Convertion du InputStream en String.
     * @param stream
     * @param len
     * @return
     * @throws IOException
     */
    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    /**
     * On prépare les url pour la requete
     * @param query
     */
    protected void prepareUrlRequest(String query) {

        Boolean scribe_on = preferences.getBoolean("scribe", true);
        String scribeIP = preferences.getString("scribeIp", "192.168.0.1");
        String scribePORT = preferences.getString("scribePort", "4300");

        String clientIP = preferences.getString("clientIp", "192.168.0.1");
        String clientPORT = preferences.getString("clientPort", "8888");

        Boolean use_speak_plugin = preferences.getBoolean("use_speak_plugin", false);
        String serverIP = preferences.getString("serverIp", "192.168.0.1");
        String serverPORT = preferences.getString("serverPort", "8080");

        String sarah_name = preferences.getString("sarah_name", "Sarah");
        String sarah_use = preferences.getString("sarah_set_her_name", "0");

        Boolean tts_on_pc = preferences.getBoolean("tts_on_pc", true);

        if(sarah_use.equals("0") && !query.contains(sarah_name)) {
            query = sarah_name + " " + query;
        }

        String readTimeOut;
        String connectTimeout;
        // Requete au Serveur si Scribe
        if(scribe_on) {
            String urlScribe = "http://" + scribeIP + ":" + scribePORT + "/sarah?reco=" + Uri.encode(query) + "&confidence=0.95&source=application";
            new DownloadWebpageTask().execute(urlScribe, "500", "500");

            readTimeOut = "8000";
            connectTimeout = "8000";
        } else {
            readTimeOut = "3000";
            connectTimeout = "3000";
        }

        String tts_pc = "&notts=true";
        if(tts_on_pc) {
            tts_pc = "&notts=false";
        }
        // Requete au client
        String url;
        if(use_speak_plugin) {
            url = "http://" + serverIP + ":" + serverPORT + "/sarah/speak?emulate=" + Uri.encode(query) + tts_pc;
        } else {
            url = "http://" + clientIP + ":" + clientPORT + "/?emulate=" + Uri.encode(query) + tts_pc;
        }
        new DownloadWebpageTask().execute(url, readTimeOut, connectTimeout);
    }

    /**
     * GESTION AVANT LA REQUETE
     * @param urls
     * @return
     */
    public String doInBackgroundMainActivity(String... urls) {
        if (urls[0].indexOf("sarah?reco=") < 1) {
            String erreur = mainActivity.getString(R.string.no_connexion_client);

            Snackbar.make(mainActivity.drawer_layout, erreur, Snackbar.LENGTH_LONG).show();
            return erreur;
        } else {
            String erreur = mainActivity.getString(R.string.no_connexion_scribe);

            Snackbar.make(mainActivity.drawer_layout, erreur, Snackbar.LENGTH_LONG).show();
            return erreur;
        }
    }

    public void doInBackgroundActionActivity(String... urls) {
        if(urls[0].indexOf("sarah?reco=") < 1 ) {
            String erreur = actionActivity.getString(R.string.no_connexion_client);

            Snackbar.make(actionActivity.layout_view, erreur, Snackbar.LENGTH_LONG).show();
        } else {
            String erreur = actionActivity.getString(R.string.no_connexion_scribe);

            Snackbar.make(actionActivity.layout_view, erreur, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * GESTION APRES REQUETE FINI
     * @param result
     */
    public void onExecuteMainActivity(String result) {
        Boolean return_tts = preferences.getBoolean("return_tts", true);

        if ((result.trim().length() < 1 || result.isEmpty()) && result != "NOSPEAK") {
            mainActivity.text_response.setText(R.string.no_result);
            if (return_tts) {
                mainActivity.speechText("Désolé je n'ai pas compris");
            }
        } else if (result == "NOSPEAK") {
            //Toast.makeText(MainActivity.this, "REQUETE SCRIBE FAITE", Toast.LENGTH_SHORT).show();
        } else {
            mainActivity.text_response.setText(result);
            // On vocalise le résultat
            if (return_tts) {
                mainActivity.speechText(result);
            }
        }
    }

    public void onExecuteSpeakActivity(String result) {
        Snackbar.make(speakActivity.layoutView, result, Snackbar.LENGTH_SHORT).show();
    }

    public void onExecuteActionActivity() {
        actionActivity.action_exe.setEnabled(true);
    }

    public void onExecuteUpdateActivity(String result) {
        // On enlève la dialog de chargement
        updateActivity.progressDialog.dismiss();
        // On affiche le dialog pour donner le résultat du check
        updateActivity.showAlertDialog(result);
        // On réactive le bouton pour check les majs
        updateActivity.checkUpdate.setEnabled(true);


    }
}
