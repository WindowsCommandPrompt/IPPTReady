package sg.np.edu.mad.ipptready;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PushupTargetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushuptarget);

        AlertDialog.Builder woahThatIsTooMuch = new AlertDialog.Builder(this);

        woahThatIsTooMuch
                .setTitle("HOLD ON!")
                .setMessage("You have set the target number of push-ups to be more than 60 times. Are you sure you will be able to finish those repetitions within 1 minute?")
                .setPositiveButton(
                    "YES",
                    (DialogInterface di, int i) -> {
                        di.dismiss();
                    }
                )
                .setNegativeButton(
                    "NO",
                    (DialogInterface di, int i) -> {
                        ((EditText) findViewById(R.id.pushUpTarget)).setText("60");
                        Toast.makeText(this, "Target has been set to 60 repetitions", Toast.LENGTH_SHORT);
                        di.dismiss();
                    }
                )
                .setCancelable(false);

        ((Button) findViewById(R.id.setPushUpActivity)).setOnClickListener(function -> {
            try{
                int targetPushUps = Integer.parseInt(((EditText) findViewById(R.id.pushUpTarget)).getText().toString());
                if (targetPushUps < 0) {
                    ((EditText) findViewById(R.id.pushUpTarget)).setText("");
                    Toast.makeText(this, "The value cannot be less than 0, please try again", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (targetPushUps > 60){
                        woahThatIsTooMuch.create().show();
                    }
                    else{
                        Intent andThenRedirect = new Intent(PushupTargetActivity.this, PushupActivity.class);
                        startActivity(andThenRedirect);
                        finish(); //finish the current activity...
                    }
                }
            }
            catch (IllegalArgumentException e){
                ((EditText) findViewById(R.id.pushUpTarget)).setText("");
                Toast.makeText(this, e.getMessage() + "is not allowed over here. Only accepts whole numbers greater than 0", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
