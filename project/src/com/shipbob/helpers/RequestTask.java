package com.shipbob.helpers;

import com.shipbob.helpers.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Created by waldemar on 30.05.14.
 */
public class RequestTask {
    public HttpResponse postData(String uri, List<NameValuePair> nameValuePairs) {

        HttpResponse responce = null;
        HttpPost httppost = new HttpPost(uri);
        HttpParams httpParameters = getHttpParams();
        HttpClient httpclient = new DefaultHttpClient(httpParameters);

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//            httppost.setHeader("Authorization", "Basic " + Base64.encodeBytes((Sh.SITE_USERNAME + ":" + Sh.SITE_PASSWORD).getBytes()));
            responce = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        return responce;
    }

    public HttpResponse getData(String uri) {

        HttpResponse responce = null;
        HttpGet httpGet = new HttpGet(uri);
        HttpParams httpParameters = getHttpParams();
        HttpClient httpclient = new DefaultHttpClient(httpParameters);

        try {

            responce = httpclient.execute(httpGet);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responce;
    }

    public HttpParams getHttpParams() {

        HttpParams httpParameters = new BasicHttpParams();

        return httpParameters;
    }


}
