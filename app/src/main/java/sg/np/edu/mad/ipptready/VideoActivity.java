package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference videoRef = db.collection("IPPTVideo");

        DocumentReference runRef = videoRef.document("2.4km Run");
        DocumentReference cooldownRef = videoRef.document("Cool down");
        DocumentReference pushupRef = videoRef.document("Push-ups");
        DocumentReference situpRef = videoRef.document("Sit-ups");
        DocumentReference warmupRef = videoRef.document("Warm up");

        runRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        run = (List<String>) document.get("video");
                    } else {
                        Log.d(DEBUG, "Unable to retrieve 2.4km run videos");
                        Toast.makeText(VideoActivity.this, "Unable to retrieve 2.4km run videos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(DEBUG, "get failed with ", task.getException());
                }
            }
        });

        cooldownRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        cooldown = (List<String>) document.get("video");
                    } else {
                        Log.d(DEBUG, "Unable to retrieve Cool Down videos");
                        Toast.makeText(VideoActivity.this, "Unable to retrieve Cool Down videos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(DEBUG, "get failed with ", task.getException());
                }
            }
        });

        pushupRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        pushup = (List<String>) document.get("video");
                    } else {
                        Log.d(DEBUG, "Unable to retrieve Push-Ups videos");
                        Toast.makeText(VideoActivity.this, "Unable to retrieve Push-Ups videos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(DEBUG, "get failed with ", task.getException());
                }
            }
        });

        situpRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        situp = (List<String>) document.get("video");
                    } else {
                        Log.d(DEBUG, "Unable to retrieve Sit-Ups videos");
                        Toast.makeText(VideoActivity.this, "Unable to retrieve Sit-Ups videos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(DEBUG, "get failed with ", task.getException());
                }
            }
        });

        warmupRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        warmup = (List<String>) document.get("video");
                    } else {
                        Log.d(DEBUG, "Unable to retrieve Warm Up videos");
                        Toast.makeText(VideoActivity.this, "Unable to retrieve Warm Up videos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(DEBUG, "get failed with ", task.getException());
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.videoRecyclerView);
        VideoAdapter adapter = new VideoAdapter(run, cooldown, pushup, situp, warmup, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}