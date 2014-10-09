package com.shipbob.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.ShipBob.activities.UpdatePhoneNumberActivity;
import com.shipbob.GlobalMethods;
import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.models.UserProfileTable;
import com.shipbob.helpers.Log;

import org.json.JSONObject;

/**
 * Created by waldemar on 09.06.14.
 */
public class InsertNewUserJsonTask extends AsyncTask<Object, Void, String> {

    Context context;
    ProgressDialog progDailog;

    public InsertNewUserJsonTask(Context context, ProgressDialog progDailog) {
        this.context = context;
        this.progDailog = progDailog;

    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progDailog.setMessage("Fetching your Information...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }

    protected String doInBackground(Object... params) {
        String url = (String) params[0];
        JSONObject j = (JSONObject) params[1];
        return makeInsertNewUserUrlRequest(url, j);
    }

    protected void onPostExecute(String result) {
        try {
            progDailog.dismiss();
            JSONObject jsonResult = new JSONObject(result);
            String successResponse = jsonResult.getString("Success");
            String payload = jsonResult.getString("PayLoad");
            JSONObject userProfile = new JSONObject(payload);
            if (successResponse.equals("true")) {
                // insert userProfileInformation into the database
                UserProfileTable userProfileTable = new UserProfileTable();
                userProfileTable.setFirstName(userProfile.getString("FirstName"));
                userProfileTable.setLastName(userProfile.getString("LastName"));
                userProfileTable.setUserId(userProfile.getInt("UserId"));
                userProfileTable.setEmail(userProfile.getString("Email"));
                
                GlobalMethods.setDefaultsForPreferences("email", userProfileTable.getEmail(), context);

                long returnedIndex=GlobalDatabaseHandler.insertUserProfileInSqlLite(context, userProfileTable);
                
                if(returnedIndex<0){
                	Crashlytics.log("User not inserted into the SQLLite Database");
                }

                Toast.makeText(context, userProfileTable.getFirstName() + ": success", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, UpdatePhoneNumberActivity.class);
                intent.putExtra("register", true);
                context.startActivity(intent);

            } else {
                Log.e(successResponse.toString());
                //TO DO:: ERROR PAGE
            }

        } catch (Exception e) {
            Log.e(e.getLocalizedMessage());
            //TO DO:: ERROR PAGE
        }
    }

    public String makeInsertNewUserUrlRequest(String url, JSONObject jsonObject) {
        return GlobalMethods.makePostRequestWithJsonObject(url, jsonObject);
    }
//    private void setEmailSharedPrferences(GraphUser user) {
//
//        if (user != null) {
//            GlobalMethods.setDefaultsForPreferences("email", user.getProperty("email").toString(), context);
//            return;
//        }
//        if (facebookGraphUser != null) {
//            GlobalMethods.setDefaultsForPreferences("email", facebookGraphUser.getProperty("email").toString(), LoginActivity.this);
//        }
//    }
}
