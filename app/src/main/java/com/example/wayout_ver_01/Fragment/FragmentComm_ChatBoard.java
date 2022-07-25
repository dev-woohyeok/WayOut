package com.example.wayout_ver_01.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.wayout_ver_01.Activity.Chat.ChatWrite;
import com.example.wayout_ver_01.Activity.Chat.Chat.ChatBoard_adapter;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_room;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.FragmentCommChatBoardBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentComm_ChatBoard extends Fragment {
    private FragmentCommChatBoardBinding bind;
    private ChatBoard_adapter chatBoard_adapter;
    private String category = "room_name";
    private InputMethodManager imm;
    private String search = "";
    private int page = 1, size = 8;
    private boolean isStop = true;


    public static FragmentComm_ChatBoard newInstance() {
        Bundle args = new Bundle();
        FragmentComm_ChatBoard fragment = new FragmentComm_ChatBoard();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = FragmentCommChatBoardBinding.inflate(inflater, container, false);
        View view = bind.getRoot();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        bind.chatBoardRv.setLayoutManager(linearLayoutManager);
        chatBoard_adapter = new ChatBoard_adapter(view.getContext());
        bind.chatBoardRv.setAdapter(chatBoard_adapter);

        // 인원 구하기 방 만들기
        bind.chatBoardWriteBtn.setOnClickListener(v -> {
            Intent i = new Intent(view.getContext(), ChatWrite.class);
            startActivity(i);
        });

        /* 스와이프 새로고침 */
        bind.chatBoardSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                category = "room_name";
                search = "";
                isStop = true;
                chatBoard_adapter.itemsClear();
                getData();
                bind.chatBoardSwipe.setRefreshing(false);
            }
        });

        /* 스크롤 페이징 */
        bind.chatBoardScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    getScroll();
                }
            }
        });


        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        page = 1;
        isStop = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("ChatBoard,116", "onStop호출? : " + isStop);
        chatBoard_adapter.itemsClear();
        isStop = false;
        getData();
        
    }

    private void getScroll() {
        if (page == 1) {
            page = 2;
        }
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_room>> call = retrofitInterface.getChatRoom(page, size, category, search);
        call.enqueue(new Callback<ArrayList<DTO_room>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_room>> call, Response<ArrayList<DTO_room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().size() > 0) {
                        page++;
                    }
                    for (DTO_room room : response.body()) {
                        chatBoard_adapter.addItem(room);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_room>> call, Throwable t) {

            }
        });
    }


    private void getData() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_room>> call = retrofitInterface.getChatRoom(page, size, category, search);
        call.enqueue(new Callback<ArrayList<DTO_room>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_room>> call, Response<ArrayList<DTO_room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (DTO_room room : response.body()) {
                        chatBoard_adapter.addItem(room);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_room>> call, Throwable t) {
                Log.e("chatBoard,141", "chatBoard 에러 : " + t);
            }
        });
    }
}