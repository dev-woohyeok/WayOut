package com.example.wayout_ver_01.Activity.MyPage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.Fragment.FragmentManage_Cafe;
import com.example.wayout_ver_01.Fragment.FragmentManage_Theme;
import com.example.wayout_ver_01.Fragment.FragmentSearch_Cafe;
import com.example.wayout_ver_01.Fragment.FragmentSearch_Theme;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.ViewPager.VP_adapter;
import com.example.wayout_ver_01.databinding.ActivityMyManageBinding;
import com.example.wayout_ver_01.databinding.FragmentSearchBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyManage extends AppCompatActivity {
    private ActivityMyManageBinding bind;
    private FragmentManage_Cafe fragmentManage_cafe;
    private FragmentManage_Theme fragmentManage_theme;
    private ViewPager2 viewPager2;
    private View view;
    private TabLayout tabLayout;
    private VP_adapter search_vp_adapter;
    private String user_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMyManageBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        CreateFragment();
        CreateViewPager();
        settingTablayout();

    }

    private void CreateFragment() {
        user_index = PreferenceManager.getString(getApplicationContext(),"userIndex");
        fragmentManage_cafe = FragmentManage_Cafe.newInstance();
        fragmentManage_theme = FragmentManage_Theme.newInstance();
    }

    private void CreateViewPager(){
        viewPager2 = bind.myLikeManageVp;
        search_vp_adapter = new VP_adapter(this);
        search_vp_adapter.addItem(fragmentManage_cafe);
        search_vp_adapter.addItem(fragmentManage_theme);
        viewPager2.setAdapter(search_vp_adapter);

    }

    private void settingTablayout(){
        tabLayout = bind.myLikeManageTab;
        // Tab 레이아웃과 ViewPager 를 연결하여 스와이프 탭 다 가능하게함
        new TabLayoutMediator(
                tabLayout,
                viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        String[] data = {"내 카페", "내 테마"};
                        tab.setText(data[position]);
                    }
                }).attach();

    }

}