package com.dapm.android.weatherapiapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID = "https://www.metaweather.com/api/location/";

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

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
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

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getCityForecastById(String cityId, VolleyResponseListener volleyResponseListener) {
        List<WeatherReportModel> report = new ArrayList<>();
        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        volleyResponseListener.onError("Something wrong");
                    }
                }
        );

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    // public List<WeatherReportModel> getCityForecastByName(String cityName) {
    // }
}
