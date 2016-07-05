package com.pyozer.sarah;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActionPreferences {

    protected String MAIN_PREFERENCE = "list_save";
    protected String PREFERENCE = "ActionList";
    protected String JSON_TITLE = "list_actions";

    protected ActionActivity actionActivity = null;
    protected ActionDeleteActivity actionDeleteActivity = null;

    public Context context;

    public ActionPreferences(ActionActivity actionActivity) {
        this.actionActivity = actionActivity;
    }

    public ActionPreferences(ActionDeleteActivity actionDeleteActivity) {
        this.actionDeleteActivity = actionDeleteActivity;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Récupère les données JSON actuelle
     */
    protected String GetPreferences() {
        // On initialise la valeur par defaut
        JSONObject Objdefault = null;
        try {
            Objdefault = new JSONObject().put(JSON_TITLE, new JSONArray());
        } catch(JSONException e) {
            e.printStackTrace();
        }
        // On récupère les données actuelle
        SharedPreferences sharedPref = context.getSharedPreferences(MAIN_PREFERENCE, Context.MODE_PRIVATE);
        String data = sharedPref.getString(PREFERENCE, Objdefault.toString());

        if(data == null || data == "" || data.isEmpty() || data.equals(Objdefault.toString())) {
            try {
                String defaultAsk = "Il est quelle heure";

                ArrayList<String> itemListnew = new ArrayList<>();
                itemListnew.add(defaultAsk);
                SavePreferences(itemListnew);

                JSONObject default_obj = new JSONObject().put("request", defaultAsk);
                JSONArray default_array = new JSONArray().put(default_obj);
                JSONObject main_obj = new JSONObject().put(JSON_TITLE, default_array);

                data = main_obj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * Charge les données actuelle dans la liste
     */
    protected void LoadPreferences() {

        String json = GetPreferences();

        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray categories = jsonObj.getJSONArray(JSON_TITLE);

            if(actionActivity != null) {
                actionActivity.itemsList.clear();

                for (int i = 0; i < categories.length(); i++) {
                    JSONObject catObj = (JSONObject) categories.get(i);
                    actionActivity.itemsList.add(catObj.getString("request"));
                }
                actionActivity.populateSpinner();
            } else if(actionDeleteActivity != null) {
                actionDeleteActivity.itemsList.clear();

                for (int i = 0; i < categories.length(); i++) {
                    JSONObject catObj = (JSONObject) categories.get(i);
                    actionDeleteActivity.itemsList.add(catObj.getString("request"));
                }
                actionDeleteActivity.populateListView();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sauvegarder les informations JSON
     * */
    protected void SavePreferences(ArrayList itemsList) {
        // TODO Auto-generated method stub
        try {
            JSONArray NewData = new JSONArray();

            for (int i = 0; i < itemsList.size(); i++) {
                JSONObject dataObj = new JSONObject();
                dataObj.put("request", itemsList.get(i));
                NewData.put(dataObj);
            }

            JSONObject newDataObj = new JSONObject();
            newDataObj.put(JSON_TITLE, NewData);

            String data2save = newDataObj.toString();

            SharedPreferences sharedPref = context.getSharedPreferences(MAIN_PREFERENCE, Context.MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.putString(PREFERENCE, data2save);
            prefEditor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void ClearPreferences() {
        actionDeleteActivity.itemsList.clear();

        SharedPreferences sharedPref = context.getSharedPreferences(MAIN_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.clear();
        prefEditor.apply();
    }
}
