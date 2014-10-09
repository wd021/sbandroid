package com.facebook.ShipBob.activities;

import android.widget.FrameLayout;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.facebook.ShipBob.R;
import com.shipbob.ShipBobApplication;
import com.shipbob.helpers.Log;
import org.json.JSONObject;

import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.models.UserProfileTable;
import com.shipbob.databasehandler.UserProfileTableDatabaseHandler;
import com.shipbob.GlobalMethods;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;


public class CompleteProfile_PhoneNumber extends SherlockActivity {

    public static final int MENU_CLOSE_BUTTON = 0;

    private EditText phoneNumber;
    private FrameLayout nextStepButton;
    private UserProfileTable userProfileTable;

    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";

    Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActionBar().setDisplayHomeAsUpEnabled(true);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.addphonenumber);

        String title = "My Profile";
        int resId = getIntent().getIntExtra(MainActivity.EXTRA_RESOURCE_ID, 0);
        setTitle(title);

        getActionBar().setDisplayHomeAsUpEnabled(false);

        nextStepButton = (FrameLayout) findViewById(R.id.savePhoneNumber);
        attachClickEventToSavePhoneNumber();
//		CrossImageButton();

        phoneNumber = (EditText) findViewById(R.id.phoneNumberEditText);

    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {

            case MENU_CLOSE_BUTTON:
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        this.menu = menu;

        menu.add(Menu.NONE, MENU_CLOSE_BUTTON, 0, "").setIcon(R.drawable.ic_action_krestik)
                .setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_ALWAYS | com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);
    }


    public UserProfileTable GetUserProfile(Context c, String email) {

        UserProfileTableDatabaseHandler userDbHandler = new UserProfileTableDatabaseHandler(c);
        UserProfileTable existingUserProfile = userDbHandler.getContact(email);
        return existingUserProfile;
    }


    private void attachClickEventToSavePhoneNumber() {

        nextStepButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                if (!validateContact())
                    return;

                String emailAddress = GlobalMethods.getDefaultsForPreferences("email", getApplicationContext());
                UserProfileTable existingUserProfile = GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), emailAddress);
                if (existingUserProfile != null) {
                    userProfileTable = existingUserProfile;
                    userProfileTable.setPhoneNumber(phoneNumber.getText().toString());
                    long returnedInteger = new UserProfileTableDatabaseHandler(CompleteProfile_PhoneNumber.this).updateUserProfilePhoneNumber(emailAddress, phoneNumber.getText().toString());
                    if (returnedInteger > 0) {
                        updatePhoneNumber(phoneNumber.getText().toString());
                    } else {
                        //To DO:: Error in Adding Data to SQLLITE. Create Record in SQLLITE Again.
                    }

                } else {
                    //TO DO:: Make Sure userProfileTable is not null. Make a JSON Call to Server to populate the profile Information
                }
            }
        });
    }


    //Json Stuff
    public String MakeUpdatePhoneNumberRequest(String URL, JSONObject j) {

        return GlobalMethods.makePostRequestWithJsonObject(URL, j);
    }

    private class JSONPOSTFeedTask extends AsyncTask<Object, Void, String> {
        ProgressDialog progDailog = new ProgressDialog(CompleteProfile_PhoneNumber.this);

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
            return MakeUpdatePhoneNumberRequest(url, j);
        }

        protected void onPostExecute(String result) {
            try {
                progDailog.dismiss();
                JSONObject jsonResult = new JSONObject(result);
                String successResponse = jsonResult.getString("Success");
                String payload = jsonResult.getString("PayLoad");
                JSONObject userProfile = new JSONObject(payload);
                if (successResponse.equals("true")) {
                    MoveToNextActivity();
                } else {
                    //TO DO Show Error Alert and Make User Save Again.
                    // Show Alert.Class
                }

            } catch (Exception e) {

                Log.e(e.getLocalizedMessage());
                progDailog.dismiss();
                //TO DO Show Error Alert and Make User Save Again.
                // Show Alert.Class
            }
        }
    }

    public void updatePhoneNumber(String phoneNumber) {

        try {
            JSONObject j = new JSONObject();
            j.put("EmailAddress", userProfileTable.getEmail());
            j.put("PhoneNumber", phoneNumber);
            new JSONPOSTFeedTask()
                    .execute(
                            ShipBobApplication.UPDATE_PHONE_NUMBER, j);

        } catch (Exception e) {
            //TO DO:: Start ErrorActivity
            e.printStackTrace();

        }

    }

    private void MoveToNextActivity() {

        String creditCard = userProfileTable.getLastFourCreditCard();
        if (creditCard == null || creditCard.equals("null") || creditCard.isEmpty() || creditCard == "")
            MoveToCompleteCreditCardProfile();

        else if (IsFirstTimeLogin()) MoveToIntroductionActivity();

        else MoveToCameraActivity();

    }

    private void MoveToCompleteCreditCardProfile() {

        Intent intent = new Intent(CompleteProfile_PhoneNumber.this, CompleteProfile_CreditCard.class);
        if (IsFirstTimeLogin()) {
            intent.putExtra("FirstTimeLogin", "true");
        }

        startActivity(intent);
    }

    private void MoveToCameraActivity() {
        Intent intent = new Intent(CompleteProfile_PhoneNumber.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);

    }

    private void MoveToIntroductionActivity() {
        Intent intent = new Intent(CompleteProfile_PhoneNumber.this,
                FirstTimeLogInComplete.class);
        intent.putExtra(EXTRA_TITLE, "Camera");
        intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
        intent.putExtra(EXTRA_MODE, 0);
        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);

    }


    private boolean IsFirstTimeLogin() {

        String firstTimeLogItent = getIntent().getStringExtra("FirstTimeLogin");
        if (firstTimeLogItent != null && firstTimeLogItent.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateContact() {
        boolean validateContactResult = true;
        String errorNames = null;
        EditText phoneNumber = (EditText) findViewById(R.id.phoneNumberEditText);
        if (phoneNumber.getText().toString().length() == 0) {
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

}
