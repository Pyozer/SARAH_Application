package com.pyozer.sarah;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UpdateActivity extends AppCompatActivity {

    protected TextView checkResult;
    protected Button checkUpdate;
    protected Button downloadUpdate;

    protected ProgressDialog progressDialog;

    protected View update_layout;

    protected HttpRequest HttpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        HttpRequest = new HttpRequest(this);

        checkResult = (TextView) findViewById(R.id.checkResult);
        checkUpdate = (Button) findViewById(R.id.checkUpdate);
        downloadUpdate = (Button) findViewById(R.id.downloadUpdate);

        update_layout = findViewById(R.id.update_layout);

        // On vérifie la connexion internet
        if(!checkInternet()){
            // Si pas internet, on met un message et désactive le bouton micro
            Snackbar snackbar = Snackbar
                    .make(update_layout, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction("Rafraichir", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(UpdateActivity.this, UpdateActivity.class));
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();

            checkUpdate.setEnabled(false);
        } else {
            // Si internet, on active le bouton micro
            checkUpdate.setEnabled(true);
        }

        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                StartCheckUpdate();
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
    protected void StartCheckUpdate() {
        String url = "https://raw.githubusercontent.com/Pyozer/SARAH_Application/master/last_version.txt";
        HttpRequest.new DownloadWebpageTask().execute(url, "3000", "3000");
    }

    protected void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mise à jour");
        progressDialog.setMessage("Chargement");
        progressDialog.show();
    }

    /**
     * Affiche la fenêtre de dialog
     */
    public void showAlertDialog(String last_version) {

        final String version2download = last_version.trim();
        String actual_version = getString(R.string.version_app);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.update_dialog_title));

        if(actual_version.equals(version2download) || actual_version == version2download) {
            alert.setMessage(getString(R.string.update_check_noupdate));
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            alert.setMessage(getString(R.string.update_dialog_newupdate));
            alert.setPositiveButton(getString(R.string.update_dialog_download), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Pyozer/SARAH_Application/raw/master/SARAH_v" + version2download + ".apk"));
                    startActivity(browserIntent);
                }
            });
        }
        alert.setCancelable(true);
        AlertDialog dialog = alert.create();
        dialog.show();
    }

}
