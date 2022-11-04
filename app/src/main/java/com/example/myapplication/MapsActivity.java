package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

class Beach{
    String name;
    double[] loc = new double[2];
}

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SetPolygons {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private List<Circle> circles;
    private Polyline currentPolyline;
    private Marker originMarker;
    private Marker destMarker;
    private List<Marker> extraMarkers;
    String duration;
    enum Mode { RESTAURANTS, LOTS, INITIALIZE };
    Mode mode;
    String beach_name;
    LatLng lot1, lot2;
    LatLng beach_dest;
    RestaurantPacks availableRestaurants;

    FirebaseDatabase root;
    DatabaseReference reference;
    ArrayList<Beach> beaches = new ArrayList<Beach>();


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

        if (mode.equals("initialize")) {
            this.mode = Mode.INITIALIZE;

            root = FirebaseDatabase.getInstance();
            reference = root.getReference("beaches");

            Context context = this;
            ArrayList<Beach> beaches = new ArrayList<>();

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot get_beach : dataSnapshot.getChildren()) {

                        Beach beach = new Beach();

                        beach.name = get_beach.child("name").getValue().toString();

                        beach.loc[0] = get_beach.child("lat").getValue(double.class);
                        beach.loc[1] = get_beach.child("long").getValue(double.class);

                        beaches.add(beach);
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Failed to read values.", error.toException());
                }
            });

        } else {
            this.beach_name = intent.getStringExtra("name");
            double loc[] = intent.getDoubleArrayExtra("loc");
            this.beach_dest = new LatLng(loc[0], loc[1]);
            if (mode.equals("beach lots")) {
                this.mode = Mode.LOTS;
                double lot1loc[] = intent.getDoubleArrayExtra("lot1");
                double lot2loc[] = intent.getDoubleArrayExtra("lot2");
                this.lot1 = new LatLng(lot1loc[0], lot1loc[1]);
                this.lot2 = new LatLng(lot2loc[0], lot2loc[1]);
            } else // restaurants
            {
                this.mode = Mode.RESTAURANTS;
                this.availableRestaurants = (RestaurantPacks) intent.getSerializableExtra("restaurants");
            }
            destMarker = null;
            duration = null;
            circles = new ArrayList<Circle>();
            extraMarkers = new ArrayList<Marker>();
        }
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

    public void moveToLocationZoom(double lat, double lon, float zoom)
    {
        LatLng loc = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
    }

    public void addLocationMarker(double lat, double lon, String markerText)
    {
        LatLng loc = new LatLng(lat, lon);
        extraMarkers.add(mMap.addMarker(new MarkerOptions().position(loc).title(markerText)));
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

    public void addDestinationInformation(String durr)
    {
        Log.i("MAP", "destination clicked");
        if(!(destMarker.getTitle().contains(" mins") || destMarker.getTitle().contains(" min")))
        {
            destMarker.setTitle(destMarker.getTitle() + ": " + durr);
        }
        destMarker.showInfoWindow();
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i("map", "map ready");
        LatLng home = new LatLng(34.0168108, -118.2717179);
        LatLng dest1, dest2;

        if(mode == Mode.INITIALIZE){
            moveToLocationZoom(home.latitude, home.longitude, 15.0f);
            for(Beach beach: beaches){
                addLocationMarker(beach.loc[0], beach.loc[1], beach.name);
            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker m)
                {
                    if(mode != Mode.INITIALIZE)
                        return false;

                    Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                    intent.putExtra("beach_name", m.getTitle());
                    startActivity(intent);
                    return false;
                }
            });
        }

        else if(mode == Mode.LOTS)
        {
            dest1 = lot1;
            dest2 = lot2;
            originMarker = mMap.addMarker(new MarkerOptions().position(home).title("Home"));
            moveToLocationZoom(beach_dest.latitude, beach_dest.longitude, 15.0f);
            addLocationMarker(dest1.latitude, dest1.longitude, "Lot 1");
            addLocationMarker(dest2.latitude, dest2.longitude, "Lot 2");
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker m)
                {
                    if(mode != Mode.LOTS)
                        return false;
                    if(m.getPosition() == originMarker.getPosition())
                        return false;
                    LatLng home = originMarker.getPosition();
                    LatLng dest = m.getPosition();
                    destMarker = m;
                    drawPathFromAtoB(home.latitude, home.longitude, dest.latitude, dest.longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, 12.0f));
                    return false;
                }
            });
        }
        else
        {
            moveToLocationZoom(beach_dest.latitude, beach_dest.longitude, 16.0f);
            drawCircleAt(beach_dest.latitude, beach_dest.longitude, (int)(availableRestaurants.distance *0.31));
            Log.i("INFO", String.format("distance: %d", availableRestaurants.distance));
            for (Restaurant r: availableRestaurants.restaurants)
            {
                Log.i("INFO", String.format("%s, %f", r.name, r.distance ));
                String name = r.name;
                double distance = r.distance;
                String title = String.format("%s (%d ft)", name, (long)distance);
                addLocationMarker(r.coords[0], r.coords[1], title);
            }
        }
    }
}
