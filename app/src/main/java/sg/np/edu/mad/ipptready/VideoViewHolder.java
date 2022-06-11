package sg.np.edu.mad.ipptready;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class VideoViewHolder extends RecyclerView.ViewHolder{
    protected TextView videoType;
    protected TextView videoName;
    // protected Button playButton;
    protected ImageView thumbnail;

    public VideoViewHolder(View v) {
        super(v);
        videoType = v.findViewById(R.id.recyclerVideoType);
        // playButton = v.findViewById(R.id.recyclerPlayButton);
        videoName = v.findViewById(R.id.recyclerVideoName);
        thumbnail = v.findViewById(R.id.recyclerThumbnail);
    }
}
