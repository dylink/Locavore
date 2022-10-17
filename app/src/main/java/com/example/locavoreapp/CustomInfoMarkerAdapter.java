package com.example.locavoreapp;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CustomInfoMarkerAdapter implements GoogleMap.InfoWindowAdapter {
    private final Activity context;
    StorageReference storageReference;

    public CustomInfoMarkerAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        storageReference = FirebaseStorage.getInstance().getReference();

        View view = context.getLayoutInflater().inflate(R.layout.custom_info_marker, null);
        TextView titre = (TextView) view.findViewById(R.id.titre);
        StorageReference storageReference2 = storageReference.child("ImagesOffres/" + marker.getTitle() + "/" + marker.getTitle() + 0);
        titre.setText(marker.getTitle());
        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(marker.getSnippet());
        ImageView image = (ImageView) view.findViewById(R.id.image);
        Glide.with(this.context).using(new FirebaseImageLoader()).load(storageReference2).into(image);

        return view;
    }
}
