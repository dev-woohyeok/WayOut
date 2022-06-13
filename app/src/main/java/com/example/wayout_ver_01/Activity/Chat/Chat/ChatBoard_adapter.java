package com.example.wayout_ver_01.Activity.Chat.Chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Activity.Chat.ChatJoin;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.R;

import java.text.ParseException;
import java.util.ArrayList;

public class ChatBoard_adapter extends RecyclerView.Adapter<ChatBoard_adapter.viewHolder> {
    Context context;
    ArrayList<DTO_room> items = new ArrayList<>();

    public ChatBoard_adapter(Context context) {
        this.context = context;
    }

    public void addItem(DTO_room item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void itemsClear(){
        int size =items.size();
        items.clear();
        notifyItemRangeRemoved(0,size);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom, parent, false);
        return new viewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DTO_room item = items.get(position);
        Log.e("chatRoomadapter", "pos : " + position + " 참여자 : " + item.getJoin_number() + " 모집인원 : " +item.getRoom_max());
        if (item.getJoin_number() >= item.getRoom_max()) {
            holder.item_wait.setText("모집완료");
            holder.item_wait.setBackgroundResource(R.drawable.category_check_end);
        }else {
            holder.item_wait.setText("모집중");
            holder.item_wait.setBackgroundResource(R.drawable.category_check);
        }
        holder.item_title.setText(item.getRoom_name());
        holder.item_name.setText(item.getRoom_writer());
        holder.item_max.setText("/" + item.getRoom_max() + "명");
        holder.item_join.setText(""+ item.getJoin_number());
        try {
            holder.item_date.setText(DateConverter.resultDateToString(item.getRoom_date(), "yyyy-MM-dd"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(context)
                .load(item.getUser_image())
                .circleCrop()
                .into(holder.item_profile);
        Glide.with(context)
                .load(item.getRoom_image())
                .centerCrop()
                .into(holder.item_room_img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView item_wait, item_title, item_name, item_max, item_join, item_date;
        ImageView item_profile, item_room_img;

        public viewHolder(@NonNull View itemView, ChatBoard_adapter adapter) {
            super(itemView);

            item_wait = itemView.findViewById(R.id.item_room_wait);
            item_title = itemView.findViewById(R.id.item_room_title);
            item_name = itemView.findViewById(R.id.item_room_writer_name);
            item_max = itemView.findViewById(R.id.item_room_max);
            item_join = itemView.findViewById(R.id.item_room_join);
            item_date = itemView.findViewById(R.id.item_room_date);
            item_profile = itemView.findViewById(R.id.item_room_writer_image);
            item_room_img = itemView.findViewById(R.id.item_room_image);

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if(pos != RecyclerView.NO_POSITION) {
                    DTO_room room = adapter.items.get(getBindingAdapterPosition());
                    if(room.getJoin_number() == room.getRoom_max()){
                        Toast.makeText(adapter.context, "모집이 완료된 방입니다.",Toast.LENGTH_SHORT).show();
                    }else {
                        DTO_room item = adapter.items.get(getBindingAdapterPosition());
                        Intent intent = new Intent(itemView.getContext(), ChatJoin.class);
                        intent.putExtra("room_index", item.getRoom_id());
                        itemView.getContext().startActivity(intent);
                        Log.e("chatBoard_adapter,91", "room_index : " + item.getRoom_id());
                    }
                }
            });
        }
    }
}
