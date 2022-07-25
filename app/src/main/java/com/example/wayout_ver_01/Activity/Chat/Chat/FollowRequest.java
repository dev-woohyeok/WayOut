package com.example.wayout_ver_01.Activity.Chat.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivityFollowRequestBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowRequest extends AppCompatActivity {
    private ActivityFollowRequestBinding bind;
    private String user_id;
    private Request_adapter request_adapter;
    private int page = 1, size = 8;
    private boolean scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityFollowRequestBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        /* 데이터 세팅 */
        Intent intent = getIntent();
        user_id =  intent.getStringExtra("user_id");

        /* 어뎁터 세팅 */
        request_adapter = new Request_adapter(FollowRequest.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FollowRequest.this, LinearLayoutManager.VERTICAL, false);
        bind.RequestRv.setLayoutManager(linearLayoutManager);
        bind.RequestRv.setAdapter(request_adapter);
        bind.RequestRv.setItemAnimator(null);

        getData();

        /* 스와이프 */
        bind.RequestSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                request_adapter.itemsClear();
                getData();
                bind.RequestSwipe.setRefreshing(false);
            }
        });

        /* 스크롤 페이징 */
        /* 리사이클러뷰 스크롤시 */
        bind.RequestRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /* 최상단 스크롤시 작동 */
                if (!bind.RequestRv.canScrollVertically(1)) {
                    if (!scroll) {
                        scroll = true;
                        getScroll();
                    }
                }
            }
        });
    }

    private void getScroll() {
        if (page == 1) {
            page = 2;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_follow> call = retrofitInterface.getFollowRequest(user_id,page,size);
        call.enqueue(new Callback<DTO_follow>() {
            @Override
            public void onResponse(Call<DTO_follow> call, Response<DTO_follow> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getFollows().size() > 0){
                        page++;
                        scroll = false;
                    }

                    for(DTO_follow item : response.body().getFollows()){
                        request_adapter.addItem(item);
                    }

                    if(request_adapter.getItemCount() > 0){
                        bind.RequestTv.setVisibility(View.GONE);
                    }else{
                        bind.RequestTv.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(Call<DTO_follow> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ FollowRequest, 팔로우 요청 데이터 호출 실패  : " + t + " ]");
                Log.e("//===========//", "================================================");
            }
        });

    }


    private void getData() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_follow> call = retrofitInterface.getFollowRequest(user_id,page,size);
        call.enqueue(new Callback<DTO_follow>() {
            @Override
            public void onResponse(Call<DTO_follow> call, Response<DTO_follow> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for(DTO_follow item : response.body().getFollows()){
                        request_adapter.addItem(item);
                    }
                    if(request_adapter.getItemCount() > 0){
                        bind.RequestTv.setVisibility(View.GONE);
                    }else{
                        bind.RequestTv.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(Call<DTO_follow> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ FollowRequest, 팔로우 요청 데이터 호출 실패  : " + t + " ]");
                Log.e("//===========//", "================================================");
            }
        });

    }
}