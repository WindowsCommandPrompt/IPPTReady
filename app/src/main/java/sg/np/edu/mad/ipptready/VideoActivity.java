package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VideoActivity extends AppCompatActivity {
    // Global variables
    private static final String DEBUG = "DEBUG"; // Debug tag
    Map<String, List<String>> videosList = new HashMap<String, List<String>>(); // Hashmap to store videos based on their categories
    String videoIds = ""; // String of video ids to be used to retrieve all video data at once

    // Store order of activities (to be displayed in video activity)
    ArrayList<String> orderOfActivities = new ArrayList<String>() {
        {
            add("Warm up");
            add("2.4km Run");
            add("Push-ups");
            add("Sit-ups");
            add("Cool down");
        }
    };
    ArrayList<Integer> noOfVideos = new ArrayList<>(); // Store number of videos in each category

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

        // Get video links from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("IPPTVideo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (!snapshot.isEmpty()) {
                        for (int i = 0; i < snapshot.size(); i++) {
                            for (DocumentSnapshot document : snapshot) {
                                // Turn into video object
                                List<String> videoArray = document.toObject(Video.class).video;
                                int number = 0; // variable for number of videos for the category
                                if (document.getId().equals(orderOfActivities.get(i))) {
                                    videosList.put(orderOfActivities.get(i), videoArray);
                                    for (String videoId : videoArray) {
                                        videoIds += videoId + ","; // Add video id to string
                                        number += 1; // Increment number of videos
                                        Log.d(DEBUG, orderOfActivities.get(i) + ":" + videoId);
                                    }
                                    noOfVideos.add(number); // Store number of videos for the category
                                    break;
                                }
                            }
                        }

                        // remove last comma from videoIds string
                        videoIds = videoIds.substring(0, videoIds.length()-1);

                        // YouTube DATA API V3 query link to retrieve video data (video titles, video descriptions, video thumbnail links)
                        String jsonLink = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails,snippet&fields=items/snippet(title,description,thumbnails/medium/url),items/contentDetails/duration&key=" + WatchVideoActivity.youtubeAPIKEY + "&id=" + videoIds;
                        Log.d(DEBUG, jsonLink);
                        String jsonString = ""; // Json result to be stored in this string

                        // url object stores link to YouTube api query
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

                        // VideoAdapter initialized
                        VideoAdapter adapter = new VideoAdapter(videosList, jsonString, noOfVideos,VideoActivity.this);

                        // RecyclerView
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