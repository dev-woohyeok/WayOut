package com.example.wayout_ver_01.Fragment;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchCafe_adpater;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.FragmentManageCafeBinding;
import com.example.wayout_ver_01.databinding.FragmentSearchCafeBinding;
import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentManage_Cafe extends Fragment {
    private FragmentManageCafeBinding bind;
    private SearchCafe_adpater adpater;
    private int page= 1, size= 8;
    private String user_id;

    public static FragmentManage_Cafe newInstance() {
        FragmentManage_Cafe fragment = new FragmentManage_Cafe();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
    @Override
    public void onStop() {
        super.onStop();
        page = 1;
    }

    private void getData() {
        if(page == 1){
            adpater.clearItems();
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_shop>> call = retrofitInterface.getMyCafe(page, size, user_id);
        call.enqueue(new Callback<ArrayList<DTO_shop>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_shop>> call, Response<ArrayList<DTO_shop>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {

                        // 로그 세팅
                        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                                .showThreadInfo(false)// (Optional) Whether to show thread info or not. Default true
                                .methodCount(2)// (Optional) How many method line to show. Default 2
                                .methodOffset(0)// (Optional) Hides internal method calls up to offset. Default 5
                                .tag("PRETTY_LOGGER")// (Optional) Global tag for every log. Default PRETTY_LOGGER
                                .build();
                        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

                        Gson gson = new Gson();
                        String str = gson.toJson(response.body());
                        Logger.json(str);


                        adpater.addItem(new DTO_shop(
                                response.body().get(i).getName(),
                                response.body().get(i).getIndex(),
                                response.body().get(i).getImage(),
                                response.body().get(i).getAddress(),
                                response.body().get(i).getTotal()
                        ));
                    }
                }
                if(adpater.getItemCount() > 0) {
                    bind.manageCafeTv.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<DTO_shop>> call, Throwable t) {

                Log.e("Cafe, getData()", "에러 : " + t);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bind = FragmentManageCafeBinding.inflate(inflater,container,false);
        View view = bind.getRoot();

        user_id = PreferenceManager.getString(requireActivity(),"userIndex");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        bind.manageCafeRv.setLayoutManager(linearLayoutManager);
        adpater = new SearchCafe_adpater(requireContext());
        bind.manageCafeRv.setAdapter(adpater);

        // 스크롤 페이징
        bind.manageCafeScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {

                    getScroll();

                }
            }
        });

        return view;
    }

    private void getScroll() {

        if (page == 1) {
            page = 2;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_shop>> call = retrofitInterface.getMyCafe(page, size, user_id);
        call.enqueue(new Callback<ArrayList<DTO_shop>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_shop>> call, Response<ArrayList<DTO_shop>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().size() > 0){
                        page++;
                    }
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

                Log.e("Cafe, getData()", "에러 : " + t);
            }
        });


    }
}