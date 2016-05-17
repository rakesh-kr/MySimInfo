package com.rkr.mysiminfo;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class SettingsPreferences extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),SelectionActivity.class);
        Utility utility=new Utility(getApplicationContext());
        startActivity(intent);
        utility.finishTask(SettingsPreferences.this);
    }
}
