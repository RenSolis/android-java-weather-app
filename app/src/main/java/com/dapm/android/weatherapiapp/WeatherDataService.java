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

    public interface ForecastByIdResponse {
        void onResponse(List<WeatherReportModel> weatherReportModels);

        void onError(String message);
    }

    public void getCityForecastById(String cityId, ForecastByIdResponse forecastByIdResponse) {
        List<WeatherReportModel> weatherReportModels = new ArrayList<>();
        String url = QUERY_FOR_CITY_WEATHER_BY_ID + cityId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray consolidatedWeatherList = response.getJSONArray("consolidated_weather");

                            for (int i = 0; i < consolidatedWeatherList.length(); i++) {
                                WeatherReportModel oneDayWeather = new WeatherReportModel();
                                JSONObject consolidateWeather = (JSONObject) consolidatedWeatherList.get(i);

                                oneDayWeather.setId(consolidateWeather.getInt("id"));
                                oneDayWeather.setWeather_state_name(consolidateWeather.getString("weather_state_name"));
                                oneDayWeather.setWeather_state_abbr(consolidateWeather.getString("weather_state_abbr"));
                                oneDayWeather.setWind_direction_compass(consolidateWeather.getString("wind_direction_compass"));
                                oneDayWeather.setCreated(consolidateWeather.getString("created"));
                                oneDayWeather.setApplicable_date(consolidateWeather.getString("applicable_date"));
                                oneDayWeather.setMin_temp(consolidateWeather.getLong("min_temp"));
                                oneDayWeather.setMax_temp(consolidateWeather.getLong("max_temp"));
                                oneDayWeather.setThe_temp(consolidateWeather.getLong("the_temp"));
                                oneDayWeather.setWind_speed(consolidateWeather.getLong("wind_speed"));
                                oneDayWeather.setWind_direction(consolidateWeather.getLong("wind_direction"));
                                oneDayWeather.setVisibility(consolidateWeather.getLong("visibility"));
                                oneDayWeather.setAir_pressure(consolidateWeather.getInt("air_pressure"));
                                oneDayWeather.setHumidity(consolidateWeather.getInt("humidity"));
                                oneDayWeather.setPredictability(consolidateWeather.getInt("predictability"));

                                weatherReportModels.add(oneDayWeather);
                            }

                            forecastByIdResponse.onResponse(weatherReportModels);
                        } catch (JSONException error) {
                            error.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        forecastByIdResponse.onError("Something wrong");
                    }
                }
        );

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    public interface GetCityForecastByNameCallback {
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public void getCityForecastByName(String cityName, GetCityForecastByNameCallback getCityForecastByNameCallback) {
        getCityId(cityName, new VolleyResponseListener() {
            @Override
            public void onResponse(String cityId) {
                getCityForecastById(cityId, new ForecastByIdResponse() {
                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        getCityForecastByNameCallback.onResponse(weatherReportModels);
                    }

                    @Override
                    public void onError(String message) {
                        getCityForecastByNameCallback.onError("Something wrong");
                    }
                });
            }

            @Override
            public void onError(String message) {
                getCityForecastByNameCallback.onError("Something wrong");
            }
        });
    }
}
