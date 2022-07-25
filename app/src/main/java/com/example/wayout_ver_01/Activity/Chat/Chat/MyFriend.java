package com.example.wayout_ver_01.Activity.Chat.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.wayout_ver_01.Fragment.Fragment_follow;
import com.example.wayout_ver_01.Fragment.Fragment_following;
import com.example.wayout_ver_01.RecyclerView.ViewPager.VP_adapter;
import com.example.wayout_ver_01.databinding.ActivityMyfriendBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyFriend extends AppCompatActivity {
    private ActivityMyfriendBinding bind;
    Fragment_following fragmentFriend_following;
    Fragment_follow fragmentFriend_follow;
    VP_adapter vp_adapter;
    TabLayout tab;
    View view;
    private String user_id, user_index;
    private boolean friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMyfriendBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        user_index = intent.getStringExtra("user_index");
        friend = intent.getBooleanExtra("friend",false);

        createFragment();
        createViewPager();
        settingViewpage();

        boolean follow = intent.getBooleanExtra("follow",true);
        if(follow){
            bind.myLikeManageVp.setCurrentItem(0);
        }else{
            bind.myLikeManageVp.setCurrentItem(1);
        }



    }

    private void settingViewpage() {

        new TabLayoutMediator(
                bind.myFriendTab,
                bind.myLikeManageVp,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        String[] data = {"팔로우", "팔로잉"};
                        tab.setText(data[position]);
                    }
                }
        ).attach();


    }

    private void createViewPager() {
        vp_adapter = new VP_adapter(this);
        vp_adapter.addItem(fragmentFriend_follow);
        vp_adapter.addItem(fragmentFriend_following);
        bind.myLikeManageVp.setAdapter(vp_adapter);
    }

    private void createFragment() {
        fragmentFriend_follow = Fragment_follow.newInstance(user_id, friend);
        fragmentFriend_following = Fragment_following.newInstance(user_id, friend);
    }
}