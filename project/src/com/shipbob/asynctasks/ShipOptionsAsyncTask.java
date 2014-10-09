package com.shipbob.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.facebook.ShipBob.activities.CompleteOrderActivity;
import com.facebook.ShipBob.activities.LoginActivity;
import com.facebook.ShipBob.activities.MainActivity;
import com.shipbob.GlobalMethods;
import com.shipbob.ShipBobApplication;
import com.shipbob.helpers.Log;
import com.shipbob.helpers.RequestTask;
import com.shipbob.models.Option;
import com.shipbob.models.Order;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by waldemar on 12.06.14.
 */
public class ShipOptionsAsyncTask extends AsyncTask<Object, Void, HttpResponse> {

    Context context;
    ProgressDialog progDailog;

    public ShipOptionsAsyncTask(Context context, ProgressDialog progDailog) {
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
    }

    @Override
    protected HttpResponse doInBackground(Object... params) {
        String url = ShipBobApplication.SHIP_OPTIONS;
        return getOptions(url);
    }

    @Override
    protected void onPostExecute(HttpResponse response) {
        String json = "";

        HttpEntity httpEntity = null;

        try {
            httpEntity = response.getEntity();
            json = EntityUtils.toString(httpEntity);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(e.getLocalizedMessage());

        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(e.getLocalizedMessage());
        }

        Log.d(json);


        if (!json.equals("")) {


            ShipBobApplication.options.clear();
            Option defaultOption = new Option();
            defaultOption.setId(0);
            defaultOption.setName("SHIPPING OPTIONS");
            ShipBobApplication.options.add(defaultOption);

            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray userContactsJsonArray = jsonObject.getJSONArray("PayLoad");

                for (int i = 0; i < userContactsJsonArray.length(); i++) {
                    JSONObject jUserContact = userContactsJsonArray.getJSONObject(i);
                    Option option = new Option();  // create a new object here

                    option.setId(jUserContact.getInt("ShipOptionsId"));
                    option.setName(jUserContact.getString("Name"));
                    option.setDescription(jUserContact.getString("Description"));

                    ShipBobApplication.options.add(option);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



        }

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);



    }

    private HttpResponse getOptions(String url) {

        return new RequestTask().getData(url);
    }
}
