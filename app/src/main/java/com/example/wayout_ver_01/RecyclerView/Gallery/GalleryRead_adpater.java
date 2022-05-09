package com.example.wayout_ver_01.RecyclerView.Gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;
import com.example.wayout_ver_01.Retrofit.DTO_image;

import java.util.ArrayList;

public class GalleryRead_adpater extends RecyclerView.Adapter<GalleryRead_adpater.viewHolder> {
    private ArrayList<DTO_gallery> items = new ArrayList<>();
    private Context context;

    public GalleryRead_adpater(Context context) {
        this.context = context;
    }

    public void clearItems() {
        items.clear();
    }

    public void addItem(DTO_gallery item) {
        items.add(item);
    }

    public String getUri(int pos) {
        return items.get(pos).getImageUri();
    }

    public ArrayList<DTO_gallery> getItems() {
        return items;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.item_free_read_image,parent,false);
        return new viewHolder(item, this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        // 이미지 uri 불러오기
        DTO_gallery item = items.get(position);
        // 이미지 세팅
        Glide.with(context)
                .load(item.getImageUri())

                .fitCenter()
                .into(holder.galleryRead_image);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private ImageView galleryRead_image;

        public viewHolder(@NonNull View itemView, GalleryRead_adpater adapter) {
            super(itemView);

            galleryRead_image = itemView.findViewById(R.id.freeRead_container_image);
        }

    }
}
