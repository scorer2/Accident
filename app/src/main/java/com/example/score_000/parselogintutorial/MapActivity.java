package com.example.score_000.parselogintutorial;
/**
 * Created by score_000 on 4/4/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
public class MapActivity extends Activity {
    LatLng newPos;

    Location location = null; // location
    double latitude; // latitude
    double longitude; // longitude
    ParseGeoPoint mapGeoPoint;
    private GoogleMap map;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        final TextView txtMapDesc = (TextView) findViewById(R.id.txtMapDesc);

        GPSLocation currentLocation;
        currentLocation = new GPSLocation(this);

        // Getting latitude of the current location
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
                txtMapDesc.setText("  Now Press Save");
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                newPos = null;
                txtMapDesc.setText("     Tap and hold");
            }
        });

        Button btnCancelMap = (Button) findViewById(R.id.btnMapCancel);
        btnCancelMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();//go back to submit report
            }
        });

        Button btnMapSave = (Button) findViewById(R.id.btnSaveMap);
        btnMapSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newPos == null) {
                    Toast.makeText(MapActivity.this, "Please select a location then Save.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapActivity.this, "Location saved.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("lat", newPos.latitude);
                    i.putExtra("lng", newPos.longitude);
                    startActivity(i);//go to mainActivity
                }
            }
        });
    }
}