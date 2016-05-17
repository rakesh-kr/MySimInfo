package com.rkr.mysiminfo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rkadurra on 19-Oct-15.
 */
public class MySharedPreferences {
    public static final String MyPREFERENCES = "Credentials";
    public static String Password = "myPasswordKey";
    public static String READ_PHONE_STATE = "READ_PHONE_STATE";
    public static String ShouldWeAskAgain = "shouldAskAgainKey";
    public static String AskedReadPhoneStateBefore = "askedReadPhoneStateBefore";
    public static String AskedAccessCourseLocationBefore = "askedAccessCourseLocationBefore";
    public static String FirstTime = "isFirstTime";
    public static String PhoneNumber="myPhoneNumberKey";
    private Context context;
    private String seed = "hfhgfdtrdhhjhgfytfytdtrdjhyugtff";

    public MySharedPreferences(Context myContext) {
        context = myContext;
    }

    public boolean addBoolenKeyValuePair(String key, boolean value, SharedPreferences sharedPreferences, boolean storeEncrypted) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addStringKeyValuePair(String key, String value, SharedPreferences sharedPreferences, boolean storeEncrypted) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            if (storeEncrypted) {
                value = SecurityActivity.encrypt(seed, value);

            }
            editor.putString(key, value);
            editor.commit();
            //for debug purpose
            //Toast.makeText(context.getApplicationContext(), "Key: " + key + " Value: " + value + " added", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getMyStringSharedPrefs(String key, SharedPreferences sharedPreferences, boolean isEncrypted) {
        String temp = sharedPreferences.getString(key, null);
        try {
            if (temp != null && isEncrypted) {
                temp = SecurityActivity.decrypt(seed, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    public boolean getMyBooleanSharedPrefs(String key, SharedPreferences sharedPreferences, boolean isEncrypted) {
        return (sharedPreferences.getBoolean(key, false));


    }
}
