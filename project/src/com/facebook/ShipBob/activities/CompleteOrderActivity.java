package com.facebook.ShipBob.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.shipbob.GlobalMethods;
import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.databasehandler.UserProfileTableDatabaseHandler;
import com.shipbob.models.UserProfileTable;
import com.facebook.ShipBob.R;

public class CompleteOrderActivity extends SherlockActivity {

    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";
    private UserProfileTable userProfile;

    Button trackOrdered;
    Button cancelOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        setContentView(R.layout.activity_completeorder);
        trackOrdered = (Button) findViewById(R.id.trackOrder);
        cancelOrder = (Button) findViewById(R.id.cancelOrder);

        FragmentManager manager = getFragmentManager();
        

        String title = "Shipping Order Received";
        int resId = getIntent().getIntExtra(EXTRA_RESOURCE_ID, 0);
        setTitle(title);
        
        String emailAddress = GlobalMethods.getDefaultsForPreferences("email", getApplicationContext());
        UserProfileTable existingUserProfile = GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), emailAddress);
        
        if (existingUserProfile != null) {

            userProfile = existingUserProfile;
            userProfile.setCouponCode(null);
            new UserProfileTableDatabaseHandler(CompleteOrderActivity.this).addUserProfileCouponCode(emailAddress, null);

        }
        
        trackOrdered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompleteOrderActivity.this, MainActivity.class);
                intent.putExtra("page", MainActivity.ORDER_PAGE);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	int orderId = getIntent().getIntExtra(
						"shipOrderId", -1);
            	
        		new JSONFeedTask()
        				.execute("http://shipbobapi.azurewebsites.net/api/Orders/CancelOrderForUser/?OrderId="
        						+ orderId);
            }
        });

    }

    public String readJSONFeed(String URL) {

        return GlobalMethods.MakeandReceiveHTTPResponse(URL);
    }

    private class JSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
            	 Intent intent = new Intent(CompleteOrderActivity.this, MainActivity.class);
                 intent.putExtra("page",MainActivity.ORDER_PAGE);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 startActivity(intent);

            } catch (Exception e) {
                Log.d("JSONFeedTask", e.getLocalizedMessage());
            }
        }
    }


    public void onPause() {
        super.onPause();
        this.finish();
    }

    public void onStop() {
        super.onStop();
        this.finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}
