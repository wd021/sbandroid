package com.shipbob.asynctasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

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
public class LoginUserAsyncTask extends AsyncTask<Object, Void, String> {

    Context context;
    ProgressDialog progDailog;
    String email;
    String password;

    public LoginUserAsyncTask(Context context, ProgressDialog progDailog, String email, String password) {
        this.context = context;
        this.progDailog = progDailog;
        this.email = email;
        this.password = password;
    }

    @Override
    protected void onPreExecute() {

        try {
            if (progDailog != null) {
                progDailog.setMessage("Fetching your Information...");
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(true);
                progDailog.show();
            }
        } catch (Error e) {
            e.printStackTrace();
        }


    }

    @Override
    protected String doInBackground(Object... params) {

        String url = ShipBobApplication.LOGIN_URL;
        JSONObject j = (JSONObject) params[0];


        
        return doLogin(url, j);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (progDailog != null) {
                progDailog.dismiss();
            }
            if (result.equals("true")) {

                GlobalMethods.setDefaultsForPreferences("login", "true", context);
                GlobalMethods.setDefaultsForPreferences("pass", password, context);
                new GetUserProfileAsyncTask(context, progDailog)
                        .execute(
                                ShipBobApplication.GET_USER_PROFILE + email);

            } else {

                new AlertDialog.Builder(context)
                        .setTitle("Warning")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Invalid login or password")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();


                            }
                        })
                        .create()
                        .show();

                //TO DO:: ERROR PAGE
            }

        } catch (Exception e) {
            Log.e(e.getLocalizedMessage());
            //TO DO:: ERROR PAGE
        }

    }

    private String doLogin(String url, JSONObject jsonObject) {


        return GlobalMethods.makePostRequestWithJsonObject(url, jsonObject);
    }


}
