package com.example.wayout_ver_01.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchTheme_adpater;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.DTO_theme;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.FragmentSearchThemeBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentSearch_Theme extends Fragment {
    private String str;
    private FragmentSearchThemeBinding binding;
    private SearchTheme_adpater adpater;
    private int page = 1;
    private int size = 8;

    public static FragmentSearch_Theme newInstance(String content) {
        FragmentSearch_Theme fragment = new FragmentSearch_Theme();
        Bundle args = new Bundle();
        args.putString("검색어", content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             str = getArguments().getString("검색어");
             Log.e("Theme" ,"content : " + str );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("resume" ,"content : " + str );
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchThemeBinding.inflate(inflater, container, false);
        Log.e("Theme_Frag_onCV", "검색어 값 : " + str);
        View view = binding.getRoot();

        // 어뎁터 세팅
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(),2,LinearLayoutManager.VERTICAL,false);
        binding.searchThemeRv.setLayoutManager(gridLayoutManager);
        adpater = new SearchTheme_adpater(requireContext());
        binding.searchThemeRv.setAdapter(adpater);

//        binding.searchThemeSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                page = 1;
//                str = "";
//                getData();
//                binding.searchThemeSwipe.setRefreshing(false);
//            }
//        });

        binding.searchThemeScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollX == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if(page == 1){
                        page = 2;
                    }
                    getScroll();
                }
            }
        });

        return view;
    }

    private void getScroll() {
        Log.e("Theme, getData()", "검색어 : " + str);
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_theme>> call = retrofitInterface.getSearchTheme(page, size, str);
        call.enqueue(new Callback<ArrayList<DTO_theme>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_theme>> call, Response<ArrayList<DTO_theme>> response) {
                if(response.body() != null &  response.isSuccessful()){
                    for(int i = 0; i < response.body().size(); i++ ){

                        adpater.scrollItem(new DTO_theme(
                                response.body().get(i).getIndex(),
                                response.body().get(i).getName(),
                                response.body().get(i).getDifficult(),
                                response.body().get(i).getLimit(),
                                response.body().get(i).getGenre(),
                                response.body().get(i).getCafe(),
                                response.body().get(i).getImage()
                        ));
                        adpater.notifyItemInserted((page - 1) * size + i);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_theme>> call, Throwable t) {

            }
        });


    }

    private void getData() {
        if(page == 1){
            adpater.clearItem();
        }

        Log.e("Theme, getData()", "검색어 : " + str);
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_theme>> call = retrofitInterface.getSearchTheme(page, size, str);
        call.enqueue(new Callback<ArrayList<DTO_theme>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_theme>> call, Response<ArrayList<DTO_theme>> response) {
                    if(response.body() != null &  response.isSuccessful()){
                        for(int i = 0; i < response.body().size(); i++ ){

                            adpater.addItem(new DTO_theme(
                                    response.body().get(i).getIndex(),
                                    response.body().get(i).getName(),
                                    response.body().get(i).getDifficult(),
                                    response.body().get(i).getLimit(),
                                    response.body().get(i).getGenre(),
                                    response.body().get(i).getCafe(),
                                    response.body().get(i).getImage()
                            ));
                        }
                    }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_theme>> call, Throwable t) {

            }
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}