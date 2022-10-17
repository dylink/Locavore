package com.example.locavoreapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Offre extends AppCompatActivity implements OnMapReadyCallback {
    TextView titreT;
    TextView textT;
    TextView prixT;
    Offreclass offre;
    LikeButton likeButton;
    boolean liked;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    List<Uri> images;
    CarouselView carouselView;
    int nbImages;
    FirebaseUser user;
    boolean isCollapsed = false;
    TextView nbLikes;
    TextView date;
    SupportMapFragment mapFragment;
    GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_offre);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("offres");
        textT = (TextView) findViewById(R.id.text);
        likeButton = (LikeButton) findViewById(R.id.likebutton);
        liked=false;
        user = FirebaseAuth.getInstance().getCurrentUser();
        final ImageView arrow = (ImageView) findViewById(R.id.more_button);
        images = new ArrayList<>();
        nbImages = 0;
        date = (TextView) findViewById(R.id.date);
        titreT = (TextView) findViewById(R.id.titre);
        prixT = (TextView) findViewById(R.id.prix);
        carouselView = (CarouselView) findViewById(R.id.image);
        nbLikes = (TextView) findViewById(R.id.nblikes);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.main_scrollview);
        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    case MotionEvent.ACTION_UP:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });


        textT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    if (isCollapsed) {
                        textT.setMaxLines(Integer.MAX_VALUE);
                        findViewById(R.id.more_button).setRotation(180);
                    } else {
                        findViewById(R.id.more_button).setRotation(0);
                        textT.setMaxLines(5);
                    }
                    isCollapsed = !isCollapsed;
            }
        });

        Intent intent = getIntent();
        final String titre = intent.getStringExtra("offre");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                offre = snapshot.child(titre).getValue(Offreclass.class);
                titreT.setText(offre.titre);
                textT.setText(offre.text);
                prixT.setText(offre.prix + "€");
                nbImages = offre.nbImages;

                if(!offre.latitude.equals("")){
                    LatLng position = new LatLng(Double.parseDouble(offre.latitude), Double.parseDouble(String.valueOf(offre.longitude)));
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5));
                    gMap.addMarker(new MarkerOptions().title(offre.titre).position(position));
                }
                carouselView.setImageListener(imageListener);
                if(nbImages == 0){
                    carouselView.setPageCount(1);
                }
                else{
                    carouselView.setPageCount(nbImages);
                }
                if(offre.text.length() > 150) {
                    arrow.setVisibility(View.VISIBLE);
                    isCollapsed = true;
                }
                nbLikes.setText(getString(R.string.favorite)+" : " + offre.nbLikes);
                date.setText(offre.date);

                databaseReference.child(offre.titre).child("ListID").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot snapshot) {
                        if (snapshot.child(user.getUid()).exists()) {
                            likeButton.setLiked(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                offre.nbLikes++;
                Map<String, Object> map = new HashMap<>();
                map.put("nbLikes", offre.nbLikes);
                databaseReference.child(offre.titre).updateChildren(map);
                databaseReference.child(offre.titre).child("ListID").child(user.getUid()).setValue("0");
                nbLikes.setText(getString((R.string.favorite)) +" : " + offre.nbLikes);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                offre.nbLikes--;
                Map<String, Object> map = new HashMap<>();
                map.put("nbLikes", offre.nbLikes);
                databaseReference.child(offre.titre).updateChildren(map);
                databaseReference.child(offre.titre).child("ListID").child(user.getUid()).removeValue();
                nbLikes.setText(getString(R.string.favorite)+" : " + offre.nbLikes);
            }
        });
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            if(nbImages == 0){
                imageView.setImageResource(R.drawable.noimage);
            }
            else{
                StorageReference storageReference2 = storageReference.child("ImagesOffres/" + offre.titre + "/" + offre.titre + position);
                Glide.with(Offre.this).using(new FirebaseImageLoader()).load(storageReference2).into(imageView);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(offre.userId.equals(user.getUid())) {
            getMenuInflater().inflate(R.menu.menuoffre, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.poubelle:
                final CharSequence[] options = { getString(R.string.askToDeleteOffer), getString(R.string.cancel) };
                AlertDialog.Builder builder = new AlertDialog.Builder(Offre.this);
                builder.setTitle(getString(R.string.askToDeleteOffer));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals(getString(R.string.deleteOffer))){
                            databaseReference.child(offre.titre).removeValue();
                            for(int i = 0; i<nbImages; i++){
                                storageReference.child("ImagesOffres/" + offre.titre + "/" + offre.titre + i).delete();
                            }

                            finish();
                            System.exit(0);
                        }
                        else if (options[item].equals(getString(R.string.cancel))){
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

    }
}