package com.example.wayout_ver_01.RecyclerView.FreeBoard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wayout_ver_01.R;

import java.util.ArrayList;

public class FreeRead_adapter extends RecyclerView.Adapter<FreeRead_adapter.viewHolder> {
    Context context;
    ArrayList<FreeRead_image> items = new ArrayList<>();
    private final String TAG = this.getClass().getSimpleName();

    public FreeRead_adapter(Context context) {
        this.context = context;
    }

    public void add_item(FreeRead_image item){
        items.add(item);
//        Log.e(TAG, "내용 : items : " + items.get(0).getImage_uri());
    }
    public void clearItems(){
        items.clear();
    }

    public ArrayList<FreeRead_image> getItems(){
        return items;
    }

    public String getUri (int pos){
        return items.get(pos).getImage_uri();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_free_read_image, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FreeRead_image item = items.get(position);

//        Log.e(TAG, "내용 : item_uri : " + item.getImage_uri());
        Glide.with(context)
                .load(item.getImage_uri())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.image_uri);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView image_uri ;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            image_uri = itemView.findViewById(R.id.freeRead_container_image);
        }
    }
}
