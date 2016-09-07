package com.example.sadashivsinha.mprosmart.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION))
                {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                }
                else
                {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);

                    // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }

        if(getIntent().hasExtra("fullAddress"))
        {
            address = getIntent().getStringExtra("fullAddress");
            projectId = getIntent().getStringExtra("projectId");
            projectName = getIntent().getStringExtra("projectName");

            GeocodingLocation locationAddress = new GeocodingLocation();
            locationAddress.getAddressFromLocation(address,
                    getApplicationContext(), new GeocoderHandler());

            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
        }

        else
        {
            Toast.makeText(MapsActivity.this, "Address Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        setUpMap();

    }

    private void setUpMap()
    {
        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                .title(projectId).snippet(projectName));

        LatLng myCoordinates = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 12);
        mMap.animateCamera(yourLocation);

//        // Enable MyLocation Layer of Google Map
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//
//        // Get LocationManager object from System Service LOCATION_SERVICE
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        // Create a criteria object to retrieve provider
//        Criteria criteria = new Criteria();
//
//        // Get the name of the best provider
//        String provider = locationManager.getBestProvider(criteria, true);
//
//        // Get Current Location
////        Location myLocation = locationManager.getLastKnownLocation(provider);
//
//        // set map type
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        // Get latitude of the current location
////        double latitude = myLocation.getLatitude();
//        double latitudeVal = Double.parseDouble(latitude);
//
//        // Get longitude of the current location
////        double longitude = myLocation.getLongitude();
//        double longitudeVal = Double.parseDouble(longitude);
//
//
//        // Create a LatLng object for the current location
//        LatLng latLng = new LatLng(latitudeVal, longitudeVal);
//
//        // Show the current location in Google Map
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//
//        // Zoom in the Google Map
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title(projectId).snippet(projectName));
//
//        LatLng myCoordinates = new LatLng(latitudeVal, longitudeVal);
//        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 12);
//        mMap.animateCamera(yourLocation);
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

                mapFragment.getMapAsync(MapsActivity.this);
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
}