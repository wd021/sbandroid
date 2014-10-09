package com.facebook.ShipBob.activities;

import com.facebook.ShipBob.R;
import com.shipbob.models.UserProfileTable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FirstTimeLogInComplete extends Activity {

    private EditText phoneNumber;
    private Button nextStepButton;
    private UserProfileTable userProfileTable;

    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";

    @Override
    public void onCreate(Bundle savedInstanceState) {


        // Remove notification bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstimelogincomplete);


        nextStepButton = (Button) findViewById(R.id.getStarted);
        AttachClickEventListener();

    }

    private void AttachClickEventListener() {
        nextStepButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MoveToCameraActivity();
            }
        });
    }

    ;

    private void MoveToCameraActivity() {
        Intent intent = new Intent(FirstTimeLogInComplete.this, MainActivity.class);
        intent.putExtra(EXTRA_TITLE, "Camera");
        intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
        intent.putExtra(EXTRA_MODE, 0);
        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);

    }
}
