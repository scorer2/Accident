package com.example.score_000.parselogintutorial;

/**
 * Created by score_000 on 4/4/2015.
 */

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

public class MapActivity extends Activity {
    LatLng newPos;
    private GPSLocation currentLocation;
    Location location = null; // location
    double latitude; // latitude
    double longitude; // longitude
    ParseGeoPoint geoPoint;

    private GoogleMap map;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Getting latitude of the current location
        currentLocation = new GPSLocation(this);
        double currLat = currentLocation.getLatitude();

        // Getting longitude of the current location
        double currLng = currentLocation.getLongitude();

        LatLng myPos = new LatLng(currLat, currLng);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);

        map.getUiSettings().setZoomControlsEnabled(true);//
        map.getUiSettings().setCompassEnabled(true);//
        map.getUiSettings().setAllGesturesEnabled(true);//

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myPos, 15);
        map.animateCamera(update);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                map.clear();
                newPos = latLng;
                map.addMarker(new MarkerOptions().position(newPos).title("Choose this Location!")).showInfoWindow();
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
            }
        });

        Button btnMapSave = (Button) findViewById(R.id.btnSaveMap);

        btnMapSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geoPoint = new ParseGeoPoint(newPos.latitude, newPos.longitude);
                SubmitReportFragment obj = new SubmitReportFragment();
                obj.geoPoint = geoPoint;


            }
        });
    }

    /**
     * Function to get latitude
     * */

     public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude of selected position
        return newPos.latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude of selected position
        return newPos.longitude;
    }

    public ParseGeoPoint getPosition(){
        return geoPoint;
    }
}