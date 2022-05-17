package com.example.wayout_ver_01.RecyclerView.Theme;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Activity.Search.SearchCafe_read;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_shop;

import java.util.ArrayList;

public class SearchCafe_adpater extends RecyclerView.Adapter<SearchCafe_adpater.viewHolder> {
    Context context;
    ArrayList<DTO_shop> items = new ArrayList<>();

    public SearchCafe_adpater(Context context) {
        this.context = context;
    }

    public void addItem(DTO_shop item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void scrollItem(DTO_shop item) {
        items.add(item);
    }

    public void clearItems() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public SearchCafe_adpater.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_shop, parent, false);

        return new viewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchCafe_adpater.viewHolder holder, int position) {
        DTO_shop item = items.get(position);

        holder.item_rate.setText("" + item.getTotal());
        holder.item_name.setText(item.getName());
        if (!item.getImage().equals("null")) {
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
        ImageView item_image;
        TextView item_name, item_rate;

        public viewHolder(@NonNull View itemView, SearchCafe_adpater adpater) {
            super(itemView);

            item_name = itemView.findViewById(R.id.item_searchShop_name);
            item_image = itemView.findViewById(R.id.item_searchShop_image);
            item_rate = itemView.findViewById(R.id.item_searchShop_grade);

            itemView.setOnClickListener(v -> {
                String index = adpater.items.get(getBindingAdapterPosition()).getIndex();
                Intent intent = new Intent(itemView.getContext(), SearchCafe_read.class);
                intent.putExtra("인덱스", index);
                itemView.getContext().startActivity(intent);
                Log.e("searchCafe_adapter, 84", "index : " + index);
            });
        }
    }
}
