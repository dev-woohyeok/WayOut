package com.example.wayout_ver_01.RecyclerView.Gallery;

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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wayout_ver_01.Activity.Gallery.GalleryBoard_read;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.text.ParseException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryBoard_adapter extends RecyclerView.Adapter<GalleryBoard_adapter.viewHolder> {
    ArrayList<DTO_gallery> items = new ArrayList<>();
    Context context;

    public GalleryBoard_adapter(Context context) {
        this.context = context;
    }

    public void addItem (DTO_gallery item){
        items.add(item);
    }

    public void clearList() {
        items.clear();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_galleryboard,parent,false);

        return new viewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DTO_gallery item = items.get(position);
        // 이미지
        Glide.with(context)
                .load(item.getImage())
                .fitCenter()
                .into(holder.itemGallery_image);

        // 좋아요 버튼 세팅
        holder.is_click = item.isClick();
        if(holder.is_click){
            holder.itemGallery_like_btn.setImageResource(R.drawable.heartblack);
            holder.is_click = true;
        }else{
            holder.itemGallery_like_btn.setImageResource(R.drawable.heartwhite);
            holder.is_click = false;
        }
        // 카페명 세팅
        holder.itemGallery_cafe.setText("카페명 : " + item.getCafe());
        // 테마명 세팅
        holder.itemGallery_theme.setText("테마명 : " +item.getTheme());
        // 작성자 세팅
        holder.itemGallery_writer.setText("작성자명 : " + item.getWriter());
        // 날짜 세팅
        try {
            holder.itemGallery_date.setText(DateConverter.resultDateToString(item.getDate(),"yyyy-MM-dd"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 좋아요 갯수 세팅
        holder.itemGallery_like_num.setText(""+item.getTotal_like());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView itemGallery_image, itemGallery_like_btn;
        TextView itemGallery_cafe, itemGallery_theme, itemGallery_writer, itemGallery_date, itemGallery_like_num;
        boolean is_click;
        boolean click;

        public viewHolder(@NonNull View itemView, GalleryBoard_adapter adapter) {
            super(itemView);
            itemGallery_image = itemView.findViewById(R.id.itemGallery_image);
            itemGallery_like_btn = itemView.findViewById(R.id.itemGallery_like_btn);
            itemGallery_cafe = itemView.findViewById(R.id.itemGallery_cafe);
            itemGallery_theme = itemView.findViewById(R.id.itemGallery_theme);
            itemGallery_writer = itemView.findViewById(R.id.itemGallery_writer);
            itemGallery_date = itemView.findViewById(R.id.itemGallery_date);
            itemGallery_like_num = itemView.findViewById(R.id.itemGallery_like_num);

            // item 클릭시 게시판 넘어가는 메소드 -> 게시판 번호를 intent 로 넘겨준다.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        // 인텐트로 넘겨주는 정보 : 게시판 번호, 작성자
                        // 게시판 번호 : DB 요청시
                        // 작성자 : 글쓴이 확인
                        Intent intent = new Intent(itemView.getContext(), GalleryBoard_read.class);
                        intent.putExtra("board_number", adapter.items.get(pos).getBoard_number());
                        intent.putExtra("writer", adapter.items.get(pos).getWriter());
                        itemView.getContext().startActivity(intent);
                    }
                }
            });

            // 좋아요 버튼 클릭시 메소드
            itemGallery_like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String board_number = adapter.items.get(getAdapterPosition()).getBoard_number();

                    // 좋아요 등록 해제 메소드
                    if(is_click) {
                        itemGallery_like_btn.setImageResource(R.drawable.heartwhite);
                        is_click = false;
                        click_unlike(board_number);
                    }else{
                        itemGallery_like_btn.setImageResource(R.drawable.heartblack);
                        is_click = true;
                        click_like(board_number);
                    }


                }
            });
            
            // 게시물 클릭시 메소드

        }

        private void click_unlike(String board_number) {
            String user_id = PreferenceManager.getString(itemView.getContext(), "autoNick");
            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_gallery> call = retrofitInterface.deleteGalleryLike(user_id, board_number);
            call.enqueue(new Callback<DTO_gallery>() {
                @Override
                public void onResponse(Call<DTO_gallery> call, Response<DTO_gallery> response) {
                    if(response.isSuccessful() && response.body() != null) {
                        int total = response.body().getTotal_like();
                        itemGallery_like_num.setText(total +"");
                    }
                }

                @Override
                public void onFailure(Call<DTO_gallery> call, Throwable t) {

                }
            });

        }

        private void click_like(String board_number) {
            String user_id = PreferenceManager.getString(itemView.getContext(),"autoNick");
            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_gallery> call = retrofitInterface.writeGalleryLike(user_id,board_number);
            call.enqueue(new Callback<DTO_gallery>() {
                @Override
                public void onResponse(Call<DTO_gallery> call, Response<DTO_gallery> response) {
                    if(response.isSuccessful() && response.body() != null) {
                       int total = response.body().getTotal_like();
                       itemGallery_like_num.setText(total + "");
                    }
                }

                @Override
                public void onFailure(Call<DTO_gallery> call, Throwable t) {

                }
            });
        }
    }
}
//    int total = adapter.items.get(getAdapterPosition()).getTotal_like();
//    int plus;
//                    if(is_click){
//                        plus = -1;
//                        if(click){
//                            itemGallery_like_btn.setImageResource(R.drawable.heartblack);
//                            itemGallery_like_num.setText(total + "");
//                            click = false;
//                            click_like();
//                        }else {
//                            itemGallery_like_btn.setImageResource(R.drawable.heartwhite);
//                            itemGallery_like_num.setText((total + plus) + "");
//                            click = true;
//                            click_unlike();
//                        }
//
//                    }else {
//                        plus = 1;
//                        if(click){
//                            itemGallery_like_btn.setImageResource(R.drawable.heartwhite);
//                            itemGallery_like_num.setText(total + "");
//                            click = false;
//                        }else {
//                            itemGallery_like_btn.setImageResource(R.drawable.heartblack);
//                            itemGallery_like_num.setText((total + plus) + "");
//                            click = true;
//                        }
//
//                    }
