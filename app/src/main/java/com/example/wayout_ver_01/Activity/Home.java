package com.example.wayout_ver_01.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.wayout_ver_01.Fragment.FragmentChatting;
import com.example.wayout_ver_01.Fragment.FragmentCommunity;
import com.example.wayout_ver_01.Fragment.FragmentHome;
import com.example.wayout_ver_01.Fragment.FragmentMypage;
import com.example.wayout_ver_01.Fragment.FragmentSearch;
import com.example.wayout_ver_01.R;
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

        replaceFragment(fragmentHome);

//        getSupportFragmentManager().beginTransaction().replace(R.id.containers, fragmentHome).commit();
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigation);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_tab:
                        replaceFragment(fragmentHome);
                        return true;
                    case R.id.search_tab:
                        replaceFragment(fragmentSearch);
                        return true;
                    case R.id.community_tab:
                        replaceFragment(fragmentCommunity);
                        return true;
                    case R.id.chat_tab:
                        replaceFragment(fragmentChatting);
                        return true;
                    case R.id.myPage_tab:
                        replaceFragment(fragmentMypage);
                        return true;
                }
                return false;
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,R.anim.right_to_left,R.anim.left_to_right);
        // back 스택에 저장
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.containers, fragment);
        fragmentTransaction.commit();
    }
}