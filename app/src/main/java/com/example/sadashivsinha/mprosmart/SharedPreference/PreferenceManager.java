package com.example.sadashivsinha.mprosmart.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sadashiv on 21/9/15.
 */
public class PreferenceManager {
    private static PreferenceManager _instance;

    private SharedPreferences preferences;

    public PreferenceManager(Context context) {preferences = getSharedPreference(context);}

    public static PreferenceManager getInstance(Context context){
        return _instance == null ? _instance= new PreferenceManager(context)
                :_instance;
    }

    public SharedPreferences getSharedPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_FILE, 0);
        return preferences;

    }
    public void putString(String key, String value){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(key, value).commit();

    }

    public void putInt(String key, int value){
        SharedPreferences.Editor ed=preferences.edit();
        ed.putInt(key, value).commit();
    }

    public int getInt(String key){
        return preferences.getInt(key,0);
    }

    public String getString(String key){
        return preferences.getString(key, "");
    }

    public void putBoolean(String key, boolean value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defaultValue){
        return preferences.getBoolean(key, defaultValue);
    }

    public boolean getBoolean(String key){
        return getBoolean(key, false);
    }
}
