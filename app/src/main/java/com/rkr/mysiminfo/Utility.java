package com.rkr.mysiminfo;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.ClipboardManager;
import android.widget.Toast;

/**
 * Created by rkadurra on 30-Mar-16.
 */
public class Utility {
    Context myContext;

    public Utility(Context context) {
        myContext = context;
    }

    //test
    public boolean isMarshMore() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    /*function returns true if device is < marshmallow*/
    public boolean hasPermission(String permission) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (myContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        } else {
            return true;
        }
    }

    public void copyClipBoardContent(String data,String toastMsg){
        int sdk=Build.VERSION.SDK_INT;
        if(sdk <= Build.VERSION_CODES.HONEYCOMB) {

            ClipboardManager clipboard = (ClipboardManager) myContext.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(data);


        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) myContext.getSystemService(Context.CLIPBOARD_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ClipData clip = ClipData.newPlainText("Clip",data);
                clipboard.setPrimaryClip(clip);
            }
        }
        Toast.makeText(myContext.getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
    }

    public void finishTask(Activity activity){
        activity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        activity.finish();
    }

}
