package com.androidexperiments.shadercam.example;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kimsanghwan on 2017-02-28.
 */

public class Sharedpreference {
    public static final String SHARED_PREFERENCE_NAME = "app";
    public static SharedPreferences sharedpreferences;

    public static int getState(Context context) {
        sharedpreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedpreferences.getInt("state", 0);
    }

    public static void setState(Context context, int state) {
        sharedpreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedpreferences.edit().putInt("state", state).commit();
    }

}
