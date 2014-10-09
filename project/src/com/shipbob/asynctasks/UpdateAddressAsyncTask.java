package com.shipbob.asynctasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.facebook.ShipBob.activities.LoginActivity;
import com.facebook.ShipBob.activities.MainActivity;
import com.facebook.ShipBob.activities.ReturnAddress;
import com.facebook.ShipBob.activities.UpdatePhoneNumberActivity;
import com.shipbob.GlobalMethods;
import com.shipbob.ShipBobApplication;
import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.databasehandler.UserProfileTableDatabaseHandler;
import com.shipbob.helpers.Log;
import com.shipbob.helpers.RequestTask;
import com.shipbob.models.Option;
import com.shipbob.models.UserProfileTable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by waldemar on 12.06.14.
 */
public class UpdateAddressAsyncTask extends AsyncTask<Object, Void, String> {

    Context context;
    ProgressDialog progDailog;
    JSONObject pickUpLocation;
    
    public UpdateAddressAsyncTask(Context context, ProgressDialog progDailog) {
        this.context = context;
        this.progDailog = progDailog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

		progDailog.setMessage("Checking current location falls within our pick-up zone");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
    }

    @Override
    protected String doInBackground(Object... params) {
        String url = ShipBobApplication.UPDATE_ADDRESS;
        JSONObject j = (JSONObject) params[0];
        pickUpLocation=j;
        return getOptions(url, j);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            progDailog.dismiss();
       /*     JSONObject jsonResult = new JSONObject(result);
            String successResponse = jsonResult.getString("Success");
            String payload = jsonResult.getString("PayLoad");*/
            JSONObject userProfile = pickUpLocation;
            if (result.equals("true")) {
                // insert userProfileInformation into the database
                UserProfileTable userProfileTable = new UserProfileTable();
                userProfileTable.setCity(userProfile.getString("City"));
                userProfileTable.setStreetAddress1(userProfile.getString("Street_Address1"));
                userProfileTable.setZipCode(userProfile.getString("ZipCode"));
                /*   userProfileTable.setCountry(userProfile.getString("Country"));
                userProfileTable.setState(userProfile.getString("State"));
*/
                GlobalDatabaseHandler.insertUserProfileInSqlLite(context, userProfileTable);
                updatePickUpLocation();
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("page",MainActivity.MAIN_PAGE);
                context.startActivity(intent);

            } else {
            	
            	Builder alertdialogBox= new AlertDialog.Builder(this.context)
                .setTitle("Oops!")
				.setMessage("Sorry your current location is outside our pick-up zone.")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            	
            	alertdialogBox.create();
            	Activity activityRunning=(Activity)this.context;
            	if(!activityRunning.isFinishing()){
            		alertdialogBox.show();
            	}
                //TO DO:: ERROR PAGE
            }

        } catch (Exception e) {
            Log.e(e.getLocalizedMessage());
            //TO DO:: ERROR PAGE
        }
    }




    private String getOptions(String url, JSONObject jsonObject) {

        return GlobalMethods.makePostRequestWithJsonObject(url, jsonObject);
    }
    
    private void updatePickUpLocation() throws JSONException{
    	
    	  UserProfileTableDatabaseHandler databaseHandler = new UserProfileTableDatabaseHandler(this.context);

          ShipBobApplication.currentAddress = new com.shipbob.models.ReturnAddress();
          ShipBobApplication.currentAddress.setAddress(pickUpLocation.getString("Street_Address1"));
          ShipBobApplication.currentAddress.setStreetAddress2(pickUpLocation.getString("StreetAddress2"));
          ShipBobApplication.currentAddress.setCity(pickUpLocation.getString("City"));
/*                ShipBobApplication.currentAddress.setState(state.getText().toString());
*/                ShipBobApplication.currentAddress.setZip(Integer.valueOf(pickUpLocation.getString("ZipCode").toString()));

          UserProfileTable userProfileTable = new UserProfileTable();
          userProfileTable.setStreetAddress1(ShipBobApplication.currentAddress.getAddress());
/*                userProfileTable.setState(ShipBobApplication.currentAddress.getState());
*/                userProfileTable.setCity(ShipBobApplication.currentAddress.getCity());
          userProfileTable.setStreetAddress2(ShipBobApplication.currentAddress.getStreetAddress2());
          userProfileTable.setEmail(pickUpLocation.getString("EmailAddress"));
          userProfileTable.setZipCode(String.valueOf(ShipBobApplication.currentAddress.getZip()));

          databaseHandler.updateAddress(userProfileTable);
    }
    
    
    
}
