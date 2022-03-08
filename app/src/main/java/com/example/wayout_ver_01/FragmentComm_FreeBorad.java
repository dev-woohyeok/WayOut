package com.example.wayout_ver_01;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentComm_FreeBorad extends Fragment {

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comm__free_borad, container, false);

        // 리사이클러뷰 설정 및 호출
        recyclerView = view.findViewById(R.id.free_rv);
        // 리사이클러뷰의 크기를 고정 시켜서 효율 상승 // 만들때마다 다시 만들지 않게 설정함
        recyclerView.setHasFixedSize(true);
        // 리사이클러뷰 방향 설정하기
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        return view;
    }
}