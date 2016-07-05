package com.pyozer.sarah;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActionDeleteActivity extends AppCompatActivity {

    protected Button action_delete;
    protected ListView list_actions;

    private View layout_actiondelete;

    protected ArrayAdapter adapter;
    protected ArrayList<String> itemsList;

    ActionPreferences ActionPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_delete);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        layout_actiondelete = findViewById(R.id.layout_actiondelete);

        action_delete = (Button) findViewById(R.id.action_delete);
        list_actions = (ListView) findViewById(R.id.list_actions);
        list_actions.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        itemsList = new ArrayList<String>();
        // On charge les éléments du spinner
        ActionPreferences = new ActionPreferences(this);
        ActionPreferences.setContext(getApplicationContext());
        ActionPreferences.LoadPreferences();

        action_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int itemListLength = itemsList.size();
                SparseBooleanArray checkedItemPositions = list_actions.getCheckedItemPositions();

                if(itemListLength == 0) {
                    Snackbar.make(layout_actiondelete, getString(R.string.error_item_empty), Snackbar.LENGTH_LONG).show();
                } else if(checkedItemPositions.size() == 0) {
                    Snackbar.make(layout_actiondelete, getString(R.string.error_item_nocheck), Snackbar.LENGTH_LONG).show();
                } else {
                    int itemCount = list_actions.getCount();

                    for(int i = itemCount - 1; i >= 0; i--){
                        if(checkedItemPositions.get(i)){
                            itemsList.remove(itemsList.get(i));
                        }
                    }
                    if(itemsList.size() == 0) {
                        Snackbar.make(layout_actiondelete, getString(R.string.action_delete_success_empty), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(layout_actiondelete, getString(R.string.action_delete_success), Snackbar.LENGTH_LONG).show();
                    }
                    checkedItemPositions.clear();
                    adapter.notifyDataSetChanged();
                    ActionPreferences.SavePreferences(itemsList);
                    populateListView();
                }
            }
        });
    }

    /**
     * Permet de remplir le Spinner
     * */
    protected void populateListView() {
        // On supprime d'abord tout les éléments
        list_actions.setAdapter(null);
        // On les remets
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < itemsList.size(); i++) {
            lables.add(itemsList.get(i));
        }
        // Creating adapter for spinner
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, lables);
        // attaching data adapter to spinner
        list_actions.setAdapter(adapter);
    }

    /**
     * Affiche la fenêtre de dialoge pour ajouter une action
     */
    public void showAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.action_delete_dialog_title));
        alert.setMessage(getString(R.string.action_delete_dialog_message));
        alert.setCancelable(true);
        alert.setNegativeButton(getString(R.string.action_delete_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton(getString(R.string.action_delete_dialog_valid), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActionPreferences.ClearPreferences();
                adapter.notifyDataSetChanged();
                populateListView();

                Snackbar.make(layout_actiondelete, getString(R.string.action_all_deleted), Snackbar.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.menu_actiondelete, menu);
        return true;
    }

    /**
     * Gère les items du menu
     */
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_action_delete_all:
                showAlertDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
