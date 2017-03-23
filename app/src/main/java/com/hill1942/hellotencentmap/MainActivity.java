package com.hill1942.hellotencentmap;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Debug;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TencentLocationListener {

    private TextView tv;
    private Button btn;
    private TencentLocationManager locationManager;

    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        locationManager = TencentLocationManager.getInstance(this);

        final TencentLocationRequest request = TencentLocationRequest.create();
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_GEO);
        request.setInterval(5000);


        tv  = (TextView) findViewById(R.id.lc_tv);
        btn = (Button) findViewById(R.id.lc_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("view", "click");

                String[] permissions = {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[0])
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, ACCESS_COARSE_LOCATION_REQUEST_CODE);
                } else {
                    Log.i("view", "request location update");
                    int error = locationManager.requestLocationUpdates(request, MainActivity.this);
                    Log.i("tenent lbs: ", "error: " + error);
                }
                //TencentLocationRequest request = TencentLocationRequest.create();
                //int error = locationManager.requestLocationUpdates(request, self);
            }
        });
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String s) {
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            double longitude = tencentLocation.getLongitude();
            double latitude  = tencentLocation.getLatitude();

            String str = "(" + longitude + ", " + latitude + ")";
            tv.setText(str);

            Log.i("tencent location: ", str);

        } else {
            // 定位失败
            Log.i("tencent location", "failed");
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        Log.i("tencent location: ", "status");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("tencent location", "got permission");
                // Permission Granted
            } else {
                // Permission Denied
                Log.i("tencent location", "Permission Denied");
            }
        }
    }
}
