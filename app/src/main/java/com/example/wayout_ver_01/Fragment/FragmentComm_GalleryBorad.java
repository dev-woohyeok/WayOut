package com.example.wayout_ver_01.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.example.wayout_ver_01.Activity.Gallery.GalleryBoard_write;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Gallery.GalleryBoard_adapter;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentComm_GalleryBorad extends Fragment {

    private ImageView like, galleryBoard_reset, galleryBoard_search_btn;
    private EditText galleryBoard_search;
    private TextView galleryBoard_tv_spinner;
    private Button galleryBoard_write_btn;
    private RecyclerView galleryBoard_rv;
    private NestedScrollView galleryBoard_scroll;
    private ProgressBar galleryBoard_progress;
    private GalleryBoard_adapter galleryBoard_adapter;
    private Spinner galleryBoard_spinner;
    private boolean isClicked;
    private String category = "cafe";
    private InputMethodManager imm;
    private String search_con;
    private SwipeRefreshLayout galleryBoard_swipe;

    // search true 면 검색시 검색어가 적용되서 스크롤이 적용된다.
    // scroll 이 true 면 기존의 처음 불러온 아이템 들을 삭제하지 않고, 스크롤 페이징을 적용한다.
    private boolean scroll, search;
    private int page = 1, limit = 8;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comm__gallery_borad, container, false);

        // view 들 연결
        viewSet(view);
        // 검색창 필터 적용 1.카페명 / 2.테마명 / 3. 작성자
        setSpinner(view);
        // 리사이클러뷰 설정 및 호출
        setAdpater(view);

        // 글쓰기 엑티비티로 이동하기
        galleryBoard_write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 글 작성하기
                Intent intent = new Intent(view.getContext(), GalleryBoard_write.class);
                intent.putExtra("mode", false);
                startActivity(intent);
            }
        });

        // 스크롤 햇을때 페이징 구현
        galleryBoard_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                galleryBoard_progress.setVisibility(View.VISIBLE);
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    page ++;
                    // 검색시 검색 필터 스크롤
                    if(search){
                        // 검색 내용 필터 스크롤
                        getSearch();
                    }else {
                        // 기본 내용 스크롤
                        getScroll();
                    }
                    galleryBoard_progress.setVisibility(View.INVISIBLE);
                }
            }
        });

        // 검색 버튼 클릭시 필터 적요시키기
        galleryBoard_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 보낼 내용 세팅
                search_con = galleryBoard_search.getText().toString();

                //  검색 후 초기화 작업
                page = 1;
                galleryBoard_reset.setVisibility(View.VISIBLE);
                galleryBoard_rv.requestFocus();
                search = true;

                // 검색 결과에 해당하는 아이템 가져오기
                getSearchItems();

            }
        });

        galleryBoard_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 설정 초기화
                galleryBoard_search.setText("");
                page = 1;
                search = false;
                scroll = false;
                search_con = "";
                galleryBoard_reset.setVisibility(View.GONE);

                // 초기 게시판 가져오기
                getItems();
            }
        });
        // swipeRefresh 시 작동하는 명령 입력
        galleryBoard_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // swipe 시 진행할 동작
                // search true 면 검색시 검색어가 적용되서 스크롤이 적용된다.
                // scroll 이 true 면 기존의 처음 불러온 아이템 들을 삭제하지 않고, 스크롤 페이징을 적용한다.
                // page 는 스크롤 페이징 기본 설정
                // galleryBoard_search 는 검색어 입력창
                // search_con 은 검색어 필터를 적용할 내용
                // galleryBoard_reset 은 검색기능 종료시 종료 버튼
                // getItems() 는 처음 보이는 게시판을 세팅해준다.
                galleryBoard_search.setText("");
                page = 1;
                search = false;
                scroll = false;
                search_con = "";
                galleryBoard_reset.setVisibility(View.GONE);
                getItems();

