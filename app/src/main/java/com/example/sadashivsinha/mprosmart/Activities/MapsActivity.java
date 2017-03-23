package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{

    String address, projectId, projectName;
    String latitude, longitude;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    final int PERMISSION_MAP = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

            if(!hasPermissions(MapsActivity.this, PERMISSIONS)){
                ActivityCompat.requestPermissions((Activity) MapsActivity.this, PERMISSIONS,
                        PERMISSION_MAP);
            }
            else
            {
                mapFragment.getMapAsync(MapsActivity.this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        setUpMap();
    }

    private void setUpMap()
    {

        if(getIntent().hasExtra("fullAddress"))
        {
            address = getIntent().getStringExtra("fullAddress");
            projectId = getIntent().getStringExtra("projectId");
            projectName = getIntent().getStringExtra("projectName");

            GeocodingLocation locationAddress = new GeocodingLocation();
            locationAddress.getAddressFromLocation(address,
                    getApplicationContext(), new GeocoderHandler());

        }

        else
        {
            Toast.makeText(MapsActivity.this, "Address Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    private class GeocoderHandler extends Handler
    {
        @Override
        public void handleMessage(Message message)
        {
            String locationAddress;
            switch (message.what)
            {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            if(locationAddress!=null)
            {
                String[] parts = locationAddress.split("\n");
                latitude = parts[0]; // 004
                longitude = parts[1]; // 034556

                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                        .title(projectId).snippet(projectName));

                LatLng myCoordinates = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 12);
                mMap.animateCamera(yourLocation);
            }
        }
    }


    public class GeocodingLocation {

        private static final String TAG = "GeocodingLocation";

        public void getAddressFromLocation(final String locationAddress,
                                                  final Context context, final Handler handler)
        {
            Thread thread = new Thread() {
                @Override
                public void run()
                {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    String result = null;
                    try {
                        List addressList = geocoder.getFromLocationName(locationAddress, 1);
                        if (addressList != null && addressList.size() > 0) {
                            Address address = (Address) addressList.get(0);
                            StringBuilder sb = new StringBuilder();
                            sb.append(address.getLatitude()).append("\n");
                            sb.append(address.getLongitude()).append("\n");
                            result = sb.toString();
                        }
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, "Unable to connect to Geocoder", e);
                    }

                    finally
                    {
                        Message message = Message.obtain();
                        message.setTarget(handler);
                        if (result != null)
                        {
                            message.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("address", result);
                            message.setData(bundle);
                        }
                        else
                        {
                            message.what = 1;
                            Bundle bundle = new Bundle();
                            result = "Unable to fetch address.";
                            bundle.putString("address", result);
                            message.setData(bundle);
                        }
                        message.sendToTarget();
                    }
                }
            };
            thread.start();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_MAP:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapFragment.getMapAsync(MapsActivity.this);

                } else {
                    Toast.makeText(MapsActivity.this, "Location Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}