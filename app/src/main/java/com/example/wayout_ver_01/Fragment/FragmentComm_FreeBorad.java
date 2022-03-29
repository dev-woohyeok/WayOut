package com.example.wayout_ver_01.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wayout_ver_01.Activity.FreeBoard.FreeBoard_read;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.FreeBoard;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.FreeBoard_adapter;
import com.example.wayout_ver_01.Activity.FreeBoard.FreeBoard_write;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_board;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentComm_FreeBorad extends Fragment {

    RecyclerView recyclerView;
    FreeBoard_adapter freeBoard_adapter;
    private final String TAG = this.getClass().getSimpleName();
    Button freeBoard_write;
    EditText freeBoard_search;
    ImageView freeBoard_search_btn;
    ImageView freeBoard_reply;
    ProgressBar freeBoard_progress;
    NestedScrollView freeBoard_scroll;
    boolean scroll;
    int page =1, limit = 8;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comm__free_borad, container, false);
        freeBoard_write = view.findViewById(R.id.freeBoard_write_btn);
        freeBoard_search = view.findViewById(R.id.freeBoard_search);
        freeBoard_search_btn = view.findViewById(R.id.freeBoard);

        freeBoard_progress = view.findViewById(R.id.freeBoard_progress);
        freeBoard_scroll = view.findViewById(R.id.freeBoard_scroll);

        // 리사이클러뷰 설정 및 호출
        recyclerView = view.findViewById(R.id.free_rv);
        // 리사이클러뷰의 크기를 고정 시켜서 효율 상승 // 만들때마다 다시 만들지 않게 설정함
//        recyclerView.setHasFixedSize(true);
        // 리사이클러뷰 방향 설정하기 , 어떤 레이아웃 매니저를 선택하냐에 따라서 보여주는 형태가 달라짐
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager); // 리사이크러뷰에 레이아웃 매니저 설정

        // 어뎁터 세팅
        freeBoard_adapter = new FreeBoard_adapter(requireContext());


        freeBoard_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), FreeBoard_write.class);
                // 클린, 뉴 , single
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // 리사이클러뷰 스크롤 페이징
        freeBoard_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {
                    page ++;
                    getScroll(freeBoard_adapter,recyclerView);
                    freeBoard_progress.setVisibility(View.INVISIBLE);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getItems(freeBoard_adapter,recyclerView);
    }

    private void getScroll(FreeBoard_adapter adapter, RecyclerView recyclerView){
        // FreeBoard db 가져오기
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_board>> call = retrofitInterface.getFreeBoard(page,limit);
        call.enqueue(new Callback<ArrayList<DTO_board>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DTO_board>> call, @NonNull Response<ArrayList<DTO_board>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        Log.e(TAG, "내용 : 레트로핏 통신 성공");
                        adapter.add_item(new FreeBoard
                                (
                                        response.body().get(i).getTitle(),
                                        response.body().get(i).getWriter(),
                                        response.body().get(i).getDate(),
                                        response.body().get(i).getBoardNum(),
                                        response.body().get(i).getReply_number()
                                ));

//                        Log.e(TAG, "내용 : ResponseBody" + response.body());
//                        Log.e(TAG, "내용 : List 길이 : " + response.body().size() );
//                        Log.e(TAG, "내용 : title : " + i + response.body().get(i).getTitle());
//                        Log.e(TAG, "내용 : writer : "+ i + response.body().get(i).getWriter());
//                        Log.e(TAG, "내용 : date : " + i + response.body().get(i).getDate());
//                        Log.e(TAG, "내용 : BoardNum : "+ i + response.body().get(i).getBoardNum());

                        adapter.notifyItemInserted(i + (page-1)*8);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<DTO_board>> call, @NonNull Throwable t) {

            }
        });
    }


    public void getItems(FreeBoard_adapter adapter, RecyclerView recyclerView){
        // FreeBoard db 가져오기
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_board>> call = retrofitInterface.getFreeBoard(page,limit);
        call.enqueue(new Callback<ArrayList<DTO_board>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DTO_board>> call, @NonNull Response<ArrayList<DTO_board>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    if(!scroll) {
                        adapter.clear_list();
                    }
                    for (int i = 0; i < response.body().size(); i++) {
                        Log.e(TAG, "내용 : 레트로핏 통신 성공");
                        adapter.add_item(new FreeBoard
                                (
                                        response.body().get(i).getTitle(),
                                        response.body().get(i).getWriter(),
                                        response.body().get(i).getDate(),
                                        response.body().get(i).getBoardNum(),
                                        response.body().get(i).getReply_number()
                                ));

//                        Log.e(TAG, "내용 : ResponseBody" + response.body());
//                        Log.e(TAG, "내용 : List 길이 : " + response.body().size() );
//                        Log.e(TAG, "내용 : title : " + i + response.body().get(i).getTitle());
//                        Log.e(TAG, "내용 : writer : "+ i + response.body().get(i).getWriter());
//                        Log.e(TAG, "내용 : date : " + i + response.body().get(i).getDate());
//                        Log.e(TAG, "내용 : BoardNum : "+ i + response.body().get(i).getBoardNum());

                        adapter.notifyItemInserted(i + (page-1)*8);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<DTO_board>> call, @NonNull Throwable t) {

            }
        });

    }
}