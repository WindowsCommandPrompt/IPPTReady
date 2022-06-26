package sg.np.edu.mad.ipptready;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
    // Global Variables
    private static final String DEBUG = "DEBUG"; // Debug tag

    public Map<String, List<String>> videosList; // Hashmap to store videos based on their categories
    String jsonString; // store YouTube Data API results
    Context ctx; // VideoActivity Context

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
    ArrayList<Integer> noOfVideos; // Store number of videos in each category

    // other variables
    int totalVideos = 0; // to be used in getItemCount()
    String videoTitle = "";
    String videoType = "";
    String videoDescription = "";
    int actualPosition = 0; // Used in onBindViewHolder

    // VideoAdapter Constructor
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
        // If orientation changes, bottom margin changes for last video
        ViewGroup.MarginLayoutParams videoEntryParams = (ViewGroup.MarginLayoutParams) holder.videoEntry.getLayoutParams();
        if (holder.getAdapterPosition() == totalVideos - 1) {
            int orientation = ctx.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                videoEntryParams.bottomMargin = 2000;
            } else {
                videoEntryParams.bottomMargin = 550;
            }

        }
        else {
            videoEntryParams.bottomMargin = 0;
        }

        // Get videoType and video position to be inferred from videosList (hashmap)
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

        // Set videoID in a hidden TextView so that play button can set up intent by getting video id from this TextView
        holder.videoIDTextView.setText(videosList.get(videoType).get(actualPosition));
        holder.videoIDTextView.setVisibility(View.GONE);

        // If video is first video in the category, display category heading, else hide it
        if (actualPosition == 0) {
            holder.videoType.setText(videoType);
            holder.videoType.setVisibility(View.VISIBLE);
        }
        else {
            holder.videoType.setVisibility(View.GONE);
        }

        try {
            // Get video title from jsonString
            JSONObject obj = new JSONObject(jsonString);
            JSONArray items = obj.getJSONArray("items");
            JSONObject snippet = items.getJSONObject(position).getJSONObject("snippet");
            videoTitle = snippet.getString("title");
            holder.videoName.setText(videoTitle);

            // Get video description from jsonString
            videoDescription = snippet.getString("description");
            // Set video description in a hidden TextView so that play button can set up intent by getting video description from this TextView
            holder.videoDescription.setText(videoDescription);
            holder.videoDescription.setVisibility(View.GONE);

            // Get video thumbnail url from jsonString
            String imageURL = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
            Picasso.with(ctx).load(imageURL).into(holder.thumbnail);

            // Set OnClickListener on Play Button
            holder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Set up intent to WatchVideo
                    Intent watchVideoIntent = new Intent(view.getContext(), WatchVideoActivity.class);
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
