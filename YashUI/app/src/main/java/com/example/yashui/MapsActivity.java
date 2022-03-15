package com.example.yashui;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;

//    private GoogleMap mMap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(20.5, 79);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
    import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity  {
        Button addressButton;
        TextView addressTV;
        TextView latLongTV;
        GoogleMap mMap;
    GeocodingLocation locationAddresses;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);


            addressTV = (TextView) findViewById(R.id.addressTV);
            latLongTV = (TextView) findViewById(R.id.latLongTV);

            addressButton = (Button) findViewById(R.id.addressButton);
            addressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    EditText editText = (EditText) findViewById(R.id.addressET);
                    String address = editText.getText().toString();

                     locationAddresses= new GeocodingLocation();
                    locationAddresses.getAddressFromLocation(address,
                            getApplicationContext(), new GeocoderHandler());



                }
            });

        }
    private class GeocoderHandler extends Handler {
            @Override
            public void handleMessage(Message message) {
                String locationAddress;
                switch (message.what) {
                    case 1:
                        Bundle bundle = message.getData();
                        locationAddress = bundle.getString("address");
                        break;
                    default:
                        locationAddress = null;
                }
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;

                        // Add a marker in Sydney and move the camera
                        LatLng sydney = new LatLng(locationAddresses.getLatitude(),locationAddresses.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        float zoomLevel  = 16.0f;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,zoomLevel));
                    }
                });
                latLongTV.setText(locationAddress);
            }
        }
    }
