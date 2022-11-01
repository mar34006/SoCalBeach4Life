package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SetPolygons {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private List<Circle> circles;
    private Polyline currentPolyline;
    private Marker originMarker;
    private Marker destMarker;
    private Marker destMarker2;
    private List<Marker> extraMarkers;
    String duration;
    enum Mode { RESTAURANTS, LOTS };
    Mode mode;
    String beach_name;
    LatLng lot1, lot2;
    RestaurantPacks availableRestaurants;


    private static final int DEFAULT_STROKE = Color.argb(100, 220, 0, 0);
    private static final int DEFAULT_FILL = Color.argb(40, 0, 0, 140);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        this.beach_name = intent.getStringExtra("name");
        if(mode.equals("beach lots"))
        {
            this.mode = Mode.LOTS;
            double lot1loc[] = intent.getDoubleArrayExtra("lot1");
            double lot2loc[] = intent.getDoubleArrayExtra("lot2");
            this.lot1 = new LatLng(lot1loc[0], lot1loc[1]);
            this.lot2 = new LatLng(lot2loc[0], lot2loc[1]);
        }
        else // restaurants
        {
            this.mode = Mode.RESTAURANTS;
            this.availableRestaurants = (RestaurantPacks) intent.getSerializableExtra("restaurants");
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        destMarker = null;
        duration = null;
        circles = new ArrayList<Circle>();
        extraMarkers = new ArrayList<Marker>();
    }

    public void drawCircleAt(double lat, double lon, int radius_m)
    {
        drawCircleAt(lat, lon, radius_m, DEFAULT_STROKE, DEFAULT_FILL);
    }

    public void drawCircleAt(double lat, double lon, int radius_m, int strokeColor, int fillColor)
    {
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lon))
                .radius(radius_m)
                .strokeColor(strokeColor)
                .fillColor(fillColor));
        circles.add(circle);
    }

    public void moveToLocation(double lat, double lon)
    {
        LatLng loc = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }

    public void addLocationMarker(double lat, double lon, String markerText)
    {
        LatLng loc = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(loc).title(markerText));
    }

    public void drawPathFromAtoB(double latA, double lonA, double latB, double lonB)
    {
        LatLng start = new LatLng(latA, lonA);
        LatLng end = new LatLng(latB, lonB);
        new FetchMapData(MapsActivity.this).execute(start, end);
    }

    public void setPolyLine(PolylineOptions options)
    {
        if(currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline(options);
    }

    public void addDestinationInformation(MarkerOptions ops, String durr)
    {
        if(destMarker != null)
            destMarker.remove();
        duration = durr;
        destMarker = mMap.addMarker(ops);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i("map", "map ready");
        LatLng home = new LatLng(34.0168108, -118.2717179);
        LatLng bch_dst = new LatLng(34.0356343,-118.538096);
        LatLng dest, dest2;
        if(mode == Mode.LOTS)
        {
            dest = lot1;
            dest2 = lot2;
            originMarker = mMap.addMarker(new MarkerOptions().position(home).title("Home"));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bch_dst, 10.0f));
            MarkerOptions option1 = new MarkerOptions().position(dest2).title("Lot 1");
            MarkerOptions option2 = new MarkerOptions().position(dest2).title("Lot 2");
            extraMarkers.add(mMap.addMarker(option1));
            extraMarkers.add(mMap.addMarker(option2));
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker m)
                {
                    if(mode != Mode.LOTS)
                        return false;
                    if(m.getPosition() == originMarker.getPosition())
                        return false;
                    LatLng home = originMarker.getPosition();
                    LatLng dest = m.getPosition();
                    drawPathFromAtoB(home.latitude, home.longitude, dest.latitude, dest.longitude);
        /*if(dest != null)
            dest.remove();
         */
                    if(duration != null && !(m.getTitle().contains(" mins") || m.getTitle().contains(" min")))
                    {
                        m.setTitle(m.getTitle() + ": " + duration + " min.");
                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
                    return false;
                }
            });
        }
        else
        {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bch_dst, 13.0f));
            drawCircleAt(bch_dst.latitude, bch_dst.longitude, (int)(availableRestaurants.distance + 800));
            Log.i("INFO", String.format("distance: %d", availableRestaurants.distance));
            for (Restaurant r: availableRestaurants.restaurants)
            {
                Log.i("INFO", String.format("%s, %f", r.name, r.distance ));
                MarkerOptions option = new MarkerOptions();
                LatLng d =  new LatLng(r.coords[0], r.coords[1]);
                option.position(d);
                String name = r.name;
                double distance = r.distance;
                option.title(String.format("%s (%d ft)", name, (long)distance));
                option.snippet("Lot close to " + beach_name);
                extraMarkers.add(mMap.addMarker(option));
            }
            /*Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(-33.87365, 151.20689))
                .radius(1000)
                .strokeColor(Color.argb(100, 220, 0, 0))
                .fillColor(Color.argb(40, 0, 0, 140)));
            //dest = new LatLng(34.0223519,-118.2873057); // usc for now*/
        }
        //LatLng usc = new LatLng(34.0223519,-118.2873057);

        /*Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(-33.87365, 151.20689))
                .radius(1000)
                .strokeColor(Color.argb(100, 220, 0, 0))
                .fillColor(Color.argb(40, 0, 0, 140)));*/

        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                drawPathFromAtoB(home.latitude, home.longitude, latLng.latitude, latLng.longitude);
               /*if(dest != null)
                   dest.remove();
               if(duration != null)
                   dest = mMap.addMarker(new MarkerOptions().position(latLng).title(String.format("Destination (%s)", duration)));
               else
                   dest = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));

                mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });*/
    }
}
