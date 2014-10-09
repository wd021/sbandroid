package com.shipbob.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.facebook.ShipBob.activities.LoginActivity;
import com.facebook.ShipBob.activities.UpdatePhoneNumberActivity;
import com.shipbob.GlobalMethods;
import com.shipbob.ShipBobApplication;
import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.helpers.Log;
import com.shipbob.models.UserProfileTable;

import org.json.JSONObject;

/**
 * Created by waldemar on 11.06.14.
 */
public class RegisterAsyncTask extends AsyncTask<Object, Void, String> {

    Context context;
    ProgressDialog progDailog;
    String password;

    public RegisterAsyncTask(Context context, ProgressDialog progDailog, String password) {
        this.context = context;
        this.progDailog = progDailog;
        this.password = password;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progDailog.setMessage("Please waiting...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);
        progDailog.show();


    }

    @Override
    protected String doInBackground(Object... params) {

        String url = ShipBobApplication.REGISTER_USER;
        JSONObject j = (JSONObject) params[0];



        return doRegister(url, j);
    }

    private String doRegister(String url, JSONObject j) {

        return GlobalMethods.makePostRequestWithJsonObject(url, j);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            progDailog.dismiss();
            JSONObject jsonResult = new JSONObject(result);
            String successResponse = jsonResult.getString("Success");

            if (successResponse.equals("true")) {

                String payload = jsonResult.getString("PayLoad");
                JSONObject userProfile = new JSONObject(payload);
                // insert userProfileInformation into the database
                UserProfileTable userProfileTable = new UserProfileTable();
                userProfileTable.setFirstName(userProfile.getString("FirstName"));
                userProfileTable.setLastName(userProfile.getString("LastName"));
                userProfileTable.setUserId(userProfile.getInt("UserId"));
                userProfileTable.setEmail(userProfile.getString("Email"));

                GlobalDatabaseHandler.insertUserProfileInSqlLite(
                        context, userProfileTable);


                try {
                    JSONObject j = new JSONObject();
                    j.put("EmailAddress", userProfileTable.getEmail());
                    j.put("Password", password);

                    new LoginUserAsyncTask(context, new ProgressDialog(context), userProfileTable.getEmail(), password).execute(j);

                } catch (Exception e) {

                    Log.e(e.getLocalizedMessage());


                }


            } else {
                Log.e(successResponse.toString());
                Toast.makeText(context, jsonResult.getString("Error"), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(e.getLocalizedMessage());
            //TO DO:: ERROR PAGE
        }
    }


}
