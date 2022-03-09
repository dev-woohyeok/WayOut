package com.example.wayout_ver_01;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


public class FragmentCommunity extends Fragment {

    FragmentComm_FreeBorad fragmentComm_freeBorad;
    FragmentComm_GalleryBorad fragmentComm_galleryBorad;
    FragmentComm_ChatBoard fragmentComm_chatBoard;
    private final String TAG = this.getClass().getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentComm_freeBorad = new FragmentComm_FreeBorad();
        fragmentComm_galleryBorad = new FragmentComm_GalleryBorad();
        fragmentComm_chatBoard = new FragmentComm_ChatBoard();


//        return inflater.inflate(R.layout.fragment_community, container, false);
        // 화면 사용시 전환
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        // 상단 탭 생성
        TabLayout tabs = view.findViewById(R.id.topTab_community);
        // 상단 탭에 클릭할 애들 달아줌
        tabs.addTab(tabs.newTab().setText("자유 게시판"));
        tabs.addTab(tabs.newTab().setText("갤러리 게시판"));
        tabs.addTab(tabs.newTab().setText("인원 구하기"));

        // 자유 게시판 기본 세팅
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.community_containers, fragmentComm_freeBorad);
        fragmentTransaction.commit();

        // 클릭시 리스너
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 클릭한 텝 Position 값 가져옴
                int position = tab.getPosition();
                Log.e(TAG, "내용 : 선택한 탭 위치 : " + position);

                Fragment selectedFragment = null;
                switch (position) {
                    case 0:
                        selectedFragment = fragmentComm_freeBorad;
                        break;
                    case 1:
                        selectedFragment = fragmentComm_galleryBorad;
                        break;
                    case 2:
                        selectedFragment = fragmentComm_chatBoard;
                        break;
                }
                replaceFragment(selectedFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return view;
    }

    private void replaceFragment(Fragment fragment) {
        // 프래그먼트에서
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.left_to_right, R.anim.right_to_left);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.community_containers, fragment);
        fragmentTransaction.commit();
    }
}