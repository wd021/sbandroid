package com.shipbob;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.widget.FrameLayout;

import com.shipbob.helpers.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import com.shipbob.vendors.bitmaphandling.BitmapScaler;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GlobalMethods {

	public View contextView;
	public FragmentManager manager;
	
	public GlobalMethods(View c, FragmentManager manager){
    	this.contextView=c;
    	this.manager=manager;
}
	
	
//	public void hideMainFragment(){
//	       ImageView productInformation = (ImageView) this.contextView.findViewById(R.id.productThumbnail);
//
//	       Fragment map = manager.findFragmentById(R.id.map);
//	       FragmentTransaction transaction = manager.beginTransaction();
//	       transaction.hide(map).commit();
//
//            FrameLayout initiateShipBob=(FrameLayout)this.contextView.findViewById(R.id.initiateshipBob);
//	        initiateShipBob.setVisibility(View.INVISIBLE);
//
//	        TextView  streetAddress=(TextView)contextView.findViewById(R.id.streetAddress);
//	        streetAddress.setVisibility(View.INVISIBLE);
//
//	}
	
	public static void setDefaultsForPreferences(String key, String value, Context context) {
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    String existingValue=prefs.getString(key, null);
//	    if(existingValue==null){
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(key, value);
	    editor.commit();
//	    }
	}
	
	public static String getDefaultsForPreferences(String key, Context context) {
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    return preferences.getString(key, null);
	}
	  
	  
    
	public static String MakeandReceiveHTTPResponse(String Url)
	{
		StringBuilder stringBuilder = new StringBuilder();
		String Result = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(Url);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				Result = ReadHTTPResponse(response);

			} else {
				Log.e("Failed to download file");
			}
		} catch (Exception e) {
			Log.e(e.getLocalizedMessage());
		}
		return Result;
	}
	
	public static String MakeandReceiveHTTPPostResponse(String Url, List<NameValuePair> nameValuePairs)
	{
		StringBuilder stringBuilder = new StringBuilder();
		String Result = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Url);
		try {
			 JSONObject j = new JSONObject();
			
			    j.put("UserId", "2");
			   j.put("StripeCustomerId", "123dsdsds");
			   j.put("LastFour", "8302");  
			   j.put("CardExpiryMonth", "22");
			   j.put("CardExpiryYear", "21");
			   j.put("CardType", "Visa");
			
		  
			 
			httpPost.setHeader("Content-Type", "application/json");
			 StringEntity se = new StringEntity(j.toString(), HTTP.UTF_8);
			    se.setContentType("application/json");
			    httpPost.setEntity(se);

		        	
			HttpResponse response = httpClient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				Result = ReadHTTPResponse(response);

				
			} else {
				Log.e("Failed to download file");
			}
		} catch (Exception e) {
			Log.e(e.getLocalizedMessage());
		}
		return Result;
	}
	
	
	public static String makePostRequestWithJsonObject(String Url, JSONObject jsonObject) {
		StringBuilder stringBuilder = new StringBuilder();
		String Result = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Url);
		try {
			
			httpPost.setHeader("Content-Type", "application/json");
			 StringEntity se = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
			    se.setContentType("application/json");
			    httpPost.setEntity(se);

			HttpResponse response = httpClient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				Result = ReadHTTPResponse(response);
			} else {
				Log.e("Failed to download file");
			}
		} catch (Exception e) {
			Log.e(e.getLocalizedMessage());
		}
		return Result;
	}

    public static String makePostRequestWithJsonObject(String Url, JSONArray jsonArray) {
        StringBuilder stringBuilder = new StringBuilder();
        String Result = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Url);
        try {

            httpPost.setHeader("Content-Type", "application/json");
            StringEntity se = new StringEntity(jsonArray.toString(), HTTP.UTF_8);
            se.setContentType("application/json");
            httpPost.setEntity(se);

            HttpResponse response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                Result = ReadHTTPResponse(response);
            } else {
                Log.e("Failed to download file");
            }
        } catch (Exception e) {
            Log.e(e.getLocalizedMessage());
        }
        return Result;
    }
	
	public static String MakeandReceiveHTTPPostImages(String Url)
	{
		StringBuilder stringBuilder = new StringBuilder();
		String Result = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Url);
		 File pictureFileDir = getDir();
		    String photoFile="Picture_20144025114051.jpg";
		    String filename = pictureFileDir.getPath() + File.separator + photoFile;
		    
		    try {

	            File fileName = new File(filename);
	            final int THUMBNAIL_SIZE = 512;
	            
            BitmapScaler scaler = new BitmapScaler(fileName, THUMBNAIL_SIZE);

			Bitmap bitmap = scaler.getScaled();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		    byte[] data = baos.toByteArray();
		    String  strBase64 = Base64.encodeToString(data,0);
		    
		    
			 JSONObject j = new JSONObject();
			   j.put("ImageString" , strBase64);
			    j.put("UserContactsId", "2");
			    j.put("ShipOptions", "1");
			    j.put("UserId", "2");
			httpPost.setHeader("Content-Type", "application/json");
			 StringEntity se = new StringEntity(j.toString(), HTTP.UTF_8);
			    se.setContentType("application/json");
			    httpPost.setEntity(se);

		        	
			HttpResponse response = httpClient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				Result = ReadHTTPResponse(response);

				
			} else {
				Log.e("Failed to download file");
			}
		} catch (Exception e) {
			Log.e(e.getLocalizedMessage());
		}
		return Result;
	}


	  private static File getDir() {
		    File sdDir = Environment
		      .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		    return new File(sdDir, "CameraAPIDemo");
		  }

	public static  String ReadHTTPResponse(HttpResponse response) throws IllegalStateException, IOException
	{
		StringBuilder stringBuilder = new StringBuilder();
		HttpEntity entity = response.getEntity();
		InputStream inputStream = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
	}
		inputStream.close();
		return stringBuilder.toString();
}


	
}
