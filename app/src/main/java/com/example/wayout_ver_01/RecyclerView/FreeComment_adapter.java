package com.example.wayout_ver_01.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_free_reply;

import java.util.ArrayList;

public class FreeComment_adapter extends RecyclerView.Adapter<FreeComment_adapter.viewHolder> {
    Context context;
    ArrayList<FreeRead_reply> items;

    public FreeComment_adapter(Context context) {
        this.context = context;
    }

    public void addItem(FreeRead_reply item) {
        items.add(item);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.item_free_comment_reply,parent,false);
        return new viewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {


        public viewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
