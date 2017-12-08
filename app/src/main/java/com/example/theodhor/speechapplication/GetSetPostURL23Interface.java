package com.example.theodhor.speechapplication;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IT-004 on 17/10/2559.
 */
interface GetSetPostURL23Interface {
    void onDownloadFinished(Context activity, JSONArray result) throws JSONException;
    void onDownloadFinished(Context activity, JSONObject result) throws JSONException;
    void onDownloadFinished(Context activity, String result);
}
