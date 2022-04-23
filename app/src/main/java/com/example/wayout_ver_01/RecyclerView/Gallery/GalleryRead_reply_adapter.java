package com.example.wayout_ver_01.RecyclerView.Gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_comment;

import java.util.ArrayList;

public class GalleryRead_reply_adapter extends RecyclerView.Adapter<GalleryRead_reply_adapter.viewHolder> {
    ArrayList<DTO_comment> item = new ArrayList<>();
    Context context;

    public GalleryRead_reply_adapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_free_comment_reply,parent,false);

        return new viewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        public viewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
