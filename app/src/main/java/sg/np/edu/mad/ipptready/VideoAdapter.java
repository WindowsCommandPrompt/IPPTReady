package sg.np.edu.mad.ipptready;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {
    private static final String DEBUG = "DEBUG";

    public Map<String, List<String>> videosList;
    String jsonString;
    Context ctx;

    ArrayList<String> orderOfActivities = new ArrayList<String>() {
        {
            add("Warm up");
            add("2.4km Run");
            add("Push-ups");
            add("Sit-ups");
            add("Cool down");
        }
    };
    ArrayList<Integer> noOfVideos;
    int totalVideos = 0;
    String videoTitle = "";
    String videoId = "";
    String videoType = "";
    String videoDescription = "";
    int actualPosition = 0;

    public VideoAdapter(Map<String, List<String>> VideosList, String JsonString, ArrayList<Integer> NoOfVideos, Context context) {
        videosList = VideosList;
        jsonString = JsonString;
        noOfVideos = NoOfVideos;
        ctx = context;

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
        ViewGroup.MarginLayoutParams videoEntryParams = (ViewGroup.MarginLayoutParams) holder.videoEntry.getLayoutParams();
        if (holder.getAdapterPosition() == totalVideos - 1) {
            videoEntryParams.bottomMargin = 100;
        }
        else {
            videoEntryParams.bottomMargin = 0;
        }

        if (position < noOfVideos.get(0)) {
            actualPosition = position;
            videoType = orderOfActivities.get(0);
        }
        else if (position < noOfVideos.get(0) + noOfVideos.get(1)) {
            actualPosition = position - noOfVideos.get(0);
            videoType = orderOfActivities.get(1);
        }
        else if (position < noOfVideos.get(0) + noOfVideos.get(1) + noOfVideos.get(2)) {
            actualPosition = position - noOfVideos.get(0) - noOfVideos.get(1);
            videoType = orderOfActivities.get(2);
        }
        else if (position < noOfVideos.get(0) + noOfVideos.get(1) + noOfVideos.get(2) + noOfVideos.get(3)) {
            actualPosition = position - noOfVideos.get(0) - noOfVideos.get(1) - noOfVideos.get(2);
            videoType = orderOfActivities.get(3);
        }
        else if (position < noOfVideos.get(0) + noOfVideos.get(1) + noOfVideos.get(2) + noOfVideos.get(3) + noOfVideos.get(4)) {
            actualPosition = position - noOfVideos.get(0) - noOfVideos.get(1) - noOfVideos.get(2) - noOfVideos.get(3);
            videoType = orderOfActivities.get(4);
        }

        holder.videoIDTextView.setText(videosList.get(videoType).get(actualPosition));
        holder.videoIDTextView.setVisibility(View.GONE);

        if (actualPosition == 0) {
            holder.videoType.setText(videoType);
            holder.videoType.setVisibility(View.VISIBLE);
        }
        else {
            holder.videoType.setVisibility(View.GONE);
        }

        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray items = obj.getJSONArray("items");
            JSONObject snippet = items.getJSONObject(position).getJSONObject("snippet");
            videoTitle = snippet.getString("title");
            holder.videoName.setText(videoTitle);

            videoDescription = snippet.getString("description");
            holder.videoDescription.setText(videoDescription);
            holder.videoDescription.setVisibility(View.GONE);

            String imageURL = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
            Picasso.with(ctx).load(imageURL).into(holder.thumbnail);

            holder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent watchVideoIntent = new Intent(view.getContext(), WatchVideo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Title", holder.videoName.getText().toString());
                    bundle.putString("Video Id", holder.videoIDTextView.getText().toString());
                    bundle.putString("Description", holder.videoDescription.getText().toString());
                    watchVideoIntent.putExtras(bundle);
                    view.getContext().startActivity(watchVideoIntent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getItemCount() { return totalVideos; }
}
