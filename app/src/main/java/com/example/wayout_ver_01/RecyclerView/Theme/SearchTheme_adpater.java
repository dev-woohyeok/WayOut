package com.example.wayout_ver_01.RecyclerView.Theme;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Activity.Search.SearchTheme_read;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_theme;

import java.util.ArrayList;

public class SearchTheme_adpater extends RecyclerView.Adapter<SearchTheme_adpater.viewHolder> {
    Context context;
    ArrayList<DTO_theme> items = new ArrayList<>();

    public SearchTheme_adpater(Context context) {
        this.context = context;
    }

    public void addItem(DTO_theme item){
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void scrollItem(DTO_theme item){
        items.add(item);
    }

    public void clearItem(){
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_theme, parent,false);
        return new viewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DTO_theme item = items.get(position);

        holder.item_name.setText(item.getName());
        holder.item_genre.setText(item.getGenre());
        holder.item_cafe.setText(item.getCafe());
        holder.item_limit.setText(item.getLimit());
        holder.item_diff.setText(item.getDifficult());
        holder.item_rate.setText(item.getRate()+"");

        if(!item.getImage().equals("null")) {
            Glide.with(context)
                    .load(item.getImage())
                    .fitCenter()
                    .into(holder.item_image);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView item_name, item_genre, item_cafe, item_limit, item_diff, item_rate;
        ImageView item_image;

        public viewHolder(@NonNull View itemView, SearchTheme_adpater adpater) {
            super(itemView);

            item_name = itemView.findViewById(R.id.item_searchTheme_name);
            item_genre = itemView.findViewById(R.id.item_searchTheme_genre);
            item_cafe = itemView.findViewById(R.id.item_searchTheme_cafe);
            item_limit = itemView.findViewById(R.id.item_searchTheme_limit);
            item_diff = itemView.findViewById(R.id.item_searchTheme_difficult);
            item_image = itemView.findViewById(R.id.item_searchTheme_image);
            item_rate = itemView.findViewById(R.id.item_searchTheme_grade);

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(itemView.getContext(), SearchTheme_read.class);
                i.putExtra("theme_index", adpater.items.get(getBindingAdapterPosition()).getIndex());
                itemView.getContext().startActivity(i);
            });
        }
    }
}
