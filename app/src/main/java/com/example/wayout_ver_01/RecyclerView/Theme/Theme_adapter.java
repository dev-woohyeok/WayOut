package com.example.wayout_ver_01.RecyclerView.Theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.ItemTouchHelperListener;
import com.example.wayout_ver_01.Retrofit.DTO_theme;

import java.io.File;
import java.util.ArrayList;

public class Theme_adapter extends RecyclerView.Adapter<Theme_adapter.viewHolder> implements ItemTouchHelperListener {
    ArrayList<DTO_theme> items = new ArrayList<DTO_theme>();
    boolean theme_xbtn;
    Context context ;

    public Theme_adapter(Context context) {
        this.context = context;
    }

    public void addItem(DTO_theme item){
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void onDelete(){
        this.theme_xbtn = true;
    }

    public ArrayList<DTO_theme> getItems(){
        return items;
    }


    @NonNull
    @Override
    public Theme_adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme,parent,false);

        return new viewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull Theme_adapter.viewHolder holder, int position) {
        DTO_theme item = items.get(holder.getBindingAdapterPosition());

        if(theme_xbtn){
        holder.item_no.setVisibility(View.VISIBLE);
        }

        // 이미지 세팅
        if(item.getImages().size() > 0) {
            Glide.with(context)
                    .asBitmap()
                    .load(item.getImages().get(0))
                    .fitCenter()
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.item_image.setImageBitmap(resource);
                            items.get(holder.getBindingAdapterPosition()).setBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }

        // 테마 item 에 Data 세팅해주기
        holder.item_name.setText(items.get(holder.getBindingAdapterPosition()).getName());
        holder.item_diff.setText("난이도 : " +items.get(holder.getBindingAdapterPosition()).getDifficult());
        holder.item_limit.setText("제한시간 : " +items.get(holder.getBindingAdapterPosition()).getLimit());
        holder.item_cafe.setText(items.get(holder.getBindingAdapterPosition()).getCafe());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        // 이동할 객체 선택
        DTO_theme item = items.get(from_position);
        // 이동시킬 객체 삭제
        items.remove(from_position);
        // 이동시킬 객체 이동
        items.add(to_position,item);
        //데이터 이동 알림
        notifyItemChanged(from_position,to_position);
        return true;
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private TextView item_name, item_cafe, item_genre, item_limit, item_diff, item_grade;
        private ImageView item_image, item_no;

        public viewHolder(@NonNull View itemView, Theme_adapter adapter) {
            super(itemView);
            setView();

            item_no.setOnClickListener(v ->{
                int pos = getBindingAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    adapter.items.remove(pos);
                    adapter.notifyItemRemoved(pos);
                }
            });
        }

        private void setView() {
            item_no = itemView.findViewById(R.id.itemTheme_xBtn);
            item_name = itemView.findViewById(R.id.itemTheme_name);
            item_cafe = itemView.findViewById(R.id.itemTheme_cafe);
            item_genre = itemView.findViewById(R.id.itemTheme_genre);
            item_limit = itemView.findViewById(R.id.itemTheme_limit);
            item_diff = itemView.findViewById(R.id.itemTheme_difficult);
            item_grade = itemView.findViewById(R.id.itemTheme_grade);
            item_image = itemView.findViewById(R.id.itemTheme_image);
        }
    }
}
