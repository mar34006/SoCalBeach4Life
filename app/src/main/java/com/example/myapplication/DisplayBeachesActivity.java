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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    double loc[] = new double[2];

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public double getLat() {
        return loc[0];
    }

    public void setLat(double l) {
        this.loc[0] = l;
    }

    public double getLong() {
        return loc[1];
    }

    public void setLong(double l) {
        this.loc[1] = l;
    }
}

class ExtraMarker {
    Marker m;
    String hidden;

    ExtraMarker(Marker _m) {
        this.m = _m;
    }
}

public class DisplayBeachesActivity extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseDatabase root;
    DatabaseReference reference;
    ArrayList<Beach> beaches = new ArrayList<>();
    String user;
    Boolean forceDataChange = true;
    List<ExtraMarker> markers = new ArrayList<ExtraMarker>();

    private GoogleMap mMap;
    private FusedLocationProviderClient mLoc;
    double myLocation[] = new double[2];
    String selected_beach = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityMapsBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        setContentView(R.layout.activity_display_beaches);
        FragmentManager support = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) support.findFragmentById(R.id.beaches_map);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        root = FirebaseDatabase.getInstance();
        reference = root.getReference();
        reference = reference.child("beaches");
        DisplayBeachesActivity THIS = this;

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
                    Map<String, Object> beachs = (Map<String, Object>) dataSnapshot.getValue();
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
        this.mLoc = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getLocation()
    {
        @SuppressLint("MissingPermission")
        Task<Location> tsk = this.mLoc.getLastLocation();
        tsk.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
                    Log.i("PERMISSION", "Success getting location");
                    Log.i("LOCATION", location.toString());
                    myLocation[0] = location.getLatitude();
                    myLocation[1] = location.getLongitude();
                }
                else
                {
                    Log.i("LOCATION", "Location was Null!");
                }

            }
        });
        tsk.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("PERMISSION", "Failure getting location");
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            getLocation();
        }
        else
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                Log.i("PERMISSION", "show rationale");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 525);
                getLocation();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 525);
            }
        }
    }

    public void moveToLocationZoom(double lat, double lon, float zoom) {
        LatLng loc = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
    }

    public void addLocationMarker(double lat, double lon, String markerText, String hidden_name) {
        LatLng loc = new LatLng(lat, lon);
        MarkerOptions options = new MarkerOptions().position(loc).title(markerText);
        Marker m = mMap.addMarker(options);
        ExtraMarker em = new ExtraMarker(m);
        em.hidden = hidden_name;
        markers.add(em);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (Beach b : beaches) {
            addLocationMarker(b.getLat(), b.getLong(), b.getName(), b.hidden_name);
            Log.i("BEACH DISPLAY", String.format("%s: (%f, %f)", b.getName(), b.getLat(), b.getLong()));
        }
        LatLng home;
        if (this.myLocation[0] != 0.0) {
            home = new LatLng(this.myLocation[0], this.myLocation[1]);
        } else {
            home = new LatLng(34.0168108, -118.2717179);
        }
        moveToLocationZoom(home.latitude, home.longitude, 10.0f);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker m) {
                for (ExtraMarker em : markers) {
                    if (em.m.getTitle().equals(m.getTitle())) {
                        selected_beach = em.hidden;
                        Log.i("DISPLAY BEACHES", em.hidden);
                    }
                }
                return false;
            }
        });
    }

    public void onClickBeach(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("beach_name", selected_beach);
        intent.putExtra("my_location", this.myLocation);
        startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int code, String permissions[], int grants[]) {
        //super.onRequestPermissionsResult(code, permissions, grants);
        if(code == 525)
        {
            if(grants.length > 0 && grants[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("PERMISSION", "Permission Granted!");
            }
            else
            {
                Log.i("PERMISSION", "Permission Not Granted!");
            }
        }
        switch (code) {
            case 1:
                if (grants.length > 0 && grants[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DisplayBeachesActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    this.mLoc = LocationServices.getFusedLocationProviderClient(this);
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        this.mLoc.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Log.i("LOC", String.format("(%f, %f)", location.getLatitude(), location.getLongitude()));
                                    myLocation[0] = location.getLatitude();
                                    myLocation[1] = location.getLongitude();
                                }
                            }
                        });
                    } catch(SecurityException e)
                    {}
                }
                else
                    Toast.makeText(DisplayBeachesActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }
}