package com.example.theodhor.speechapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by NONAME on 6/24/2016.
 */
public class GetSetPostURL23 extends AsyncTask<String, String, String> {
    Context activity;
//    TableFlagment tableFlagment;
    ProgressDialog pd;
    final GetSetPostURL23Interface callback;
    List<NameValuePair> params;
    boolean wait_popup = true;
    private String url;

    GetSetPostURL23(Context activity, List<NameValuePair> params, GetSetPostURL23Interface callback){
        this.callback = callback;
        this.activity = activity;
        this.params = params;
    }
    GetSetPostURL23(Context activity,String url, List<NameValuePair> params, GetSetPostURL23Interface callback){
        this.callback = callback;
        this.activity = activity;
        this.params = params;
        this.url = url;
    }
    GetSetPostURL23(Context activity, List<NameValuePair> params, GetSetPostURL23Interface callback, boolean wait_popup){
        this.callback = callback;
        this.activity = activity;
        this.params = params;
        this.wait_popup = wait_popup;
    }

    public GetSetPostURL23(List<NameValuePair> params, GetSetPostURL23Interface callback) {
        this.callback = callback;
//        this.tableFlagment = tableFlagment;
        this.params = params;
    }
    public GetSetPostURL23(List<NameValuePair> params, GetSetPostURL23Interface callback, boolean wait_popup) {
        this.callback = callback;
//        this.tableFlagment = tableFlagment;
        this.params = params;
        this.wait_popup = wait_popup;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        if(this.wait_popup){
            pd = new ProgressDialog(activity);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

    }

    protected String doInBackground(String... params) {
        String response = "";
        HttpURLConnection connection = null;
        String txtUrl = "";
        try {
            txtUrl = activity.getString(R.string.LOCAL_URL);
        }catch (Exception e){
            txtUrl = "http://180.183.249.120:501/ktcfoodsop";
        }
        txtUrl = this.url;
//        Toast.makeText(activity,txtUrl, Toast.LENGTH_SHORT).show();
        //String txtUrl = activity.getString(R.string.URL);

        try {
            if(params[0].startsWith("/"))
                txtUrl = txtUrl + params[0];
            else
                txtUrl = txtUrl + "/" + params[0];

            URL url = new URL(txtUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(this.params));
            writer.flush();
            writer.close();
            os.close();

            int responseCode=connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
        }catch (MalformedURLException e){
            response = e.toString();
        }catch (Exception e){
            response = e.toString();
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(this.wait_popup==true){
            if (pd.isShowing()){
                pd.dismiss();
            }
        }
            try {
                JSONArray jsonArray = new JSONArray(result);
                this.callback.onDownloadFinished(activity,jsonArray);
                Toast.makeText(activity,"array", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    this.callback.onDownloadFinished(activity,jsonObject);
                    Toast.makeText(activity,"object", Toast.LENGTH_SHORT).show();
                }catch (Exception ex){
                    Toast.makeText(activity,"string.", Toast.LENGTH_SHORT).show();
                    this.callback.onDownloadFinished(activity,result);
                }

            }


    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