//                // 업데이트가 끝났음을 알림
                galleryBoard_swipe.setRefreshing(false);
            }
        });

        // 로딩 종료
        galleryBoard_progress.setVisibility(View.INVISIBLE);

        return view;
    }
    // 프레그먼트 생명주기 세팅

    @Override
    public void onResume() {
        super.onResume();

        if(!search){
            getItems();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        category = "gallery_cafe";
        scroll = false;
        search = false;
        page = 1;
    }

    private void getSearchItems() {
        search_con = galleryBoard_search.getText().toString();
        page = 1;
        // con 을 포함해서 카테고리를 보냄
        galleryBoard_reset.setVisibility(View.VISIBLE);
        galleryBoard_search.setText("");
        galleryBoard_rv.requestFocus();
        search = true;
        String user_id = PreferenceManager.getString(requireContext(),"autoNick");

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_gallery>> call = retrofitInterface.getGallerySearch(page,limit,category,search_con, user_id);
        call.enqueue(new Callback<ArrayList<DTO_gallery>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_gallery>> call, Response<ArrayList<DTO_gallery>> response) {
                if(response.body() != null && response.isSuccessful()) {
                    if(!scroll) {
                        galleryBoard_adapter.clearList();
                        galleryBoard_adapter.notifyDataSetChanged();
                    }
                    for(int i = 0; i < response.body().size(); i++){
                        galleryBoard_adapter.addItem(new DTO_gallery(
                                    response.body().get(i).getWriter(),
                                    response.body().get(i).getCafe(),
                                    response.body().get(i).getTheme(),
                                    response.body().get(i).getDate(),
                                    response.body().get(i).getTotal_like(),
                                    response.body().get(i).isClick(),
                                    response.body().get(i).getContent(),
                                    response.body().get(i).getBoard_number(),
                                    response.body().get(i).getImage()));

                        galleryBoard_adapter.notifyItemInserted(i + (page - 1) * 8);
                    }
                    galleryBoard_progress.setVisibility(View.INVISIBLE);
                    imm.hideSoftInputFromWindow(galleryBoard_search.getWindowToken(), 0);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_gallery>> call, Throwable t) {

            }
        });
//        call.enqueue(new Callback<ArrayList<DTO_gallery>>() {
//            @Override
//            public void onResponse(Call<ArrayList<DTO_gallery>> call, Response<ArrayList<DTO_gallery>> response) {
//                if(response.body() != null && response.isSuccessful()){
//                    if (!scroll) {
//                        galleryBoard_adapter.clearList();
//                        galleryBoard_adapter.notifyDataSetChanged();
//                    }
//                    for (int i = 0; i < response.body().size(); i++) {
//                        galleryBoard_adapter.addItem(new DTO_gallery(
//                                    response.body().get(i).getWriter(),
//                                    response.body().get(i).getCafe(),
//                                    response.body().get(i).getTheme(),
//                                    response.body().get(i).getDate(),
//                                    response.body().get(i).getTotal_like(),
//                                    response.body().get(i).isClick(),
//                                    response.body().get(i).getContent(),
//                                    response.body().get(i).getBoard_number(),
//                                    response.body().get(i).getImage()));
//
//                        galleryBoard_adapter.notifyItemInserted(i + (page - 1) * 8);
//                    }
//                    galleryBoard_progress.setVisibility(View.INVISIBLE);
//                    imm.hideSoftInputFromWindow(galleryBoard_search.getWindowToken(), 0);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<DTO_gallery>> call, Throwable t) {
//                Toast.makeText(requireContext(), "오류 메세지 : " + t, Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

    public void getItems() {

        String user_id = PreferenceManager.getString(requireContext(), "autoNick");

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_gallery>> call = retrofitInterface.getGalleryBoard(page,limit,user_id);
        call.enqueue(new Callback<ArrayList<DTO_gallery>>() {

            @Override
            public void onResponse(Call<ArrayList<DTO_gallery>> call, Response<ArrayList<DTO_gallery>> response) {
                if(response.isSuccessful() && response.body() != null){
                    galleryBoard_adapter.clearList();
                    galleryBoard_adapter.notifyDataSetChanged();
                    for (int i = 0; i < response.body().size(); i++){
                                galleryBoard_adapter.addItem(new DTO_gallery(
                                        response.body().get(i).getWriter(),
                                        response.body().get(i).getCafe(),
                                        response.body().get(i).getTheme(),
                                        response.body().get(i).getDate(),
                                        response.body().get(i).getTotal_like(),
                                        response.body().get(i).isClick(),
                                        response.body().get(i).getContent(),
                                        response.body().get(i).getBoard_number(),
                                        response.body().get(i).getImage()));
                                galleryBoard_adapter.notifyItemInserted(i);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_gallery>> call, Throwable t) {

            }
        });
    }

    private void getSearch() {
    }

    private void getScroll() {
        scroll = true;
        String user_id = PreferenceManager.getString(requireContext(), "autoNick");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_gallery>> call = retrofitInterface.getGalleryBoard(page,limit,user_id);
        call.enqueue(new Callback<ArrayList<DTO_gallery>>() {

            @Override
            public void onResponse(Call<ArrayList<DTO_gallery>> call, Response<ArrayList<DTO_gallery>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for (int i = 0; i < response.body().size(); i++){
                        galleryBoard_adapter.addItem(new DTO_gallery(
                                response.body().get(i).getWriter(),
                                response.body().get(i).getCafe(),
                                response.body().get(i).getTheme(),
                                response.body().get(i).getDate(),
                                response.body().get(i).getTotal_like(),
                                response.body().get(i).isClick(),
                                response.body().get(i).getContent(),
                                response.body().get(i).getBoard_number(),
                                response.body().get(i).getImage()));
                        galleryBoard_adapter.notifyItemInserted(galleryBoard_adapter.getItemCount() + i);
                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_gallery>> call, Throwable t) {

            }
        });
    }

    private void setAdpater(View view) {
        //span 갯수 체크
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL ,false);
        galleryBoard_rv.setLayoutManager(gridLayoutManager);
        galleryBoard_adapter = new GalleryBoard_adapter(requireContext());
        galleryBoard_rv.setAdapter(galleryBoard_adapter);
    }

    private void setSpinner(View view) {
        Spinner spinner = view.findViewById(R.id.galleryBoard_spinner);
        String[] items = {"카페명", "테마명", "작성자"};

        // 스피너 어뎁터 설정 1.권한 2.레이아웃 3. 리스트
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                galleryBoard_tv_spinner.setText(items[position]);
                switch (position) {
                    case 1:
                        category = "gallery_theme";
                        break;
                    case 2:
                        category = "gallery_writer";
                        break;
                    default:
                        category = "gallery_cafe";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void viewSet(View view) {
        galleryBoard_swipe = view.findViewById(R.id.galleryBoard_swipe);
        galleryBoard_reset = view.findViewById(R.id.galleryBoard_reset);
        galleryBoard_search_btn = view.findViewById(R.id.galleryBoard_search_btn);
        galleryBoard_search = view.findViewById(R.id.galleryBoard_search);
        galleryBoard_tv_spinner = view.findViewById(R.id.galleryBoard_tv_spinner);
        galleryBoard_write_btn = view.findViewById(R.id.galleryBoard_write_btn);
        galleryBoard_rv = view.findViewById(R.id.galleryBoard_rv);
        galleryBoard_scroll = view.findViewById(R.id.galleryBoard_scroll);
        galleryBoard_progress = view.findViewById(R.id.galleryBoard_progress);
        imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}