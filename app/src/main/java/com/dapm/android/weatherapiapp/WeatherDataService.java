package com.dapm.android.weatherapiapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataService {

    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    Context context;
    String cityId;

    public WeatherDataService(Context ctx) {
        context = ctx;
    }

    public interface VolleyResponseListener {
        void onResponse(String cityId);

        void onError(String message);
    }

    public void getCityId(String cityName, VolleyResponseListener volleyResponseListener) {
        String url = QUERY_FOR_CITY_ID + cityName;

        JsonArrayRequest jsonResult = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cityId = "";
                        try {
                            JSONObject cityInfo = response.getJSONObject(0);
                            cityId = cityInfo.getString("woeid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        volleyResponseListener.onResponse(cityId);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        volleyResponseListener.onError("Something wrong");
                    }
                }
        );

        MySingleton.getInstance(context).addToRequestQueue(jsonResult);
    }

    // public List<WeatherReportModel> getCityForecastById(String cityId) {
    // }

    // public List<WeatherReportModel> getCityForecastByName(String cityName) {
    // }
}
