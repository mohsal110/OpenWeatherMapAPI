package com.example.openwheathermapapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    EditText city_ed , country_ed;
    Button get_weather_btn;
    TextView result_tv;

    private String url = "https://api.openweathermap.org/data/2.5/weather";
    private String appid ="345a9a796cb0b940e03982ed5e3f3f33";

    DecimalFormat decimalFormat=new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //api.openweathermap.org/data/2.5/weather?q=London&appid={API key}
        init();

        get_weather_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestWeatherDetails(view);
            }
        });



    }


    private void requestWeatherDetails(View view){
        String tempURL="";
        String city=city_ed.getText().toString();
        String country=country_ed.getText().toString();
        if(!city.equals("")) {
            if (!country.equals("")) {
                tempURL = url + "?q=" + city + "," + country + "&appid=" + appid;
            } else{
                tempURL = url + "?q=" + city + "&appid=" + appid;
            }

        }else {
            Toast.makeText(this, "city can not be empty", Toast.LENGTH_SHORT).show();
        }

        StringRequest stringRequest=new StringRequest(Request.Method.POST, tempURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

//                Log.d("response",response);
//                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("weather");
                    JSONObject jsonObjectWeather=jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("main")+jsonObjectWeather.getString("description");

                    JSONObject jsonObjectMain=jsonObject.getJSONObject("main");
                    Double temperature=jsonObjectMain.getDouble("temp")-273.15;
                    Double feels_like=jsonObjectMain.getDouble("feels_like")-273.15;
                    float pressure=jsonObjectMain.getInt("pressure");
                    int humidity=jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind=jsonObject.getJSONObject("wind");
                    float wind_speed=jsonObjectWind.getInt("speed");
                    JSONObject jsonObjectCloud=jsonObject.getJSONObject("clouds");
                    int clouds=jsonObjectCloud.getInt("all");
                    JSONObject jsonObjectSys=jsonObject.getJSONObject("sys");
                    String country_name=jsonObjectSys.getString("country");
                    String city_name=jsonObject.getString("name");
                    String timezone=jsonObject.getString("timezone");

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    //System.out.println(formatter.format(date));

                    String output= "Current weather of "+city_name+"("+country_name+")"+
                            "\n weather description : "+description+
                            "\n temperature : "+ decimalFormat.format(temperature)+" C"+
                            "\n feels like : "+ decimalFormat.format(feels_like)+" C"+
                            "\n pressure : "+ decimalFormat.format(pressure)+" hp"+
                            "\n humidity : "+ decimalFormat.format(humidity)+" %"+
                            "\n speed of winds : "+ decimalFormat.format(wind_speed)+" m/s"+
                            "\n percentage of clouds : "+ decimalFormat.format(clouds)+" %"+
                            "\n timezone : "+ timezone+
                            "\n the time of report generation :"+formatter.format(date);

                    result_tv.setText(output);








                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void init(){
        city_ed=findViewById(R.id.city_ed);
        country_ed=findViewById(R.id.country_ed);
        get_weather_btn=findViewById(R.id.get_btn);
        result_tv=findViewById(R.id.result_tv);
    }
}