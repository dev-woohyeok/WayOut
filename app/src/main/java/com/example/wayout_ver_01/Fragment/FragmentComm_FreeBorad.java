package com.example.wayout_ver_01.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    ImageView freeBoard_search_btn, freeBoard_reset;
    ImageView freeBoard_reply;
    ProgressBar freeBoard_progress;
    TextView freeBoard_spinner_tv;
    NestedScrollView freeBoard_scroll;
    String category = "freeboard_title";
    InputMethodManager imm;
    SwipeRefreshLayout freeBoard_swipe;
    String search_con;
    boolean scroll, search;
    int page = 1, limit = 8;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comm__free_borad, container, false);
        freeBoard_write = view.findViewById(R.id.freeBoard_write_btn);
        freeBoard_search = view.findViewById(R.id.freeBoard_search);
        freeBoard_search_btn = view.findViewById(R.id.freeBoard_search_btn);
        freeBoard_progress = view.findViewById(R.id.freeBoard_progress);
        freeBoard_scroll = view.findViewById(R.id.freeBoard_scroll);
        freeBoard_spinner_tv = view.findViewById(R.id.freeBoard_tv_spinner);
        freeBoard_reset = view.findViewById(R.id.freeBoard_reset);
        freeBoard_swipe = view.findViewById(R.id.freeBoard_swipe);
        imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        Spinner spinner = view.findViewById(R.id.freeBoard_spinner);
        String[] items = {"제목", "내용", "작성자"};

        // 스피너 어뎁터 설정 1. 권한 2. 레이아웃 3. 리스트
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                freeBoard_spinner_tv.setText(items[position]);
                // 제목, 내용, 작성자
                switch (position) {
                    case 1:
                        category = "freeboard_content";
                        break;
                    case 2:
                        category = "freeboard_writer";
                        break;
                    default:
                        category = "freeboard_title";
                        break;
                }
                Log.e("자유 게시판 필터 적용 현황 ", "내용 : category : " + category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        // 리사이클러뷰 설정 및 호출
        recyclerView = view.findViewById(R.id.free_rv);
        // 리사이클러뷰의 크기를 고정 시켜서 효율 상승 // 만들때마다 다시 만들지 않게 설정함
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
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // 리사이클러뷰 스크롤 페이징
        freeBoard_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    // 검색 시도하였을때 데이터 가져오는 메서드
                    if(search){
                        getSearch(freeBoard_adapter, recyclerView);
                        // 일반적으로 데이터를 가져오는 메서드
                    }else {
                        getScroll(freeBoard_adapter, recyclerView);
                    }
                    freeBoard_progress.setVisibility(View.INVISIBLE);
                }
            }
        });

        freeBoard_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_con = freeBoard_search.getText().toString();
                page = 1;
                // con 을 포함해서 카테고리를 보냄
                freeBoard_reset.setVisibility(View.VISIBLE);
                freeBoard_search.setText("");
                recyclerView.requestFocus();
                search = true;

                RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                Call<ArrayList<DTO_board>> call = retrofitInterface.getFreeSearch(page, limit,category,search_con);
                call.enqueue(new Callback<ArrayList<DTO_board>>() {
                    @Override
                    public void onResponse(Call<ArrayList<DTO_board>> call, Response<ArrayList<DTO_board>> response) {
                        if(response.body() != null && response.isSuccessful()){
                            if (!scroll) {
                                freeBoard_adapter.clear_list();
                                freeBoard_adapter.notifyDataSetChanged();
                            }
                            for (int i = 0; i < response.body().size(); i++) {
                                Log.e(TAG, "내용 : 레트로핏 통신 성공");
                                freeBoard_adapter.add_item(new FreeBoard
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

                                freeBoard_adapter.notifyItemInserted(i + (page - 1) * 8);
                                recyclerView.setAdapter(freeBoard_adapter);
                                freeBoard_progress.setVisibility(View.INVISIBLE);
                                imm.hideSoftInputFromWindow(freeBoard_search.getWindowToken(), 0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<DTO_board>> call, Throwable t) {
                        Toast.makeText(requireContext(), "오류 메세지 : " + t, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        // 리셋 버튼
        freeBoard_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freeBoard_search.setText("");
                page = 1;
                search = false;
                scroll = false;
                search_con = "";
                getItems(freeBoard_adapter, recyclerView);
                freeBoard_reset.setVisibility(View.GONE);
            }
        });

        freeBoard_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 설정 초기화
                // 검색어 입력창 초기화, 스크롤 페이징 초기화
                // 검색 true 시 검색 스크롤 페이징 적용
                // scroll true 시 검색 스크롤시 해당 페이징 내용 불러옴
                // search_con 은 검색 필터시 적용되는 Con 내용
                // reset 은 검색모드 종료하는 단축키
                freeBoard_search.setText("");
                page = 1;
                search = false;
                scroll = false;
                search_con = "";
                getItems(freeBoard_adapter, recyclerView);
                freeBoard_reset.setVisibility(View.GONE);
                // refreshing 종료 표시
                freeBoard_swipe.setRefreshing(false);
            }
        });

        return view;
    }

    private void getSearch(FreeBoard_adapter freeBoard_adapter, RecyclerView recyclerView) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(freeBoard_search.getWindowToken(),0);
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_board>> call = retrofitInterface.getFreeSearch(page,limit,category,search_con);
        call.enqueue(new Callback<ArrayList<DTO_board>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_board>> call, Response<ArrayList<DTO_board>> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    for (int i = 0; i < response.body().size(); i++) {
                        freeBoard_adapter.add_item(new FreeBoard
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
                        freeBoard_adapter.notifyItemInserted(i + (page - 1) * 8);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_board>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.e("자유게시판 onResume 에서 search", "search : " + search);
        // 검색이 아닐때만 기본 게시판 세팅 불러오기
        if(!search) {
            getItems(freeBoard_adapter, recyclerView);
//            Log.e("FreeBoard_검색전 초기화", "검색전 초기화 완료");
        }else {
//            Log.e("freeBoard_검색후 초기화", "검색후 초기화 완료");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("짜유게시판 라이프 사이클 파괴시", " 파괴됨");
    }

    private void getScroll(FreeBoard_adapter adapter, RecyclerView recyclerView) {
        // FreeBoard db 가져오기
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_board>> call = retrofitInterface.getFreeBoard(page, limit);
        call.enqueue(new Callback<ArrayList<DTO_board>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DTO_board>> call, @NonNull Response<ArrayList<DTO_board>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
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
                        adapter.notifyItemInserted(i + (page - 1) * 8);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<DTO_board>> call, @NonNull Throwable t) {

            }
        });
    }


    public void getItems(FreeBoard_adapter adapter, RecyclerView recyclerView) {
        // FreeBoard db 가져오기
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_board>> call = retrofitInterface.getFreeBoard(page, limit);
        call.enqueue(new Callback<ArrayList<DTO_board>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DTO_board>> call, @NonNull Response<ArrayList<DTO_board>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!scroll) {
                        adapter.clear_list();
                        adapter.notifyDataSetChanged();
                    }
                    for (int i = 0; i < response.body().size(); i++) {
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

                        adapter.notifyItemInserted(i + (page - 1) * 8);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<DTO_board>> call, @NonNull Throwable t) {

            }
        });

    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        scroll = false;
//        search = false;
//        page = 1;
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        category = "freeboard_title";
        scroll = false;
        search = false;
        search_con = "";
        page = 1;
    }
}