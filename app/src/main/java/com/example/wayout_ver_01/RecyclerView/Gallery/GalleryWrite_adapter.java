package com.example.wayout_ver_01.RecyclerView.Gallery;

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
import com.example.wayout_ver_01.RecyclerView.FreeBoard.ItemTouchHelperListener;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;
import com.example.wayout_ver_01.Retrofit.DTO_image;

import java.util.ArrayList;

public class GalleryWrite_adapter extends RecyclerView.Adapter<GalleryWrite_adapter.viewHolder> implements ItemTouchHelperListener {
    ArrayList<DTO_gallery> items = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    ArrayList<DTO_image> list = new ArrayList<>();
    TextView tv_imageNumber;
    Context context;

    public GalleryWrite_adapter(Context context, TextView tv_imageNumber) {
        this.context = context;
        this.tv_imageNumber = tv_imageNumber;
    }

    public void setBitmap(Bitmap bitmap) {
        this.items.add(new DTO_gallery(bitmap));
    }

    public void setUri(String uri) {
        this.items.add(new DTO_gallery(uri));
    }

    public void setItems(ArrayList<DTO_gallery> items) {
        this.items = items;
    }

    public void addItems(String item) {
        items.add(new DTO_gallery(item));
        notifyItemInserted(items.size());
    }

    public ArrayList<String> getImages() {
        images.clear();
        for (int i = 0; i < items.size(); i++) {
            images.add(items.get(i).getImageUri());
        }
        return images;
    }

    public void clearImages() {
        items.clear();
        notifyDataSetChanged();
    }

    public void setUriList(ArrayList<String> uriList) {
        for (int i = 0; i < uriList.size(); i++) {
            items.add(new DTO_gallery(uriList.get(i)));
            Log.e("수정모드 인텐트 uri 확인", " uri : " + uriList.get(i));
        }
        ;
    }

    public ArrayList<DTO_gallery> getItems() {
        return items;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_freewrite, parent, false);

        return new viewHolder(view, this, tv_imageNumber);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DTO_gallery item = items.get(position);
        String imageUri = item.getImageUri();
        tv_imageNumber.setText(items.size()+"/3");

        Glide.with(context)
                // 비트맵으로 불러오기
                .asBitmap()
                .load(imageUri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // 다 비트맵으로 전환해서 저장한다.
                        holder.galleryWrite_img.setImageBitmap(resource);
                        items.get(position).setBitmap(resource);
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
        DTO_gallery item = items.get(from_position);
        // 이동시킬 객체 삭제
        items.remove(from_position);
        // 이동 시킬 객체 이동
        items.add(to_position, item);
        // 데이터 이동 알림
        notifyItemMoved(from_position, to_position);
        return true;
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private ImageView galleryWrite_img;
        private ImageView galleryWrite_remove;

        public viewHolder(@NonNull View itemView, GalleryWrite_adapter adapter, TextView textView) {
            super(itemView);
            galleryWrite_img = itemView.findViewById(R.id.freeWrite_container);
            galleryWrite_remove = itemView.findViewById(R.id.freeWrite_remove);

            // 삭제 버튼 클릭시 리사이클러뷰에서 해당 아이템 삭제
            galleryWrite_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        adapter.items.remove(pos);
                        adapter.tv_imageNumber.setText(adapter.getItemCount() + "/3");
                        adapter.notifyItemRemoved(pos);
                    }
                }
            });
        }
    }
}
