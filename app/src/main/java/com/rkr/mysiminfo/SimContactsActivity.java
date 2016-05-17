package com.rkr.mysiminfo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SimContactsActivity extends AppCompatActivity {
    ListView lView;
    ListViewAdapter listViewAdapter;
    List<String> nameList;
    List<String> numberList;
    String[] contact_names;
    String[] phone_numbers;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list_view);
        linearLayout=(LinearLayout)findViewById(R.id.list_of_contacts_id);
        nameList=new ArrayList<String>();
        numberList=new ArrayList<String>();
        readContacts();
        contact_names=nameList.toArray(new String[nameList.size()]);
        phone_numbers=numberList.toArray(new String[numberList.size()]);
        lView = (ListView) findViewById(R.id.list_of_contacts);
        listViewAdapter = new ListViewAdapter(this, contact_names, phone_numbers,R.layout.display_contacts_list_item);
        lView.setAdapter(listViewAdapter);
        registerForContextMenu(lView);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openContextMenu(view);
            }
        });
    }

    private void readContacts() {
        try {
            Uri simUri = Uri.parse("content://icc/adn");
            Cursor cur = this.getContentResolver().query(simUri,
                    null, null, null, null);
            Log.i("MY_SIM_INFO", "total: " + cur.getCount());


            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String name = cur.getString(cur.getColumnIndex("name"));
                    name = name.replace("|", "");
                    String phone = "";
                        // get the phone number
                        phone = cur.getString(cur.getColumnIndex("number"));
                        phone.replaceAll("\\D", "");
                        phone.replaceAll("&", "");

                    nameList.add(name);
                    numberList.add(phone);
                }
            }else {
                showTextView();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        MenuInflater inflater = getMenuInflater();
        menu.setHeaderTitle(contact_names[info.position]);
        inflater.inflate(R.menu.context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String data=phone_numbers[info.position];
        switch (item.getItemId()) {
            case R.id.call:
                //tel: is needed
                String number="tel:"+data;
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(number));
                startActivity(intent);
                return true;
            case R.id.copy:
                Utility utility=new Utility(getApplicationContext());
                utility.copyClipBoardContent(data,data+" copied to clipboard");
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utility utility=new Utility(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), SelectionActivity.class);
        startActivity(intent);
        utility.finishTask(SimContactsActivity.this);

    }

    private void showTextView() {
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv=new TextView(this);
        tv.setLayoutParams(lparams);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setText("No Contacts in sim or sim not present");
        this.linearLayout.addView(tv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.generic_menu, menu);
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.action_share);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Utility utility=new Utility(getApplicationContext());
        switch (id){
            case R.id.action_help:
                //TODO
                break;
            case android.R.id.home:
                Intent i=new Intent(getApplicationContext(),SelectionActivity.class);
                startActivity(i);
                utility.finishTask(SimContactsActivity.this);
                return true;
            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
