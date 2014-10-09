package com.shipbob.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.facebook.ShipBob.activities.CompleteOrderActivity;
import com.shipbob.GlobalMethods;
import com.shipbob.ShipBobApplication;
import com.shipbob.databasehandler.ShippingInformationDatabaseHandler;
import com.shipbob.helpers.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by waldemar on 12.06.14.
 */
public class OrderAsyncTask extends AsyncTask<Object, Void, String> {

    Context context;
    ProgressDialog progDailog;

    public OrderAsyncTask(Context context, ProgressDialog progDailog) {
        this.context = context;
        this.progDailog = progDailog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progDailog.setMessage("Finding the closest Ship Captain!...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();
    }


    @Override
    protected String doInBackground(Object... params) {
        String url = ShipBobApplication.MAKE_ORDER;
        JSONArray j = (JSONArray) params[0];
        return makeOrder(j, url);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            progDailog.dismiss();

            JSONObject jsonResult = new JSONArray(result).getJSONObject(0);
            String successResponse = jsonResult.getString("Success");
            String payload = jsonResult.getString("PayLoad");
            if (successResponse.equals("true")) {
                Intent intent = new Intent(context, CompleteOrderActivity.class);
              //  JSONArray payloadOrders = jsonResult.getJSONArray("PayLoad");
              /*  for (int i=0; i<payloadOrders.length(); i++) {*/
					JSONObject orderSummary = new JSONObject(payload);
                    int orderId = orderSummary.getInt("OrderId");
                	intent.putExtra("shipOrderId", orderId);
                //}

                ShippingInformationDatabaseHandler shippingInformation= new ShippingInformationDatabaseHandler(context);
                if (shippingInformation.clearShipItems() > 0) {

                }

                context.startActivity(intent);
            } else {
                Log.e( jsonResult.getString("Error").toString());
            }



        } catch (Exception e) {
            Log.e(e.getLocalizedMessage());
            //TO DO:: ERROR PAGE
        }
    }

    private String makeOrder(JSONArray jsonArray, String url) {


        return GlobalMethods.makePostRequestWithJsonObject(url, jsonArray);
    }
}
