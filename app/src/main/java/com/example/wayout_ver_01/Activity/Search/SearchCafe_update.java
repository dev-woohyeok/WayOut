package com.example.wayout_ver_01.Activity.Search;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Activity.CreateShop.CreateShop_address;
import com.example.wayout_ver_01.Activity.CreateShop.CreateShop_write;
import com.example.wayout_ver_01.Activity.CreateShop.CreateTheme_write;
import com.example.wayout_ver_01.Class.NetworkState;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.ItemTouchHelperCallback;
import com.example.wayout_ver_01.RecyclerView.Gallery.GalleryWrite_adapter;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;
import com.example.wayout_ver_01.Retrofit.DTO_image;
import com.example.wayout_ver_01.Retrofit.DTO_img;
import com.example.wayout_ver_01.Retrofit.DTO_review;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.DTO_theme;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivitySearchCafeUpdateBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class SearchCafe_update extends AppCompatActivity {
    private ActivitySearchCafeUpdateBinding bind;
    private ArrayList<String> images = new ArrayList<>();
    private GalleryWrite_adapter cafe_update_adapter;
    private ItemTouchHelper itemTouchHelper;
    private String open_time, close_time, call, cafe_index;
    private ProgressDialog progressDialog;
    private InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySearchCafeUpdateBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        setAdapter();
        setData();

        // 이미지 갤러리에서 3장까지 가져오기
        bind.cafeUpdateImg.setOnClickListener(v -> {
            // 절대 경로로 담아 놓은 임시 리스트를 초기화
            // 재선택시 기존의 이미지와 중복되지 않기 위해서
            images.clear();

            // 선택한 이미지는 3개 까지 가능하다
            if (cafe_update_adapter.getItemCount() < 3) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 2222);
            } else {
                Toast.makeText(getApplicationContext(), "사진은 3장까지 선택이 가능합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        bind.cafeUpdateKeyboard.setOnClickListener(v -> {
            if(getCurrentFocus() !=  null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        /* 카카오 API -> 도로명 주소 가져오기 */
        bind.cafeUpdateAddress.setOnClickListener(v -> {
            int status = NetworkState.getConnectivityStatus(getApplicationContext());
            // 인터넷 연결이 되어있는 경우만
            if (status == NetworkState.TYPE_MOBILE || status == NetworkState.TYPE_WIFI) {
                Log.e("주소 페이지 설정", "인터넷 연결 확인 OK ");
                Intent i = new Intent(getApplicationContext(), CreateShop_address.class);
                // 화면 전환 애니메이션 없애기
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(0, 0);
                startActivityForResult(i, 3333);
            } else {
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세여", Toast.LENGTH_SHORT).show();
            }
        });

        /* 오픈 시간 설정 */
        bind.cafeUpdateOpen.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(SearchCafe_update.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    (TimePickerDialog.OnTimeSetListener) (view, hourOfDay, minute) -> {
                        String am = "오전 ";
                        String hour = hourOfDay + " : ";
                        String min = minute + "";

                        if (hourOfDay < 10) {
                            hour = "0" + hourOfDay + " : ";
                        } else if (hourOfDay >= 12) {
                            am = "오후 ";
                            if (hourOfDay > 12) {
                                hour = (hourOfDay - 12) + " : ";
                            }
                        }
                        if (minute < 10) {
                            min = "0" + minute;
                        }
                        open_time = am + hour + min;
                        bind.cafeUpdateOpenTime.setText(open_time);
                    }
                    , 9, 0, false);
            timePickerDialog.setTitle("오픈 시간 설정");
            timePickerDialog.show();
        });

        /* 마감 시간 설정 */
        bind.cafeUpdateClose.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(SearchCafe_update.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    (TimePickerDialog.OnTimeSetListener) (view, hourOfDay, minute) -> {
                        String am = "오전 ";
                        String hour = hourOfDay + " : ";
                        String min = minute + "";

                        if (hourOfDay < 10) {
                            hour = "0" + hourOfDay + " : ";
                        } else if (hourOfDay >= 12) {
                            am = "오후 ";
                            if (hourOfDay > 12) {
                                hour = (hourOfDay - 12) + " : ";
                            }
                        }
                        if (minute < 10) {
                            min = "0" + minute;
                        }
                        close_time = am + hour + min;
                        bind.cafeUpdateCloseTime.setText(close_time);
                    }
                    , 22, 0, false);
            timePickerDialog.setTitle("마감 시간 설정");
            timePickerDialog.show();
        });

        /* Info 입력 */
        bind.cafeUpdateInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (bind.cafeUpdateInfo.isFocusable() && !s.toString().isEmpty()) {
                    int number = bind.cafeUpdateInfoNumber.getText().toString().length();
                    bind.cafeUpdateInfoNumber.setText(number + "");
                    if (number >= 200) {
                        number = 200;
                        Toast.makeText(getApplicationContext(), "200자까지 입력이 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /* 매장 정보 수정 후 -> result ok 돌아감 */
        bind.cafeUpdateSubmit.setOnClickListener(v -> {

            // 데이터 세팅
            // 매장 이름
            // 매장 주소 (주소, 상세 정보)
            // 매장 소개
            // 영업 시간 (open, close)
            // 휴무일
            RequestBody shop_name = RequestBody.create(bind.cafeUpdateName.getText().toString(), MediaType.parse("text/plain"));
            RequestBody shop_address = RequestBody.create(bind.cafeUpdateAddress.getText().toString(), MediaType.parse("text/plain"));
            RequestBody shop_address_more = RequestBody.create(bind.cafeUpdateAddressMore.getText().toString(), MediaType.parse("text/plain"));
            RequestBody shop_info = RequestBody.create(bind.cafeUpdateInfo.getText().toString(), MediaType.parse("text/plain"));
            RequestBody shop_open = RequestBody.create(bind.cafeUpdateOpenTime.getText().toString(), MediaType.parse("text/plain"));
            RequestBody shop_close = RequestBody.create(bind.cafeUpdateCloseTime.getText().toString(), MediaType.parse("text/plain"));
            RequestBody shop_holiday = RequestBody.create(bind.cafeUpdateHoliday.getText().toString(), MediaType.parse("text/plain"));
            RequestBody shop_index = RequestBody.create(cafe_index, MediaType.parse("text/plain"));

            ArrayList<DTO_gallery> items = new ArrayList<>();
            items = cafe_update_adapter.getItems();
            ArrayList<MultipartBody.Part> files = new ArrayList<>();

            // bitmap 을 File 로 바꿔서 저장
            if(items != null) {
                for (int i = 0; i < items.size(); i++){
                    File file = bitmapToFile(items.get(i).getBitmap(), "bitmaps" + i);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-date"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" + i, i + "", requestFile);
                    files.add(body);
                }
            }

//            // 매장 사진 (이미지 uri)
//            ArrayList<String> shop_image = cafe_update_adapter.getImages();
//            ArrayList<MultipartBody.Part> files = new ArrayList<>();
//            for(int i =0; i < shop_image.size(); i++){
//                // 절대주소를 파일 객체로 만듬
//                File file = new File(shop_image.get(i));
//                RequestBody RequestFile = RequestBody.create(file, MediaType.parse("multipart/form-date"));
//                MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" + i, i+"", RequestFile);
//                files.add(body);
//            }

            // 수정 업데이트 레트로핏 -> 이름 , 난이도, 제한시간 , 소개, 매장이름, 매장 번호
            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_shop> call = retrofitInterface.updateCafe(shop_name,shop_address,shop_address_more,shop_info,shop_open,shop_close,shop_holiday,shop_index,files);
            call.enqueue(new Callback<DTO_shop>() {
                @Override
                public void onResponse(Call<DTO_shop> call, Response<DTO_shop> response) {
                    if(response.body() != null && response.isSuccessful()){
                        Intent i = new Intent(getBaseContext(), SearchCafe_read.class);
                        i.putExtra("callback","cafe_update");
                        setResult(RESULT_OK,i);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<DTO_shop> call, Throwable t) {
                    Log.e("update_cafe, 226", "카페 업데이트 오류 : " + t);
                }
            });
        });

        /* =============================================== */
    }

    private void setAdapter() {
        // 키보드 설정
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // 이미지 리사이클러뷰 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        bind.cafeUpdateImageRv.setLayoutManager(linearLayoutManager);
        cafe_update_adapter = new GalleryWrite_adapter(getBaseContext(), bind.cafeUpdateImgNum);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(cafe_update_adapter));
        itemTouchHelper.attachToRecyclerView(bind.cafeUpdateImageRv);
        bind.cafeUpdateImageRv.setAdapter(cafe_update_adapter);
    }

    private void setData() {
        Intent i = getIntent();
        call = i.getStringExtra("call");
        cafe_index = i.getStringExtra("cafe_index");


        // 로딩바 세팅
        progressDialog = new ProgressDialog(SearchCafe_update.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        /* 2 초 초과시 작동 */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 2초가 지나면 다이얼로그 닫기
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);
            }
        });
        thread.start();

        /* 카페 고유값, 페이지, 사이즈 , 유저 고유값 */
        String user_id = PreferenceManager.getString(getBaseContext(), "userIndex");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_shop> call = retrofitInterface.getCafeRead(cafe_index, 1, 8, user_id);
        call.enqueue(new Callback<DTO_shop>() {
            @Override
            public void onResponse(Call<DTO_shop> call, Response<DTO_shop> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // 게시글 설정 , 카페 이름,
                    bind.cafeUpdateName.setText(response.body().getName());
                    bind.cafeUpdateOpenTime.setText(response.body().getOpen());
                    bind.cafeUpdateCloseTime.setText(response.body().getClose());
                    bind.cafeUpdateHoliday.setText(response.body().getHoliday());
                    bind.cafeUpdateAddress.setText(response.body().getAddress());
                    bind.cafeUpdateAddressMore.setText(response.body().getMore_address());
                    bind.cafeUpdateInfo.setText(response.body().getInfo());
                    bind.cafeUpdateInfoNumber.setText("" + response.body().getInfo().length());
                    ArrayList<DTO_image> images = response.body().getImages();
                    Log.e("이미지 확인", images.get(0).getImage_uri());
                    for(int i =0; i < images.size(); i++){
                        cafe_update_adapter.addItems(images.get(i).getImage_uri());
                    }
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DTO_shop> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 2222:

                    if (data == null) { // 이미지 선택을 안했을때
                        Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                    } else {// 이미지를 한개 이상 선택한 경우 -> 1개 or 2개 이상
                        if (data.getClipData() == null) { // 이미지를 1개 선택한 경우
                            String image_uri = getReadPathFromUri(data.getData());
                            images.add(image_uri);
                        } else { // 이미지를 여러장 선택한 경우 2장 이상
                            ClipData clipData = data.getClipData();

                            if (clipData.getItemCount() > 3) {// 3장 이상 선택시
                                Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < clipData.getItemCount(); i++) { // 가져온 사진 갯수 만큼 반복
                                    String image_uri = getReadPathFromUri(clipData.getItemAt(i).getUri());
                                    images.add(image_uri);
                                }
                            }
                        }

                    }
                    // 저장한 이미지 경로를 Adapter 에 추가해서 이제 선택이 가능하도록 변경
                    // 선택한 이미지와 기존의 이미지가 3개를 넘어가면 예외 처리
                    if (cafe_update_adapter.getItemCount() + images.size() < 4) {
                        for (int i = 0; i < images.size(); i++) {
                            cafe_update_adapter.setUri(images.get(i));
                            cafe_update_adapter.notifyItemInserted(cafe_update_adapter.getItemCount());
                            // 아이템 숫자 변경
                            bind.cafeUpdateImgNum.setText(cafe_update_adapter.getItemCount() + "/3");
                        }
                    }
                    break;

                case 3333:
                    if (resultCode == RESULT_OK) {
                        String address = data.getExtras().getString("address");
                        if (address != null) {
                            Log.e("주소 설정", "주소 설정값 : " + address);
                            bind.cafeUpdateAddress.setText(address);
                            bind.cafeUpdateAddressMore.requestFocus();
                        }
                    }

            }
        }
        ////
    }

    private String getReadPathFromUri(Uri uri) {
        String[] strArray = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), uri, strArray, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String image_uri = cursor.getString(columnIndex);
        cursor.close();
        return image_uri;
    }

    public File bitmapToFile (Bitmap getBitmap, String filename)
    {
        //create a file to write bitmap data
        File f = new File(getCacheDir(), filename);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

//Convert bitmap to byte array
        Bitmap bitmap = getBitmap;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }
    @Override
    /* 키보드 자동 내리기 세팅 */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            // 사각형을 만드는 클래스 (Rectangle) 직사각형
            Rect rect = new Rect();
            // focus 된 View 의 전체 면적을 가져옴
            focusView.getGlobalVisibleRect(rect);
            // 현재 이벤트가 일어난 x, y 좌표를 가져옴
            int x = (int) ev.getX(), y = (int) ev.getY();
            // 클릭이벤트가 focusView 범위 안에 일어났는지 확인
            if (!rect.contains(x, y)) {
                imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
