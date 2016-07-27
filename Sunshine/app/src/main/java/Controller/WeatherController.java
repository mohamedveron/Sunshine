package Controller;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import com.example.esc.sunshine.MainActivityFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ESC on 7/23/2016.
 */
public class WeatherController {
  public  WeatherController(){

  }
    public static void getWeather(){

        new FetchWeatherTask().execute("94043");
    }

    private static class FetchWeatherTask extends AsyncTask<String,Void,String[]> {

        private String getReadableDate(long time){
            SimpleDateFormat date = new SimpleDateFormat("EEE DDD MM");
            return date.format(time);
        }

        private String formatHighLow(double high,double low){
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);
            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        private String[] jsonParser(String jsonStr,int days){
            try {
                JSONObject json = new JSONObject(jsonStr);
                JSONArray daysList = json.getJSONArray("list");
                Time dayTime = new Time();
                dayTime.setToNow();
                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
                dayTime = new Time();

                String[] result = new String[days];
                for(int i=0;i<days;i++){
                    String day;
                    String description;
                    String highAndLow;
                    JSONObject dayForcast = daysList.getJSONObject(i);
                    long dateTime;
                    dateTime = dayTime.setJulianDay(julianStartDay+i);
                    day = getReadableDate(dateTime);
                    JSONObject weatherObject = dayForcast.getJSONArray("weather").getJSONObject(0);
                    description = weatherObject.getString("main");
                    JSONObject temperatureObject = dayForcast.getJSONObject("temp");
                    double high = temperatureObject.getDouble("max");
                    double low = temperatureObject.getDouble("min");

                    highAndLow = formatHighLow(high, low);
                                 result[i] = day + " - " + description + " - " + highAndLow;
                          }
                for (String s : result) {
                                   Log.v("LOG_TAG", "Forecast entry: " + s);
                              }
                  return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader buffer = null;
            String result = null;
            try {
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=2e952ce2eab139ff8a5dbd1a97b6bd83");
                final String base_url = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String query_param = "q";
                final String format_param = "mode";
                final String units_param = "units";
                final String days_param = "cnt";
                final String appid_param = "APPID";
                Uri builduri = Uri.parse(base_url).buildUpon().appendQueryParameter(query_param,params[0])
                        .appendQueryParameter(format_param,"json").appendQueryParameter(units_param,"metric")
                        .appendQueryParameter(days_param,"7").appendQueryParameter(appid_param,"2e952ce2eab139ff8a5dbd1a97b6bd83").build();
                URL url = new URL(builduri.toString());
                connection = (HttpURLConnection)url.openConnection();
                InputStream stream  = connection.getInputStream();
                if(stream == null)
                    return null;
                buffer = new BufferedReader(new InputStreamReader(stream));
                StringBuffer sbuffer = new StringBuffer();
                String line;
                while((line = buffer.readLine())!= null)
                    sbuffer.append(line);
                if(sbuffer.length() == 0)
                    return null;
                result = sbuffer.toString();
                Log.v("here ", result);
                return jsonParser(result, 7);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(connection != null)
                    connection.disconnect();
                if(buffer != null)
                    try {
                        buffer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String[] result){
            //super.onPostExecute(result);
            MainActivityFragment.adapter.clear();
            for(String s : result)
                MainActivityFragment.adapter.add(s);
        }
    }
}
