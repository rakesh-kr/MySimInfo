package com.rkr.mysiminfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkadurra on 28-Mar-16.
 */
public class SimInfoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String[] displayList = {"SIM state", "Country", "Operator Code", "Network", "Network Type","dBM Level","ASU Level","Signal Strength", "Serial No.", "Subscriber ID", "Voice Mail No.", "In Roaming", "SMS Capable",
            "Voice Capable"};
    String[] displayListActualItems;
    ListView lView;
    ListViewAdapter listViewAdapter;
    String simStatus = null;
    private View coordinatorLayoutView;
    MySharedPreferences mySharedPreferences;
    SharedPreferences sharedPreferences;
    int dBmlevel = -99999;
    int asulevel = -99999;
    String signalStrength="UNKNOWN";
    String whichNetwork="UNKNOWN";
    String dbmLevel="NA";
    String asuLevel="NA";
    SignalStrengthListener signalStrengthListener;
    Utility utility;
    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_siminfo_list_view);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();
        }

        //start the signal strength listener
        signalStrengthListener = new SignalStrengthListener();
        utility=new Utility(getApplicationContext());
        coordinatorLayoutView = findViewById(R.id.snackbarPosition);
        mySharedPreferences = new MySharedPreferences(getApplicationContext());
        sharedPreferences = getSharedPreferences(MySharedPreferences.MyPREFERENCES, MODE_PRIVATE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(signalStrengthListener, SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS | SignalStrengthListener.LISTEN_CELL_INFO);
        int simState = telephonyManager.getSimState();
        switch (simState) {

            case (TelephonyManager.SIM_STATE_ABSENT):
                simStatus = "SIM ABSENT";
                break;
            case (TelephonyManager.SIM_STATE_NETWORK_LOCKED):
                simStatus = "NETWORK LOCKED";
                break;
            case (TelephonyManager.SIM_STATE_PIN_REQUIRED):
                simStatus = "PIN REQUIRED";
                break;
            case (TelephonyManager.SIM_STATE_PUK_REQUIRED):
                simStatus = "PUK REQUIRED";
                break;
            case (TelephonyManager.SIM_STATE_UNKNOWN):
                simStatus = "UNKNOWN";
                Toast.makeText(getApplicationContext(), "Sim state unknown", Toast.LENGTH_SHORT).show();
                break;
            case (TelephonyManager.SIM_STATE_READY):
                simStatus = "SIM READY";

                break;
            default:
                Toast.makeText(getApplicationContext(), "Sim state unknown sorry", Toast.LENGTH_SHORT).show();
        }
        getSimInfo(telephonyManager);

    }



    private void getSimInfo(TelephonyManager telephonyManager) {
        List<String> simItems = new ArrayList<String>();
        simItems.add(simStatus);
        // Get the SIM country ISO code
        String simCountry = telephonyManager.getSimCountryIso();
        simItems.add(simCountry);

        // Get the operator code of the active SIM (MCC + MNC)
        String simOperatorCode = telephonyManager.getSimOperator();
        simItems.add(simOperatorCode);

        // Get the name of the SIM operator
        String simOperatorName = telephonyManager.getSimOperatorName();
        simItems.add(simOperatorName);

        //newwork type
        String networkType = null;
        networkType = getNetworkType(telephonyManager);
        simItems.add(networkType);

        //get signal strength
        getCellInfo(telephonyManager);


        if (dBmlevel!=-99999) {
            dbmLevel = String.valueOf(dBmlevel);
        }
        simItems.add(dbmLevel);


        if (asulevel!=-99999){
            asuLevel = String.valueOf(asulevel);
        }
        simItems.add(asuLevel);

        //SIGNAL STRENGTH
        simItems.add(signalStrength);

        // Get the SIM’s serial number
        String simSerial = null;
        try {
            simSerial = telephonyManager.getSimSerialNumber();
        } catch (SecurityException e) {
            simSerial = " ";
        }
        simItems.add(simSerial);

        String subscriberId = null;
        try {
            subscriberId = telephonyManager.getSubscriberId();
        } catch (SecurityException e) {
            subscriberId = " ";
        }
        simItems.add(subscriberId);

        String voiceMailNum = null;
        try {
            voiceMailNum = telephonyManager.getVoiceMailNumber();
        } catch (SecurityException e) {
            voiceMailNum = " ";
        }
        simItems.add(voiceMailNum);

        String isRaoming = null;
        isRaoming = telephonyManager.isNetworkRoaming() ? "YES" : "NO";
        simItems.add(isRaoming);

        String smsCapable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            smsCapable = telephonyManager.isSmsCapable() ? "YES" : "NO";
        } else {
            smsCapable = "NO";
        }
        simItems.add(smsCapable);

        String voiceCapale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            voiceCapale = telephonyManager.isVoiceCapable() ? "YES" : "NO";
        } else {
            voiceCapale = "NO";
        }
        simItems.add(voiceCapale);



        displayListActualItems = simItems.toArray(new String[simItems.size()]);
        lView = (ListView) findViewById(R.id.list_of_sim_info);
        listViewAdapter = new ListViewAdapter(this, displayList, displayListActualItems,R.layout.display_list_item);
        lView.setAdapter(listViewAdapter);
        lView.setOnItemClickListener(this);

    }

    private String calculateStrength(String type,int asu) {
        String level="NA";
        if (whichNetwork.equals("GSM")) {
            //Toast.makeText(getApplicationContext(),"gsm",Toast.LENGTH_SHORT).show();
            if (asu <= 2 || asu == 99) level = "NONE/UNKNOWN";
            else if (asu >= 12) level = "GREAT";
            else if (asu >= 8) level = "GOOD";
            else if (asu >= 5) level = "MODERATE";
            else level = "POOR";
        }else {
            //Toast.makeText(getApplicationContext(),"cdma",Toast.LENGTH_SHORT).show();
            if (asu >= -75) level = "GREAT";
            else if (asu >= -85) level = "GOOD";
            else if (asu >= -95) level = "MODERATE";
            else if (asu >= -100) level = "POOR";
            else level = "NONE/UNKNOWN";
        }

        return level;
    }

    private void getCellInfo(TelephonyManager telephonyManager) {
        List<CellInfo> cellInfoList = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cellInfoList = telephonyManager.getAllCellInfo();
            //Checking if list values are not null
            if (cellInfoList != null) {
                for (final CellInfo info : cellInfoList) {
                    if (info instanceof CellInfoGsm) {
                        //GSM Network
                        whichNetwork="GSM";
                        CellSignalStrengthGsm cellSignalStrength = ((CellInfoGsm) info).getCellSignalStrength();
                        dBmlevel = cellSignalStrength.getDbm();
                        asulevel = cellSignalStrength.getAsuLevel();
                    } else if (info instanceof CellInfoCdma) {
                        //CDMA Network
                        whichNetwork="CDMA";
                        CellSignalStrengthCdma cellSignalStrength = ((CellInfoCdma) info).getCellSignalStrength();
                        dBmlevel = cellSignalStrength.getDbm();
                        asulevel = cellSignalStrength.getAsuLevel();
                    } else if (info instanceof CellInfoLte) {
                        //LTE Network
                        whichNetwork="LTE";
                        CellSignalStrengthLte cellSignalStrength = ((CellInfoLte) info).getCellSignalStrength();
                        dBmlevel = cellSignalStrength.getDbm();
                        asulevel = cellSignalStrength.getAsuLevel();
                    } else if (info instanceof CellInfoWcdma) {
                        //WCDMA Network
                        whichNetwork="WCDMA";
                        CellSignalStrengthWcdma cellSignalStrength = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            cellSignalStrength = ((CellInfoWcdma) info).getCellSignalStrength();
                            dBmlevel = cellSignalStrength.getDbm();
                            asulevel = cellSignalStrength.getAsuLevel();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Unknown type of cell signal.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            signalStrength=calculateStrength(whichNetwork,asulevel);
        }


    }

    private String getNetworkType(TelephonyManager tempTm) {
        int netCode = tempTm.getNetworkType();
        String NetTypeStr;
        switch (netCode) {
            case 0:
                NetTypeStr = "UNKNOWN";
                break;
            case 1:
                NetTypeStr = "GPRS";
                break;
            case 2:
                NetTypeStr = "EDGE";
                break;
            case 3:
                NetTypeStr = "UMTS";
                break;
            case 4:
                NetTypeStr = "CDMA";
                break;
            case 5:
                NetTypeStr = "EVDO_0";
                break;
            case 6:
                NetTypeStr = "EVDO_A";
                break;
            case 7:
                NetTypeStr = "1xRTT";
                break;
            case 8:
                NetTypeStr = "HSDPA";
                break;
            case 9:
                NetTypeStr = "HSUPA";
                break;
            case 10:
                NetTypeStr = "HSPA";
                break;
            case 11:
                NetTypeStr = "iDen";
                break;
            case 12:
                NetTypeStr = "EVDO_B";
                break;
            case 13:
                NetTypeStr = "LTE";
                break;
            case 14:
                NetTypeStr = "eHRPD";
                break;
            case 15:
                NetTypeStr = "HSPA+";
                break;
            default:
                NetTypeStr = "UNKNOWN";
                break;
        }
        return NetTypeStr;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        telephonyManager.listen(signalStrengthListener, SignalStrengthListener.LISTEN_NONE);
        Intent intent = new Intent(getApplicationContext(), SelectionActivity.class);
        startActivity(intent);
        utility.finishTask(SimInfoActivity.this);
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

        //noinspection SimplifiableIfStatement
        switch (id) {
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
                Intent i = new Intent(getApplicationContext(), SelectionActivity.class);
                startActivity(i);
                Utility utility=new Utility(getApplicationContext());
                utility.finishTask(SimInfoActivity.this);
                return true;
            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String prepareDataForSharing() {
        int count = 0;
        String shareStr = "My Sim Details:*\n=================\n";
        for (String item : displayList) {
            if (displayListActualItems[count] == null) {
                shareStr += item + " : NA\n";
                count += 1;
            } else {
                shareStr += item + " : " + displayListActualItems[count++] + "\n";
            }
        }
        shareStr += "=================\n";
        shareStr += "Shared from : " + getApplicationInfo().loadLabel(getPackageManager()).toString() + "\n";
        shareStr += "* For security reason please delete after use\n";

        return shareStr;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (displayListActualItems[position]!=null) {
            utility.copyClipBoardContent(displayListActualItems[position], displayListActualItems[position] + " copied to clipboard");
        }else{
            Toast.makeText(getApplicationContext(),"Nothing to copy",Toast.LENGTH_SHORT).show();
        }
    }

    private class SignalStrengthListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrengths) {
            getCellInfo(telephonyManager);
            String networkType = null;
            networkType = getNetworkType(telephonyManager);
            displayListActualItems[4]=networkType;
            displayListActualItems[5]=String.valueOf(dBmlevel);
            displayListActualItems[6]=String.valueOf(asulevel);
            displayListActualItems[7]=signalStrength;


            listViewAdapter.notifyDataSetChanged();
            super.onSignalStrengthsChanged(signalStrengths);
        }
    }

    @Override
    protected void onPause() {
        telephonyManager.listen(signalStrengthListener, SignalStrengthListener.LISTEN_NONE);
        super.onPause();
    }

    @Override
    protected void onResume() {
        telephonyManager.listen(signalStrengthListener,SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS|SignalStrengthListener.LISTEN_CELL_INFO);
        super.onResume();
    }
}
