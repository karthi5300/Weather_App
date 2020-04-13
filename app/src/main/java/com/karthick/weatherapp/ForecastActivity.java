package com.karthick.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.karthick.weatherapp.data.ForecastData;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastActivity extends AppCompatActivity {

    /*    USER INPUT
     ***************
     */
    String CITY_NAME = "London";    //CITY NAME

    int DAYS = 11;
    String APP_ID = "ebfcac32bda131ed5a160f2757938396";

    private static DecimalFormat df = new DecimalFormat("0");

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ConstraintLayout constraintLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        Intent receivedIntet = getIntent();
        int h = Objects.requireNonNull(receivedIntet.getExtras()).getInt("hour");

        setTitle(R.string.weather_forecast);

        progressBar = findViewById(R.id.progress_bar);
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

                //RECEIVING THE RESPONSE
                ForecastData forecastData = response.body();

                if (forecastData != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                //SET BACKGROUND BASED ON INTENT RECEIVED
                if (h >= 7 && h <= 10) {
                    constraintLayout.setBackgroundResource(R.drawable.morning);
                } else if (h >= 11 && h <= 16) {
                    constraintLayout.setBackgroundResource(R.drawable.noon);
                } else if (h >= 17 && h <= 18) {
                    constraintLayout.setBackgroundResource(R.drawable.evening);
                } else if (h >= 19 && h <= 22) {
                    constraintLayout.setBackgroundResource(R.drawable.night);
                } else if ((h == 23 || (h >= 0 && h <= 6))) {
                    constraintLayout.setBackgroundResource(R.drawable.midnight);
                }

                /*    GET WEATHER DETAILS FOR 10 DAYS
                 ************************************
                 */

                //INITIALIZATION FOR DATES
                ArrayList<String> days = new ArrayList<>();
                DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");

                //INITIALIZATION FOR TEMPERATURE
                ArrayList<String> temperature = new ArrayList<>();

                //INITIALIZATION FOR ICON
                ArrayList<Drawable> icon = new ArrayList<>();
                int weatherId;

                //INITIALIZATION FOR TEMPERATURE DESCRIPTION
                ArrayList<String> description = new ArrayList<>();

                //LOOP FOR 10 TIMES
                for (int i = 1; i <= 10; i++) {
                    //GET DATES
                    assert forecastData != null;
                    Date d = new Date(forecastData.getList()[i].getDt() * 1000);
                    days.add(i - 1, dateFormat.format(d));

                    //GET TEMPERATURE
                    double tempCMax = convertToCelcius(forecastData.getList()[i].getTemp().getMax());    //CONVERTING MAX KELVIN TO CELCIUS
                    double tempCMin = convertToCelcius(forecastData.getList()[i].getTemp().getMin());    //CONVERTING MIN KELVIN TO CELCIUS
                    String tempFMaxValue = (df.format(tempCMax)) + "°C";  //ROUNDING OFF TO 1 DECIMAL PLACE
                    String tempFMinValue = (df.format(tempCMin)) + "°C";    //ROUNDING OFF TO 1 DECIMAL PLACE
                    temperature.add(i - 1, tempFMaxValue + " | " + tempFMinValue);

                    //GET ICON USING WEATHER ID
                    weatherId = forecastData.getList()[i].getWeather()[0].getId();

                    if (weatherId >= 200 && weatherId <= 299) {
                        icon.add(i - 1, getResources().getDrawable(R.drawable.thunderstorm));
                    } else if (weatherId >= 300 && weatherId <= 399) {
                        icon.add(i - 1, getResources().getDrawable(R.drawable.shower_rain));
                    } else if (weatherId >= 500 && weatherId <= 599) {
                        icon.add(i - 1, getResources().getDrawable(R.drawable.shower_rain));
                    } else if (weatherId >= 600 && weatherId <= 699) {
                        icon.add(i - 1, getResources().getDrawable(R.drawable.snow));
                    } else if (weatherId >= 700 && weatherId <= 799) {
                        icon.add(i - 1, getResources().getDrawable(R.drawable.mist));
                    } else if (weatherId == 800) {
                        icon.add(i - 1, getResources().getDrawable(R.drawable.d_clear_sky));
                    } else if (weatherId == 801) {
                        icon.add(i - 1, getResources().getDrawable(R.drawable.d_few_clouds));
                    } else if (weatherId == 802) {
                        icon.add(i - 1, getResources().getDrawable(R.drawable.scattered_clouds));
                    } else if (weatherId == 803 || weatherId == 804) {
                        icon.add(i - 1, getResources().getDrawable(R.drawable.broken_clouds));
                    }

                    //GET TEMPERATURE DESCRIPTION
                    String desc = forecastData.getList()[i].getWeather()[0].getDescription();
                    description.add(i - 1, desc);
                }

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ForecastActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);

                adapter = new ForecastAdapter(days, temperature, icon, description);
                recyclerView.setAdapter(adapter);

                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(recyclerView);
            }

            private double convertToCelcius(double temp) {
                return temp - 273.15;
            }

            @Override
            public void onFailure(Call<ForecastData> call, Throwable t) {
                if (t.getMessage() != null) {
                    Log.d(TAG, t.getMessage());
                }
            }
        });
    }
}