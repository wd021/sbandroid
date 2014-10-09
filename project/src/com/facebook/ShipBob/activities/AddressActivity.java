package com.facebook.ShipBob.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.shipbob.GlobalMethods;
import com.facebook.ShipBob.R;
import com.shipbob.ShipBobApplication;
import com.shipbob.adapters.AddressAdapter;
import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.models.ShippingInformationTable;
import com.shipbob.models.UserAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by waldemar on 05.06.14.
 */
public class AddressActivity extends Activity {

    Button AddNewContact;
    ShippingInformationTable shipInformation;

    ArrayList<UserAddress> userContacts = new ArrayList<UserAddress>();
    ArrayList<UserAddress> searchResult;


    ListView addressListView;
    AddressAdapter addressAdapter;

    EditText searchEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Where is the item going?");

        searchEditText = (EditText) findViewById(R.id.editTextSearchContact);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = searchEditText.getText().toString();

                if (searchString.length() > 0) {
                    searchAddress(searchString);
                } else {
                    for (UserAddress userAddress : userContacts) {
                        searchResult.add(userAddress);
                    }

                    addressAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addressListView = (ListView) findViewById(R.id.addressListView);

        searchResult = new ArrayList<UserAddress>(userContacts);
        addressAdapter = new AddressAdapter(this, R.layout.row_address, searchResult);

        addressListView.setAdapter(addressAdapter);

        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserAddress userAddress = (UserAddress) addressListView.getItemAtPosition(position);

                long recordId = createShippingRecord(userAddress);

                Intent intent = new Intent(AddressActivity.this, CameraActivity.class);
                intent.putExtra("recordId", recordId);
                startActivity(intent);
            }
        });

        String email = GlobalMethods.getDefaultsForPreferences("email", getApplicationContext());

        Long shipInformationTableId_long = getIntent().getLongExtra("shipInformationTableId", -1);
        int shipInformationTableId = shipInformationTableId_long.intValue();
        shipInformation = GlobalDatabaseHandler.getShippingInformationTableById(this, shipInformationTableId);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.addButton);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressActivity.this, DestinationAddress.class);
                startActivity(intent);
            }
        });

        retrieveUserContacts(email);

    }

    private long createShippingRecord(UserAddress userAddress) {

        long shipInformationTableId = GlobalDatabaseHandler.insertShippingInformation(this, userAddress);
        return shipInformationTableId;
    }

    public void retrieveUserContacts(String email) {
        new JSONFeedTask()
                .execute(ShipBobApplication.GET_USER_CONTACTS + email);

    }

    private class JSONFeedTask extends AsyncTask<String, Void, String> {
        ProgressDialog progDailog = new ProgressDialog(AddressActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog.setMessage("Loading Contacts");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                fillContactsTable(result);
                progDailog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
                progDailog.dismiss();
            }
        }
    }

    public String readJSONFeed(String URL) {
        return GlobalMethods.MakeandReceiveHTTPResponse(URL);
    }

    private void fillContactsTable(String contactsResult) {

        JSONObject jsonObject;


        try {
            jsonObject = new JSONObject(contactsResult);
            JSONArray userContactsJsonArray = jsonObject.getJSONArray("PayLoad");

            if(userContactsJsonArray.length()==0){
                TextView recentContactTextView = (TextView)findViewById(R.id.textViewRecentContact);
                recentContactTextView.setText("Tap to Add a Contact");
                recentContactTextView.setGravity(Gravity.CENTER);
                recentContactTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                recentContactTextView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    	 Intent intent = new Intent(AddressActivity.this, DestinationAddress.class);
                         startActivity(intent);
                    }
                });
                recentContactTextView.setPadding(15, 0, 5, 0);
                RelativeLayout  searchEditTextLayout = (RelativeLayout) findViewById(R.id.SearchContactLayout);
                searchEditTextLayout.setVisibility(View.GONE);
            }
            
            for (int i = 0; i < userContactsJsonArray.length(); i++) {
                JSONObject jUserContact = userContactsJsonArray.getJSONObject(i);
                UserAddress contact = new UserAddress();  // create a new object here
                contact.setUserContactId((int) jUserContact.getInt("UserContactId"));
                contact.setUserId((int) jUserContact.getInt("UserId"));
                contact.setStreetAddress1(jUserContact.getString("StreetAddress1"));
                contact.setStreetAddress2(jUserContact.getString("StreetAddress2"));
                contact.setHouseNumber(jUserContact.getString("HouseNumber"));
                contact.setZipCode(jUserContact.getString("ZipCode"));
                contact.setName(jUserContact.getString("Name"));
                contact.setState(jUserContact.getString("State"));
                contact.setCity(jUserContact.getString("City"));

                searchResult.add(contact);
                userContacts.add(contact);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       
        addressAdapter.notifyDataSetChanged();

    }

    private void searchAddress(String str) {
        int textLength = str.length();

        searchResult.clear();

        for (int i = 0; i < userContacts.size(); i++) {
            String title = userContacts.get(i).getName().toString();

            if (textLength <= title.length()) {
                if (str.equalsIgnoreCase(title.substring(0, textLength))) {
                    searchResult.add(userContacts.get(i));
                    continue;
                }
            }

        }

        addressAdapter.notifyDataSetChanged();
    }


}