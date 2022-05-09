package com.example.wayout_ver_01.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchCafe_adpater;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.FragmentSearchCafeBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentSearch_Cafe extends Fragment {
    private String search;
    private FragmentSearchCafeBinding binding;
    private SearchCafe_adpater adpater;
    private int page = 1;
    private int size = 8;

    public static FragmentSearch_Cafe newInstance(String search) {
        FragmentSearch_Cafe fragmentSearch_cafe = new FragmentSearch_Cafe();
        Bundle bundle = new Bundle();
        bundle.putString("검색어", search);
        fragmentSearch_cafe.setArguments(bundle);
        return fragmentSearch_cafe;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            search = getArguments().getString("검색어");
            Log.e("Cafe, 49" ,"검색어 : " + search );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 데이터 뿌려주기
        page = 1;
        getData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchCafeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // 애니메이터 설정

        RecyclerView.ItemAnimator animator = binding.searchShopRv.getItemAnimator();
        if(animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator)animator).setSupportsChangeAnimations(false);
        }

        // 어뎁터 세팅
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.searchShopRv.setLayoutManager(linearLayoutManager);
        adpater = new SearchCafe_adpater(requireContext());
        binding.searchShopRv.setAdapter(adpater);


//        // 새로고침 설정 -> 페이징과 검색어 설정 초기화
//        binding.searchShopSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                page = 1;
//                search = "";
//                getData();
//                binding.searchShopSwipe.setRefreshing(false);
//            }
//        });

        // 스크롤 페이징
        binding.searchShopScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
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

        Log.e("Cafe, getData()", "검색어 : " + search);
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_shop>> call = retrofitInterface.getSearchCafe(page, size, search);
        call.enqueue(new Callback<ArrayList<DTO_shop>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_shop>> call, Response<ArrayList<DTO_shop>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    page++;

                    for (int i = 0; i < response.body().size(); i++) {
                        adpater.scrollItem(new DTO_shop(
                                response.body().get(i).getName(),
                                response.body().get(i).getIndex(),
                                response.body().get(i).getImage()
                        ));
                        adpater.notifyItemInserted((page - 1) * size + i);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_shop>> call, Throwable t) {
                Log.e("Cafe, getData()", "검색어 : " + search);
                Log.e("Cafe, getData()", "에러 : " + t);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getData() {
        // 데이터 뿌려주기
        if(page == 1){
            adpater.clearItems();
        }

        Log.e("Cafe, getData()", "검색어 : " + search);
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_shop>> call = retrofitInterface.getSearchCafe(page, size, search);
        call.enqueue(new Callback<ArrayList<DTO_shop>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_shop>> call, Response<ArrayList<DTO_shop>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        adpater.addItem(new DTO_shop(
                                response.body().get(i).getName(),
                                response.body().get(i).getIndex(),
                                response.body().get(i).getImage()
                        ));
                    }
                }

            }

            @Override
            public void onFailure(Call<ArrayList<DTO_shop>> call, Throwable t) {
                Log.e("Cafe, getData()", "검색어 : " + search);
                Log.e("Cafe, getData()", "에러 : " + t);
            }
        });
    }
}


