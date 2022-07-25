package com.example.wayout_ver_01.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_follow;
import com.example.wayout_ver_01.Activity.Chat.Chat.Following_adapter;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.FragmentFollowingBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_following extends Fragment {
    private FragmentFollowingBinding bind;
    private String user_id;
    private int page = 1, size = 8;
    private Following_adapter followingAdapter;
    private boolean isStop, scroll,friend;

    public Fragment_following() {
    }

    public static Fragment_following newInstance(String user_id, boolean friend) {
        Fragment_following fragment = new Fragment_following();
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
            friend = getArguments().getBoolean("friend",false);
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
        bind = FragmentFollowingBinding.inflate(inflater, container, false);
        View view = bind.getRoot();
        /* 어뎁터 세팅 */
        followingAdapter = new Following_adapter(requireContext(), bind.followingTv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        bind.followingRv.setLayoutManager(linearLayoutManager);
        bind.followingRv.setAdapter(followingAdapter);
        bind.followingRv.setItemAnimator(null);
        followingAdapter.setFriend(friend);

        /* 스크롤 페이징 */
        /* 리사이클러뷰 스크롤시 */
        bind.followingRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /* 최상단 스크롤시 작동 */
                if (!bind.followingRv.canScrollVertically(1)) {
                    if (!scroll) {
                        scroll = true;
                        getScroll();
                    }
                }
            }
        });


        getData();

        return view;
    }

    private void getScroll() {
        if (page == 1) {
            page = 2;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_follow> call = retrofitInterface.getFollowing(user_id,page,size);
        call.enqueue(new Callback<DTO_follow>() {
            @Override
            public void onResponse(Call<DTO_follow> call, Response<DTO_follow> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getFollows().size() > 0){
                        page++;
                        scroll = false;
                    }

                    for (DTO_follow item : response.body().getFollows()) {
                        followingAdapter.addItem(item);
                    }
                }
            }

            @Override
            public void onFailure(Call<DTO_follow> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ FragFollwing  팔로잉 데이터 가져오기 에러 :: " + t + "  ]");
                Log.e("//===========//", "================================================");
            }
        });
    }

    private void getData() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_follow> call = retrofitInterface.getFollowing(user_id,page,size);
        call.enqueue(new Callback<DTO_follow>() {
            @Override
            public void onResponse(Call<DTO_follow> call, Response<DTO_follow> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (DTO_follow item : response.body().getFollows()) {
                        followingAdapter.addItem(item);
                    }
                }
            }

            @Override
            public void onFailure(Call<DTO_follow> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ FragFollwing  팔로잉 데이터 가져오기 에러 :: " + t + "  ]");
                Log.e("//===========//", "================================================");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        isStop = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isStop){
            isStop = false;
            followingAdapter.itemsClear();
            page = 1;
            getData();
            scroll = false;
        }
    }
}