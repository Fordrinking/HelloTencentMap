package com.hill1942.hellotencentmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hill1942.hellotencentmap.SENetwork.NetworkUtil;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        TencentLocationListener {

    private TextView tv;
    private Button btn;
    private NetworkUtil mNetworkUtil;
    private TencentLocationManager locationManager;
    private String mOpenId;

    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenId = getUID();


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

            locationManager.removeUpdates(MainActivity.this);
            // 定位成功
            double longitude = tencentLocation.getLongitude();
            double latitude  = tencentLocation.getLatitude();

            String str = "(" + longitude + ", " + latitude + ")";
            tv.setText(str);

            Log.i("tencent location: ", str);

            mNetworkUtil = NetworkUtil.getInstance();
            if (mNetworkUtil != null) {
                // Execute the async download.
                Map<String, String> params = new HashMap<String, String>();
                params.put("openid", mOpenId);
                params.put("longitude", String.valueOf(longitude));
                params.put("latitude", String.valueOf(latitude));
                Log.i("SENetwork:  ", "start url trans");
                mNetworkUtil.startURLTrans("http://lbs.hill1942.com/api/upload", params);
            }
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

    public String getUID() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        deviceId = deviceId +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits

        String uid = SETool.md5(deviceId);

        Log.i("Main Activity UID", uid);

        return uid;
    }
}
