package sg.np.edu.mad.ipptready;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

public class navFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nav, container, false);

        // Onclicklistener for Cycle feature
        view.findViewById(R.id.cycleButtonNav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CycleIntent = new Intent(getActivity(), CycleActivity.class);

                CycleIntent.putExtra("Email", ((HomeActivity) getActivity()).EmailAddress);
                CycleIntent.putExtra("User", ((HomeActivity) getActivity()).user);
                startActivity(CycleIntent);
            }
        });

        // Onclicklistener for video feature
        view.findViewById(R.id.videoButtonNav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent VideoIntent = new Intent(getActivity(), VideoActivity.class);
                startActivity(VideoIntent);
            }
        });

        // Onclicklistener for info feature
        view.findViewById(R.id.infoButtonNav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent InformationIntent = new Intent(getActivity(), InformationActivity.class);
                startActivity(InformationIntent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}