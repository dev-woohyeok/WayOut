package com.example.wayout_ver_01;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentComm_FreeBorad extends Fragment {

    RecyclerView recyclerView;
    FreeBoard_adapter freeBoard_adapter;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comm__free_borad, container, false);

        ProgressDialog progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();




        // 리사이클러뷰 설정 및 호출
        recyclerView = view.findViewById(R.id.free_rv);
        // 리사이클러뷰의 크기를 고정 시켜서 효율 상승 // 만들때마다 다시 만들지 않게 설정함
        recyclerView.setHasFixedSize(true);
        // 리사이클러뷰 방향 설정하기 , 어떤 레이아웃 매니저를 선택하냐에 따라서 보여주는 형태가 달라짐
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager); // 리사이크러뷰에 레이아웃 매니저 설정

        // 어뎁터 세팅
        freeBoard_adapter = new FreeBoard_adapter(requireContext());
        recyclerView.setAdapter(freeBoard_adapter);
        // 아이템 터치 헬퍼 달아주기 , swipe move 가능하게 해줌
//        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback(freeBoard_adapter));
        // 리사이클러뷰에 item touch helper 를 붙인다.
//        helper.attachToRecyclerView(recyclerView);
        // 어뎁터에 아이템 넣기


        // 현재 시간 가져오기
        long now = System.currentTimeMillis();
        // Date 생성하기
        Date date = new Date(now);
        // 가져오고 싶은 형식으로 가져오기
        /* yy : 년도 , MM : 날짜, dd : 날짜 ,a : 오전, 오후,
         hh : 12시간제 , HH : 24 시간제, mm : 분, ss : 초 */
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 a HH시 mm분");

        // String 형식으로 Convert
        String getDate = dateFormat.format(date);
        Log.e(TAG, "내용 : Date :" +getDate );

        for (int i = 0; i < 20; i++) {
            freeBoard_adapter.add_item(new FreeBoard("제목" + i, "http://3.34.133.23/Img/KqMcNlPMCh.jpg", "글쓴이" + i,  ""+getDate,"20","1"));
            freeBoard_adapter.notifyItemChanged(i);
        }
        progressDialog.dismiss();


        return view;
    }
}