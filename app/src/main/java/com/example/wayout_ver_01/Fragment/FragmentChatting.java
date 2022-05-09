package com.example.wayout_ver_01.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentChatting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentChatting extends Fragment {

    public static FragmentChatting newInstance() {

        Bundle args = new Bundle();

        FragmentChatting fragment = new FragmentChatting();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatting, container, false);
    }
}