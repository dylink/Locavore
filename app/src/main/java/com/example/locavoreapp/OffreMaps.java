package com.example.locavoreapp;

import android.view.Menu;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class OffreMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public ArrayList<Offreclass> offreListe = new ArrayList<>();
    private DatabaseReference mPostReference;
    FloatingActionButton mapsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_offre);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offre_maps);
        mPostReference = FirebaseDatabase.getInstance().getReference().child("offres");
        mapsButton = (FloatingActionButton) findViewById(R.id.mapsButton);
        mapsButton.setImageResource(R.drawable.menu);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Offreclass offre = child.getValue(Offreclass.class);
                    offreListe.add(offre);
                }

                for (Offreclass offreclass : offreListe) {
                    if (!(offreclass.longitude.equals("")) && !(offreclass.latitude.equals(""))) {
                        mMap.addMarker(new MarkerOptions()
                                .title(offreclass.titre)
                                .snippet(offreclass.text)
                                .position(new LatLng(Double.parseDouble(offreclass.latitude), Double.parseDouble(offreclass.longitude))));
                    }
                }

                CustomInfoMarkerAdapter adapter = new CustomInfoMarkerAdapter(OffreMaps.this);
                mMap.setInfoWindowAdapter(adapter);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        };
        mPostReference.addValueEventListener(postListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuoffre, menu);
        return true;
    }

    public void showOffre (View view){
        finish();
        overridePendingTransition(R.anim.no_animation,R.anim.slide_down);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.no_animation,R.anim.slide_down);
    }

    public void placeMarker (String str, double latitude, double longitude){
        if (mMap != null) {
            LatLng marker = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
            mMap.addMarker(new MarkerOptions().title(str).position(marker));
        }
    }



}