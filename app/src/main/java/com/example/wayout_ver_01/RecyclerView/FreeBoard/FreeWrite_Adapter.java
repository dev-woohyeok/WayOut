package com.example.wayout_ver_01.RecyclerView.FreeBoard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_img;

import java.util.ArrayList;

public class FreeWrite_Adapter extends RecyclerView.Adapter<FreeWrite_Adapter.ViewHolder> implements ItemTouchHelperListener {

    ArrayList<DTO_img> items = new ArrayList<>();
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    Context context;
    TextView textView;
    private final String TAG = this.getClass().getSimpleName();

    public FreeWrite_Adapter(Context context, TextView textView) {

        this.context = context;
        this.textView = textView;
    }

    public void addItem(DTO_img item, int pos) {
        this.items.add(pos, item);
        Log.e(TAG, "내용 : items : " + items.get(pos).getImageUri());
    }

    public ArrayList<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public void setItems(ArrayList<DTO_img> items) {
        this.items = items;
    }

    public void setItem(String uri) {
        items.add(new DTO_img(uri));
    }

    public void clearBitmaps(){
        bitmaps.clear();
    }

    public ArrayList<DTO_img> getItems() {
        return this.items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_freewrite, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUri = items.get(holder.getAdapterPosition()).getImageUri();

        Log.e(TAG, "내용 : onBindViewHolder : " + imageUri);
        Glide.with(context)
                // 비트맵으로 불러오기
                .asBitmap()
                .load(imageUri)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.freeWrite_img.setImageBitmap(resource);
//                        bitmaps.add(resource);
                        items.get(holder.getAdapterPosition()).setBitmap(resource);
                        Log.e(TAG, "내용 : bitmap : " + resource);
                        Log.e(TAG, "내용 : bitmaps : " +  bitmaps);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        // 이동 객체 저장
        DTO_img uri = items.get(from_position);
//        Bitmap bitmap = bitmaps.get(from_position);
        // 이동시킬 객체 삭제
        // 이동 시킬 객체 이동
        items.remove(from_position);
        items.add(to_position, uri);
//        bitmaps.remove(from_position);
//        bitmaps.add(to_position, bitmap);
        // 데이터 이동 알림
        notifyItemMoved(from_position, to_position);
        return true;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView freeWrite_img;
        ImageView freeWrite_remove;

        public ViewHolder(@NonNull View itemView, FreeWrite_Adapter freeWrite_adapter) {
            super(itemView);

            freeWrite_img = itemView.findViewById(R.id.freeWrite_container);
            freeWrite_remove = itemView.findViewById(R.id.freeWrite_remove);

            freeWrite_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 현재 클릭하는 position 가져오기
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        freeWrite_adapter.items.remove(pos);
                        freeWrite_adapter.textView.setText(freeWrite_adapter.getItemCount() + "/3");
                    }
                    freeWrite_adapter.notifyItemRemoved(pos);

                }
            });

        }
    }
}
