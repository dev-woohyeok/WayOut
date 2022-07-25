package com.example.wayout_ver_01.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_follow;
import com.example.wayout_ver_01.Activity.Chat.Chat.FollowRequest;
import com.example.wayout_ver_01.Activity.Chat.Chat.Follow_adapter;
import com.example.wayout_ver_01.Activity.Chat.Chat.Invite;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.FragmentFollowBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_follow#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_follow extends Fragment {
    private FragmentFollowBinding bind;
    private Follow_adapter followAdapter;
    private String user_id;
    private int page = 1, size = 8;
    private boolean isStop, scroll, friend;


    public Fragment_follow() {
        // Required empty public constructor
    }

    public static Fragment_follow newInstance(String user_id, boolean friend) {
        Fragment_follow fragment = new Fragment_follow();
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        args.putBoolean("friend", friend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_id = getArguments().getString("user_id");
            friend = getArguments().getBoolean("friend", false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bind = FragmentFollowBinding.inflate(inflater, container, false);
        View view = bind.getRoot();

        /* 팔로우 요청 확인 하러가기 */
        bind.followList.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FollowRequest.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });

        if (friend) {
            bind.followList.setVisibility(View.GONE);
        } else {
            bind.followList.setVisibility(View.VISIBLE);
        }

        /* 팔로우 어뎁터 세팅 */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        bind.followRv.setLayoutManager(linearLayoutManager);
        followAdapter = new Follow_adapter(requireContext(), bind.followTv);
        bind.followRv.setAdapter(followAdapter);
        bind.followRv.setItemAnimator(null);
        followAdapter.setfriend(friend);

        /* 스크롤 페이징 */
        /* 리사이클러뷰 스크롤시 */
        bind.followRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /* 최상단 스크롤시 작동 */
                if (!bind.followRv.canScrollVertically(1)) {
                    if (!scroll) {
                        scroll = true;
                        getScroll();
                    }
                }
            }
        });




        /* 팔로우 기본 데이터 세팅 */
        getData();

        return view;
    }

    private void getScroll() {
        if (page == 1) {
            page = 2;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_follow> call = retrofitInterface.getFollower(user_id, page, size);
        call.enqueue(new Callback<DTO_follow>() {
            @Override
            public void onResponse(Call<DTO_follow> call, Response<DTO_follow> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getFollows().size() > 0) {
                        page++;
                        scroll = false;
                    }


                    /* 팔로우 요청 인원 */
                    if (response.body().getFollow_count() > 0) {
                        bind.followListCount.setText("" + response.body().getFollow_count());
                    } else {
                        bind.followListCount.setVisibility(View.GONE);
                    }

                    for (DTO_follow item : response.body().getFollows()) {
                        followAdapter.addItem(item);
                    }
                }
            }

            @Override
            public void onFailure(Call<DTO_follow> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ FragFollwer  팔로워 데이터 가져오기 에러 :: " + t + "  ]");
                Log.e("//===========//", "================================================");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isStop) {
            page = 1;
            followAdapter.itemsClear();
            getData();
            isStop = false;
            scroll = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isStop = true;
    }

    private void getData() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_follow> call = retrofitInterface.getFollower(user_id, page, size);
        call.enqueue(new Callback<DTO_follow>() {
            @Override
            public void onResponse(Call<DTO_follow> call, Response<DTO_follow> response) {
                if (response.isSuccessful() && response.body() != null) {
                    /* 팔로우 요청 인원 */
                    if (response.body().getFollow_count() > 0) {
                        bind.followListCount.setText("" + response.body().getFollow_count());
                    } else {
                        bind.followListCount.setVisibility(View.GONE);
                    }

                    for (DTO_follow item : response.body().getFollows()) {
                        followAdapter.addItem(item);
                    }
                }
            }

            @Override
            public void onFailure(Call<DTO_follow> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ FragFollwer  팔로워 데이터 가져오기 에러 :: " + t + "  ]");
                Log.e("//===========//", "================================================");
            }
        });

    }
}