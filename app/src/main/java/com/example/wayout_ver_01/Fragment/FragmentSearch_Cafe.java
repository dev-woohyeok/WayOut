package com.example.wayout_ver_01.Fragment;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
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
import android.view.inputmethod.InputMethodManager;

import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchCafe_adpater;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.FragmentSearchCafeBinding;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.rx3.TedPermission;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentSearch_Cafe extends Fragment {
    private String search;
    private FragmentSearchCafeBinding binding;
    private SearchCafe_adpater adpater;
    private int page = 1;
    private int size = 8;
    private boolean isChecked;
    private double cur_lat, cur_log;

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
            Log.e("Cafe, 54", "검색어 : " + search);
            Log.e("Cafe, 54", "create:  호출");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("resume", "content : " + search);
        if (!search.isEmpty()) {
            if (!isChecked) {
                adpater.clearItems();
                isChecked = true;
                Log.e("itemsClear", "아이템 클리어?");
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("Cafe", "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        page = 1;
        Log.e("cafe, 79", "onStop 호출");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("Cafe", "onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.e("Cafe", "onDestroyView");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchCafeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // 어뎁터 세팅
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.searchShopRv.setLayoutManager(linearLayoutManager);
        adpater = new SearchCafe_adpater(requireContext());
        binding.searchShopRv.setAdapter(adpater);

        RecyclerView.ItemAnimator animator = binding.searchShopRv.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        // 스크롤 페이징
        binding.searchShopScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (page == 1) {
                        page = 2;
                    }
                    getScroll();
                }
            }
        });
        getData();

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
                                response.body().get(i).getImage(),
                                response.body().get(i).getAddress(),
                                response.body().get(i).getTotal()
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


    private void getData() {


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
                                response.body().get(i).getImage(),
                                response.body().get(i).getAddress(),
                                response.body().get(i).getTotal()
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


