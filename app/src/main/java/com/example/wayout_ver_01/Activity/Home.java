package com.example.wayout_ver_01.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.wayout_ver_01.Fragment.FragmentChatting;
import com.example.wayout_ver_01.Fragment.FragmentCommunity;
import com.example.wayout_ver_01.Fragment.FragmentHome;
import com.example.wayout_ver_01.Fragment.FragmentMypage;
import com.example.wayout_ver_01.Fragment.FragmentSearch;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.ViewPager.VP_adapter;
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
    NavigationBarView navigationBarView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ViewPager2 viewPager;
    VP_adapter vp_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createFragment();
        createViewPager();

        navigationBarView = findViewById(R.id.bottom_navigation);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_tab:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.search_tab:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.community_tab:
                        viewPager.setCurrentItem(2);
                        return true;
                    case R.id.chat_tab:
                        viewPager.setCurrentItem(3);
                        return true;
                    case R.id.myPage_tab:
                        viewPager.setCurrentItem(4);
                        return true;
                }
                return false;
            }
        });
        //
    }

    private void createViewPager() {
        viewPager = findViewById(R.id.bottom_ViewPager);
        vp_adapter = new VP_adapter(this);
        vp_adapter.addItem(fragmentHome);
        vp_adapter.addItem(fragmentSearch);
        vp_adapter.addItem(fragmentCommunity);
        vp_adapter.addItem(fragmentChatting);
        vp_adapter.addItem(fragmentMypage);
        viewPager.setAdapter(vp_adapter);
        viewPager.setUserInputEnabled(false);
    }

    private void createFragment() {
        fragmentHome = FragmentHome.newInstance();
        fragmentSearch =  FragmentSearch.newInstance("");
        fragmentCommunity = FragmentCommunity.newInstance();
        fragmentChatting = FragmentChatting.newInstance();
        fragmentMypage = FragmentMypage.newInstance();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

//    private void replaceFragment(Fragment fragment){
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,R.anim.right_to_left,R.anim.left_to_right);
//        fragmentTransaction.replace(R.id.containers, fragment);
//        fragmentTransaction.addToBackStack("null");
//        fragmentTransaction.commit();
//    }
}