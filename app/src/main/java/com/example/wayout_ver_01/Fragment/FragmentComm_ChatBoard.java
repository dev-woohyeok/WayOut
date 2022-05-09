package com.example.wayout_ver_01.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.R;


public class FragmentComm_ChatBoard extends Fragment {

    public static FragmentComm_ChatBoard newInstance() {
        
        Bundle args = new Bundle();
        
        FragmentComm_ChatBoard fragment = new FragmentComm_ChatBoard();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comm__chat_board, container, false);
    }
}