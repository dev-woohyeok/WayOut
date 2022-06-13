package com.example.wayout_ver_01.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_message;
import com.example.wayout_ver_01.Activity.Chat.Service_chat;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.Fragment.FragmentChat;
import com.example.wayout_ver_01.Fragment.FragmentCommunity;
import com.example.wayout_ver_01.Fragment.FragmentHome;
import com.example.wayout_ver_01.Fragment.FragmentMypage;
import com.example.wayout_ver_01.Fragment.FragmentSearch;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.ViewPager.VP_adapter;
import com.example.wayout_ver_01.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

public class Home extends AppCompatActivity {

//    private TextView home_Out;
//    private Context mContext = this;

    //프래그먼트 선언
    FragmentHome fragmentHome;
    FragmentSearch fragmentSearch;
    FragmentCommunity fragmentCommunity;
    FragmentChat fragmentChatting;
    FragmentMypage fragmentMypage;
    NavigationBarView navigationBarView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ViewPager2 viewPager;
    VP_adapter vp_adapter;
    ActivityHomeBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        /* 소켓 서비스 시작 */
        String user_id = PreferenceManager.getString(this, "userId");
        Intent service = new Intent(this, Service_chat.class);
        service.putExtra("user_id", user_id);
        startService(service);
        createFragment();
        createViewPager();

        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(msgHomeReceiver, new IntentFilter("msgReceive_home"));


        bind.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search_tab:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.community_tab:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.chat_tab:
                        viewPager.setCurrentItem(2);
                        return true;
                    case R.id.myPage_tab:
                        viewPager.setCurrentItem(3);
                        return true;
                }
                return false;
            }
        });

        //
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(msgHomeReceiver);

        Log.w("//===========//", "================================================");
        Log.i("", "\n" + "[onDestroy home 호출 확인]");
        Log.w("//===========//", "================================================");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private BroadcastReceiver msgHomeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receive = intent.getStringExtra("msg");
            DTO_message message = makeDTO(receive);
            int selected_id = 2131296498;
            int now_id = bind.bottomNavigation.getSelectedItemId();

            Log.w("//===========//", "================================================");
            Log.i("", "\n" + "[ Home_Receive  :: Home_msg 받아옴 ]");
            Log.i("", "\n" + "[ Home_Receive  :: R.id.chat_tab " + R.id.chat_tab + " ]");
            Log.i("", "\n" + "[ Home_Receive  :: Frag Receive 로 전송 ]");
            Log.w("//===========//", "================================================");
            /* 현재 chat fragment 를 보고 있으면 메세지를 전달해 줌  */
            Intent intent1 = new Intent("msgReceive_frag");
            intent1.putExtra("msg", receive);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
        }
    };

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


    private void createViewPager() {
        viewPager = findViewById(R.id.bottom_ViewPager);
        vp_adapter = new VP_adapter(this);
//        vp_adapter.addItem(fragmentHome);
        vp_adapter.addItem(fragmentSearch);
        vp_adapter.addItem(fragmentCommunity);
        vp_adapter.addItem(fragmentChatting);
        vp_adapter.addItem(fragmentMypage);
        viewPager.setAdapter(vp_adapter);
        viewPager.setUserInputEnabled(false);
    }

    private void createFragment() {
//        fragmentHome = FragmentHome.newInstance();
        fragmentSearch = FragmentSearch.newInstance("");
        fragmentCommunity = FragmentCommunity.newInstance();
        fragmentChatting = FragmentChat.newInstance();
        fragmentMypage = FragmentMypage.newInstance();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}