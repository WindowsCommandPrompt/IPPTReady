package sg.np.edu.mad.ipptready;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class InformationViewHolder extends RecyclerView.ViewHolder {

    TextView infoText1, infoText2;
    ImageView infoImage;

    public InformationViewHolder(View itemView){
        super(itemView);
        infoText1 = itemView.findViewById(R.id.infoName);
        infoText2 = itemView.findViewById(R.id.infoDescription);
        infoImage = itemView.findViewById(R.id.infoImageView);
    }
}
