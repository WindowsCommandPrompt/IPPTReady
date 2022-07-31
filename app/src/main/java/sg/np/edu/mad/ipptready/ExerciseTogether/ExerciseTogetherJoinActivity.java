package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.uk.tastytoasty.TastyToasty;

import java.util.concurrent.ExecutionException;

import sg.np.edu.mad.ipptready.LoginActivity;
import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherJoinActivity extends AppCompatActivity {
    // Exercise Together feature done by: BRYAN KOH

    // Global variables
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private Button qrCodeFoundButton;
    private String qrCode;
    private PreviewView cameraPreviewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private CountDownTimer myCountDown;
    private boolean countdownExecuted = false;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_join);

        // Scan Button to scan QR Code is hidden when camera not directed at a QR Code
        // When Scan button is clicked, device vibrates and user is led to joining process activity (to process the QR Code)
        qrCodeFoundButton = findViewById(R.id.qrCodeFoundButton);
        qrCodeFoundButton.setVisibility(View.INVISIBLE);
        qrCodeFoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vibrate when QR Code is scanned
                final VibrationEffect vibrationEffect1;
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrationEffect1 = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }

                // Intent to Joining Process activity
                Intent joinIntent = new Intent(ExerciseTogetherJoinActivity.this, ExerciseTogetherJoiningProcessActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userId", getIntent().getStringExtra("userId"));
                bundle.putString("qrCode", qrCode);
                /*TastyToasty.makeText(ExerciseTogetherJoinActivity.this, qrCode, TastyToasty.SHORT,null, R.color.greendark, R.color.white, false).show();*/
                Log.d(ExerciseTogetherJoinActivity.class.getSimpleName(), "QR Code Found: " + qrCode);
                joinIntent.putExtras(bundle);
                startActivity(joinIntent);
                finish();
            }
        });

        // Get Preview View and set up ProcessCameraProvider
        cameraPreviewView = findViewById(R.id.previewCameraJoinSession);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        requestCamera();
    }

    // Get user's permission to enable camera
    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    // Get Permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Start camera if permission is granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                // Display toast message if permission not granted.
                TastyToasty.error(this, "Camera Permission Denied").show();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                TastyToasty.error(this, "Error starting camera " + e.getMessage()).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        cameraPreviewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        // Set Surface Provider (Use case receives data from camera)
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(cameraPreviewView.createSurfaceProvider());

        // Select camera (set camera requirements)
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Analyse the QR Code on the screen
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        // If QR Code is found, set scan QR code button to visible for a few seconds
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String _qrCode) {
                qrCode = _qrCode;
                qrCodeFoundButton.setVisibility(View.VISIBLE);
                if (!countdownExecuted)
                {
                    countDownTimer();
                    countdownExecuted = true;
                }
            }

            // Set scan QR code button to invisible after countdown ends
            @Override
            public void qrCodeNotFound() {
                if (!countdownExecuted) qrCodeFoundButton.setVisibility(View.INVISIBLE);
            }
        }));

        // Binds use case to LifecycleOwner
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }

    // Countdown for scan qr button to be visible
    private void countDownTimer(){
        myCountDown = new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() { countdownExecuted = false; }
        };
        myCountDown.start();
    }
}