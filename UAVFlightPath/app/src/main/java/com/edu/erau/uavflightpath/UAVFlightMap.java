package com.edu.erau.uavflightpath;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class UAVFlightMap extends FragmentActivity{

    public final static String EXTRA_MESSAGE = "com.edu.erau.MESSAGE";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker[] markers = new Marker[1000];
    private int[] velcoity = new int[1000];
    private int[] altitude = new int[1000];
    private int markerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uavflight_map);
        setUpMapIfNeeded();



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                markers[markerCount] = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(false)
                        .title("Waypoint #" + markerCount)
                        .snippet("Latitude: " + latLng.latitude + "Longitude: " + latLng.longitude));
                velcoity[markerCount] = 0;
                altitude[markerCount] = 0;
                if (markerCount > 0) {
                    PolylineOptions pO = new PolylineOptions().width(2).color(Color.BLACK).add(markers[markerCount].getPosition(), markers[markerCount - 1].getPosition());
                    mMap.addPolyline(pO);
                    velcoity[markerCount] = velcoity[markerCount-1];
                    altitude[markerCount] = altitude[markerCount-1];
                }
                waypointDB waypointHandler = new waypointDB(UAVFlightMap.this, null, null, 1);
                waypoint Waypoint = new waypoint(markers[markerCount].getTitle(), latLng.latitude, latLng.longitude, altitude[markerCount], velcoity[markerCount]);
                waypointHandler.addWaypoint(Waypoint);
                markerCount++;
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                return false;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                markers[markerCount] = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(false)
                        .title("Waypoint #" + markerCount)
                        .snippet("Latitude: " + latLng.latitude
                                +"Longitude: " + latLng.longitude
                        ));
                velcoity[markerCount] = 0;
                altitude[markerCount] = 0;
                if (markerCount > 0) {
                    PolylineOptions pO = new PolylineOptions().width(2).color(Color.BLACK).add(markers[markerCount].getPosition(), markers[markerCount - 1].getPosition());
                    mMap.addPolyline(pO);
                    velcoity[markerCount] = velcoity[markerCount-1];
                    altitude[markerCount] = altitude[markerCount-1];
                }
                markers[markerCount].setSnippet(markers[markerCount].getSnippet() + "Altitude: "+ altitude[markerCount] + "Velocity: " + velcoity[markerCount]);
                waypointDB waypointHandler = new waypointDB(UAVFlightMap.this, null, null, 1);
                waypoint Waypoint = new waypoint(markers[markerCount].getTitle(), latLng.latitude, latLng.longitude, altitude[markerCount], velcoity[markerCount]);
                waypointHandler.addWaypoint(Waypoint);

                markerCount++;

                Intent intent = new Intent(UAVFlightMap.this, waypointDisplay.class);
                String message = "";
                for (int i=0; i<markerCount; i++){
                    message = message + "Waypoint #"+ i + ":"
                            + markers[i].getPosition().latitude + ":"
                            + markers[i].getPosition().longitude + ":"
                            + velcoity[i] + ":"
                            + altitude[i]+ ";";
                }
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
       // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private void updatemapmarkers(){

    }
}
