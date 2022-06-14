package sg.np.edu.mad.ipptready;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class WatchVideo extends YouTubeBaseActivity {
    Button playBtn;
    YouTubePlayerView youtubePlayerView;
    String youtubeAPIKEY = "AIzaSyCwAQeCpPkjrhV-e5Gh__Ny2njKlyiCP58";
    TextView videoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("Title");
        String videoId = bundle.getString("Video Id");

        playBtn = findViewById(R.id.watchVideoButton);
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        videoTitle = findViewById(R.id.videoPlayerTitle);
        videoTitle.setText(title);

        YouTubePlayer.OnInitializedListener initializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(), "Failed to play video.", Toast.LENGTH_SHORT).show();
            }
        };

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youtubePlayerView.initialize("AIzaSyAvNGZVAdLBll7AwyxEdFaZBfB-0QDsHMU", initializedListener);
            }
        });
    }
}