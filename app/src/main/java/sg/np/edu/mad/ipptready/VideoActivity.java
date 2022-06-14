package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {
    private static final String DEBUG = "DEBUG";
    List<String> run;
    List<String> cooldown;
    List<String> pushup;
    List<String> situp;
    List<String> warmup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        for (DocumentSnapshot document : snapshot) {
                            List<String> videoArray = document.toObject(Video.class).video;
                            if (document.getId().equals("2.4km Run")) {
                                run = videoArray;
                            }
                            else if ((document.getId()).equals("Cool down")) {
                                cooldown = videoArray;
                            }
                            else if ((document.getId()).equals("Push-ups")) {
                                pushup = videoArray;
                            }
                            else if ((document.getId()).equals("Sit-ups")) {
                                situp = videoArray;
                            }
                            else if ((document.getId()).equals("Warm up")) {
                                warmup = videoArray;
                            }
                        }

                        VideoAdapter adapter = new VideoAdapter(run, cooldown, pushup, situp, warmup, VideoActivity.this);

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