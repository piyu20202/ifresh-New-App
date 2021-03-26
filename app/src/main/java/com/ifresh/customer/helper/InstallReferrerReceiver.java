package com.ifresh.customer.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class InstallReferrerReceiver  extends BroadcastReceiver {


    private static final String TAG = "InstallReferrerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {
            String referrer = "";
            Bundle extras = intent.getExtras();
            if (extras != null) {
                referrer = extras.getString("referrer");
            }
            Log.e(TAG, "Referal Code Is: " + referrer);
            //AppMethod.setStringPreference(context, AppConstant.PREF_REF_ID, referrer);
        }
    }
}
