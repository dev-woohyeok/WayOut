package com.example.wayout_ver_01.Activity.Chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.JsonMaker;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_room;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivityChatJoinBinding;

import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatJoin extends AppCompatActivity {
    ActivityChatJoinBinding bind;
    private String user_id, room_id, room_title;
    private int room_max;
    int join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityChatJoinBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        /* intent 및 데이터 세팅 */
        getData();

        /* 참여버튼 이미 참여한 사람은 참가 불가 */
        bind.chatJoinEnter.setOnClickListener(v -> {
            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_room> call = retrofitInterface.writerJoin(user_id, room_id);
            call.enqueue(new Callback<DTO_room>() {
                @Override
                public void onResponse(Call<DTO_room> call, Response<DTO_room> response) {
                    if (response.body() != null && response.isSuccessful()) {
                        Intent intent = new Intent(ChatJoin.this, ChatRoom.class);
                        /* 넘겨줄 데이터
                         * 방고유값 , 방 새로 입장시 */
                        intent.putExtra("room_id", room_id);
                        intent.putExtra("room_join", true);
                        startActivity(intent);
                        Log.e("//===========//", "================================================");
                        Log.e("", "\n" + "[ ChatJoin, JoinEnter :: room_id : " + room_id + " ]");
                        Log.e("", "\n" + "[ ChatJoin, JoinEnter :: room_title" + room_title + " ]");
                        Log.e("", "\n" + "[ ChatJoin, JoinEnter :: user_id :  user_id  : " + user_id + " ]");
                        Log.e("//===========//", "================================================");
                    }
                    finish();
                }

                @Override
                public void onFailure(Call<DTO_room> call, Throwable t) {
                    Log.e("//===========//", "================================================");
                    Log.e("", "\n" + "[ ChatJoin, JoinEnter :: error_log : " + t + " ]");
                    Log.e("//===========//", "================================================");
                    finish();
                }
            });
        });

    }

    private void getData() {

        Intent intent = getIntent();
        room_id = intent.getStringExtra("room_index");

        Log.w("//===========//", "================================================");
        Log.i("", "\n" + "[ ChatJoin, JoinEnter :: room_id : " + room_id + " ]");
        Log.w("//===========//", "================================================");

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_room> call = retrofitInterface.getRoomJoin(room_id);
        call.enqueue(new Callback<DTO_room>() {
            @Override
            public void onResponse(Call<DTO_room> call, Response<DTO_room> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bind.chatJoinTitle.setText(response.body().getRoom_name());
                    bind.chatJoinInfo.setText(response.body().getRoom_info());
                    bind.chatJoinUserName.setText(response.body().getRoom_writer());
                    bind.chatJoinMax.setText("/" + response.body().getRoom_max() + "명");
                    bind.chatJoinJoin.setText("" + response.body().getJoin_number());
                    try {
                        bind.chatJoinDate.setText(DateConverter.resultDateToString(response.body().getRoom_date(), "yyyy-MM-dd"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Glide.with(getApplicationContext()).load(response.body().getUser_image()).circleCrop().into(bind.chatWriterImage);
                    Glide.with(getApplicationContext()).load(response.body().getRoom_image()).into(bind.chatJoinImage);

                    room_id = response.body().getRoom_id();
                    room_title = response.body().getRoom_name();
                    room_max = response.body().getRoom_max();
                    user_id = PreferenceManager.getString(ChatJoin.this, "userId");

                    Log.w("//===========//", "================================================");
                    Log.i("", "\n" + "[ ChatJoin, JoinEnter :: room_id : " + room_id + " ]");
                    Log.i("", "\n" + "[ ChatJoin, JoinEnter :: room_title" + room_title + " ]");
                    Log.i("", "\n" + "[ ChatJoin, JoinEnter :: room_max" + room_max + " ]");
                    Log.i("", "\n" + "[ ChatJoin, JoinEnter :: user_id :  user_id  : " + user_id + " ]");
                    Log.w("//===========//", "================================================");


                    /*  이미 참여한 방은 참가 못하게 막음 */
                    ArrayList<String> join = response.body().getJoin_names();
                    if (join.contains(user_id)) {
                        bind.chatJoinEnter.setText("이미 참가한 방입니다.");
                        bind.chatJoinEnter.setBackgroundResource(R.drawable.category_check_end);
                        bind.chatJoinEnter.setClickable(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<DTO_room> call, Throwable t) {
                Log.e("chatJoin,44", "join 에러 : " + t);
            }
        });

    }
}