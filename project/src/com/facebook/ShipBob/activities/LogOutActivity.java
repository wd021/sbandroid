package com.facebook.ShipBob.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.Session;
import com.facebook.android.Facebook;
import com.facebook.ShipBob.R;
import com.shipbob.helpers.Log;

public class LogOutActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        Button button = (Button) findViewById(R.id.logout);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFacebookLogout();
            }
        });


    }

    public void callFacebookLogout() {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        } else {

            session = new Session(this);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
            //clear your preferences if saved

        }

        Toast.makeText(this, "You are logged out!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 0);
        	


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
       super.onPause();
    }

    @Override
    public void onDestroy() {
         super.onDestroy();

    }


}