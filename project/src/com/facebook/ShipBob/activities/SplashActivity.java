package com.facebook.ShipBob.activities;

import com.crashlytics.android.Crashlytics;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.ShipBob.R;
import com.shipbob.ShipBobApplication;
import com.shipbob.asynctasks.ShipOptionsAsyncTask;
import com.shipbob.models.Option;

/**
 * Created by waldemar on 12.06.14.
 */
public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

		setContentView(R.layout.activity_splash);

		
        if (ShipBobApplication.isOnline(this)) {
           	if(ShipBobApplication.options!=null){
        		if(ShipBobApplication.options.size()==0){
        			BuildShippingOptionsList();
        		
        		}
    			MoveToLoginActivity();
        	}
        	else{
        		ShipBobApplication.options=new ArrayList<Option>();
    			BuildShippingOptionsList();
    			MoveToLoginActivity();
        	}
           	

        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("No internet connection.");
            builder1.setCancelable(false);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            SplashActivity.this.finish();
                        }
                    });


            AlertDialog alert11 = builder1.create();
            alert11.show();

        }


    }
    
    private void BuildShippingOptionsList(){
    	
    	ShipBobApplication.options.clear();
        Option defaultOption = new Option();
        defaultOption.setId(0);
        defaultOption.setName("SHIPPING OPTIONS");
        ShipBobApplication.options.add(defaultOption);
        
        
        Option option1 = new Option();
        option1.setId(1);
        option1.setName("Express- 1 Day");
        ShipBobApplication.options.add(option1);

        Option option2 = new Option();
        option2.setId(2);
        option2.setName("Priority- 3 Days");
        ShipBobApplication.options.add(option2);

        Option option3 = new Option();
        option3.setId(3);
        option3.setName("Ground- 3-7 Days");
        ShipBobApplication.options.add(option3);
    }
    
    private void MoveToLoginActivity(){
    	  Intent intent = new Intent(this, LoginActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(intent);
    }
}
