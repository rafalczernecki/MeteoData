package com.rafalczernecki.meteodata.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.rafalczernecki.meteodata.MeteoDataApplication;
import com.rafalczernecki.meteodata.interfaces.MeasuresReceivable;
import com.rafalczernecki.meteodata.interfaces.ServerConnectionCheckReceiver;
import com.rafalczernecki.meteodata.entities.Server;
import com.rafalczernecki.meteodata.entities.SingleQuantityMeasure;
import com.rafalczernecki.meteodata.utils.SharedPreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommunicationHelper {
    public static final String URL_PROTOCOL = "http://";
    public static final String SERVER_PORT = "80";
    public static final String SERVER_METHOD_GET_DATA = "get_data";
    public static final String ARG_TYPE_OF_DATA = "typeOfData";
    public static final String ARG_START_DATE = "startDate";
    public static final String ARG_END_DATE = "endDate";
    public static final String SERVER_METHOD_TEST_CONNECTION = "test_connection";
    public static final String MEASURE_TIME_IN_MILLIS = "measureTimeInMillis";
    public static final String MEASURE_VALUE = "value";

    private SharedPreferencesHelper sharedPrefsHelper;
    private Context context;
    private MeasuresReceivable measuresReceiver;
    private ServerConnectionCheckReceiver serverConnectionCheckReceiver;

    public CommunicationHelper(Context context, MeasuresReceivable measuresReceiver,
                               ServerConnectionCheckReceiver serverConnectionCheckReceiver) {
        this.context = context;
        this.measuresReceiver = measuresReceiver;
        this.serverConnectionCheckReceiver = serverConnectionCheckReceiver;
        sharedPrefsHelper = new SharedPreferencesHelper(context);
    }

    public CommunicationHelper(Context context,
                               ServerConnectionCheckReceiver serverConnectionCheckReceiver) {
        this.context = context;
        this.serverConnectionCheckReceiver = serverConnectionCheckReceiver;
        sharedPrefsHelper = new SharedPreferencesHelper(context);
    }

    public void getMeteoDataFromServer(int typeOfData,
                                       Long startTimestampInMillis,
                                       Long endTimestampInMillis) {

        String url = URL_PROTOCOL + sharedPrefsHelper.getCurrentServerIpAddress() +
                ":" + SERVER_PORT + "/" + SERVER_METHOD_GET_DATA;
        final ArrayList<SingleQuantityMeasure> measures = new ArrayList<>();

        JSONObject postParams = new JSONObject();
        try {
            postParams.put(ARG_TYPE_OF_DATA, typeOfData);
            postParams.put(ARG_START_DATE, startTimestampInMillis);
            postParams.put(ARG_END_DATE, endTimestampInMillis);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray postParamsArray = new JSONArray();
        postParamsArray.put(postParams);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, postParamsArray,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        while (count < response.length()) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(count);
                                SingleQuantityMeasure measure = new SingleQuantityMeasure();
                                measure.setMeasureTimeInMillis(jsonObject.getLong(MEASURE_TIME_IN_MILLIS));
                                measure.setValue(jsonObject.getDouble(MEASURE_VALUE));
                                measures.add(measure);
                                count++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        measuresReceiver.receiveMeasures(measures);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                measuresReceiver.receiveMeasures(null);
            }
        });
        MeteoDataApplication.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public void testConnectionWithServer() {
        String url = URL_PROTOCOL + sharedPrefsHelper.getCurrentServerIpAddress() +
                ":" + SERVER_PORT + "/" + SERVER_METHOD_TEST_CONNECTION;

        StringRequest testConnectionRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        serverConnectionCheckReceiver.
                                receiveServerConnectionStatus(Server.CONNECTION_OK);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                serverConnectionCheckReceiver.
                        receiveServerConnectionStatus(Server.CONNECTION_ERROR);
            }
        });
        MeteoDataApplication.getInstance(context).addToRequestQueue(testConnectionRequest);
    }
}
