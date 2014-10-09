package com.shipbob.asynctasks;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.shipbob.GlobalMethods;
import com.shipbob.ShipBobApplication;
import com.shipbob.adapters.OrderAdapter;
import com.shipbob.helpers.Log;
import com.shipbob.helpers.RequestTask;
import com.shipbob.models.Order;

/**
 * Created by waldemar on 12.06.14.
 */
public class AllOrdersAsyncTask extends AsyncTask<Object, Void, String> {

    Context context;
    ProgressDialog progDailog;
    ArrayList<Order> orders;
    OrderAdapter orderAdapter;

    public AllOrdersAsyncTask(Context context, ProgressDialog progDailog, ArrayList<Order> orders, OrderAdapter orderAdapter) {
        this.context = context;
        this.progDailog = progDailog;
        this.orders = orders;
        this.orderAdapter = orderAdapter;
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


    @Override
    protected String doInBackground(Object... params) {

        String url = ShipBobApplication.ALL_ORDERS + GlobalMethods.getDefaultsForPreferences("email", context);
        return getOrder(url);
    }

    @Override
    protected void onPostExecute(String json) {
        try {
            progDailog.dismiss();



            Log.d(json);


            if (!json.equals("")) {


                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray userContactsJsonArray = jsonObject.getJSONArray("PayLoad");

                    for (int i = 0; i < userContactsJsonArray.length(); i++) {
                        JSONObject jUserContact = userContactsJsonArray.getJSONObject(i);
                        Order order = new Order();  // create a new object here
                        order.setId((int) jUserContact.getInt("OrderId"));
                        order.setImageUrl((String) jUserContact.getString("ImageUrl"));
                        order.setContactName((String) jUserContact.getString("UserContactName"));
                        order.setContactId((int) jUserContact.getInt("UserContactsId"));
                        order.setStatus((String) jUserContact.getString("OrderStatus"));
                        order.setUserId((int) jUserContact.getInt("UserId"));
                        order.setShipOptions((String) jUserContact.getString("ShipOptions"));
                        order.setInsertDate((String) jUserContact.getString("InsertDate"));


                        orders.add(order);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                orderAdapter.notifyDataSetChanged();

            }



        } catch (Exception e) {
            Log.e(e.getLocalizedMessage());

            e.printStackTrace();
            //TO DO:: ERROR PAGE
        }
    }

    private String getOrder(String url) {
        String json = "";

        HttpResponse httpResponse = new RequestTask().getData(url);

        HttpEntity httpEntity = null;

        try {
            httpEntity = httpResponse.getEntity();
            json = EntityUtils.toString(httpEntity);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(e.getLocalizedMessage());

        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(e.getLocalizedMessage());
        }

        return json;
    }
}
