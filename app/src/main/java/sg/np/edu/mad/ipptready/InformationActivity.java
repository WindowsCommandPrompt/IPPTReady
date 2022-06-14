package sg.np.edu.mad.ipptready;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class InformationActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    String s1[], s2[];
    int infoImages[] ={R.drawable.guidelines, R.drawable.pass_criteria, R.drawable.fail,
            R.drawable.exemption, R.drawable.stations, R.drawable.calculation, R.drawable.awards,
            R.drawable.location};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        recyclerView = findViewById(R.id.infoRecyclerView);

        s1 = getResources().getStringArray(R.array.information_name);
        s2 = getResources().getStringArray(R.array.information_description);

        InformationAdapter informationAdapter = new InformationAdapter(this, s1, s2, infoImages);
        recyclerView.setAdapter(informationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}