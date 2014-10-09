package com.facebook.ShipBob.activities;


import com.facebook.ShipBob.R;
import com.shipbob.ShipBobApplication;
import com.shipbob.models.UserAddress;
import org.json.JSONObject;

import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.models.UserProfileTable;
import com.shipbob.databasehandler.UserProfileTableDatabaseHandler;
import com.shipbob.GlobalMethods;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class DestinationAddress extends FragmentActivity {

    private EditText streetAddress1;
    private TextView streetAddress2;
    private Button saveContact;
    private UserProfileTable userProfileTable;
    private Context context;
    EditText contactName;
    EditText apptNumber;
    EditText streetAddress;
    EditText city;
    EditText zipCode;
    EditText country;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinationaddress);
        context = DestinationAddress.this;

        String title = "Add a New Contact";
        setTitle(title);
        String email = GlobalMethods.getDefaultsForPreferences("email", getApplicationContext());
        userProfileTable = GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), email);

        contactName = (EditText) findViewById(R.id.contactName);
        apptNumber = (EditText) findViewById(R.id.apptNumber);
        streetAddress = (EditText) findViewById(R.id.streetName);
        city = (EditText) findViewById(R.id.city);
        zipCode = (EditText) findViewById(R.id.zipCode);
        country = (EditText) findViewById(R.id.Country);

        saveContact = (Button) findViewById(R.id.saveContact);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        AttachClickEventToSaveAddress();


    }

    private void AttachClickEventToSaveAddress() {

        saveContact.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                if (!validateContact())
                    return;

                insertNewContact();
            }
        });
    }

    public void insertNewContact() {

        try {

            UserAddress contact = new UserAddress();
            contact.setName(contactName.getText().toString());
            contact.setStreetAddress1(streetAddress.getText().toString());
            contact.setHouseNumber(apptNumber.getText().toString());
            contact.setCity(city.getText().toString());
            contact.setCountry(country.getText().toString());
            contact.setZipCode(zipCode.getText().toString());


            JSONObject j = new JSONObject();
            j.put("Name", contact.getName());
            j.put("StreetAddress1", contact.getStreetAddress1());
            j.put("HouseNumber", contact.getHouseNumber());
            j.put("City", contact.getCity());
            j.put("State", contact.getState());
            j.put("Country", contact.getCountry());
            j.put("ZipCode", contact.getZipCode());
            j.put("EmailAddress", userProfileTable.getEmail());

            new JSONPOSTFeedTask()
                    .execute(
                            ShipBobApplication.INSERT_CONTACT, j);

        } catch (Exception e) {
            // TO DO Show Error Alert and Make User Save Again.
            // Show Alert.Class
            e.printStackTrace();

        }

    }


    private class JSONPOSTFeedTask extends AsyncTask<Object, Void, String> {
        ProgressDialog progDailog = new ProgressDialog(DestinationAddress.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog.setMessage("Adding Contact...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        protected String doInBackground(Object... params) {
            String url = (String) params[0];
            JSONObject j = (JSONObject) params[1];
            return MakeInsertContactRequest(url, j);
        }

        protected void onPostExecute(String result) {
            try {
                progDailog.dismiss();
                JSONObject jsonResult = new JSONObject(result);
                String successResponse = jsonResult.getString("Success");
                String payload = jsonResult.getString("PayLoad");
                if (successResponse.equals("true")) {
                    MoveToNextActivity();
                } else {
                    // TO DO:: Move To Error Page
                }
            } catch (Exception e) {

                Log.d("JSONFeedTask", e.getLocalizedMessage());
                progDailog.dismiss();
                // TO DO Show Error Alert and Make User Save Again.
                // Show Alert.Class
            }
        }
    }

    // Json Stuff
    public String MakeInsertContactRequest(String URL, JSONObject j) {
        return GlobalMethods.makePostRequestWithJsonObject(URL, j);
    }

    public boolean validateContact() {
        boolean validateContactResult = true;
        String errorNames = null;
        EditText enteredzipCode = (EditText) findViewById(R.id.zipCode);
        if (enteredzipCode.getText().toString().length() == 0) {
            enteredzipCode.setError("Zip Code is required!");
            validateContactResult = false;
            errorNames = "ZipCode";
        }

        EditText contactName = (EditText) findViewById(R.id.contactName);
        if (contactName.getText().toString().length() == 0) {
            contactName.setError("Contact Name is required!");
            validateContactResult = false;
            if (errorNames != null) {
                errorNames = errorNames.concat(", Contact Name");
            } else errorNames = "Contact Name";
        } else {
            contactName.setError(null);
        }

        EditText streetName = (EditText) findViewById(R.id.streetName);
        if (streetName.getText().toString().length() == 0) {
            streetName.setError("Street Name is required!");
            validateContactResult = false;
            if (errorNames != null) {
                errorNames = errorNames.concat(", Street Name");
            } else errorNames = "Street Name";
        } else {
            streetName.setError(null);
        }

        EditText city = (EditText) findViewById(R.id.city);
        if (city.getText().toString().length() == 0) {
            city.setError("Street Name is required!");
            validateContactResult = false;
            if (errorNames != null) {
                errorNames = errorNames.concat(", city");
            } else errorNames = "City";
        } else {
            streetName.setError(null);
        }

        EditText country = (EditText) findViewById(R.id.Country);
        if (country.getText().toString().length() == 0) {
            country.setError("Country is required!");
            validateContactResult = false;
            if (errorNames != null) {
                errorNames = errorNames.concat(", Country");
            } else errorNames = "Country";
        } else {
            country.setError(null);
        }

        if (!validateContactResult) {
            Toast.makeText(this, errorNames + " is required.",
                    Toast.LENGTH_SHORT).show();
        }
        return validateContactResult;


    }

    private void MoveToNextActivity() {
        Intent intent = new Intent(DestinationAddress.this, AddressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
