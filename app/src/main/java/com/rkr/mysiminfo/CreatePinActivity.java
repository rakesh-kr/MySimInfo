package com.rkr.mysiminfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by rkadurra on 28-Mar-16.
 */
public class CreatePinActivity extends AppCompatActivity {

    private String passcode, confirmPasscode,mobile_num;
    SharedPreferences sharedPreferences;
    MySharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        final EditText passcodeEditTxt, confirmPasscodeEditTxt,mobileEditTxt;
        Button createButton;
        passcodeEditTxt = (EditText) findViewById(R.id.enter_passcode_editTxt);
        confirmPasscodeEditTxt = (EditText) findViewById(R.id.reenter_passcode_editTxt);
        mobileEditTxt=(EditText)findViewById(R.id.phone_num_editTxt_id);
        createButton = (Button) findViewById(R.id.create_button);
        //sharedPreferences = getSharedPreferences(MySharedPreferences.MyPREFERENCES, MODE_PRIVATE);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mySharedPreferences = new MySharedPreferences(getApplicationContext());
        if (!sharedPreferences.getBoolean(MySharedPreferences.FirstTime, false)) {
            Toast.makeText(getApplicationContext(), "Pin not created..", Toast.LENGTH_SHORT).show();
        }
        //initialise
        mySharedPreferences.addBoolenKeyValuePair(MySharedPreferences.AskedReadPhoneStateBefore, false, sharedPreferences, false);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passcode = passcodeEditTxt.getText().toString().trim();
                confirmPasscode = confirmPasscodeEditTxt.getText().toString().trim();
                mobile_num=mobileEditTxt.getText().toString().trim();

                if (passcode.length() == confirmPasscode.length() && passcode.equals(confirmPasscode) && passcode.length() == 4) {
                    MySharedPreferences mySharedPreferences = new MySharedPreferences(getApplicationContext());
                    if (mobile_num.length()==10){
                        if (mySharedPreferences
                                .addStringKeyValuePair(MySharedPreferences.Password, passcode, sharedPreferences, true) &&
                            mySharedPreferences
                                    .addStringKeyValuePair(MySharedPreferences.PhoneNumber,mobile_num,sharedPreferences,false)){
                            if (mySharedPreferences.addBoolenKeyValuePair(MySharedPreferences.FirstTime, true, sharedPreferences, false)) {
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "is first time does not update", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Password/phone number did not update", Toast.LENGTH_SHORT).show();
                        }
                    }else if (mobile_num.length()==0){
                        if (mySharedPreferences
                                .addStringKeyValuePair(MySharedPreferences.Password, passcode, sharedPreferences, true))
                        {
                            if (mySharedPreferences.addBoolenKeyValuePair(MySharedPreferences.FirstTime, true, sharedPreferences, false)
                                    &&
                                    mySharedPreferences.addStringKeyValuePair(MySharedPreferences.PhoneNumber,"0000000000",sharedPreferences,false))
                            {
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "is first time does not update", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Password did not update", Toast.LENGTH_SHORT).show();
                        }
                    }else if (mobile_num.length()>0 && mobile_num.length()<10){
                        Toast.makeText(getApplicationContext(), "enter valid mobile number", Toast.LENGTH_SHORT).show();
                    }


                } else if (passcode.length() != 4) {
                    Toast.makeText(getApplicationContext(), "Please enter exactly 4 digits", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Passcodes not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
