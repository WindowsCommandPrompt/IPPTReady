package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VideoActivity extends AppCompatActivity {
    private static final String DEBUG = "DEBUG";
    Map<String, List<String>> videosList = new HashMap<String, List<String>>();
    String videoIds = "";
    ArrayList<String> orderOfActivities = new ArrayList<String>() {
        {
            add("Warm up");
            add("2.4km Run");
            add("Push-ups");
            add("Sit-ups");
            add("Cool down");
        }
    };
    ArrayList<Integer> noOfVideos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_video);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("IPPTVideo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (!snapshot.isEmpty()) {
                        for (int i = 0; i < snapshot.size(); i++) {
                            for (DocumentSnapshot document : snapshot) {
                                List<String> videoArray = document.toObject(Video.class).video;
                                int number = 0;
                                if (document.getId().equals(orderOfActivities.get(i))) {
                                    videosList.put(orderOfActivities.get(i), videoArray);
                                    for (String videoId : videoArray) {
                                        videoIds += videoId + ",";
                                        number += 1;
                                        Log.d(DEBUG, orderOfActivities.get(i) + ":" + videoId);
                                    }
                                    noOfVideos.add(number);
                                    break;
                                }
                            }
                        }

                        videoIds = videoIds.substring(0, videoIds.length()-1);

                        String jsonLink = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails,snippet&fields=items/snippet(title,thumbnails/medium/url),items/contentDetails/duration&key=AIzaSyCwAQeCpPkjrhV-e5Gh__Ny2njKlyiCP58&id=" + videoIds;
                        Log.d(DEBUG, jsonLink);
                        String jsonString = "";

                        URL url = null;
                        try {
                            url = new URL(jsonLink);
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        HttpURLConnection conn = null;
                        try {
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            int responseCode = conn.getResponseCode();
                            if (responseCode != 200) {
                                throw new RuntimeException("HttpResponseCode: " + responseCode);
                            } else {
                                Scanner scanner = new Scanner(url.openStream());
                                while (scanner.hasNext()) {
                                    jsonString += scanner.nextLine();
                                }
                                scanner.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        VideoAdapter adapter = new VideoAdapter(videosList, jsonString, noOfVideos,VideoActivity.this);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        RecyclerView recyclerView = findViewById(R.id.videoRecyclerView);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
        });
    }
}