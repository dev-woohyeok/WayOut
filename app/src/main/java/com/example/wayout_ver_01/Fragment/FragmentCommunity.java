package com.example.wayout_ver_01.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.Fragment.FragmentComm_ChatBoard;
import com.example.wayout_ver_01.Fragment.FragmentComm_FreeBorad;
import com.example.wayout_ver_01.Fragment.FragmentComm_GalleryBorad;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.ViewPager.VP_adapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class FragmentCommunity extends Fragment {

    FragmentComm_FreeBorad fragmentComm_freeBorad;
    FragmentComm_GalleryBorad fragmentComm_galleryBorad;
    FragmentComm_ChatBoard fragmentComm_chatBoard;
    ViewPager2 viewPager;
    VP_adapter vp_adapter;
    TabLayout tab;
    View view;
    private final String TAG = this.getClass().getSimpleName();

    public static FragmentCommunity newInstance() {
        Bundle args = new Bundle();
        FragmentCommunity fragment = new FragmentCommunity();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community, container, false);

        createFragment();
        createViewPager();
        settingViewPager();

        return view;
    }

    private void settingViewPager() {
        // 상단 탭 생성
        TabLayout tabs = view.findViewById(R.id.topTab_community);



        new TabLayoutMediator(
                tabs,
                viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    String[] data = {"자유 게시판", "갤러리 게시판", "인원 구하기"};
                    tab.setText(data[position]);
                    }
                }
        ).attach();

    }

    private void createViewPager() {
        viewPager = view.findViewById(R.id.comm_viewPager);
        vp_adapter = new VP_adapter(this);
        vp_adapter.addItem(fragmentComm_freeBorad);
        vp_adapter.addItem(fragmentComm_galleryBorad);
        vp_adapter.addItem(fragmentComm_chatBoard);
        viewPager.setAdapter(vp_adapter);
    }

    private void createFragment() {
        fragmentComm_freeBorad = FragmentComm_FreeBorad.newInstance();
        fragmentComm_galleryBorad = FragmentComm_GalleryBorad.newInstance();
        fragmentComm_chatBoard = FragmentComm_ChatBoard.newInstance();
    }

}