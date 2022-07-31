package sg.np.edu.mad.ipptready;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.np.edu.mad.ipptready.Cycle.CycleActivity;
import sg.np.edu.mad.ipptready.ExerciseTogether.ExerciseTogetherActivity;
import sg.np.edu.mad.ipptready.ExerciseTogether.ExerciseTogetherWaitingRoomActivity;
import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;

public class NavFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nav, container, false);
        Context ctx = view.getContext();
        Internet internet = new Internet();

        // Onclicklistener for Cycle feature
        view.findViewById(R.id.cycleButtonNav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internet.isOnline(ctx))
                {
                    Intent CycleIntent = new Intent(getActivity(), CycleActivity.class);

                    CycleIntent.putExtra("userId", ((HomeActivity) getActivity()).EmailAddress);
                    CycleIntent.putExtra("DOB", ((HomeActivity)getActivity()).user.DoB);
                    startActivity(CycleIntent);
                }
                else internet.noConnectionAlert(ctx);
            }
        });

        // Onclicklistener for video feature
        view.findViewById(R.id.videoButtonNav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (internet.isOnline(ctx))
                {
                    Intent VideoIntent = new Intent(getActivity(), VideoActivity.class);
                    startActivity(VideoIntent);
                }
                else internet.noConnectionAlert(ctx);
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

        // Onclicklistener for Exercise Together feature
        view.findViewById(R.id.ExTgtButtonNav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (internet.isOnline(ctx))
                {
                    Intent ExTgtIntent = new Intent(getActivity(), ExerciseTogetherActivity.class);
                    ExTgtIntent.putExtra("userId", ((HomeActivity) getActivity()).EmailAddress);
                    startActivity(ExTgtIntent);
                }
                else internet.noConnectionAlert(ctx);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}