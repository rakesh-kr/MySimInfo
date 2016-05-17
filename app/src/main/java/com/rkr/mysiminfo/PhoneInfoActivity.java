package com.rkr.mysiminfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class PhoneInfoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String[] displayList = {"Line No.","Phone Type","IMEI","Model","Manufacturer","Android API Level","SDK Version"};
    String[] displayListActualItems;
    ListView lView;
    ListViewAdapter listViewAdapter;
    String simStatus = null;
    private View coordinatorLayoutView;
    MySharedPreferences mySharedPreferences;
    SharedPreferences sharedPreferences;
    Utility utility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_phoneinfo_list_view);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"exception",Toast.LENGTH_SHORT).show();
        }
        coordinatorLayoutView = findViewById(R.id.snackbarPosition);
        utility=new Utility(getApplicationContext());
        mySharedPreferences = new MySharedPreferences(getApplicationContext());
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        getPhoneInfo(telephonyManager);
    }

    private void getPhoneInfo(TelephonyManager telephonyManager) {
        List<String> simItems = new ArrayList<String>();
        String lineNum;
        lineNum = telephonyManager.getLine1Number();
        if (lineNum==null){
            lineNum="UNKNOWN";
        }
        if (lineNum.equals("NA") || lineNum.equals("UNKNOWN") || lineNum.isEmpty()){
            lineNum=mySharedPreferences.getMyStringSharedPrefs(MySharedPreferences.PhoneNumber,sharedPreferences,false);
            if (lineNum==null){
                lineNum="Please add";
                Toast.makeText(getApplicationContext(),"phone num",Toast.LENGTH_SHORT).show();
            }
        }
        simItems.add(lineNum);

        //phone type
        String phType=getPhoneType(telephonyManager);
        simItems.add(phType);
        if (phType.equals("CDMA")){
            displayList[1]="MEID";
        }
        String imei = null;
        try {
            imei = telephonyManager.getDeviceId();
        } catch (SecurityException e) {
            imei = "";
        }
        simItems.add(imei);

        String divName= Build.DEVICE;
        simItems.add(divName);

        String manf=Build.MANUFACTURER;
        simItems.add(manf);

        String androidVer=Build.VERSION.RELEASE;
        simItems.add(androidVer);

        String SDK=String.valueOf(Build.VERSION.SDK_INT);
        simItems.add(SDK);



        displayListActualItems = simItems.toArray(new String[simItems.size()]);
        lView = (ListView) findViewById(R.id.list_of_phone_info);
        listViewAdapter = new ListViewAdapter(this, displayList, displayListActualItems,R.layout.display_list_item);
        lView.setAdapter(listViewAdapter);
        lView.setOnItemClickListener(this);

    }


    String getPhoneType(TelephonyManager phonyManager){
        int phoneType = phonyManager.getPhoneType();
        switch(phoneType){
            case TelephonyManager.PHONE_TYPE_NONE:
                return "NONE";
            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM";

            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "SIP";
            default:
                return "UNKNOWN";
        }

    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),SelectionActivity.class);
        startActivity(intent);
        utility.finishTask(PhoneInfoActivity.this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.generic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = prepareDataForSharing();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.action_help:
                //TODO
                break;
            case android.R.id.home:
                Utility utility=new Utility(getApplicationContext());
                Intent i=new Intent(getApplicationContext(),SelectionActivity.class);
                startActivity(i);
                utility.finishTask(PhoneInfoActivity.this);
                return true;
            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String prepareDataForSharing() {
        int count=0;
        String shareStr="My Phone Details:*\n=================\n";
        for (String item : displayList) {
            if (displayListActualItems[count]==null){
                shareStr+=item+" : NA\n";
                count+=1;
            }else {
                shareStr += item + " : " + displayListActualItems[count++] + "\n";
            }
        }
        shareStr+="=================\n";
        shareStr+="Shared from : "+getApplicationInfo().loadLabel(getPackageManager()).toString()+"\n";
        shareStr+="* For security reason please delete after use\n";

        return  shareStr;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (displayListActualItems[position]!=null) {
            utility.copyClipBoardContent(displayListActualItems[position], displayListActualItems[position] + " copied to clipboard");
        }else{
            Toast.makeText(getApplicationContext(),"Nothing to copy",Toast.LENGTH_SHORT).show();
        }
    }
}
