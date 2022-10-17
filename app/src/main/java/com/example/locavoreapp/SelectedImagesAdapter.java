package com.example.locavoreapp;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder> {
    private final List<Uri> images;
    DeleteSelectedImage delete;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selected_images, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.selectedImage.setImageURI(images.get(position));
        holder.crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete.setClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public SelectedImagesAdapter(List<Uri> img, DeleteSelectedImage del){
        this.images = img;
        this.delete = del;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView selectedImage;
        ImageView crossButton;
        ViewHolder(View view){
            super(view);
            selectedImage = view.findViewById(R.id.image_selected);
            crossButton = view.findViewById(R.id.close_button);
        }
    }
}
