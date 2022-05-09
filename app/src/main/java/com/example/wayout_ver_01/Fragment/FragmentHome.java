package com.example.wayout_ver_01.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.R;

public class FragmentHome extends Fragment {

    public static FragmentHome newInstance() {
        
        Bundle args = new Bundle();
        
        FragmentHome fragment = new FragmentHome();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}