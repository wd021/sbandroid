package com.facebook.ShipBob.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.crashlytics.android.Crashlytics;
import com.facebook.ShipBob.R;
import com.shipbob.ShipBobApplication;
import com.shipbob.helpers.Log;

import org.json.JSONObject;

import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.models.UserProfileTable;
import com.shipbob.databasehandler.UserProfileTableDatabaseHandler;
import com.shipbob.GlobalMethods;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class UpdatePhoneNumberActivity extends FragmentActivity implements View.OnClickListener {


    public static final int MENU_CLOSE_BUTTON = 0;

    private EditText phoneNumber;
    private FrameLayout saveButton;
    private UserProfileTable userProfile;

    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";

    Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addphonenumber);

        String title = "Update Profile";
        setTitle(title);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        saveButton = (FrameLayout) findViewById(R.id.savePhoneNumber);
        saveButton.setOnClickListener(this);
        phoneNumber = (EditText) this.findViewById(R.id.phoneNumberEditText);

        UserProfileTable existingUserProfile = GlobalDatabaseHandler.GetUserProfile(this, GlobalMethods.getDefaultsForPreferences("email", this));
        if (existingUserProfile != null) {
        	Crashlytics.log("profile is not null");

        	userProfile = existingUserProfile;
            String profilephoneNumber = userProfile.getPhoneNumber();
            if (profilephoneNumber!=null) {
            	if(!profilephoneNumber.equals("null"))
                phoneNumber.setText(profilephoneNumber);
            } else {
                phoneNumber.setText("");
            }
            
        }
        
        else{
        	Crashlytics.log("phone number is null");
        	phoneNumber.setText("");
        }
           
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

        menu.add(Menu.NONE, MENU_CLOSE_BUTTON, 0, "").setIcon(R.drawable.ic_action_krestik)
                .setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_ALWAYS | com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                break;

            case MENU_CLOSE_BUTTON:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Json Stuff
    public String makeUpdatePhoneNumberRequest(String URL, JSONObject j) {

        return GlobalMethods.makePostRequestWithJsonObject(URL, j);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.savePhoneNumber:

                if (!validateContact())
                    return;

                String emailAddress = GlobalMethods.getDefaultsForPreferences("email", getApplicationContext());
                if(emailAddress=="null"||emailAddress==null||emailAddress==""){
            		  Crashlytics.log("For some Reason there is no emailAddress on the Defaults");
                }
                UserProfileTable existingUserProfile = GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), emailAddress);
                if (existingUserProfile != null) {
                    userProfile = existingUserProfile;
                    userProfile.setPhoneNumber(phoneNumber.getText().toString());
                    long returnedInteger = new UserProfileTableDatabaseHandler(UpdatePhoneNumberActivity.this)
                            .updateUserProfilePhoneNumber(emailAddress, phoneNumber.getText().toString());
                    if (returnedInteger > 0) {
                        updatePhoneNumber(phoneNumber.getText().toString());
                    } else {
                        //To DO:: Error in Adding Data to SQLLITE. Create Record in SQLLITE Again.
                        updatePhoneNumber(phoneNumber.getText().toString());

                		  Crashlytics.log("For some Reason the updatingUserProfile on the database was null");
                    }

                } else {
          		  Crashlytics.log("For some Reason the existingUserProfile on the database was null");
                    //TO DO:: Make Sure userProfile is not null. Make a JSON Call to Server to populate the profile Information
                    updatePhoneNumber(phoneNumber.getText().toString());
                	Log.e("Make Sure userProfile is not null. Make a JSON Call to Server to populate the profile Information");
                }
                break;
        }
    }

    public boolean validateContact() {
        boolean validateContactResult = true;
        String errorNames = null;

        if (phoneNumber.getText().toString().trim().length() == 0) {
            phoneNumber.setError("Phone Number is required!");
            validateContactResult = false;
            errorNames = "Phone Number";
        }

        if (!validateContactResult) {
            Toast.makeText(this, errorNames + " is required.",
                    Toast.LENGTH_SHORT).show();
        }
        return validateContactResult;


    }

    private class JSONPOSTFeedTask extends AsyncTask<Object, Void, String> {
        ProgressDialog progDailog = new ProgressDialog(UpdatePhoneNumberActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog.setMessage("Saving Phone Number...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        protected String doInBackground(Object... params) {
            String url = (String) params[0];
            JSONObject j = (JSONObject) params[1];
            return makeUpdatePhoneNumberRequest(url, j);
        }

        protected void onPostExecute(String result) {
            try {
                progDailog.dismiss();
                JSONObject jsonResult = new JSONObject(result);
                String successResponse = jsonResult.getString("Success");
                String payload = jsonResult.getString("PayLoad");
                JSONObject userProfile = new JSONObject(payload);
                if (successResponse.equals("true")) {
                
                	
                    if (getIntent().getBooleanExtra("register", false)) {
                        Intent intent = new Intent(UpdatePhoneNumberActivity.this, UpdateCreditCardActivity.class);
                        intent.putExtra("register", true);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(UpdatePhoneNumberActivity.this,
                                MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("page", getIntent().getIntExtra("page", MainActivity.PROFILE_PAGE));
                        startActivity(intent);
                    }


                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(UpdatePhoneNumberActivity.this).create();
                    alertDialog.setMessage(jsonResult.getString("Error"));

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }

            } catch (Exception e) {

                Log.d(e.getLocalizedMessage());
                progDailog.dismiss();
                //TO DO Show Error Alert and Make User Save Again.
                // Show Alert.Class
            }
        }
    }

    public void updatePhoneNumber(String phoneNumber) {

        try {
            JSONObject j = new JSONObject();
            j.put("EmailAddress", userProfile.getEmail());
            j.put("PhoneNumber", phoneNumber);
            new JSONPOSTFeedTask()
                    .execute(
                            ShipBobApplication.UPDATE_PHONE_NUMBER, j);

        } catch (Exception e) {
           e.printStackTrace();
 		  Crashlytics.logException(e);

        }

    }
    
    private void SetSqlLitePhoneNumber(String phoneNumber,int userId){
    	   String emailAddress = GlobalMethods.getDefaultsForPreferences("email", getApplicationContext());
           if(emailAddress=="null"||emailAddress==null||emailAddress==""){
       		  Crashlytics.log("For some Reason there is no emailAddress on the Defaults");
           }
           UserProfileTable existingUserProfile = GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), emailAddress);
       
        	   
        	   UserProfileTable userProfileTable= new UserProfileTable();
        	   userProfileTable.setPhoneNumber(phoneNumber);
        	   userProfileTable.setUserId(userId);
               long returnedIndex=GlobalDatabaseHandler.insertUserProfileInSqlLite(getApplicationContext(), userProfileTable);
               
               if(returnedIndex<0)
               Crashlytics.log("For some Reason the updatingUserProfile on the database was null/couldnt insert new entries either");
         
       
    }
    
}