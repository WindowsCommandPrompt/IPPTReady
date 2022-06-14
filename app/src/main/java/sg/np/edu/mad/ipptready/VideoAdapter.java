package sg.np.edu.mad.ipptready;

import android.content.Context;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {
    private static final String DEBUG = "DEBUG";
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    public List<String> run, cooldown, pushup, situp, warmup;
    int totalVideos = 0;
    ArrayList<Integer> noOfVideos;
    Context ctx;

    public VideoAdapter(List<String> Run, List<String> Cooldown, List<String> Pushup, List<String> Situp, List<String> Warmup, Context context) {
        run = Run;
        cooldown = Cooldown;
        pushup = Pushup;
        situp = Situp;
        warmup = Warmup;
        ctx = context;

        noOfVideos = new ArrayList<>();
        noOfVideos.add(warmup.size());
        noOfVideos.add(run.size());
        noOfVideos.add(pushup.size());
        noOfVideos.add(situp.size());
        noOfVideos.add(cooldown.size());

        for (int v : noOfVideos) {
            totalVideos += v;
        }
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_recyclerview, parent, false);
        return new VideoViewHolder(item);
    }

    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        String videoId = "";
        String videoType = "";
        int actualPosition = 0;
        if (position == totalVideos - 1) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.videoEntry.getLayoutParams();
            params.bottomMargin = 100;

        }

        if (position < noOfVideos.get(0)) {
            actualPosition = position;
            videoId = warmup.get(actualPosition);
            videoType = "Warm Up";
        }
        else if (position < noOfVideos.get(0) + noOfVideos.get(1)) {
            actualPosition = position - noOfVideos.get(0);
            videoId = run.get(actualPosition);
            videoType = "2.4km Run";
        }
        else if (position < noOfVideos.get(0) + noOfVideos.get(1) + noOfVideos.get(2)) {
            actualPosition = position - noOfVideos.get(0) - noOfVideos.get(1);
            videoId = pushup.get(actualPosition);
            videoType = "Push-Ups";
        }
        else if (position < noOfVideos.get(0) + noOfVideos.get(1) + noOfVideos.get(2) + noOfVideos.get(3)) {
            actualPosition = position - noOfVideos.get(0) - noOfVideos.get(1) - noOfVideos.get(2);
            videoId = situp.get(actualPosition);
            videoType = "Sit-Ups";
        }
        else if (position < noOfVideos.get(0) + noOfVideos.get(1) + noOfVideos.get(2) + noOfVideos.get(3) + noOfVideos.get(4)) {
            actualPosition = position - noOfVideos.get(0) - noOfVideos.get(1) - noOfVideos.get(2) - noOfVideos.get(3);
            videoId = situp.get(actualPosition);
            videoType = "Cool Down";
        }

        if (actualPosition == 0) {
            holder.videoType.setText(videoType);
            holder.videoType.setVisibility(View.VISIBLE);
        }
        else {
            holder.videoType.setVisibility(View.GONE);
        }

        try {
            String jsonString = "";
            String jsonLink = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails,snippet&fields=items/snippet(title,thumbnails/medium/url),items/contentDetails/duration&key=AIzaSyCwAQeCpPkjrhV-e5Gh__Ny2njKlyiCP58&id=" + videoId;

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

            JSONObject obj = new JSONObject(jsonString);
            JSONArray items = obj.getJSONArray("items");
            JSONObject snippet = items.getJSONObject(0).getJSONObject("snippet");
            holder.videoName.setText(snippet.getString("title"));
            Log.d(DEBUG, snippet.getString("title"));

            String imageURL = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
            Picasso.with(ctx).load(imageURL).into(holder.thumbnail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getItemCount() { return totalVideos; }

}
