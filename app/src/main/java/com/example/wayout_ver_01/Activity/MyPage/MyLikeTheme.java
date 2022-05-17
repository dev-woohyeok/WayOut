package com.example.wayout_ver_01.Activity.MyPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchTheme_adpater;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.DTO_theme;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivityMyLikeThemeBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLikeTheme extends AppCompatActivity {
    private ActivityMyLikeThemeBinding bind;
    private int page = 1, size = 8;
    private SearchTheme_adpater myLike_Theme_adapter;
    private String user_id;

    @Override
    protected void onStop() {
        super.onStop();
        page = 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        if (page == 1) {
            myLike_Theme_adapter.clearItem();
        }
        Log.e("함 등짝을 보자", "page : " + page);
        Log.e("함 등짝을 보자", "page : " + size);
        Log.e("함 등짝을 보자", "page : " + user_id);
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_theme>> call = retrofitInterface.getMyLikeTheme(page,size,user_id);
        call.enqueue(new Callback<ArrayList<DTO_theme>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_theme>> call, Response<ArrayList<DTO_theme>> response) {

                for(int i = 0; i < response.body().size(); i++ ){

                    myLike_Theme_adapter.addItem(new DTO_theme(
                            response.body().get(i).getIndex(),
                            response.body().get(i).getName(),
                            response.body().get(i).getDifficult(),
                            response.body().get(i).getLimit(),
                            response.body().get(i).getGenre(),
                            response.body().get(i).getCafe(),
                            response.body().get(i).getImage(),
                            response.body().get(i).getRate()
                    ));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_theme>> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMyLikeThemeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        user_id = PreferenceManager.getString(getApplicationContext(), "userIndex");

        GridLayoutManager linearLayoutManager = new GridLayoutManager(getBaseContext(),2, GridLayoutManager.VERTICAL, false);
        bind.myLikeThemeRv.setLayoutManager(linearLayoutManager);
        myLike_Theme_adapter = new SearchTheme_adpater(getBaseContext());
        bind.myLikeThemeRv.setAdapter(myLike_Theme_adapter);

        bind.myLikeThemeScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    getScroll();
                }
            }
        });
    }

    private void getScroll() {
        if(page == 1){
            page = 2;
        }


        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_theme>> call = retrofitInterface.getMyLikeTheme(page,size,user_id);
        call.enqueue(new Callback<ArrayList<DTO_theme>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_theme>> call, Response<ArrayList<DTO_theme>> response) {

                for(int i = 0; i < response.body().size(); i++ ){
                    if(response.body().size() > 0){
                        page ++;
                    }
                    myLike_Theme_adapter.addItem(new DTO_theme(
                            response.body().get(i).getIndex(),
                            response.body().get(i).getName(),
                            response.body().get(i).getDifficult(),
                            response.body().get(i).getLimit(),
                            response.body().get(i).getGenre(),
                            response.body().get(i).getCafe(),
                            response.body().get(i).getImage(),
                            response.body().get(i).getRate()
                    ));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_theme>> call, Throwable t) {

            }
        });

    }
}