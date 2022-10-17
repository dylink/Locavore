package com.example.locavoreapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private final ArrayList<Offreclass> listOffre;
    private final LayoutInflater layoutInflater;
    private final Context context;
    FirebaseUser user;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public CustomAdapter(Context context, ArrayList<Offreclass>listOffre){
        this.listOffre = listOffre;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listOffre.size();
    }

    @Override
    public Object getItem(int position) {
        return listOffre.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OffreViewHolder holder;
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.view_offreviewholder,null);
            holder = new OffreViewHolder();
            holder.titre = (TextView) convertView.findViewById(R.id.titre);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.prix = (TextView) convertView.findViewById(R.id.prix);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.fav = (LikeButton) convertView.findViewById(R.id.fav);
            convertView.setTag(holder);
        }else{
            holder = (OffreViewHolder) convertView.getTag();
        }

        final Offreclass offre = this.listOffre.get(position);
        holder.titre.setText(offre.titre);
        holder.text.setText(offre.text);
        holder.prix.setText(offre.prix + "€");
        holder.fav.setVisibility(View.VISIBLE);

        DatabaseReference keyRef = FirebaseDatabase.getInstance().getReference().child("offres").child(offre.titre);
        keyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> children = snapshot.child("ListID").getChildren();
                for (DataSnapshot child : children){
                    if(child.getKey().equals(user.getUid())){
                        holder.fav.setLiked(true);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        if(offre.nbImages > 0){
            StorageReference storageReference2 = storageReference.child("ImagesOffres/" + offre.titre + "/" + offre.titre + 0);
            Glide.with(this.context).using(new FirebaseImageLoader()).load(storageReference2).into(holder.image);
        }
        else{
            holder.image.setImageResource(R.drawable.noimage);
        }
        return convertView;
    }

    static class OffreViewHolder {
        public TextView titre;
        public TextView text;
        public TextView prix;
        public ImageView image;
        public LikeButton fav;
    }

}
