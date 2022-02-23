package com.example.wayout_ver_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationBarView;

public class Home extends AppCompatActivity {

//    private TextView home_Out;
//    private Context mContext = this;

    //프래그먼트 선언
    FragmentHome fragmentHome;
    FragmentSearch fragmentSearch;
    FragmentCommunity fragmentCommunity;
    FragmentChatting fragmentChatting;
    FragmentMypage fragmentMypage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentHome = new FragmentHome();
        fragmentSearch = new FragmentSearch();
        fragmentCommunity = new FragmentCommunity();
        fragmentChatting = new FragmentChatting();
        fragmentMypage = new FragmentMypage();

        getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentHome).commit();
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigation);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentHome).commit();
                        return true;
                    case R.id.search_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentSearch).commit();
                        return true;
                    case R.id.community_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentCommunity).commit();
                        return true;
                    case R.id.chat_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentChatting).commit();
                        return true;
                    case R.id.myPage_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentMypage).commit();
                        return true;
                }
                return false;
            }
        });

//        home_Out = findViewById(R.id.home_out);
//
//        home_Out.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PreferenceManager.setString(mContext, "autoId", "");
//                PreferenceManager.setString(mContext, "autoPw", "");
//                Intent intent = new Intent(Home.this, MainActivity.class);
//                startActivity(intent);
//
//            }
//        });

    }
}