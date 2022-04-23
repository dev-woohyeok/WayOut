package com.example.wayout_ver_01.RecyclerView.Gallery;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wayout_ver_01.Activity.Gallery.GalleryBoard_reply;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_comment;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;

import java.text.ParseException;
import java.util.ArrayList;

public class GalleryRead_comment_adapter extends RecyclerView.Adapter<GalleryRead_comment_adapter.viewHolder> {
    Context context;
    ArrayList<DTO_comment> items = new ArrayList<>();

    public GalleryRead_comment_adapter(Context context) {
        this.context = context;
    }

    public void clearItems(){
        items.clear();
    }

    public void submit_item(DTO_comment item){
        items.add(0, item);
    }

    public void add_item(DTO_comment item){
        items.add(item);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.item_gallery_comment,parent,false);

        return new viewHolder(item,this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
//        // 댓글에 대댓글이 없으면 삭제
//        if(holder.reply_adapter.getItemCount() < 1){
//            holder.item_more.setVisibility(View.GONE);
//        }

        DTO_comment item = items.get(position);
        holder.item_writer.setText("작성자 : "+ item.getWriter());
        holder.item_content.setText("내용 : " +item.getContent());
        try {
            holder.item_date.setText(DateConverter.resultDateToString(item.getDate(),"M월 d일 a h:mm"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(!(item.getWriter().equals(PreferenceManager.getString(context,"autoNick")))){
            holder.item_menu.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private TextView item_writer, item_content, item_date, item_reply_btn, item_more_tv, item_write;
        private ImageView item_menu, item_more_iv;
        private RecyclerView item_rv;
        private ConstraintLayout item_more;
        private GalleryRead_reply_adapter reply_adapter;

        public viewHolder(@NonNull View itemView, GalleryRead_comment_adapter adapter) {
            super(itemView);
            item_write = itemView.findViewById(R.id.galleryComment_write_reply);
            reply_adapter = new GalleryRead_reply_adapter(itemView.getContext());
            item_writer = itemView.findViewById(R.id.galleryComment_writer);
            item_content = itemView.findViewById(R.id.galleryComment_content);
            item_date = itemView.findViewById(R.id.galleryComment_date);
//            item_reply_btn = itemView.findViewById(R.id.galleryComment_write_reply);
//            item_more_tv = itemView.findViewById(R.id.galleryComment_more_tv);
//            item_more_iv = itemView.findViewById(R.id.galleryComment_more_iv);
            item_menu = itemView.findViewById(R.id.galleryComment_menu);
//            item_rv = itemView.findViewById(R.id.galleryComment_reply_rv);
//            item_more = itemView.findViewById(R.id.galleryComment_more);

            item_write.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(itemView.getContext(), GalleryBoard_reply.class);
                        intent.putExtra("index", adapter.items.get(pos).getComment_index());
                        intent.putExtra("board_number", adapter.items.get(pos).getBoard_number());
                        intent.putExtra("writer", adapter.items.get(pos).getWriter());
                        intent.putExtra("content", adapter.items.get(pos).getContent());
                        intent.putExtra("date", adapter.items.get(pos).getDate());
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
