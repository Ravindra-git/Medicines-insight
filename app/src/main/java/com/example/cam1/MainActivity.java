package com.example.cam1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("script");
//        PyObject object = pyObject.callAttr("main","mfd by the dose \n" +
//                "of \n" +
//                "Yogaprol\n" +
//                "-AM \n" +
//                "Tablet.");
        PyObject object = pyObject.callAttr("main");
        System.out.println(object.toString());

        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "Dependencies are not loaded yet...please try after few moments!!", Toast.LENGTH_SHORT).show();
            Log.d("er","Dependencies are downloading....try after few moment");
            return;
        }

//  Init camera source to use high resolution and auto focus
        CameraSource mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build();
        SurfaceView surfaceCameraPreview = findViewById(R.id.surface_camera_preview);
        surfaceCameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder p0, int p1, int p2, int p3) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder p0) {
                mCameraSource.stop();
            }

            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(SurfaceHolder p0) {
                try {
                    if (isCameraPermissionGranted()) {
                        mCameraSource.start(surfaceCameraPreview.getHolder());
                    } else {
                        requestForPermission();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            private void requestForPermission() {
                 if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED);
                {
                 if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)){

                 }else{
                     int MY_PERMISSIONS_REQUEST_CAMERA = 0;
                     ActivityCompat.requestPermissions(MainActivity.this,
                             new String[]{Manifest.permission.CAMERA},
                             MY_PERMISSIONS_REQUEST_CAMERA);
                 }
                }
            }

            private boolean isCameraPermissionGranted() {
                return (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
                   }
        });
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                SparseArray<TextBlock> items = detections.getDetectedItems();

                if (items.size() <= 0) {
                    return;
                }

                TextView tvResult = findViewById(R.id.tv_result);
                tvResult.post(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock item = items.valueAt(i);
                            stringBuilder.append(item.getValue());
                            stringBuilder.append("\n");
                        }
                        tvResult.setText(stringBuilder.toString());
                    }
                });
            }
        });


    }
}