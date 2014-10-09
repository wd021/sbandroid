package com.shipbob.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by waldemar on 30.05.14.
 */
public class PreferencesHelpers {

    private Context context;

    private SharedPreferences preferences;

    public PreferencesHelpers(Context context) {
        this.context = context;
    }

    public PreferencesHelpers(Context context, String nameSharedPreferences) {
        this.context = context;
        preferences = context.getSharedPreferences(nameSharedPreferences, 0);
    }

    public void savePreferences(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

}
