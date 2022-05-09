package com.example.wayout_ver_01.Activity.CreateShop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.wayout_ver_01.Activity.Home;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.RecyclerView.Theme.Search_adapter;
import com.example.wayout_ver_01.Retrofit.DTO_history;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.Retrofit.User;
import com.example.wayout_ver_01.databinding.ActivitySearchShopBinding;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search_shop extends AppCompatActivity {
    private ActivitySearchShopBinding binding;
    private Search_adapter search_adapter;

    // 유저 Index
    private String index;
    private boolean checked;
    private Gson gson;
    private Search_shop my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 최신 검색어 동작 로직
        // 1. 검색어를 입력했는지 확인 -> true 2번 진행
        // 2. 검색어를 저장하는지 아닌지 확인 ->  true 3번 진행
        // 3. 검색어가 중복됬는지 아닌지 확인 -> true 검색어 어뎁터에 추가
        // 4. onStop 에서 최종 목록을 서버에 다시 저장 (전부 지우고, 다시 저장) 유저 Index 와 dataList 를 서버로 올림

        // 검색어 Shared 에 저장 불러오기
        String str = PreferenceManager.getString(getBaseContext(), "history");
        checked = PreferenceManager.getBoolean(getBaseContext(), "isChecked");
        Log.e("최신 기록", str);

        // String -> ArrayList 변환
        gson = new Gson();
        ArrayList<String> history = gson.fromJson(str, ArrayList.class);

        // Adapter 에 넣기
        // my = 현재 엑티비티 넣기
        my = this;
        search_adapter = new Search_adapter(getBaseContext(), my);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.searchHistoryRv.setLayoutManager(gridLayoutManager);
        binding.searchHistoryRv.setAdapter(search_adapter);
        if(history != null) {
            for (int i = 0; i < history.size(); i++) {
                search_adapter.addItem(new DTO_history(history.get(i)));
            }
        }

        // switch 버튼 처리
        binding.searchHistorySwitch.setChecked(checked);


        binding.searchHistorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checked = true;
                Log.e("현재 스위치", "true");
            } else {
                checked = false;
                Log.e("현재 스위치", "false");
            }
        });


        // 모두 지우기
        binding.searchHistoryAll.setOnClickListener(v -> {
            search_adapter.clearItems();
        });

        // 검색어 입력 버튼을 눌럿을 때 -> 검색어 입력한 값이 있으면 검색 페이지로 결과를 콜백함
        binding.searchHistoryBtn.setOnClickListener(v -> {
            searchHistoryBtn();
        });


        /////// onCreate 끝
    }

    private void searchHistoryBtn() {
        // 여백 제거
        String content = binding.searchHistoryContent.getText().toString().trim();
        Log.e("Search_shop,", "보내기 전 - 검색어 : " + content);

        // 유효성 체크
        if (content.isEmpty()) {
            binding.searchHistoryContent.setError("검색어를 입력해주세요");
            binding.searchHistoryContent.requestFocus();
            return;
        }

        // 저장 버튼 true , false 확인
        if (!checked) {
            Intent i = new Intent(Search_shop.this, Home.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("검색어", content);
            setResult(RESULT_OK, i);
            finish();
            Log.e("저장안함", "응~ 저장안돼");
            return;
        }

        // 중복 체크
        if (!search_adapter.getContents().contains(content)) {
            search_adapter.addItem(new DTO_history(content));
        }

        // 콜백 요청 결과 보내기
        Intent i = new Intent(Search_shop.this, Home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("검색어", content);
        setResult(RESULT_OK, i);
        finish();
        Log.e("콜백 값 가져오기", "콜백 값 : " + content);
    }


    @Override
    protected void onStop() {
        super.onStop();

        gson = new Gson();
        String stringArr = gson.toJson(search_adapter.getContents());
        Log.e("json 변환", "값 : " + stringArr);
        PreferenceManager.setString(getBaseContext(), "history", stringArr);
        PreferenceManager.setBoolean(getBaseContext(), "isChecked", checked);
        Log.e("스위치 저장", " checked : " + checked);
        Log.e("최신 검색어 저장", " checked : " + stringArr);
    }
}