package com.example.wayout_ver_01.Activity.Chat.Chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Activity.Chat.Send;
import com.example.wayout_ver_01.Activity.Chat.Service_chat;
import com.example.wayout_ver_01.Class.JsonMaker;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.net.Socket;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatDrawer_adapter extends RecyclerView.Adapter<ChatDrawer_adapter.VH> {
    ArrayList<DTO_message> items = new ArrayList<>();
    Context context;
    private String room_id;
    private String room_title;

    @Override
    public void setStateRestorationPolicy(@NonNull StateRestorationPolicy strategy) {
        super.setStateRestorationPolicy(strategy);
    }

    public void setRoom_title (String room_title){
        this.room_title = room_title;
    }

    public ChatDrawer_adapter(Context context) {
        this.context = context;
    }

    public void addItem(DTO_message item) {
        this.items.add(item);
        notifyItemInserted(items.size());
    }

    public void clearItem(){
        int size = getItemCount();
        items.clear();
        notifyItemRangeRemoved(0,size);
    }

    public void joinItem(String name, String writer, String image){
        DTO_message item = new DTO_message();
        item.setName(name);
        item.setRoom(writer);
        item.setImage(image);
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void deleteItem(String name, String writer, String image){
        for(int i = 0; i<items.size(); i++){
            if(items.get(i).getName().equals(name) && items.get(i).getRoom().equals(writer) && items.get(i).getImage().equals(image)){
                items.remove(items.get(i));
                notifyItemRemoved(i);
            }
        }
    }

    public void setList(ArrayList<DTO_message> list){
        this.items = list;
        notifyItemRangeChanged(0,list.size());
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer_user, parent, false);
        return new VH(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        DTO_message item = items.get(position);
        String user_id = PreferenceManager.getString(context, "userId");
        holder.item_name.setText(item.getName());
        Glide.with(context).load(item.getImage()).circleCrop().into(holder.item_image);

        /* 방장일때 */
        if (item.getName().equals(item.getRoom())) {
            holder.item_star.setVisibility(View.VISIBLE);
        } else {
            holder.item_star.setVisibility(View.GONE);
        }

        // 남이 만든 방인 경우
        if (!user_id.equals(item.getRoom())) {
            holder.item_kick.setVisibility(View.GONE);
        } else {
            holder.item_kick.setVisibility(View.VISIBLE);
        }

        // 내가 아이디인 경우
        if (item.getName().equals(user_id)) {
            holder.item_kick.setVisibility(View.GONE);
            holder.item_follow.setVisibility(View.GONE);
        }


        Log.e("//===========//", "================================================");
        Log.e("", "\n" + "[ DrawerAdapter user_id : " + user_id + " ]");
        Log.e("", "\n" + "[ DrawerAdapter join_name : " + item.getName() + " ]");
        Log.e("", "\n" + "[ DrawerAdapter user_image : " + item.getImage() + " ]");
        Log.e("", "\n" + "[ DrawerAdapter writer_Id : " + item.getRoom() + " ]");
        Log.e("//===========//", "================================================");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView item_name, item_kick, item_follow;
        ImageView item_image, item_star;


        public VH(@NonNull View itemView, ChatDrawer_adapter adapter) {
            super(itemView);
            item_star = itemView.findViewById(R.id.item_drawer_star);
            item_name = itemView.findViewById(R.id.item_drawer_name);
            item_follow = itemView.findViewById(R.id.item_drawer_friend);
            item_kick = itemView.findViewById(R.id.item_drawer_kick);
            item_image = itemView.findViewById(R.id.item_drawer_image);


            item_kick.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {

                    DTO_message item = adapter.items.get(pos);
                    RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                    Call<DTO_room> call = retrofitInterface.deleteRoomOut(adapter.room_id, item.getName());
                    call.enqueue(new Callback<DTO_room>() {
                        @Override
                        public void onResponse(Call<DTO_room> call, Response<DTO_room> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String msg = JsonMaker.makeJson("kick",adapter.room_id,item.getName(),item.getName()+" 님을 내보냈습니다.","","","io", item.getRoom_name());
                                Socket socket = Service_chat.getSocket();
                                Send send = new Send(socket,msg);
                                send.start();
                            }
                        }

                        @Override
                        public void onFailure(Call<DTO_room> call, Throwable t) {
                            Log.e("//===========//", "================================================");
                            Log.e("", "\n" + "[ ChatRoom Drawer kick 에러  :  " + t + " ]");
                            Log.e("//===========//", "================================================");
                        }
                    });

                }
            });

        }
    }
}
