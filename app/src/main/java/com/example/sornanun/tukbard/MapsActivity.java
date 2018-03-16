package com.example.sornanun.tukbard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, FirebaseController.FirebaseCallback {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Double latitude;
    private Double longitude;
    private ImageButton changeViewBTN;
    private int DistanceForLook;
    private int TimeForUpdateLocation;
    FirebaseController firebaseController = new FirebaseController(this, "map");
    ArrayList<Monk> monkArrayList = new ArrayList<Monk>();
    SpotsDialog dialog;

    String TAG_sornanun = "SornanunCheck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        changeViewBTN = (ImageButton) findViewById(R.id.changeView_btn);
        changeViewBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMapTypeSelectorDialog();
            }
        });

        try {
            // Create an instance of GoogleAPIClient.
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        } catch (Exception e) {
            new AlertDialog.Builder(this).setTitle("ขออภัย")
                    .setMessage("ไม่สามารถสร้างแผนที่ได้ พบปัญหา " + e)
                    .setPositiveButton("รับทราบ", null).show();
        }

        getPreferencesFromLocal();
    }


    private void getPreferencesFromLocal() {
        try {
            // get value was setting from local data
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            DistanceForLook = Integer.valueOf(settings.getString("distance_for_look", "1"));
            TimeForUpdateLocation = Integer.valueOf(settings.getString("time_for_update_location", "10"));
            // Convert data
            DistanceForLook = DistanceForLook * 1000; // 1000 meter is 1 kilometer
        } catch (Exception e) {
            new AlertDialog.Builder(this).setTitle("ขออภัย")
                    .setMessage("ไม่สามารถดึงข้อมูลการตั้งค่าได้ พบปัญหา " + e)
                    .setPositiveButton("รับทราบ", null).show();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            checkLocationPermission();
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

                // set default map focus to last know location
                CameraUpdate point = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14.0f);
                // moves camera to coordinates
                mMap.moveCamera(point);
                // animates camera to coordinates
                mMap.animateCamera(point);
            } else {
                // set default map focus to bangkok
                CameraUpdate point = CameraUpdateFactory.newLatLngZoom(new LatLng(13.751263, 100.504173), 7.0f);
                // moves camera to coordinates
                mMap.moveCamera(point);
                // animates camera to coordinates
                mMap.animateCamera(point);
            }
            mMap.getUiSettings().setZoomControlsEnabled(true);

            // Process of this app
            stepProcess();

        } catch (Exception ex) {
            new AlertDialog.Builder(this).setTitle("ขออภัย")
                    .setMessage("ไม่สามารถกำหนดเป้าหมายไปยังประเทศไทยได้ พบปัญหา " + ex)
                    .setPositiveButton("รับทราบ", null).show();
        }
    }

    public void stepProcess() {
        try {

            if (isNetworkAvailable(this.getApplicationContext())) {
                if (checkLocationEnabled() == true) {

                    dialog = new SpotsDialog(MapsActivity.this, "กรุณารอสักครู่...");
                    dialog.show();

                    if (canGetLocation()) {
                        setCurrentLocation();
                    } else {
                        dialog.setMessage("กำลังระบุตำแหน่งปัจจุบัน...");
                        while (canGetLocation() == false) {
                            try {
                                Thread.sleep(1000);    //1000 milliseconds is one second.

                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        setCurrentLocation();
                    }

                    timeToGetDataFromFirebase();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                    final String message = "แอพพลิเคชั่นต้องการเปิดการระบุตำแหน่งบนอุปกรณ์ของคุณ \nคุณต้องการเปิด GPS หรือไม่";
                    builder.setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    //// Open setting
                                    startActivity(new Intent(action));
                                    //// check permission before get location
                                    if (checkLocationPermission()) {
                                        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                                                Manifest.permission. ACCESS_FINE_LOCATION)
                                                == PackageManager.PERMISSION_GRANTED) {
                                            permissionStatus = true;
                                        }
                                    }
                                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                    if (mLastLocation != null)
                                        stepProcess(); // if can get location will go to next process

                                    d.dismiss(); // close alert dialog
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                }
                            });
                    builder.create().show();
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("ตรวจสอบการเชื่อมต่อ")
                        .setMessage("ต้องการเชื่อมต่ออินเทอร์เน็ต โปรดตรวจสอบการเชื่อมต่ออินเทอร์เน็ตเพื่อค้นหาสถานที่ของพระสงฆ์")
                        .setPositiveButton("รับทราบ", null).show();
            }
        } catch (Exception e) {
            new AlertDialog.Builder(this).setTitle("ขออภัย")
                    .setMessage("ไม่สามารถทำงานตามลำดับต่อไปได้ พบปัญหา " + e)
                    .setPositiveButton("รับทราบ", null).show();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        timerUpdate.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                permissionStatus = true;
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                    || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                mMap.setMyLocationEnabled(true);
//            }
//        } else {
//            mMap.setMyLocationEnabled(true);
//        }
        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                permissionStatus = true;
            }
        }
        mMap.setMyLocationEnabled(true);

    }

    //////------------------ Custom methods ---------------------
    Timer timerUpdate = new Timer();

    public void timeToGetDataFromFirebase() {

        // get data application setting that user just set
        getPreferencesFromLocal();

        // check this activity is opening.
        if (mGoogleApiClient.isConnected()) {
            try {
                timerUpdate = new Timer();
                timerUpdate.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    //update ui
                                    dialog.setMessage("กำลังดึงข้อมูลตำแหน่งพระสงฆ์...");
                                    Log.d(TAG_sornanun, "Send request data to firebase");
                                    getMonkLocationFromFirebase();
                                } catch (Exception e) {
                                    Log.e(TAG_sornanun, "Error from run on thread : " + e);
                                }
                            }
                        });
                    }
                }, 0, TimeForUpdateLocation * 1000);

            } catch (Exception e) {
                Log.e(TAG_sornanun, "Error in loop " + e.toString());
                Toast.makeText(this, "เกิดข้อผิดพลาดบางอย่าง " + e.toString(), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Not Run in loop", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void getMonkLocationFromFirebase() {
        try {
            firebaseController.getMonk();
        } catch (Exception e) {
            new AlertDialog.Builder(this).setTitle("ขออภัย")
                    .setMessage("ไม่สามารถดึงข้อมูลตำแหน่งจากฐานข้อมูลได้ พบปัญหา " + e)
                    .setPositiveButton("รับทราบ", null).show();
        }
    }

    @Override
    public void firebaseReturnValue(ArrayList<Monk> monkList) {
        Log.d(TAG_sornanun, "Data returned from firebaseController Size is : " + monkList.size());
        this.monkArrayList = monkList;
        selectOnlyDistanceFromSetting();
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        Toast.makeText(this, "อัพเดทตำแหน่งเมื่อ " + currentTime, Toast.LENGTH_SHORT).show();
    }

    private void selectOnlyDistanceFromSetting() {
        if (monkArrayList.size() > 0) {
            Location your_location = new Location("YourLocation");
            your_location.setLatitude(latitude);
            your_location.setLongitude(longitude);

            // remove all older marker before add new marker
            mMap.clear();

            for (Monk singleMonk : monkArrayList) {
                Double MonkLat = Double.valueOf(singleMonk.getMyLat());
                Double MonkLong = Double.valueOf(singleMonk.getMyLong());
                String Address = singleMonk.getMyAddress();

                Location monk_location = new Location("MonkLocation");
                monk_location.setLatitude(MonkLat);
                monk_location.setLongitude(MonkLong);

                double distance = your_location.distanceTo(monk_location);
                if (distance <= DistanceForLook) {
                    setMarker(MonkLat, MonkLong, Address);
                }
            }
            Log.d(TAG_sornanun, "Mark monk on map finished");
            dialog.dismiss();
        } else {
            dialog.dismiss();
            new AlertDialog.Builder(this).setTitle("ขออภัย")
                    .setMessage("ไม่พบสถานที่ของพระสงฆ์ที่บิณฑบาตในระยะ " + (DistanceForLook / 1000) + " กิโลเมตร")
                    .setPositiveButton("รับทราบ", null).show();
        }
    }


    public void setMarker(Double MonkLat, Double MonkLong, String Detail) {
        // add marker on map
        mMap.addMarker(new MarkerOptions().position(new LatLng(MonkLat, MonkLong)).title(Detail).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker)));
        Log.d("Near Monk ", "lat :" + MonkLat + " long : " + MonkLong + " address : " + Detail);
    }

    public boolean checkLocationEnabled() {
        try {
            LocationManager mlocManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return enabled;
        } catch (Exception e) {
            new AlertDialog.Builder(this).setTitle("ขออภัย")
                    .setMessage("ไม่สามารถตรวจสอบสถานะ GPS ได้ พบปัญหา " + e)
                    .setPositiveButton("รับทราบ", null).show();
        }
        return false;
    }

    public boolean canGetLocation() {
        try {
            boolean connectionEnabled = false;
            //// check permission before get location
            if (checkLocationPermission()) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission. ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    permissionStatus = true;
                }
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                connectionEnabled = true;
            }
            return connectionEnabled;
        } catch (Exception e) {
            new AlertDialog.Builder(this).setTitle("ขออภัย")
                    .setMessage("ไม่สามารถดึงตำแหน่งได้ พบปัญหา " + e)
                    .setPositiveButton("รับทราบ", null).show();
        }
        return false;
    }

    public void setCurrentLocation() {
        try {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            LatLng currentLocation = new LatLng(latitude, longitude);

            // set zoom level
            float zoomLevel = 14.0f; // default
            if (DistanceForLook == 1000) zoomLevel = 15.0f;
            else if (DistanceForLook == 2000) zoomLevel = 14.2f;
            else if (DistanceForLook == 3000) zoomLevel = 13.6f;
            else if (DistanceForLook == 4000) zoomLevel = 13.0f;
            else if (DistanceForLook == 5000) zoomLevel = 12.5f;
            else if (DistanceForLook == 10000) zoomLevel = 11.2f;

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
        } catch (Exception e) {
            new AlertDialog.Builder(this).setTitle("ขออภัย")
                    .setMessage("ไม่สามารถระบุตำแหน่งปัจจุบันของคุณได้ พบปัญหา " + e)
                    .setPositiveButton("รับทราบ", null).show();
        }
    }

    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"แผนที่ถนน", "ดาวเทียม", "ภูมิประเทศ", "ผสมผสาน"};

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "เลือกรูปแบบแผนที่";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        timerUpdate.cancel();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("การเข้าถึงตำแหน่ง")
                        .setMessage("แอพพลิเคชั่นต้องการเข้าถึงตำแหน่งของคุณ กรุณาเปิด GPS และดำเนินการต่อไป")
                        .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    boolean permissionStatus = false;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission. ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        permissionStatus = true;
                    }

                } else {

                    permissionStatus = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }


}
