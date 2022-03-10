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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wayout_ver_01.Adapter.FreeBoard;
import com.example.wayout_ver_01.Adapter.FreeBoard_adapter;
import com.example.wayout_ver_01.Activity.FreeBoard_write;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comm__free_borad, container, false);

//        ProgressDialog progressDialog = new ProgressDialog(requireActivity());
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();

        freeBoard_write = view.findViewById(R.id.freeBoard_write_btn);
        freeBoard_search = view.findViewById(R.id.freeBoard_search);
        freeBoard_search_btn = view.findViewById(R.id.freeBoard);







        freeBoard_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), FreeBoard_write.class);
                // 클린, 뉴 , single
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 리사이클러뷰 설정 및 호출
        recyclerView = requireActivity().findViewById(R.id.free_rv);
        // 리사이클러뷰의 크기를 고정 시켜서 효율 상승 // 만들때마다 다시 만들지 않게 설정함
        recyclerView.setHasFixedSize(true);
        // 리사이클러뷰 방향 설정하기 , 어떤 레이아웃 매니저를 선택하냐에 따라서 보여주는 형태가 달라짐
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager); // 리사이크러뷰에 레이아웃 매니저 설정

        // 어뎁터 세팅
        freeBoard_adapter = new FreeBoard_adapter(requireContext());

        // 아이템 터치 헬퍼 달아주기 , swipe move 가능하게 해줌
//        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback(freeBoard_adapter));
        // 리사이클러뷰에 item touch helper 를 붙인다.
//        helper.attachToRecyclerView(recyclerView);
        // 어뎁터에 아이템 넣기

        getItems(freeBoard_adapter,recyclerView);
    }

    public void getItems(FreeBoard_adapter adapter, RecyclerView recyclerView){
        // FreeBoard db 가져오기
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_board>> call = retrofitInterface.getFreeBoard();
        call.enqueue(new Callback<ArrayList<DTO_board>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DTO_board>> call, @NonNull Response<ArrayList<DTO_board>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "내용 : ResponseBody" + response.body());
                    Log.e(TAG, "내용 : List 길이 : " + response.body().size() );
                    for (int i = 0; i < response.body().size(); i++) {
                        Log.e(TAG, "내용 : 레트로핏 통신 성공");
                        adapter.add_item(new FreeBoard
                                (
                                        response.body().get(i).getTitle(),
                                        response.body().get(i).getWriter(),
                                        response.body().get(i).getDate(),
                                        response.body().get(i).getBoardNum()
                                ));
                        Log.e(TAG, "내용 : title : " + i + response.body().get(i).getTitle());
                        Log.e(TAG, "내용 : writer : "+ i + response.body().get(i).getWriter());
                        Log.e(TAG, "내용 : date : " + i + response.body().get(i).getDate());
                        Log.e(TAG, "내용 : BoardNum : "+ i + response.body().get(i).getBoardNum());

                        adapter.notifyItemChanged(i);
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