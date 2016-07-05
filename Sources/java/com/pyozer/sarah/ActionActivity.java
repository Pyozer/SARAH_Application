package com.pyozer.sarah;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActionActivity extends AppCompatActivity {

    protected FloatingActionButton fab_add;
    protected Button action_exe;
    protected View layout_view;

    public SharedPreferences preferences;

    protected Spinner spinner;
    protected ArrayList<String> itemsList;
    protected ArrayAdapter<String> spinnerAdapter;

    ActionPreferences ActionPreferences;
    HttpRequest HttpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HttpRequest = new HttpRequest(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        layout_view = findViewById(R.id.layout_action);

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        action_exe = (Button) findViewById(R.id.action_exe);

        spinner = (Spinner) findViewById(R.id.list_action);

        itemsList = new ArrayList<String>();
        // On charge les éléments du spinner
        ActionPreferences = new ActionPreferences(this);
        ActionPreferences.setContext(getApplicationContext());
        ActionPreferences.LoadPreferences();

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlertDialog();
            }
        });

        action_exe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemListLength = itemsList.size();

                if(itemListLength == 0 || itemListLength < 1) {
                    Snackbar.make(layout_view, getString(R.string.error_item_empty), Snackbar.LENGTH_LONG).show();
                } else {
                    action_exe.setEnabled(false);

                    int index = spinner.getSelectedItemPosition();
                    String request = itemsList.get(index);
                    HttpRequest.prepareUrlRequest(request);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    action_exe.setEnabled(true);
                                }
                            });
                        }
                    }, 2500);
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Permet de remplir le Spinner
     * */
    protected void populateSpinner() {
        // On supprime d'abord tout les éléments
        spinner.setAdapter(null);
        // On les remets
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < itemsList.size(); i++) {
            lables.add(itemsList.get(i));
        }
        // Creating adapter for spinner
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lables);
        // attaching data adapter to spinner
        spinner.setAdapter(spinnerAdapter);
    }

    /**
     * Affiche la fenêtre de dialoge pour ajouter une action
     */
    public void displayAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_layout, null);
        final EditText request = (EditText) alertLayout.findViewById(R.id.add_request);

        AlertDialog.Builder alert = new AlertDialog.Builder(ActionActivity.this);
        alert.setTitle(getString(R.string.action_add_title));
        alert.setView(alertLayout);
        alert.setCancelable(true);
        alert.setNegativeButton(getString(R.string.action_add_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton(getString(R.string.action_add_save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String request2add = request.getText().toString().trim();

                if(request2add == null || request2add == "" || request2add.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_field_required), Toast.LENGTH_LONG).show();
                } else {
                    itemsList.add(request2add);
                    ActionPreferences.SavePreferences(itemsList);
                    populateSpinner();
                    Snackbar.make(layout_view, getString(R.string.action_add_success), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    /**
     * Créer le menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }

    /**
     * Gère les items du menu
     */
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_action_delete:
                startActivity(new Intent(this, ActionDeleteActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
