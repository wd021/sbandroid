package com.shipbob.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.shipbob.GlobalMethods;
import com.facebook.ShipBob.R;
import com.facebook.ShipBob.activities.CompleteProfile_CreditCard;
import com.facebook.ShipBob.activities.CompleteProfile_PhoneNumber;
import com.facebook.ShipBob.activities.MainActivity;
import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.models.UserProfileTable;

import org.json.JSONObject;

/**
 * Created by waldemar on 11.06.14.
 */
public class GetUserProfileAsyncTask extends AsyncTask<Object, Void, String> {

    Context context;
    ProgressDialog progDialog;

    public GetUserProfileAsyncTask(Context context, ProgressDialog progDialog) {
        this.context = context;
        this.progDialog = progDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDialog.setMessage("Saving your Information...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();
    }

    protected String doInBackground(Object... params) {
        String url = (String) params[0];
        return makeGetProfileRequest(url);
    }

    protected void onPostExecute(String result) {
        try {
            progDialog.dismiss();
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
                userProfileTable.setPhoneNumber(userProfile.getString("PhoneNumber"));
                userProfileTable.setLastFourCreditCard(userProfile.getString("LastFourCreditCard"));
                userProfileTable.setCreditCardType(userProfile.getString("CreditCardType"));
                userProfileTable.setCreditCardType(userProfile.getString("CreditCardType"));
                userProfileTable.setCardExpiryMonth(userProfile.getString("CardExpiryMonth"));
                userProfileTable.setCardExpiryYear(userProfile.getString("CardExpiryYear"));
                userProfileTable.setCity(userProfile.getString("City"));
                userProfileTable.setState(userProfile.getString("State"));
                userProfileTable.setZipCode(userProfile.getString("ZipCode"));
                userProfileTable.setCountry(userProfile.getString("Country"));
                userProfileTable.setStreetAddress1(userProfile.getString("Street_Address1"));

                GlobalDatabaseHandler.insertUserProfileInSqlLite(
                       context, userProfileTable);

                GlobalMethods.setDefaultsForPreferences("email", userProfileTable.getEmail(), context);

                if (userProfileTable.getPhoneNumber() == null || userProfileTable.getPhoneNumber() == "" || userProfileTable.getPhoneNumber().isEmpty() || userProfileTable.getPhoneNumber().equals("null")) {
                    MoveToCompletePhoneNumberActivity(false);
                } else if (userProfileTable.getLastFourCreditCard() == null || userProfileTable.getLastFourCreditCard().equals("null") || userProfileTable.getLastFourCreditCard().isEmpty() || userProfileTable.getLastFourCreditCard() == "") {
                    moveToCompleteCreditCardProfile(false);
                } else invokeMainActivity(context.getString(R.string.title1), R.drawable.icon);


            } else {

                //TO DO:: ERROR PAGE
            }

        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public String makeGetProfileRequest(String Url) {
        return GlobalMethods.MakeandReceiveHTTPResponse(Url);
    }

    private void MoveToCompletePhoneNumberActivity(boolean firstTimeLogin) {
        //setting the email in the shared preferences
        Intent intent = new Intent(context, CompleteProfile_PhoneNumber.class);
        intent.putExtra(MainActivity.EXTRA_TITLE, "PhoneNumber");
        intent.putExtra(MainActivity.EXTRA_RESOURCE_ID, R.drawable.icon);
        intent.putExtra(MainActivity.EXTRA_MODE, 0);
        if (firstTimeLogin) {
            intent.putExtra("FirstTimeLogin", "true");
        }
        context.startActivity(intent);
        // no animation of transition
//        context.overridePendingTransition(0, 0);
    }

    private void moveToCompleteCreditCardProfile(boolean firstTimeLogin) {

        Intent intent = new Intent(context, CompleteProfile_CreditCard.class);
        if (firstTimeLogin) {
            intent.putExtra("FirstTimeLogin", "true");
        }
        context.startActivity(intent);
    }

    private void invokeMainActivity(String title, int resId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);

    }
}
