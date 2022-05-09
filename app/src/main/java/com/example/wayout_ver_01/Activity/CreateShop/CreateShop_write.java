package com.example.wayout_ver_01.Activity.CreateShop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wayout_ver_01.Class.NetworkState;
import com.example.wayout_ver_01.Class.OnSingleClickListener;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.ItemTouchHelperCallback;
import com.example.wayout_ver_01.RecyclerView.Gallery.GalleryWrite_adapter;
import com.example.wayout_ver_01.databinding.ActivityCreateShopWriteBinding;

import java.util.ArrayList;

import okhttp3.MediaType;

public class CreateShop_write extends AppCompatActivity {
    private ActivityCreateShopWriteBinding binding;
    private ProgressDialog progressDialog;
    private ArrayList<String> images = new ArrayList<>();
    private GalleryWrite_adapter createShop_img_adapter;
    private ItemTouchHelper itemTouchHelper;
    private String open_hour, close_hour;
    private InputMethodManager imm;
    public static Activity my;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateShopWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setView();

        // 이미지 갤러리에서 3장까지 가져오기
        binding.createShopImg.setOnClickListener(v -> {
            showProgress();

            // 절대 경로로 담아 놓은 임시 리스트를 초기화
            // 재선택시 기존의 이미지와 중복되지 않기 위해서
            images.clear();


            // 선택한 이미지는 3개 까지 가능하다
            if (createShop_img_adapter.getItemCount() < 3) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 2222);
            } else {
                Toast.makeText(getApplicationContext(), "사진은 3장까지 선택이 가능합니다.", Toast.LENGTH_SHORT).show();
            }

