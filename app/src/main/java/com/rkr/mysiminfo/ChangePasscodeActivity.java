package com.rkr.mysiminfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by rkadurra on 06-Apr-16.
 */
public class ChangePasscodeActivity extends AppCompatActivity {
    Utility utility;
    Button updateBtn;
    EditText oldPasswordET,newPasswordET,confirmPasswordET;
    SharedPreferences sharedPreferences;
    MySharedPreferences mySharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_passcode);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "exception", Toast.LENGTH_SHORT).show();
        }
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mySharedPreferences=new MySharedPreferences(getApplicationContext());
        utility=new Utility(getApplicationContext());
        updateBtn=(Button)findViewById(R.id.change_button);
        oldPasswordET=(EditText)findViewById(R.id.enter_old_passcode_editTxt);
        newPasswordET=(EditText)findViewById(R.id.enter_new_passcode_editTxt);
        confirmPasswordET=(EditText)findViewById(R.id.reenter_passcode_editTxt);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword=newPasswordET.getText().toString().trim();
                String oldPassword=oldPasswordET.getText().toString().trim();
                String confirmPassword=confirmPasswordET.getText().toString().trim();
                String storedPassword;
                storedPassword=mySharedPreferences.getMyStringSharedPrefs(MySharedPreferences.Password,sharedPreferences,true);
                if (storedPassword!=null && newPassword.length()==4 && oldPassword.length()==4 && confirmPassword.length()==4){
                    if (storedPassword.equals(oldPassword) && newPassword.equals(confirmPassword)){
                        if (!mySharedPreferences.addStringKeyValuePair(MySharedPreferences.Password, newPassword, sharedPreferences, true)){
                            Toast.makeText(getApplicationContext(),"Failed to updated the new passcode",Toast.LENGTH_SHORT).show();
                        }else {
                            startNewActivity();
                        }
                    }else if (!storedPassword.equals(oldPassword)){
                        Toast.makeText(getApplicationContext(),"Enter correct old password",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"Passcode and confirm passcode must match",Toast.LENGTH_SHORT).show();
                    }
                }else if (newPassword.length()!=4 || oldPassword.length()!=4 || confirmPassword.length()!=4){
                    Toast.makeText(getApplicationContext(),"Please enter 4 digit passcode",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"Failed to get passcode",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void startNewActivity(){
        Intent intent=new Intent(getApplicationContext(),SettingsPreferences.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        utility.finishTask(ChangePasscodeActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startNewActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                startNewActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
