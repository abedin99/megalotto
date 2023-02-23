package com.ratechnoworld.megalotto.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ratechnoworld.megalotto.activity.SigninActivity;

public class Preferences {


    public static final String KEY_RESORT_IMAGE = "KEY_RESORT_IMAGE";
    public static final String KEY_USER = "KEY_CHAT_BY_USER";
    public static final String KEY_PASSWORD = "KEY_PASSWORD";
    public static final String KEY_CONSTANT_ID = "KEY_CONSTANT_ID";
    public static final String KEY_DEPOSITE_AMOUNT = "KEY_DEPOSITE_AMOUNT";
    public static final String KEY_PRICE = "KEY_PRICE";
    public static final String KEY_WINNER = "KEY_WINNER";
    public static final String KEY_SOLD = "KEY_SOLD";

    // SharedPreferences file name
    private static final String PREF_NAME = "com.gscscl.app";

    // private static variables
    @SuppressLint("StaticFieldLeak")
    private static Preferences instance;

    public Context context;

    // Shared Preferences
    private final SharedPreferences sharedPref;

    /**
     * The constant KEY_USER_ID.
     */
    public static final String KEY_USER_ID = "KEY_USER_ID";
    /**
     * The constant KEY_USER_NAME.
     */
    public static final String KEY_USER_NAME = "KEY_USER_NAME";
    /**
     * The constant KEY_FULL_NAME.
     */
    public static final String KEY_FULL_NAME = "KEY_FULL_NAME";
    /**
     * The constant KEY_MOBILE.
     */
    public static final String KEY_MOBILE = "KEY_MOBILE";
    /**
     * The constant KEY_EMAIL.
     */
    public static final String KEY_EMAIL = "KEY_EMAIL";
    /**
     * The constant KEY_DOB.
     */
    public static final String KEY_DOB = "KEY_DOB";
    /**
     * The constant KEY_GENDER.
     */
    public static final String KEY_GENDER = "KEY_GENDER";
    /**
     * The constant KEY_CONTST_ID.
     */
    public static final String KEY_CONTST_ID = "KEY_CONTST_ID";
    /**
     * The constant KEY_PKG_ID.
     */
    public static final String KEY_PKG_ID = "KEY_PKG_ID";
    /**
     * The constant KEY_FEE_ID.
     */
    public static final String KEY_FEE_ID = "KEY_FEE_ID";
    /**
     * The constant KEY_REFER_CODE.
     */
    public static final String KEY_REFER_CODE = "KEY_REFER_CODE";
    /**
     * The constant KEY_IS_AUTO_LOGIN.
     */
    public static final String KEY_IS_AUTO_LOGIN = "KEY_IS_AUTO_LOGIN";


    /**
     * Instantiates a new Preferences.
     *
     * @param context the context
     */
    public Preferences(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Gets instance.
     *
     * @param c the c
     * @return the instance
     */
    public static Preferences getInstance(Context c) {
        if (instance == null) {
            instance = new Preferences(c);
        }
        return instance;
    }

    /**
     * Gets shared pref.
     *
     * @return the shared pref
     */
    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    /**
     * Gets string.
     *
     * @param key the key
     * @return the string
     */
    public String getString(String key) {
        return sharedPref.getString(key, "");
    }

    /**
     * Sets string.
     *
     * @param key   the key
     * @param value the value
     */
    public void setString(String key, String value) {
        SharedPreferences.Editor sharedPrefEditor = getSharedPref().edit();
        sharedPrefEditor.putString(key, value);
        sharedPrefEditor.apply();
    }

    public void setlogout() {
        SharedPreferences.Editor sharedPrefEditor = getSharedPref().edit();
        sharedPrefEditor.clear();
        sharedPrefEditor.apply();

        Intent i = new Intent(context, SigninActivity.class);
        i.putExtra("finish", true);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    /**
     * Gets string.
     *
     * @param key the key
     * @return the string
     */
    public int getInt(String key) {
        return sharedPref.getInt(key, 0);
    }

    /**
     * Sets string.
     *
     * @param key   the key
     * @param value the value
     */
    public void setInt(String key, int value) {
        SharedPreferences.Editor sharedPrefEditor = getSharedPref().edit();
        sharedPrefEditor.putInt(key, value);
        sharedPrefEditor.apply();
    }

}
