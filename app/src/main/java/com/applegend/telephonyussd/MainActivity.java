package com.applegend.telephonyussd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSION_CONSTANT = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonCreateStudent = (Button) findViewById(R.id.btnCreateStudent);

        buttonCreateStudent.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                TelephonyManager telephonyManager =
                        (TelephonyManager) getSystemService(
                                Context.TELEPHONY_SERVICE
                        );
                Handler handler = new Handler();

                if( ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_CONSTANT);
                }

                if( ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSION_CONSTANT);
                }

                SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
                List<SubscriptionInfo> subscriptionInfoList =
                        subscriptionManager != null ? subscriptionManager.getActiveSubscriptionInfoList() : null;

                if (subscriptionInfoList != null) {
                    for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                        int subscriptionId = subscriptionInfo.getSubscriptionId();
                        Log.e("Sims", "subscriptionId " + subscriptionId);
                    }
                }

                TelephonyManager simManager = null;
                if (telephonyManager != null) {
                    simManager = telephonyManager.createForSubscriptionId(1);
                }

                TelephonyManager.UssdResponseCallback responseCallback =
                        new TelephonyManager.UssdResponseCallback() {
                            @Override
                            public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                                super.onReceiveUssdResponse(telephonyManager, request, response);
                                Toast.makeText(
                                        MainActivity.this,
                                        "Success" + response.toString(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            @Override
                            public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                                super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                                Toast.makeText(
                                        MainActivity.this,
                                        "Error response" + String.valueOf(failureCode),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        };
                try {
                    Log.e("USSD", "Trying to send ussd request");
                    if (simManager != null) {
                        simManager.sendUssdRequest("*894#",
                                responseCallback, handler);
                    }
                } catch (Exception e) {
                    String msg = e.getMessage();
                    Log.e("DEBUG", e.toString());
                    e.printStackTrace();
                }
            }
        });
    }
}
