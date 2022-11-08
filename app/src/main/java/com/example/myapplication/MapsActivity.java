package com.example.myapplication;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SetPolygons {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private List<Circle> circles;
    private Polyline currentPolyline;
    private Marker originMarker;
    private Marker destMarker;
    private List<Marker> extraMarkers;
    String duration;
    enum Mode { RESTAURANTS, LOTS };
    Mode mode;
    String beach_name;
    LatLng lot1, lot2;
    String actual_beach_name;
    String destination_restaurant_name;
    LatLng beach_dest;
    String homeAddress = null;
    LatLng home;
    LatLng a_home = null, a_dest = null;
    RestaurantPacks availableRestaurants;

    FirebaseDatabase root;
    DatabaseReference reference;

    Boolean lotClicked = false;
    Boolean restaurantClicked = false;
    String selectedRestaurantName;
    String user;
    String user_reference;
    Boolean routesExist = false;
    Boolean forceDataChange = true;


    private static final int DEFAULT_STROKE = Color.argb(100, 220, 0, 0);
    private static final int DEFAULT_FILL = Color.argb(40, 0, 0, 140);

    enum TransportationMode { WALKING, DRIVING };

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
        user = intent.getStringExtra("user");
        this.beach_name = intent.getStringExtra("name");
        this.actual_beach_name = intent.getStringExtra("a_beach_name");
        double loc[] = intent.getDoubleArrayExtra("loc");
        this.homeAddress = intent.getStringExtra("my_location");
        this.beach_dest = new LatLng(loc[0], loc[1]);

        if (mode.equals("beach lots")) {
            this.mode = Mode.LOTS;
            double lot1loc[] = intent.getDoubleArrayExtra("lot1");
            double lot2loc[] = intent.getDoubleArrayExtra("lot2");
            this.lot1 = new LatLng(lot1loc[0], lot1loc[1]);
            this.lot2 = new LatLng(lot2loc[0], lot2loc[1]);
            double h_loc[] = intent.getDoubleArrayExtra("h_loc");
            this.home = new LatLng(h_loc[0], h_loc[1]);
            // SAVE ROUTE DATA COLLECTION
            root = FirebaseDatabase.getInstance();
            reference = root.getReference("Users");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(forceDataChange) {
                        for (DataSnapshot check_user : dataSnapshot.getChildren()) {
                            if (check_user.child("email").getValue().equals(user)) {
                                user_reference = check_user.getKey();
                                break;
                            }
                        }
                        dataSnapshot = dataSnapshot.child(user_reference).child("routes");
                        if(dataSnapshot.exists()) {routesExist = true;}
                        forceDataChange = false;
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("SecondFragment", "Failed to read value.", error.toException());
                }
            });
            DatabaseReference change = reference.push();
            change.setValue("Change");
            change.removeValue();


        } else // restaurants
        {
            this.mode = Mode.RESTAURANTS;
            this.availableRestaurants = (RestaurantPacks) intent.getSerializableExtra("restaurants");
            TextView saveRoute = (TextView)findViewById(R.id.saveRoute);
            saveRoute.setText("Select a restaurant");
        }
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

    public void addLocationMarker(double lat, double lon, String markerText, String snippet)
    {
        LatLng loc = new LatLng(lat, lon);
        extraMarkers.add(mMap.addMarker(new MarkerOptions().position(loc).title(markerText).snippet(snippet)));
    }

    public void drawPathFromAtoB(double latA, double lonA, double latB, double lonB, TransportationMode mode)
    {
        LatLng start = new LatLng(latA, lonA);
        LatLng end = new LatLng(latB, lonB);
        new FetchMapData(MapsActivity.this, mode).execute(start, end);
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
        duration = durr;
        if(!(destMarker.getTitle().contains(" mins") || destMarker.getTitle().contains(" min")))
        {
            destMarker.setTitle(destMarker.getTitle() + ": " + durr);
        }
        destMarker.showInfoWindow();
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i("map", "map ready");
        LatLng dest1, dest2;
        if(mode == Mode.LOTS)
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

                    TextView saveRoute = (TextView)findViewById(R.id.saveRoute);
                    saveRoute.setText("Click to save route");
                    lotClicked = true;

                    a_home = originMarker.getPosition();
                    a_dest = m.getPosition();
                    destMarker = m;
                    drawPathFromAtoB(a_home.latitude, a_home.longitude, a_dest.latitude, a_dest.longitude, TransportationMode.DRIVING);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(a_dest, 12.0f));
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
                //String title = String.format("%s (%d ft)", name, (long)distance);
                String title = String.format(name);
                Log.i("EXTRA REST INFO", String.format("%s (%f, %f)", r.name, r.coords[0], r.coords[1]));
                this.addLocationMarker(r.coords[0], r.coords[1], title, r.hours);
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker m)
                {
                    if(mode != Mode.RESTAURANTS)
                        return false;
                    if(m.getPosition() == beach_dest)
                        return false;

                    TextView saveRoute = (TextView)findViewById(R.id.saveRoute);
                    saveRoute.setText("View menu");
                    restaurantClicked = true;
                    destination_restaurant_name = m.getTitle();
                    int breakIndex = destination_restaurant_name.lastIndexOf(':');
                    if(breakIndex > 0)
                    {
                        destination_restaurant_name = destination_restaurant_name.substring(breakIndex);
                    }

                    //GERARDO
                    //DRAW PATH NEEDS TO CALCULATE TIME BY WALKING INSTEAD IF DRIVING
                    //THX
                    LatLng home = beach_dest;
                    LatLng dest = m.getPosition();
                    destMarker = m;
                    drawPathFromAtoB(home.latitude, home.longitude, dest.latitude, dest.longitude, TransportationMode.WALKING);

                    selectedRestaurantName = m.getTitle();


                    return false;
                }
            });
        }
    }

    public void onClickBack(View v)
    {
        this.finish();
    }

    public void onClickSaveRoute(View view){
        if (mode == Mode.LOTS) {
            if (lotClicked) {

                root = FirebaseDatabase.getInstance();
                reference = root.getReference("Users").child(user_reference);

                if (routesExist) {
                    reference = reference.child("routes");
                } else {
                    reference.child("routes").setValue(true);
                }
                DatabaseReference route = reference.push();
                Calendar currentTime = Calendar.getInstance();
                Calendar after = Calendar.getInstance();
                Pattern pattern = Pattern.compile("((\\d+)\\s+hour(s)?)?\\s*((\\d+)\\s+min(s)?)", Pattern.CASE_INSENSITIVE);
                Matcher m = pattern.matcher(duration.trim());
                int hours = 0, minutes = 0;
                if(m.find())
                {
                    if(m.group(2) != null)
                    {
                        hours = Integer.valueOf(m.group(2));
                    }
                    if(m.group(5) != null) {
                        minutes = Integer.valueOf(m.group(5));
                    }
                }
                after.add(Calendar.HOUR_OF_DAY, hours);
                after.add(Calendar.MINUTE, minutes);
                route.child("Start").setValue(this.homeAddress + ": " + String.format("%f, %f", a_home.latitude, a_home.longitude)); //GERARDO
                route.child("Destination").setValue(actual_beach_name + ": " + String.format("%f, %f", a_dest.latitude, a_dest.longitude)); // GERARDO
                String date = String.format("%d/%d/%d: ", currentTime.get(Calendar.MONTH)+1, currentTime.get(Calendar.DAY_OF_MONTH), currentTime.get(Calendar.YEAR));
                String time1 = String.format("%02d:%02d", currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE));
                String time2 = String.format("%02d:%02d", after.get(Calendar.HOUR_OF_DAY), after.get(Calendar.MINUTE));
                String formattedDuration = "Date: " + date + time1 + " - " + time2 + "\nDuration: " + duration;
                route.child("Time").setValue(formattedDuration); // GERARDO

                TextView changeText = (TextView) view;
                changeText.setText("Route saved!");
            }
        }
        else if(mode == Mode.RESTAURANTS){
            if(restaurantClicked){
                for (Restaurant r: availableRestaurants.restaurants)
                {
                    if(selectedRestaurantName.equals(r.name)){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(r.menu_url));
                        startActivity(browserIntent);
                    }
                }
            }
        }
    }
}
