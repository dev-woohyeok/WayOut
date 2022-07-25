package com.example.wayout_ver_01.Activity.Chat.Chat;

import static com.example.wayout_ver_01.Class.JsonMaker.DtoToJson;

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
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Predicate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Invite_adapter extends RecyclerView.Adapter<Invite_adapter.viewHolder> {
    ArrayList<DTO_follow> items = new ArrayList<>();
    String room_id;
    String room_name;
    Context context;

    public Invite_adapter(Context context) {
        this.context = context;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public void setRoom_name(String room_name){
        this.room_name = room_name;
    }

    public void addItem(DTO_follow item) {
        items.add(item);
        notifyItemInserted(getItemCount());
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite, parent, false);
        return new viewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DTO_follow item = items.get(position);
        holder.item_name.setText(item.getUser_id());
        Glide.with(context).load(item.getUser_image()).circleCrop().into(holder.item_image);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView item_image;
        TextView item_name, item_btn;

        public viewHolder(@NonNull View itemView, Invite_adapter adapter) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_invite_image);
            item_name = itemView.findViewById(R.id.item_invite_name);
            item_btn = itemView.findViewById(R.id.item_invite_btn);

            item_btn.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    String user_id = adapter.items.get(getBindingAdapterPosition()).getUser_id();
                    RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                    Call<DTO_room> call = retrofitInterface.writerJoin(user_id, adapter.room_id);
                    call.enqueue(new Callback<DTO_room>() {
                        @Override
                        public void onResponse(Call<DTO_room> call, Response<DTO_room> response) {
                            if (response.body() != null && response.isSuccessful()) {
                                Socket socket = Service_chat.getSocket();
                                DTO_message data = new DTO_message("join", adapter.room_id, user_id, user_id + "님이 초대되셨습니다.", "", "invite", "io", 0, adapter.room_name);
                                String msg = DtoToJson(data);
                                System.out.println("ChatWrite_172 // 보내는 메시지 : " + msg);
                                /* Send Thread 로 서버로 보내기 */
                                Send send = new Send(socket, msg);
                                send.start();
                                ((Invite) adapter.context).finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<DTO_room> call, Throwable t) {
                            Log.e("//===========//", "================================================");
                            Log.e("", "\n" + "[ 초대 에러 invite_adapter : " + t + "  ]");
                            Log.e("//===========//", "================================================");
                        }
                    });


                }
            });
        }
    }
}
