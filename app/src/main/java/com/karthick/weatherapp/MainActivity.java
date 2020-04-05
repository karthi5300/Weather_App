package com.karthick.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.karthick.weatherapp.data.Weather;
import com.karthick.weatherapp.data.WeatherData;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String CITY_NAME = "Tirupur";
    String APP_ID = "ebfcac32bda131ed5a160f2757938396";
    private static DecimalFormat df = new DecimalFormat("0");
    boolean isCelcius = true;

    private ConstraintLayout mRootLayout;
    private TextView mCityName, mTempValue, mTempDescription, mWindSpeed, mWindDirection, mPressure, mHumidity, mDate;
    private ImageView mTempImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCityName = findViewById(R.id.city_name);
        mTempValue = findViewById(R.id.temperature_value);
        mTempDescription = findViewById(R.id.temperature_description);
        mWindSpeed = findViewById(R.id.wind_speed_value);
        mHumidity = findViewById(R.id.humidity_value);
        mTempImage = findViewById(R.id.temparature_image);
        mDate = findViewById(R.id.date);
        mRootLayout = findViewById(R.id.root_layout);
        mWindDirection = findViewById(R.id.wind_direction_value);
        mPressure = findViewById(R.id.pressure_value);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherMapApi openWeatherMapApi = retrofit.create(OpenWeatherMapApi.class);

        Call<WeatherData> call = openWeatherMapApi.getWeather(CITY_NAME, APP_ID);

        call.enqueue(new Callback<WeatherData>() {
            private static final String TAG = "WeatherApp";

            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {

                if (!response.isSuccessful()) {
                    mTempDescription.setText("Code : " + response.code());
                    return;
                }

                WeatherData weatherData = response.body();

                //SET CITYNAME
                if (weatherData.getName() != null) {
                    mCityName.setText(weatherData.getName());
                }

                //SET DATE
                Date expiry = new Date((weatherData.getDt()) * 1000);
                DateFormat dateFormat = new SimpleDateFormat("E, MMM dd, yyyy");
                mDate.setText(dateFormat.format(expiry));
                Log.d(TAG, expiry.toString());

                //SET BACKGROUND BASED ON TIME
                DateFormat hourFormat = new SimpleDateFormat("hh");
                int hour = Integer.parseInt(hourFormat.format(expiry));

                if (hour >= 7 && hour <= 10) {
                    mRootLayout.setBackgroundResource(R.drawable.morning);
                } else if (hour >= 11 && hour <= 16) {
                    mRootLayout.setBackgroundResource(R.drawable.noon);
                } else if (hour >= 17 && hour <= 18) {
                    mRootLayout.setBackgroundResource(R.drawable.evening);
                } else if (hour >= 19 && hour < 22) {
                    mRootLayout.setBackgroundResource(R.drawable.night);
                } else if ((hour >= 23 || (hour >= 0 && hour <= 6))) {
                    mRootLayout.setBackgroundResource(R.drawable.midnight);
                }

                //SET TEMPERATURE

                double tempC = convertToCelcius(weatherData.getMain().getTemp());    //CONVERTING KELVIN TO CELCIUS
                double tempF = convertToFahrenheit(weatherData.getMain().getTemp());    //CONVERTING KELVIN TO FAHRENHEIT
                String tempCValue = (df.format(tempC)) + "°C";  //ROUNDING OFF TO 1 DECIMAL PLACE
                String tempFValue = (df.format(tempF)) + "°F";    //ROUNDING OFF TO 1 DECIMAL PLACE
                mTempValue.setText(tempCValue);

                //TO TOGGLE DISPLAY IN FAHRENHEIT
                mTempValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isCelcius) {
                            mTempValue.setText(tempCValue);
                            isCelcius = false;
                        } else {
                            mTempValue.setText(tempFValue);
                            isCelcius = true;
                        }
                    }
                });

                //GET TEMPERATURE DESCRIPTION
                String description = weatherData.getWeather()[0].getDescription();

                if (description.equalsIgnoreCase("clear sky")) {
                    mTempDescription.setText(description);
                    mTempImage.setImageResource(R.drawable.clear_sky);
                } else if (description.equalsIgnoreCase("few clouds")) {
                    mTempDescription.setText(description);
                    mTempImage.setImageResource(R.drawable.few_clouds);
                } else if (description.equalsIgnoreCase("scattered clouds")) {
                    mTempDescription.setText(description);
                    mTempImage.setImageResource(R.drawable.scattered_clouds);
                } else if (description.equalsIgnoreCase("broken clouds")) {
                    mTempDescription.setText(description);
                    mTempImage.setImageResource(R.drawable.broken_clouds);
                } else if (description.equalsIgnoreCase("shower rain")) {
                    mTempDescription.setText(description);
                    mTempImage.setImageResource(R.drawable.shower_rain);
                } else if (description.equalsIgnoreCase("rain")) {
                    mTempDescription.setText(description);
                    mTempImage.setImageResource(R.drawable.rain);
                } else if (description.equalsIgnoreCase("thunderstorm")) {
                    mTempDescription.setText(description);
                    mTempImage.setImageResource(R.drawable.thunderstorm);
                } else if (description.equalsIgnoreCase("snow")) {
                    mTempDescription.setText(description);
                    mTempImage.setImageResource(R.drawable.snow);
                } else if (description.equalsIgnoreCase("mist")) {
                    mTempDescription.setText(description);
                    mTempImage.setImageResource(R.drawable.mist);
                }

                //SET WIND VALUE
                String windValue = String.valueOf(weatherData.getWind().getSpeed());
                mWindSpeed.setText(windValue);

                //SET WIND DIRECTION

                double degree = weatherData.getWind().getDegree();
                if ((degree >= 337.6 && degree <= 360   ) || (degree >= 0 && degree <= 22.5)) {
                    mWindDirection.setText("N ⇑");
                } else if (degree > 22.6 && degree <= 67.5) {
                    mWindDirection.setText("NE ⇗");
                } else if (degree >= 67.6 && degree <= 112.5) {
                    mWindDirection.setText("E ⇒");
                } else if (degree >= 112.6 && degree <= 157.5) {
                    mWindDirection.setText("SE ⇘");
                } else if (degree >= 157.6 && degree <= 202.5) {
                    mWindDirection.setText("S ⇓");
                } else if (degree >= 202.6 && degree <= 247.5) {
                    mWindDirection.setText("SW ⇙");
                } else if (degree >= 247.6 && degree <= 292.5) {
                    mWindDirection.setText("W ⇐");
                } else if (degree > 292.6 && degree <= 337.5) {
                    mWindDirection.setText("NW ⇖");
                }

                //SET HUMIDITY VALUE
                String humidityValue = String.valueOf(weatherData.getMain().getHumidity());
                mHumidity.setText(humidityValue);

                //SET PRESSURE VALUE
                String pressureValue = String.valueOf(weatherData.getMain().getPressure());
                mPressure.setText(pressureValue);
            }


            public double convertToCelcius(double temp) {
                return temp - 273.15;
            }

            public double convertToFahrenheit(double temp) {
                return (((temp - 273) * 9/5) + 32);
            }


            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                mTempDescription.setText(t.getMessage());
            }
        });
    }
}