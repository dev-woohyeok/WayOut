package com.example.wayout_ver_01.Fragment;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchTheme_adpater;
import com.example.wayout_ver_01.Retrofit.DTO_theme;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.FragmentBlankThemeBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentManage_Theme extends Fragment {
    private FragmentBlankThemeBinding bind;
    private int page = 1;
    private int size =8;
    private String user_id;
    private SearchTheme_adpater adpater;


    public static FragmentManage_Theme newInstance() {
        FragmentManage_Theme fragment = new FragmentManage_Theme();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentBlankThemeBinding.inflate(inflater,container,false);
        View view = bind.getRoot();
        user_id = PreferenceManager.getString(requireContext(),"userIndex");

        // 어뎁터 세팅
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(),2, LinearLayoutManager.VERTICAL,false);
        bind.manageThemeRv.setLayoutManager(gridLayoutManager);
        adpater = new SearchTheme_adpater(requireContext());
        adpater.setHasStableIds(true);
        bind.manageThemeRv.setAdapter(adpater);



        // 스크롤 페이징
        bind.manageThemeScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    getScroll();
                }
            }
        });


        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        page = 1;
    }

    private void getData() {
        if (page == 1) {
            adpater.clearItem();
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_theme>> call = retrofitInterface.getMyTheme(page, size, user_id);
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
                                response.body().get(i).getImage(),
                                response.body().get(i).getRate()
                        ));
                    }
                }
                if(adpater.getItemCount() > 0) {
                    bind.manageThemeTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_theme>> call, Throwable t) {

            }
        });
    }

    private void getScroll() {
        if(page == 1){
            page = 2;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_theme>> call = retrofitInterface.getMyTheme(page, size, user_id);
        call.enqueue(new Callback<ArrayList<DTO_theme>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_theme>> call, Response<ArrayList<DTO_theme>> response) {
                if(response.body() != null &  response.isSuccessful()){
                    for(int i = 0; i < response.body().size(); i++ ){

                        if(response.body().size() > 0){
                            page ++;
                        }

                        adpater.addItem(new DTO_theme(
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
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_theme>> call, Throwable t) {

            }
        });
    }
}