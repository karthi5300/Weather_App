package com.karthick.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.karthick.weatherapp.data.ForecastData;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastActivity extends AppCompatActivity {

    String CITY_NAME = "Toronto";
    int DAYS = 11;
    String APP_ID = "ebfcac32bda131ed5a160f2757938396";

    private static DecimalFormat df = new DecimalFormat("0");


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        Intent receivedIntet = getIntent();
        int h = receivedIntet.getExtras().getInt("hours");

        setTitle(R.string.weather_forecast);

        constraintLayout = findViewById(R.id.forecast_root_layout);
        recyclerView = findViewById(R.id.recycler_view);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())     //GSON CONVERTER
                .build();

        OpenWeatherMapApi o = retrofit.create(OpenWeatherMapApi.class);

        Call<ForecastData> call = o.getForecast(CITY_NAME, DAYS, APP_ID);

        call.enqueue(new Callback<ForecastData>() {
            private static final String TAG = "tag";

            @Override
            public void onResponse(Call<ForecastData> call, Response<ForecastData> response) {

                if (!response.isSuccessful()) {
                    return;
                }

                ForecastData forecastData = response.body();

                //SET BACKGROUND BASED ON INTENT RECEIVED
                switch (h) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 23:
                        constraintLayout.setBackgroundResource(R.drawable.midnight);
                        break;
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                        constraintLayout.setBackgroundResource(R.drawable.morning);
                        break;
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                        constraintLayout.setBackgroundResource(R.drawable.noon);
                        break;
                    case 17:
                    case 18:
                        constraintLayout.setBackgroundResource(R.drawable.evening);
                        break;
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                        constraintLayout.setBackgroundResource(R.drawable.night);
                        break;
                }

                //GET UPCOMING DATE DETAILS
                ArrayList<String> days = new ArrayList<>();
                DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");

                for (int i = 1; i <= 10; i++) {
                    Date d = new Date(forecastData.getList()[i].getDt() * 1000);
                    days.add(i - 1, dateFormat.format(d));
                }

                //GET UPCOMING DATE WEATHER
                ArrayList<String> temperature = new ArrayList<>();

                for (int i = 1; i <= 10; i++) {
                    double tempC = convertToCelcius(forecastData.getList()[i].getTemp().getDay());    //CONVERTING KELVIN TO CELCIUS
                    double tempF = convertToFahrenheit(forecastData.getList()[i].getTemp().getDay());    //CONVERTING KELVIN TO FAHRENHEIT
                    String tempCValue = (df.format(tempC)) + "°C";  //ROUNDING OFF TO 1 DECIMAL PLACE
                    String tempFValue = (df.format(tempF)) + "F";    //ROUNDING OFF TO 1 DECIMAL PLACE
                    temperature.add(i - 1, tempCValue + " | " + tempFValue);
                }

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ForecastActivity.this);
                recyclerView.setLayoutManager(layoutManager);

                adapter = new ForecastAdapter(days, temperature);
                recyclerView.setAdapter(adapter);


            }

            private double convertToCelcius(double temp) {
                return temp - 273.15;
            }

            private double convertToFahrenheit(double temp) {
                return (((temp - 273) * 9 / 5) + 32);
            }

            @Override
            public void onFailure(Call<ForecastData> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}