            hideProgress();
        });

        // 키보드 내리기 처리
        binding.CreateShopKeyboard.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        // 주소 KaKao API  -> 도로명주소 가져오기
        // 터치 안되게 막기
        binding.createShopAddress.setFocusable(false);
        binding.createShopAddress.setOnClickListener(v-> {
           {
                Log.e("주소 페이지 설정", "주소창 입력하러 이동 ");
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
            }
        });

        // 오픈 시간 설정하기
        binding.createShopOpen.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateShop_write.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
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
                        open_hour = am + hour + min;
                        binding.createShopOpenTime.setText(open_hour);
                    }
                    , 9, 0, false);
            timePickerDialog.setTitle("오픈 시간 설정");
            timePickerDialog.show();
        });

        // 마감 시간 설정하기
        binding.createShopClose.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateShop_write.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
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
                        close_hour = am + hour + min;
                        binding.createShopCloseTime.setText(close_hour);
                    }
                    , 22, 0, false);
            timePickerDialog.setTitle("마감 시간 설정");
            timePickerDialog.show();

        });

        // Info 텍스트 숫자
        binding.createShopInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 변경되기 전의 문자열을 담음
            }

            // 문자가 변경될때마다 숫자 체크
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.createShopInfo.isFocusable() && !s.toString().isEmpty()) {
                    int number = binding.createShopInfo.getText().toString().length();
                    binding.createShopInfoNumber.setText(number + "");
                    if (number >= 200) {
                        number = 200;
                        Toast.makeText(getApplicationContext(), "200자까지 입력이 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 텍스트가 변경된 이후에 호출
            }
        });

        // 매장 정보 입력후 테마 등록으로 이동
        binding.createShopSubmit.setOnClickListener(v -> {
            if(getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            // 유효성 검사
            checkData();
            // 우효성 체크
            // 매장 이름
            if (binding.createShopName.getText().toString().trim().isEmpty()) {
                binding.createShopName.setError("매장 이름을 입력해주세요");
                binding.createShopName.requestFocus();
                return;
            }
            // 사진
            if (createShop_img_adapter.getItemCount() == 0) {
                binding.createShopImg.requestFocus();
                Toast.makeText(getApplicationContext(), "매장 사진을 추가해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            // 매장 주소
            if (binding.createShopAddress.length() < 6) {
                binding.createShopAddress.requestFocus();
                Toast.makeText(getApplicationContext(),"매장 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 매장 추가 주소
            if (binding.createShopAddressMore.getText().toString().trim().isEmpty()) {
                binding.createShopAddressMore.setError("상세 정보를 입력해주세요");
                binding.createShopAddressMore.requestFocus();
                return;
            }

            // 영업시간
            if (binding.createShopOpenTime.length() < 6 || binding.createShopCloseTime.length() < 6) {
                Toast.makeText(getApplicationContext(), "영업 시간을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 휴뮤일
            if (binding.createShopHoliday.getText().toString().trim().isEmpty()) {
                binding.createShopHoliday.requestFocus();
                Toast.makeText(getApplicationContext(), "휴무일을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 데이터 세팅
            // 매장 이름
            String shop_name = binding.createShopName.getText().toString();
            // 매장 사진 (이미지 uri)
            ArrayList<String> shop_image = createShop_img_adapter.getImages();
            // 매장 주소 (주소, 상세 정보)
            String shop_address = binding.createShopAddress.getText().toString();
            String shop_address_more = binding.createShopAddressMore.getText().toString();
            // 매장 소개
            String shop_info = binding.createShopInfo.getText().toString();
            // 영업 시간 (open, close)
            String shop_open = binding.createShopOpenTime.getText().toString();
            String shop_close = binding.createShopCloseTime.getText().toString();
            // 휴무일
            String shop_holiday = binding.createShopHoliday.getText().toString();

            Intent intent = new Intent(CreateShop_write.this, CreateTheme_write.class);
            intent.putExtra("shop_name", shop_name);
            intent.putExtra("shop_image", shop_image);
            intent.putExtra("shop_address", shop_address);
            intent.putExtra("shop_address_more", shop_address_more);
            intent.putExtra("shop_info", shop_info);
            intent.putExtra("shop_open", shop_open);
            intent.putExtra("shop_close", shop_close);
            intent.putExtra("shop_holiday", shop_holiday);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            Log.e("매장 정보 전송", "shop_name : " + shop_name);
            Log.e("매장 정보 전송", "shop_image : " + shop_image);
            Log.e("매장 정보 전송", "shop_address : " + shop_address);
            Log.e("매장 정보 전송", "shop_address_more : " + shop_address_more);
            Log.e("매장 정보 전송", "info : " + shop_info);
            Log.e("매장 정보 전송", "open : " + shop_open);
            Log.e("매장 정보 전송", "close : " + shop_close);
            Log.e("매장 정보 전송", "holiday : " + shop_holiday);
        });
        /////
    }

    private void checkData() {
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
                    if (createShop_img_adapter.getItemCount() + images.size() < 4) {
                        for (int i = 0; i < images.size(); i++) {
                            createShop_img_adapter.setUri(images.get(i));
                            createShop_img_adapter.notifyItemInserted(createShop_img_adapter.getItemCount());
                            // 아이템 숫자 변경
                            binding.createShopImgNum.setText(createShop_img_adapter.getItemCount() + "/3");
                        }
                    }
                    break;

                case 3333:
                    if (resultCode == RESULT_OK) {
                        String address = data.getExtras().getString("address");
                        if (address != null) {
                            Log.e("주소 설정", "주소 설정값 : " + address);
                            binding.createShopAddress.setText(address);
                            binding.createShopAddressMore.requestFocus();
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

    private void showProgress() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private void hideProgress() {
        progressDialog.dismiss();
    }

    private void setView() {

        my = CreateShop_write.this;

        progressDialog = new ProgressDialog(CreateShop_write.this);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // 이미지 선택 리사이클러뷰 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CreateShop_write.this, LinearLayoutManager.HORIZONTAL, false);
        binding.createShopImageRv.setLayoutManager(linearLayoutManager);
        createShop_img_adapter = new GalleryWrite_adapter(CreateShop_write.this, binding.createShopImgNum);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(createShop_img_adapter));
        itemTouchHelper.attachToRecyclerView(binding.createShopImageRv);
        binding.createShopImageRv.setAdapter(createShop_img_adapter);
    }
}