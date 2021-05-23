package com.dapm.android.weatherapiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button btn_cityId, btn_getWeatherById, btn_getWeatherByName;
    EditText et_dataInput;
    ListView lv_weatherReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_cityId = findViewById(R.id.btn_getCityID);
        btn_getWeatherById = findViewById(R.id.btn_getWeatherByCityID);
        btn_getWeatherByName = findViewById(R.id.btn_getWeatherByCityName);

        et_dataInput = findViewById(R.id.et_dataInput);

        lv_weatherReport = findViewById(R.id.lv_weatherReports);

        btn_cityId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.metaweather.com/api/location/search/?query=" + et_dataInput.getText().toString();

                JsonArrayRequest jsonResult = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                String cityId = "";

                                try {
                                    JSONObject cityInfo = response.getJSONObject(0);
                                    cityId = cityInfo.getString("woeid");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(MainActivity.this, "City ID: " + cityId, Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Something wrong :(", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

                MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonResult);
            }
        });

        btn_getWeatherById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "You clicked me 2.", Toast.LENGTH_SHORT);
            }
        });

        btn_getWeatherByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputValue = et_dataInput.getText().toString();
                Toast.makeText(MainActivity.this, "You typed " + inputValue, Toast.LENGTH_SHORT);
            }
        });
    }
}