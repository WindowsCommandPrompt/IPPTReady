package sg.np.edu.mad.ipptready;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;


public class DetailedInformationActivity extends AppCompatActivity {

    TextView title, details;

    String data1, data3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_information);

        TextView textView = (TextView) findViewById(R.id.infoDetails);
        textView.setMovementMethod(new ScrollingMovementMethod());

        title = findViewById(R.id.infoName);
        details = findViewById(R.id.infoDetails);
        // get from intent and set the text on display
        getData();
        setData();
    }

    private void getData() {
        if (getIntent().hasExtra("data1") && getIntent().hasExtra("data3")) {
            data1 = getIntent().getStringExtra("data1");
            data3 = getIntent().getStringExtra("data3");
        }
        else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData(){
        title.setText(data1);
        details.setText(data3);
    }
}