package com.example.wayout_ver_01.Activity.MyPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchCafe_adpater;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivityMyLikeCafeBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLikeCafe extends AppCompatActivity {
    private ActivityMyLikeCafeBinding bind;
    private SearchCafe_adpater myLike_Cafe_adapter;
    private String user_id;
    private int page =1, size = 6;

    @Override
    protected void onResume() {
        super.onResume();
        /* 찜한 목록 불러오기 */
        getData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        page = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMyLikeCafeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        /* 유저 고유값 */
        user_id = PreferenceManager.getString(getBaseContext(), "userIndex");


        /* 어뎁터 세팅 */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        bind.MyLikeCafeRv.setLayoutManager(linearLayoutManager);
        myLike_Cafe_adapter = new SearchCafe_adpater(getBaseContext());
        bind.MyLikeCafeRv.setAdapter(myLike_Cafe_adapter);

        /* 스크롤 페이징 */
        bind.MyLikeCafeScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    getScroll();
                }
            }
        });


    }

    private void getData() {
        /* 돌아 왓을때 초기화 */
        if(page == 1){
            myLike_Cafe_adapter.clearItems();
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_shop>> call = retrofitInterface.getMyLikeCafe(page,size,user_id);
        call.enqueue(new Callback<ArrayList<DTO_shop>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_shop>> call, Response<ArrayList<DTO_shop>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if(myLike_Cafe_adapter.getItemCount() > 0){
                        bind.myLikeCafeTv.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < response.body().size(); i++) {
                        myLike_Cafe_adapter.addItem(new DTO_shop(
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

    private void getScroll() {
        if(page == 1){
            page = 2;
        }
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_shop>> call = retrofitInterface.getMyLikeCafe(page, size, user_id);
        call.enqueue(new Callback<ArrayList<DTO_shop>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_shop>> call, Response<ArrayList<DTO_shop>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().size() > 0){
                        page++;
                    }
                    for (int i = 0; i < response.body().size(); i++) {
                        myLike_Cafe_adapter.scrollItem(new DTO_shop(
                                response.body().get(i).getName(),
                                response.body().get(i).getIndex(),
                                response.body().get(i).getImage(),
                                response.body().get(i).getAddress(),
                                response.body().get(i).getTotal()
                        ));


                        myLike_Cafe_adapter.notifyItemInserted((page - 1) * size + i);
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