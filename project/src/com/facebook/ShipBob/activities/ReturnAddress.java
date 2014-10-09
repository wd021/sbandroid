package com.facebook.ShipBob.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;
import com.shipbob.GlobalMethods;
import com.facebook.ShipBob.R;
import com.shipbob.ShipBobApplication;
import com.shipbob.asynctasks.UpdateAddressAsyncTask;
import com.shipbob.models.UserProfileTable;
import com.shipbob.databasehandler.UserProfileTableDatabaseHandler;

import android.content.Context;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.stripe.android.Stripe;
import org.json.JSONObject;


public class ReturnAddress extends FragmentActivity {

    EditText enteredzipCode;
    EditText streetName;
    EditText streetAddress2;
/*    EditText state;
*/    EditText city;

    private Button saveAddress;

    String emailAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returnaddress);

        setTitle("Pick Up Location");

        emailAddress = GlobalMethods.getDefaultsForPreferences("email", getApplicationContext());

        enteredzipCode = (EditText) findViewById(R.id.zipCode);
        streetName = (EditText) findViewById(R.id.streetAddress);
        streetAddress2 = (EditText) findViewById(R.id.streetAddress2);

/*        state = (EditText) findViewById(R.id.state);
*/        city = (EditText) findViewById(R.id.city);


        saveAddress = (Button) findViewById(R.id.saveAddress);

        attachClickEventToSaveAddress();
        getActionBar().setDisplayHomeAsUpEnabled(true);

        UserProfileTableDatabaseHandler databaseHandler = new UserProfileTableDatabaseHandler(this);
        UserProfileTable user = databaseHandler.getAddress(emailAddress);

        if(user!=null){
        	if (user.getZipCode()!=null){
        	enteredzipCode.setText(user.getZipCode().equals("null") ? "" : user.getZipCode());
        	}
        	if(user.getStreetAddress1()!=null){
              	streetName.setText(user.getStreetAddress1().equals("null") ? "" : user.getStreetAddress1());
                      		
        	}
           city.setText("Chicago");
           
           if(user.getStreetAddress2()!=null){
           streetAddress2.setText(user.getStreetAddress2().equals("null")? "" : user.getStreetAddress2());
           }
        }
         }

    private void attachClickEventToSaveAddress() {

        saveAddress.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!validateContact()) return;

                try {
                    JSONObject j = new JSONObject();
                    j.put("StreetAddress2", streetAddress2.getText());
                    j.put("Street_Address1", streetName.getText());
                    j.put("City", city.getText());
                    j.put("ZipCode", enteredzipCode.getText());
/*                    j.put("State", state.getText());
*/                    j.put("EmailAddress", GlobalMethods.getDefaultsForPreferences("email", ReturnAddress.this));

                    UpdateAddressAsyncTask updateAddressAsyncTask = new UpdateAddressAsyncTask(ReturnAddress.this, new ProgressDialog(ReturnAddress.this));
                    updateAddressAsyncTask.execute(j);

                } catch (Exception e) {

                    com.shipbob.helpers.Log.e(e.getLocalizedMessage());


                }



            }
        });
    }

    public boolean validateContact() {
        boolean validateContactResult = true;
        String errorNames = null;

        if (enteredzipCode.getText().toString().length() == 0) {
            enteredzipCode.setError("Zip Code is required!");
            validateContactResult = false;
            errorNames = "ZipCode";
        } else if (!isNumeric(enteredzipCode.getText().toString())) {
            enteredzipCode.setError("Zip Code should be number!");
            validateContactResult = false;
            errorNames = "ZipCode";
        } else if (enteredzipCode.getText().toString().length() > 5) {
            enteredzipCode.setError("Wrong Zip Code!");
            validateContactResult = false;
            errorNames = "ZipCode";
        }

        if (streetName.getText().toString().length() == 0) {
            streetName.setError("Street Name is required!");
            validateContactResult = false;
            if (errorNames != null) {
                errorNames = errorNames.concat(", Street Name");
            } else errorNames = "Street Name";
        } else {
            streetName.setError(null);
        }


        if (city.getText().toString().length() == 0) {
            city.setError("Street Name is required!");
            validateContactResult = false;
            if (errorNames != null) {
                errorNames = errorNames.concat(", city");
            } else errorNames = "City";
        } else {
            streetName.setError(null);
        }


        if (!validateContactResult) {
            Toast.makeText(this, errorNames + " is required.",
                    Toast.LENGTH_SHORT).show();
        }
        return validateContactResult;


    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
