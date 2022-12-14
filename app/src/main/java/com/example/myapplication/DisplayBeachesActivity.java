package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.collections.MarkerManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Beach implements Serializable {
    String hidden_name;
    String name;
    String hours;
    double loc[] = new double[2];
    public String getName()
    {
        return name;
    }
    public void setName(String n)
    {
        this.name = n;
    }
    public double getLat()
    {
        return loc[0];
    }
    public void setLat(double l)
    {
        this.loc[0] = l;
    }
    public double getLong()
    {
        return loc[1];
    }
    public void setLong(double l)
    {
        this.loc[1] = l;
    }
    public void setHours(String h) { this.hours = h; }
    public String getHours() { return hours; }
}

class ExtraMarker
{
    Marker m;
    String hidden;
    ExtraMarker(Marker _m)
    {
        this.m = _m;
    }
}

public class DisplayBeachesActivity extends AppCompatActivity implements OnMapReadyCallback
{
    FirebaseDatabase root;
    DatabaseReference reference;
    ArrayList<Beach> beaches = new ArrayList<>();
    String user;
    Boolean forceDataChange = true;
    List<ExtraMarker> markers = new ArrayList<ExtraMarker>();
    String homeAddress = null;
    double h_loc[] = new double[2];
    EditText address = null;
    Button submit = null;
    Marker me = null;
    View mapView;


    private GoogleMap mMap;
    private boolean permissionDenied = false;
    private static final int LOCATION_REQUESTED_CODE = 58;
    String selected_beach = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityMapsBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        setContentView(R.layout.activity_display_beaches);
        FragmentManager support = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) support.findFragmentById(R.id.beaches_map);
        mapView = mapFragment.getView();
        mapView.setContentDescription("MAP NOT READY");
        Button toProfileBtn = (Button)findViewById(R.id.profilePage);

        toProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayBeachesActivity.this, ProfileActivity.class));
            }
        });

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        reference = reference.child("beaches");
        DisplayBeachesActivity THIS = this;

        address = (EditText)findViewById(R.id.editTextTextPostalAddress);
        submit = (Button)findViewById(R.id.button);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i("DISPLAY BEACHES", dataSnapshot.toString());

                //for(Object beach: beachs.values())
                //{
                //    Log.i("DISPLAY BEACHES", beach.toString());
                //}
                //Log.i("DISPLAY BEACHES", ((Map<String, Object>)dataSnapshot.getValue()).toString());
                if (forceDataChange == true) {
                    Map<String, Object> beachs = (Map<String, Object>)dataSnapshot.getValue();
                    for (String beach_name : beachs.keySet()) {
                        Beach beach = dataSnapshot.child(beach_name).getValue(Beach.class);
                        beach.hidden_name = beach_name;
                        beaches.add(beach);
                        /*if (!get_beach.getValue().equals("Change")) {
                            beach.name = get_beach.child("name").getValue().toString();
                            beach.loc[0] = get_beach.child("lat").getValue(double.class);
                            beach.loc[1] = get_beach.child("long").getValue(double.class);
                            beaches.add(beach);
                        }*/
                    }
                    forceDataChange = false;
                    mapFragment.getMapAsync(THIS);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read values.", error.toException());
            }
        });

        DatabaseReference change = reference.push();
        change.setValue("Change");
        change.removeValue();
    }

    public void moveToLocationZoom(double lat, double lon, float zoom)
    {
        LatLng loc = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
    }

    public void addLocationMarker(double lat, double lon, String markerText, String hours, String hidden_name)
    {
        LatLng loc = new LatLng(lat, lon);
        MarkerOptions options = new MarkerOptions().position(loc).title(markerText).snippet(hours);
        Marker m = mMap.addMarker(options);
        ExtraMarker em = new ExtraMarker(m);
        em.hidden = hidden_name;
        markers.add(em);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (Beach b: beaches)
        {
            String hours = "";
            if(b.hours != null)
                hours = b.hours;
            else
                hours = "No hours available.";
            addLocationMarker(b.getLat(), b.getLong(), b.getName(), hours, b.hidden_name);
            Log.i("BEACH DISPLAY", String.format("%s: (%f, %f)", b.getName(), b.getLat(), b.getLong()));
        }
        mapView.setContentDescription("MAP READY");
        LatLng home = new LatLng(34.0168108, -118.2717179);
        moveToLocationZoom(home.latitude, home.longitude, 10.0f);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker m)
            {
                for(ExtraMarker em: markers)
                {
                    if(em.m.getTitle().equals(m.getTitle()))
                    {
                        selected_beach = em.hidden;
                        Log.i("DISPLAY BEACHES", em.hidden);
                    }
                }
                return false;
            }
        });
    }

    public void onClickBeach(View v)
    {
        if(selected_beach == null || homeAddress == null)
        {
            Toast.makeText(DisplayBeachesActivity.this, "Please enter and send address, then choose beach first!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("beach_name", selected_beach);
        intent.putExtra("user", user);
        intent.putExtra("h_loc", h_loc);
        intent.putExtra("my_location", homeAddress);
        startActivity(intent);
    }


    public void receivedLocation(LatLng loca)
    {
        if(loca == null)
        {
            homeAddress = null;
            Toast.makeText(DisplayBeachesActivity.this, "Improper Address, please try again!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("LOCATION RECEIVED", String.format("%f, %f", loca.latitude, loca.longitude));
        homeAddress = address.getText().toString().trim();
        h_loc[0] = loca.latitude;
        h_loc[1] = loca.longitude;
        if(me != null)
        {
            me.remove();
        }
        me = mMap.addMarker(new MarkerOptions().position(loca).title("me").alpha(0.4f));
        Toast.makeText(DisplayBeachesActivity.this, "Address loaded!", Toast.LENGTH_SHORT).show();

    }

    public void submitAddress(View v)
    {
        String addr = address.getText().toString().trim();
        new FetchCurrentLocation(DisplayBeachesActivity.this).execute(addr);
    }

}