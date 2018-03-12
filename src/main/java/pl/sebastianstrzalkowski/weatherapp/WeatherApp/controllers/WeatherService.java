package pl.sebastianstrzalkowski.weatherapp.WeatherApp.controllers;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

@org.springframework.stereotype.Service
public class WeatherService {
    private OkHttpClient client;
    private Response response;
    private String cityName;
    private String unit;

    public JSONObject getWeather() throws JSONException {

        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/weather?q=" + getCityName() + "&units=" + getUnit() + "&APPID=96b9b4385e117c15bc95dc8095505c0c")
                .build();

        try {
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONArray returnWeatherArray() throws JSONException {

        return getWeather().getJSONArray("weather");
    }

    public JSONObject returnMainObject() throws JSONException {

        return getWeather().getJSONObject("main");
    }

    public JSONObject returnWindObject() throws JSONException {

        return getWeather().getJSONObject("wind");
    }

    public JSONObject returnSunset() throws JSONException {

        return getWeather().getJSONObject("sys");
    }

    private String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    private String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
