package sg.np.edu.mad.ipptready.InternetConnectivity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import sg.np.edu.mad.ipptready.LoginActivity;

public class Internet {

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void noConnectionAlert(Context context) {
        AlertDialog.Builder alertFail = new AlertDialog.Builder(context);
        alertFail
                .setTitle("No Internet Connection")
                .setMessage("Please ensure that you have access to the Internet to begin using IPPTReady.")
                .setCancelable(false)
                .setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "Please connect to the Internet!", Toast.LENGTH_SHORT).show();
                            }
                        });
        alertFail.create().show();
    }
}
