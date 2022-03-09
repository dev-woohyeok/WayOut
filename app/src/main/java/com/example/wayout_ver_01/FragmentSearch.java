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

public class FragmentSearch extends Fragment {
    FragmentSearch_Cafe fragmentSearch_cafe;
    FragmentSearch_Theme fragmentSearch_theme;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        fragmentSearch_cafe = new FragmentSearch_Cafe();
        fragmentSearch_theme = new FragmentSearch_Theme();

        // 상단 탭 생성
        TabLayout tabs = view.findViewById(R.id.topSearch_tab);
        // 상단 탭에 클릭할 애들 달아줌
        tabs.addTab(tabs.newTab().setText("카페 검색"));
        tabs.addTab(tabs.newTab().setText("테마 검색"));

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.search_containers, fragmentSearch_cafe).commit();

//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.replace(R.id.community_containers, fragmentSearch_cafe);
//        fragmentTransaction.commit();

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
                        selectedFragment = fragmentSearch_cafe;
                        break;
                    case 1:
                        selectedFragment = fragmentSearch_theme;
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
        fragmentTransaction.replace(R.id.search_containers, fragment);
        fragmentTransaction.commit();
    }
}