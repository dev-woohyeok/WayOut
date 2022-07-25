package com.example.wayout_ver_01.Activity.Chat.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivityInviteBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Invite extends AppCompatActivity {
    private ActivityInviteBinding bind;
    private String user_id, room_id, room_name;
    private Invite_adapter invite_adapter;
    private int page = 1, size = 8;
    private boolean isStop, scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityInviteBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        /* 처음 정보 */
        Intent intent = getIntent();
        room_id = intent.getStringExtra("room_id");
        user_id = PreferenceManager.getString(Invite.this, "userId");
        room_name = intent.getStringExtra("room_name");


        /* 어뎁터 세팅 */
        invite_adapter = new Invite_adapter(Invite.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Invite.this, LinearLayoutManager.VERTICAL, false);
        bind.InviteRv.setLayoutManager(linearLayoutManager);
        bind.InviteRv.setAdapter(invite_adapter);
        invite_adapter.setRoom_id(room_id);
        invite_adapter.setRoom_name(room_name);

        /* 스크롤 페이징 */
        /* 리사이클러뷰 스크롤시 */
        bind.InviteRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /* 최상단 스크롤시 작동 */
                if (!bind.InviteRv.canScrollVertically(1)) {
                    if (!scroll) {
                        scroll = true;
                        getScroll();
                    }
                }
            }
        });


        getData();

    }

    private void getScroll() {
        if (page == 1) {
            page = 2;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_follow> call = retrofitInterface.getInvite(room_id, user_id, page, size);
        call.enqueue(new Callback<DTO_follow>() {
            @Override
            public void onResponse(Call<DTO_follow> call, Response<DTO_follow> response) {
                if (response.isSuccessful() && response.body() != null) {
                    page++;
                    scroll = false;


                    for (DTO_follow item : response.body().getFollows()) {
                        invite_adapter.addItem(item);
                        Log.e("//===========//", "================================================");
                        Log.e("", "\n" + "[ Invite : user_id : " + item.getUser_id() + " ]");
                        Log.e("//===========//", "================================================");
                    }


                }
            }

            @Override
            public void onFailure(Call<DTO_follow> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ Invite : 초대하기 목록 불러오기 에러 " + t + " ]");
                Log.e("//===========//", "================================================");
            }
        });

    }

    private void getData() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_follow> call = retrofitInterface.getInvite(room_id, user_id, page, size);
        call.enqueue(new Callback<DTO_follow>() {
            @Override
            public void onResponse(Call<DTO_follow> call, Response<DTO_follow> response) {
                if (response.isSuccessful() && response.body() != null) {

                    /*  이미 참가중인 인원은 표시 */
                    ArrayList<String> join = response.body().getJoin_names();
                    ArrayList<DTO_follow> items = response.body().getFollows();
                    for (String str : join) {
                        for (DTO_follow item : items) {
                            if (item.getUser_id().equals(str)) {
                                items.remove(item);
                                Log.e("//===========//", "================================================");
                                Log.e("", "\n" + "[ Invite : str : " + str + ",  user_id : " + item.getUser_id() + " ]");
                                Log.e("//===========//", "================================================");
                            }
                        }
                    }
                    for (DTO_follow item : items) {
                        invite_adapter.addItem(item);
                        Log.e("//===========//", "================================================");
                        Log.e("", "\n" + "[ Invite : user_id : " + item.getUser_id() + " ]");
                        Log.e("//===========//", "================================================");
                    }
                }
            }

            @Override
            public void onFailure(Call<DTO_follow> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ Invite : 초대하기 목록 불러오기 에러 " + t + " ]");
                Log.e("//===========//", "================================================");
            }
        });
    }
}