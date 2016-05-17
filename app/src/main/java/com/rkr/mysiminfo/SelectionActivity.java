package com.rkr.mysiminfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;

public class SelectionActivity extends AppCompatActivity {
    String LOG_TAG = "MY_SIM_INFO_APP";
    String[] displayList = {"Sim information", "Sim Contacts", "Phone Information", "Settings"};
    Integer[] imageIds = {R.drawable.ic_sim, R.drawable.ic_contacts, R.drawable.ic_sim, R.drawable.ic_settings_selection};
    ListView lView;
    ListViewAdapterForImage listViewAdapterForImage;
    ListViewAdapterForSingleTextView listViewAdapterForSingleTextView;
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    Utility utility;
    private View coordinatorLayoutView;
    SharedPreferences sharedPreferences;
    MySharedPreferences mySharedPreferences;


    int resultCode = 0;
    private final static int READ_PHONE_STATE_RESULT_SIM = 100;
    private final static int READ_PHONE_STATE_RESULT_PHONE = 101;
    private final static int READ_SIM_CONTACTS_RESULT=103;

    private ArrayList<String> permissionsRejected;
    private ArrayList<String> permissionsSuccess;
    private ArrayList<String> needPermission;
    private String[] needPermissionString;
    boolean allGranted = true;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        //for navigation drawer
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        permissionsRejected = new ArrayList<>();
        permissionsSuccess = new ArrayList<>();
        needPermission = new ArrayList<>();
        utility = new Utility(this);
        mySharedPreferences = new MySharedPreferences(getApplicationContext());
        sharedPreferences = getSharedPreferences(MySharedPreferences.MyPREFERENCES, MODE_PRIVATE);
        coordinatorLayoutView = findViewById(R.id.snackbarPosition);
        lView = (ListView) findViewById(R.id.list_of_selection);
        listViewAdapterForImage = new ListViewAdapterForImage(this, displayList, imageIds);
        lView.setAdapter(listViewAdapterForImage);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myItemClickMethod(position);
            }
        });

        //for nav drawer
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void myItemClickMethod(int position) {
        switch (position) {
            case 0:
            {
                String[] permissions_list = {READ_PHONE_STATE, ACCESS_COARSE_LOCATION};
                resultCode = READ_PHONE_STATE_RESULT_SIM;
                permissions(permissions_list, "SIM", resultCode);
            }
            break;
            case 1: {
                String[] permissions_list = {READ_CONTACTS};
                resultCode = READ_SIM_CONTACTS_RESULT;
                permissions(permissions_list, "CONTACTS", resultCode);

            }
            break;
            case 2:
            {
                String[] permissions_list = {READ_PHONE_STATE, ACCESS_COARSE_LOCATION};
                resultCode = READ_PHONE_STATE_RESULT_PHONE;
                permissions(permissions_list, "PHONE", resultCode);
            }
            break;
            case 3:
                //settings
                startMyNewActivity("SETTINGS");
                break;
            default:
                break;

        }
    }

    private void addDrawerItems() {
        listViewAdapterForSingleTextView=new ListViewAdapterForSingleTextView(this,displayList,R.layout.display_single_text_list_item);
        mDrawerList.setAdapter(listViewAdapterForSingleTextView);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myItemClickMethod(position);
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Options");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void permissions(String[] perm, String what, int resCode) {
        if (!utility.isMarshMore()) {
            startMyNewActivity(what);
        }
        needPermission.clear();
        for (String permission : perm) {
            if (!utility.hasPermission(permission)) {
                needPermission.add(permission);
            }
        }
        Log.i(LOG_TAG, "Size = " + String.valueOf(needPermission.size()));
        if (needPermission.size() == 0) {
            startMyNewActivity(what);
        } else {
            needPermissionString = needPermission.toArray(new String[needPermission.size()]);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(needPermissionString, resCode);
            } else {
                Toast.makeText(getApplicationContext(), "Its not Marshmallow :)", Toast.LENGTH_SHORT).show();
                startMyNewActivity(what);
            }
        }
        Log.i(LOG_TAG, "Reached here");
    }


    private void startMyNewActivity(String type) {
        Intent intent = null;
        switch (type) {
            case "SIM":
                intent = new Intent(getApplicationContext(), SimInfoActivity.class);
                break;
            case "PHONE":
                intent = new Intent(getApplicationContext(), PhoneInfoActivity.class);
                break;
            case "CONTACTS":
                intent = new Intent(getApplicationContext(), SimContactsActivity.class);
                break;
            case "SETTINGS":
                intent = new Intent(getApplicationContext(),SettingsPreferences.class);
        }
        startActivity(intent);
        utility.finishTask(SelectionActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String type;
        switch (requestCode) {
            case READ_PHONE_STATE_RESULT_SIM:
                type = "SIM";
                decisionLogic(grantResults, permissions, type);
                break;
            case READ_PHONE_STATE_RESULT_PHONE:
                type = "PHONE";
                decisionLogic(grantResults, permissions, type);
                break;
            case READ_SIM_CONTACTS_RESULT:
                type="CONTACTS";
                decisionLogic(grantResults,permissions,type);
                break;


        }
    }

    private void decisionLogic(int[] grantResults, String[] permissions, String what) {
        int count = 0;
        permissionsSuccess.clear();
        permissionsRejected.clear();
        if (grantResults.length > 0) {
            for (int item : grantResults) {
                if (item == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "Permission granted for " + permissions[count]);
                    permissionsSuccess.add(permissions[count++]);

                } else {
                    Log.i(LOG_TAG, "Permission not granted for " + permissions[count]);
                    permissionsRejected.add(permissions[count++]);
                }
            }
        }
        if (permissionsRejected.size() > 0) {
            allGranted = false;
            showPostSnakbar();
        } else {
            startMyNewActivity(what);
        }
    }

    private void openSettings() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

    private void showPostSnakbar() {
        Snackbar
                .make(coordinatorLayoutView, "Open Settings to give permission?", Snackbar.LENGTH_LONG)
                .setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSettings();
                    }
                })
                .show();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            utility.copyClipBoardContent("", "Clipboard cleared for security reason");
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(mRunnable, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
