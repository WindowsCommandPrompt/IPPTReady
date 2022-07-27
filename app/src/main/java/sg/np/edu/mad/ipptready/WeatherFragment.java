package sg.np.edu.mad.ipptready;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherFragment extends Fragment {

    private static final String DEBUG = "DEBUG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Weather API from weatherapi.com
        String jsonLink = "https://api.weatherapi.com/v1/current.json?key=1b3c36dd2a16400a9dd112118221607&q=Singapore&aqi=no";
        Log.d(DEBUG, jsonLink);
        String jsonString = ""; // Json result to be stored in this string

        // url object stores link to weather api query
        URL url = null;
        try {
            url = new URL(jsonLink);
        } catch (Exception e){
            e.printStackTrace();
        }

        // Establish connection to retrieve results
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                // If connection successful, utilise Scanner to get results line by line
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    jsonString += scanner.nextLine(); // append each line to jsonString
                }
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject current = obj.getJSONObject("current");
            double temperature = current.getDouble("temp_c");
            TextView tempTextView = view.findViewById(R.id.homeTemp);
            tempTextView.setText(String.valueOf(temperature)+"°C");
            JSONObject condition = current.getJSONObject("condition");
            String weather = condition.getString("text");
            String icon = condition.getString("icon");
            TextView weatherTextView = view.findViewById(R.id.homeWeather);
            weatherTextView.setText(weather);
            TextView weatherisnow = view.findViewById(R.id.currentweatherisnow);
            TextView weatherbrought = view.findViewById(R.id.weatherbroughttoyou);
            MaterialCardView weatherCard = view.findViewById(R.id.cardWeather);
            if (icon.contains("day")){
                weatherCard.setCardBackgroundColor(Color.parseColor("#E6568CD8"));
                weatherisnow.setTextColor(Color.parseColor("#000000"));
                weatherTextView.setTextColor(Color.parseColor("#000000"));
                tempTextView.setTextColor(Color.parseColor("#000000"));
            }
            if (icon.contains("night")){
                weatherCard.setCardBackgroundColor(Color.parseColor("#E63B5284"));
                weatherisnow.setTextColor(Color.parseColor("#FFFFFF"));
                weatherTextView.setTextColor(Color.parseColor("#FFFFFF"));
                weatherbrought.setTextColor(Color.parseColor("#FFFFFF"));
                tempTextView.setTextColor(Color.parseColor("#FFFFFF"));
            }
            ImageView iconImageView = view.findViewById(R.id.homeWeatherIcon);
            Picasso.with(getActivity()).load("https:" + icon).into(iconImageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}