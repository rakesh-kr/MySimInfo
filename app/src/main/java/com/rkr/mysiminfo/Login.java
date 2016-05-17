package com.rkr.mysiminfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    String userPassword = null;
    private String seed = "hfhgfdtrdhhjhgfytfytdtrdjhyugtff";
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Login.this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
        }
        setContentView(R.layout.activity_login);

        final EditText editText1, editText2, editText3, editText4;
        Button buttonLogin;
        editText1 = (EditText) findViewById(R.id.editText_1stDigit);
        editText2 = (EditText) findViewById(R.id.editText_2ndDigit);
        editText3 = (EditText) findViewById(R.id.editText_3rdDigit);
        editText4 = (EditText) findViewById(R.id.editText_4ttDigit);
        editText1.requestFocus();
        editText1.setSelectAllOnFocus(true);
        editText2.setSelectAllOnFocus(true);
        editText3.setSelectAllOnFocus(true);
        editText4.setSelectAllOnFocus(true);
        buttonLogin = (Button) findViewById(R.id.button_login);
        sharedPref= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (!sharedPref.getBoolean(MySharedPreferences.FirstTime, false)) {
            //create pin
            Toast.makeText(getApplicationContext(),"Create Passcode first",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), CreatePinActivity.class);
            startActivity(intent);
            finish();
        }
        if (editText1 != null) {
            editText1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        if (editText2 != null) {
                            editText2.requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        if (editText2 != null) {
            editText2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {

                        if (editText3 != null) {
                            editText3.requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        if (editText3 != null) {
            editText3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        if (editText4 != null) {
                            editText4.requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText1.length() == 0 || editText2.length() == 0 || editText3.length() == 0 || editText4.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Enter all 4 digit of passcode", Toast.LENGTH_LONG).show();
                    editText1.setText("");
                    editText2.setText("");
                    editText3.setText("");
                    editText4.setText("");
                    editText1.requestFocus();
                } else {
                    userPassword = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString();
                    if (!validatePasscode()) {
                        editText1.setText("");
                        editText2.setText("");
                        editText3.setText("");
                        editText4.setText("");
                        editText1.requestFocus();
                    }
                }

            }
        });

    }

    private boolean validatePasscode() {
        MySharedPreferences mySharedPreferences = new MySharedPreferences(getApplicationContext());
        String storedPasscode = mySharedPreferences.getMyStringSharedPrefs(MySharedPreferences.Password, sharedPref, true);
        if (storedPasscode!=null) {
            if (storedPasscode.equals(userPassword)) {
                Intent intent = new Intent(getApplicationContext(), SelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Password not matched", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(),"Please create 4 digit passcode",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getApplicationContext(),CreatePinActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Login.this.finish();
    }
}
