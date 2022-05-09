package com.example.wayout_ver_01.RecyclerView.Theme;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wayout_ver_01.Activity.CreateShop.Search_shop;
import com.example.wayout_ver_01.Activity.Home;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_history;

import java.util.ArrayList;

public class Search_adapter extends RecyclerView.Adapter<Search_adapter.viewHolder> {
    Context context;
    ArrayList<DTO_history> items = new ArrayList<>();
    Search_shop my;

    public Search_adapter(Context context, Search_shop my) {
        this.context = context;
        this.my = my;
    }

    public void addItem(DTO_history item){
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void clearItems(){
        int size = items.size();
        for(int i = 0; i < size; i++ ){
            items.remove(0);
        }
        notifyItemRangeRemoved(0, size);
    }

    public ArrayList<DTO_history> getItems(){
        return items;
    }

    public ArrayList<String> getContents(){
        ArrayList<String> contents = new ArrayList<>();
        // 현재 검색어 리스트 데이터 싹 정리해서 보내줌
        for(int i = 0; i < items.size(); i++) {
            contents.add(items.get(i).getSearch());
        }
        return contents;
    }

    @NonNull
    @Override
    public Search_adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,parent,false);
        return new viewHolder(item, this);
    }

    @Override
    public void onBindViewHolder(@NonNull Search_adapter.viewHolder holder, int position) {
        DTO_history item = items.get(position);
        holder.item_tv.setText(item.getSearch());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private TextView item_tv;
        private ImageView item_x;

        public viewHolder(@NonNull View itemView, Search_adapter adapter) {
            super(itemView);

            item_tv = itemView.findViewById(R.id.item_history_tv);
            item_x = itemView.findViewById(R.id.item_history_x);

            // 제거
            item_x.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    adapter.items.remove(pos);
                    adapter.notifyItemRemoved(pos);
                }
            });

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                   String search = adapter.items.get(pos).getSearch();
                    Intent i = new Intent(itemView.getContext(), Home.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("검색어",search);
                    adapter.my.setResult(RESULT_OK, i);
                    adapter.my.finish();
                }
            });

        }
    }
}
