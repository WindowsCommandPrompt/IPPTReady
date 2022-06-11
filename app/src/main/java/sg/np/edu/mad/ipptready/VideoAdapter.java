package sg.np.edu.mad.ipptready;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {
    public List<String> run;
    public List<String> cooldown;
    public List<String> pushup;
    public List<String> situp;
    public List<String> warmup;
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
            String jsonString = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails,snippet&fields=items/snippet(title,thumbnails/medium/url),items/contentDetails/duration&key=AIzaSyCwAQeCpPkjrhV-e5Gh__Ny2njKlyiCP58&id=" + videoId;
            JSONObject obj = new JSONObject(jsonString);
            JSONArray items = obj.getJSONArray("items");
            JSONObject snippet = items.getJSONObject(0).getJSONObject("snippet");
            holder.videoName.setText(snippet.getString("title"));

            String imageURL = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
            Picasso.with(ctx).load(imageURL) .into(holder.thumbnail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getItemCount() { return totalVideos; }

}
