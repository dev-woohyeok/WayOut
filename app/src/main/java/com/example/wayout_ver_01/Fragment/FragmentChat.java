package com.example.wayout_ver_01.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_message;
import com.example.wayout_ver_01.Activity.Chat.ChatRoom;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.Activity.Chat.Chat.MyChat_adapter;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_room;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.FragmentChatBinding;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentChat extends Fragment {

    public static final int SERVER_PORT = 8888;
    public final static String SERVER_LOCAL_HOST = "10.0.2.2";
    MyChat_adapter myChat_adapter;
    FragmentChatBinding bind;
    private int page = 1, size = 8, plus = 0;
    private boolean isStop = true, alarm = true;
    private String user_id;
    private ActivityResultLauncher<Intent> launcher;


    public static FragmentChat newInstance() {
        Bundle args = new Bundle();
        FragmentChat fragment = new FragmentChat();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /* viewBind, broadcast 제거 */
        bind = null;
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(msgChatReceiver);
    }

    // BroadcastReceiver 로 받아서 어떻게 할지 설정
    private BroadcastReceiver msgChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DTO_room> list = myChat_adapter.getItems();
            String receive = intent.getStringExtra("msg");
            DTO_message message = makeDTO(receive);
            String writer = message.getName();
            String room_id = message.getRoom();
            String code = message.getCode();
            String type = message.getType();
            String image = message.getImage();

            /* 채팅방 늘어나면 추가 */
            if ("join".equals(code) && writer.equals(user_id)) {
                setRefresh();
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ Frag: Chat : DoJoin ]");
                Log.e("//===========//", "================================================");
            }

            /* 읽음 처리 - 내가 채팅방에서 나왓을때 */
            if ("out".equals(code) && writer.equals(user_id)) {
                setRefresh();
                return;
            }

            if(code.equals("kick") || code.equals("delete")){
                myChat_adapter.itemsClear();
                page = 1;
                getData();
            }
            if(image.equals("nope")){
                myChat_adapter.itemsClear();
                page = 1;
                getData();
            }

            /* 받은 문자 위치 옮겨주기 */
            if (!code.equals("in")) {
                if (!code.equals("out")) {
                    /* 본인 제외  */
                    if (!user_id.equals(writer)) {
                        for (int i = 0; i < list.size(); i++) {
                            DTO_room item = list.get(i);
                            /* 방번호 같은 것만 */
                            if (item.getRoom_id().equals(room_id)) {
                                list.remove(item);
                                list.add(0, item);
//                                item.setRoom_message(message.getMessage());
                                item.setRoom_count(item.getRoom_count() + 1);
                                if(type.equals("img")) {
                                    Log.e("//===========//","================================================");
                                    Log.e("","\n"+"[ FragChat, 채팅 문자 위치 옮겨주기 : type : "+type+" ]");
                                    Log.e("//===========//","================================================");
                                    item.setRoom_message("이미지가 전송되었습니다.");
                                }else{
                                    item.setRoom_message(message.getMessage());
                                }
                            }
                        }
                        myChat_adapter.setList(list);
                    }
                }
            }

            Log.e("//===========//", "================================================");
            Log.e("", "\n" + "[ Frag: Chat :: receive : " + receive + " ]");
            Log.e("", "\n" + "[ Frag: Chat :: user_id : " + user_id + ", writer : " + writer + " ]");
            Log.e("//===========//", "================================================");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bind = FragmentChatBinding.inflate(inflater, container, false);
        View view = bind.getRoot();

        // BroadcastManager 객체 생성 => 받을 준비가 됨
        LocalBroadcastManager
                .getInstance(requireContext())
                .registerReceiver(msgChatReceiver, new IntentFilter("msgReceive_frag"));

        /* 메세지 알람 */
        alarm = PreferenceManager.getBoolean(requireContext(),"alarm");
        if(alarm){
            bind.myChatAlarm.setImageResource(R.drawable.no_alarm);
        }else{
            bind.myChatAlarm.setImageResource(R.drawable.bell);
        }

        /* 알람 키고 끄기 */
        bind.myChatAlarm.setOnClickListener(v -> {
            if(alarm){

                bind.myChatAlarm.setImageResource(R.drawable.bell);
                PreferenceManager.setBoolean(requireContext(),"alarm",false);
                alarm = false;
            }else{
                bind.myChatAlarm.setImageResource(R.drawable.no_alarm);
                PreferenceManager.setBoolean(requireContext(),"alarm",true);
                alarm = true;
            }

        });



        /* 유저 데이터 */
        user_id = PreferenceManager.getString(requireContext(), "userId");

        /* 어뎁터 세팅 */
        bind.MyChatRv.setItemAnimator(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        bind.MyChatRv.setLayoutManager(linearLayoutManager);
        myChat_adapter = new MyChat_adapter(view.getContext());
        bind.MyChatRv.setAdapter(myChat_adapter);

        /* 스와이프 새로고침 */
        bind.MyChatSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRefresh();
                bind.MyChatSwipe.setRefreshing(false);
            }
        });

        /* 스크롤 페이징 */
        bind.MyChatScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    getScroll();
                }
            }
        });
        getData();
        return view;
    }

    private void setRefresh() {
        /* 현재 페이지가 2 이상일 경우 */
        if (myChat_adapter.getItemCount() > size) {
            page = 1;
            isStop = true;
            myChat_adapter.itemsClear();
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_room>> call = retrofitInterface.getMyChat(page, size, user_id);
        call.enqueue(new Callback<ArrayList<DTO_room>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ArrayList<DTO_room>> call, Response<ArrayList<DTO_room>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    /* 페이징이 2번 이상일 경우 */
                    if (myChat_adapter.getItemCount() > size) {
                        for (DTO_room item : response.body()) {
                            myChat_adapter.addItem(item);
                        }
                    } else {
                        /* 데이터만 새로 갱신됨 */
                        for (DTO_room item : response.body()) {
                            myChat_adapter.setList(response.body());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_room>> call, Throwable t) {
                Log.e("FragChat,149", "getData() 에러 : " + t);
            }
        });
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
        if (isStop) {
            page = 1;
            myChat_adapter.itemsClear();
            getData();
        }
    }

    private void getScroll() {
        if (page == 1) {
            page = 2;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_room>> call = retrofitInterface.getMyChat(page, size, user_id);
        call.enqueue(new Callback<ArrayList<DTO_room>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_room>> call, Response<ArrayList<DTO_room>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        page++;
                    }
                    int index = 0;
                    ArrayList<DTO_room> list = response.body();
                    for (DTO_room item : list) {
                        myChat_adapter.addItem(item);
                        index++;
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_room>> call, Throwable t) {
                Log.e("FragChat,149", "getData() 에러 : " + t);
            }
        });
    }

    private void getData() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_room>> call = retrofitInterface.getMyChat(page, size, user_id);
        call.enqueue(new Callback<ArrayList<DTO_room>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ArrayList<DTO_room>> call, Response<ArrayList<DTO_room>> response) {
                if (response.body() != null && response.isSuccessful()) {

                    for (DTO_room item : response.body()) {
                        myChat_adapter.addItem(item);
                        Log.e("//===========//", "================================================");
                        Log.e("", "\n" + "[ Chat_Frag : item_message " + item.getRoom_message() + " ]");
                        Log.e("", "\n" + "[ Chat_Frag : item_count " + item.getRoom_count() + " ]");
                        Log.e("//===========//", "================================================");
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_room>> call, Throwable t) {
                Log.e("FragChat,149", "getData() 에러 : " + t);
            }
        });
    }

    private static String makeJson(String code, String room, String name, String message, String image, String date, String type) {
        /* 서버에 보낼 메세지 데이터 Json 생성 */
        Gson gson = new Gson();
        DTO_message chat = new DTO_message(code, room, name, message, image, date, type);
        String jsonStr = gson.toJson(chat);
        System.out.println("ChatRoom_120 // 변환된 데이터 " + jsonStr);
        return jsonStr;
    }

    /* DTO => JsonString 으로 어디든 보내기 쉽게 */
    private static String DtoToJson(DTO_message item) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(item);
        System.out.println("ChatRoom_ // 변환된 데이터 : " + jsonStr);
        return jsonStr;
    }

    // json String => DTO 로 변경해줌
    private static DTO_message makeDTO(String json) {
        Gson gson = new Gson();
        DTO_message message = gson.fromJson(json, DTO_message.class);
        return message;
    }
}