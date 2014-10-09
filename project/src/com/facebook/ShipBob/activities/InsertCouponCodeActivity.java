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

public class InsertCouponCodeActivity extends FragmentActivity implements View.OnClickListener {
	
    public static final int MENU_CLOSE_BUTTON = 0;

    private EditText couponText;
    private FrameLayout saveButton;
    private UserProfileTable userProfile;

    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";

    Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcouponcode);

        String title = "Insert Coupon Code";
        setTitle(title);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        saveButton = (FrameLayout) findViewById(R.id.saveCouponCode);
        saveButton.setOnClickListener(this);
        couponText = (EditText) this.findViewById(R.id.couponEditText);
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
    public String makeUpdateCouponCodeRequest(String URL, JSONObject j) {

        return GlobalMethods.makePostRequestWithJsonObject(URL, j);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveCouponCode:

                if (!validateCouponCode())
                    return;

                updateCouponCode(couponText.getText().toString());

                break;
        }
    }

    public boolean validateCouponCode() {
        boolean validateCouponCodeResult = true;
        String errorNames = null;

        if (couponText.getText().toString().trim().length() == 0) {
        	couponText.setError("Coupon Code is required!");
            validateCouponCodeResult = false;
            errorNames = "Coupon Code";
        }

        if (!validateCouponCodeResult) {
            Toast.makeText(this, errorNames + " is required.",
                    Toast.LENGTH_SHORT).show();
        }
        return validateCouponCodeResult;


    }

    private class JSONPOSTFeedTask extends AsyncTask<Object, Void, String> {
        ProgressDialog progDailog = new ProgressDialog(InsertCouponCodeActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog.setMessage("Adding Coupon Code...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        protected String doInBackground(Object... params) {
            String url = (String) params[0];
            JSONObject j = (JSONObject) params[1];
            return makeUpdateCouponCodeRequest(url, j);
        }

		protected void onPostExecute(String result) {
            try {
                progDailog.dismiss();
                JSONObject jsonResult = new JSONObject(result);
                
                String successResponse = jsonResult.getString("Success");
                String payload = jsonResult.getString("PayLoad");
                
                if (successResponse.equals("true")) {
                
                  String emailAddress = GlobalMethods.getDefaultsForPreferences("email", getApplicationContext());
                  UserProfileTable existingUserProfile = GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), emailAddress);
                  
                  if (existingUserProfile != null) {

                      userProfile = existingUserProfile;
                      userProfile.setCouponCode(couponText.getText().toString());
                      new UserProfileTableDatabaseHandler(InsertCouponCodeActivity.this).addUserProfileCouponCode(emailAddress, couponText.getText().toString());

                  }
                  
                  Intent intent = new Intent(InsertCouponCodeActivity.this,MainActivity.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  intent.putExtra("page", getIntent().getIntExtra("page", MainActivity.PROFILE_PAGE));
                  startActivity(intent);

                } else {
                	                    
                    final AlertDialog alertDialog = new AlertDialog.Builder(InsertCouponCodeActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Coupon Code Incorrect");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() 
                    {
                    	public void onClick(final DialogInterface dialog, final int which) {
                    		alertDialog.dismiss();
                    	}
                   	});                   
                   	alertDialog.show();
                
                }

            } catch (Exception e) {
            	
                progDailog.dismiss();
                //TO DO Show Error Alert and Make User Save Again.
                // Show Alert.Class
            }
        }
    }

    public void updateCouponCode(String couponCode) {
    	Log.e("updateCoufwe"+couponCode);
        try {
            JSONObject j = new JSONObject();
            j.put("UserId", userProfile.getUserId());
            j.put("Name", couponCode);
            new JSONPOSTFeedTask().execute(ShipBobApplication.INSERT_PROMO_CODE, j);
            
        } catch (Exception e) {
           e.printStackTrace();
 		  Crashlytics.logException(e);

        }

    }
    
    private void SetSqlLiteCouponCode(String couponCode,int userId){
    	   String emailAddress = GlobalMethods.getDefaultsForPreferences("email", getApplicationContext());
           if(emailAddress=="null"||emailAddress==null||emailAddress==""){
       		  Crashlytics.log("For some Reason there is no emailAddress on the Defaults");
           }
           UserProfileTable existingUserProfile = GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), emailAddress);
       
        	   UserProfileTable userProfileTable= new UserProfileTable();
        	   userProfileTable.setCouponCode(couponCode);
        	   userProfileTable.setUserId(userId);
               long returnedIndex=GlobalDatabaseHandler.insertUserProfileInSqlLite(getApplicationContext(), userProfileTable);
               
               if(returnedIndex<0)
               Crashlytics.log("For some Reason the updatingUserProfile on the database was null/couldnt insert new entries either");
         
       
    }
    
}