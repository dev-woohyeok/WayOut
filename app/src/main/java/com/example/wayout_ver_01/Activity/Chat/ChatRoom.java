package com.example.wayout_ver_01.Activity.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.wayout_ver_01.Activity.Chat.Chat.ChatDrawer_adapter;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_chat;
import com.example.wayout_ver_01.Activity.FreeBoard.FreeBoard_write;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.JsonMaker;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_message;
import com.example.wayout_ver_01.Activity.Chat.Chat.Room_adapter;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_room;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivityChatRoomBinding;
import com.google.gson.Gson;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoom extends AppCompatActivity {
    private Socket socket;
    private ActivityChatRoomBinding bind;
    private String room_id = null, user_id = null, image = null, room_quit = null, receive, room_title, writer, last;
    private boolean room_join = false, scroll = false, delete = false;
    private InputMethodManager imm;
    private int page = 1, size = 10, member, room_max, IO, join_number, last_msg;
    public static Room_adapter room_adapter;
    private List<Uri> Selected;
    public static ChatDrawer_adapter drawer_adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* 채팅방 안보는 상황 */
        if (!delete) {
            String msg = JsonMaker.makeCode("out", user_id, room_id);
            Send send = new Send(socket, msg);
            send.start();
        }

        /* 등록 해제 잊지말고 하기 안하면 중복 에러 발생 */
        socket = null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
    }

    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receive = intent.getStringExtra("msg");
            DTO_message message = makeDTO(receive);
            String name = message.getName();
            String room = message.getRoom();
            String code = message.getCode();
            member = message.getMember();

            if (code.equals("delete")) {
                delete = true;
                finish();
            }

            Log.e("//===========//", "================================================");
            Log.e("", "\n" + "[ ChatRoom,60 :: 받은 메세지 : " + receive + " ]  ");
            Log.e("", "\n" + "[ ChatRoom,60 :: 현재 읽은 사람 : " + member + " 명 ]  ");
            Log.e("", "\n" + "[ ChatRoom,60 :: 방 총 인원 : " + room_max + " 명 ]  ");
            Log.e("", "\n" + "[ ChatRoom,60 :: 메세지 메소드 : " + code + " ]  ");
            Log.e("", "\n" + "[ ChatRoom,60 :: 받은 name, room : " + name + " ," + room + ",user_id :" + user_id + " ]  ");
            Log.e("//===========//", "================================================");
            /* 맴버수가 변경이 되면 */
            if (code.equals("in")) {
                setRefresh();
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ ChatRoom,60 :: 받은 코드: " + code + " ]  ");
                Log.e("", "\n" + "[ ChatRoom,60 :: 맴버 초기화 ]  ");
                Log.e("//===========//", "================================================");
            }
            if (code.equals("join")) {
                refreshJoin();
            }

            /* 조인인원수 변경시 새로고침 */
            if (code.equals("kick") || code.equals("quit")) {
                refreshJoin();
                if (name.equals(user_id)) {
                    finish();
                    return;
                }
            }

            /* 내가 보낸 메시지 제외, 방번호다른거 제외 */
            if (!name.equals(user_id) && room.equals(room_id)) {
                /* 방 접속여부 확인 메세지 등록안함 */
                if (!(code.equals("in") || code.equals("out"))) {
                    room_adapter.addItem(message);
                    bind.ChatRoomRv.scrollToPosition(room_adapter.getItemCount() - 1);
                }
            }
        }
    };

    private void refreshJoin() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_message>> call = retrofitInterface.getRefreshJoin(room_id);
        call.enqueue(new Callback<ArrayList<DTO_message>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_message>> call, Response<ArrayList<DTO_message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    drawer_adapter.clearItem();
                    for (DTO_message item : response.body()) {
                        drawer_adapter.addItem(item);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_message>> call, Throwable t) {

            }
        });

    }

    private void setRefresh() {
        int list = room_adapter.getItemCount();
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_message>> call = retrofitInterface.getMember(room_id, list);
        call.enqueue(new Callback<ArrayList<DTO_message>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_message>> call, Response<ArrayList<DTO_message>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    room_adapter.setMember(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_message>> call, Throwable t) {

            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        /* 노티 있으면 종료 시키기 */
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.cancel(1);
        notificationManager.cancelAll();

        /* Service 에서 연결된 소켓 생성하기 */
        socket = Service_chat.getSocket();
        System.out.println("ChatRoom_48 // room_id : " + room_id);

        /* 채팅방 Intent 정보 세팅하기 */
        Intent intent = getIntent();
        user_id = PreferenceManager.getString(ChatRoom.this, "userId");
        room_id = intent.getStringExtra("room_id");
        room_join = intent.getBooleanExtra("room_join", false);
        Log.e("//===========//", "================================================");
        Log.e("", "\n" + "[ ChatRoom, intent 데이터 user_id : " + user_id + "  ]");
        Log.e("", "\n" + "[ ChatRoom, intent 데이터 room_id : " + room_id + "  ]");
        Log.e("", "\n" + "[ ChatRoom, intent 데이터 room_join : " + room_join + "  ]");
        Log.e("//===========//", "================================================");

        if (room_join) {
            /* 처음 입장시 입장 메세지 보내기 */
            Socket socket = Service_chat.getSocket();
            String msg = JsonMaker.makeJson("join", room_id, user_id, user_id + " 님이 입장하셨습니다.", "", "", "io",room_title);
            Send send = new Send(socket, msg);
            send.start();
        }

        /* 필요한 정보 세팅 및 채팅내용 불러오기 */
        getChatData();

        /* 동적 broadCastReceive 등록 */
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, new IntentFilter("msgReceive_room"));

        /* 어뎁터 세팅 */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatRoom.this, LinearLayoutManager.VERTICAL, false);
        bind.ChatRoomRv.setLayoutManager(linearLayoutManager);
        room_adapter = new Room_adapter(ChatRoom.this);
        bind.ChatRoomRv.setAdapter(room_adapter);
        bind.ChatRoomRv.setItemAnimator(null);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(ChatRoom.this, LinearLayoutManager.VERTICAL, false);
        bind.chatRoomDrawerRv.setLayoutManager(linearLayoutManager1);
        drawer_adapter = new ChatDrawer_adapter(ChatRoom.this);
        bind.chatRoomDrawerRv.setAdapter(drawer_adapter);

        /* 메세지 전송시 */
        bind.ChatRoomSend.setOnClickListener(v -> {
            /* 서버에 보낼 카톡 내용 */
            String message = bind.ChatRoomMessageEt.getText().toString();
            bind.ChatRoomMessageEt.setText("");
            if (!message.isEmpty()) {
                try {
                    String now = DateConverter.setDate();
                    /* 서버에 보낼 Json 만들기 */

                    DTO_message data = new DTO_message("send", room_id, user_id, message, image, now, "msg", member,room_title);
                    String msg = DtoToJson(data);
                    System.out.println("ChatRoom_67 // 보내는 메시지 : " + msg);
                    room_adapter.addItem(data);

                    /* Send Thread 로 서버로 보내기 */
                    Send send = new Send(socket, msg);
                    send.start();
                    /* 리사이클러뷰 맨 아래로 이동 */
                    bind.ChatRoomRv.scrollToPosition(room_adapter.getItemCount() - 1);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        /* 리사이클러뷰 스크롤시 */
        bind.ChatRoomRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /* 최상단 스크롤시 작동 */
                if (!bind.ChatRoomRv.canScrollVertically(-1)) {
                    if (!scroll) {
                        scroll = true;
                        getSroll();
                    }
                }
            }
        });

        bind.ChatRoomSendImg.setOnClickListener(v -> {
            Matisse.from(ChatRoom.this)
                    .choose(MimeType.ofAll())
                    .countable(true)
                    .maxSelectable(3)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(1f)
                    .imageEngine(new GlideEngine())
                    .showPreview(false)
                    .forResult(1111);
        });

        /* 메뉴 클릭시 방장은 초대, 내보내기, 방설정  일반유저는 나가기 */
        bind.ChatRoomMenu.setOnClickListener(v -> {
            if (bind.chatRoomDrawer.isDrawerOpen(Gravity.RIGHT)) {
                bind.chatRoomDrawer.closeDrawer(Gravity.RIGHT);
            } else {
                bind.chatRoomDrawer.openDrawer(Gravity.RIGHT);
            }
        });

        /* 나가기 버튼 */
        bind.chatRoomDrawerOut.setOnClickListener(v -> {
            // 권한 가져오기
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
            // 제목
            builder.setTitle("채팅방 나가기");
            // 알림창 내용 부분
            builder.setMessage("\n나가기를 하면 대화내용이 모두 삭제되고\n채팅목록에서도 삭제됩니다.");
            // 예 버튼
            builder.setPositiveButton("나가기",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete_reply();
                        }
                    });
            // 아니오 버튼
            builder.setNegativeButton("아니요",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            // 보이기
            builder.show();
        });

        /* 방장 삭제하고 나가기 버튼 */
        bind.chatRoomDrawerDelete.setOnClickListener(v -> {
            // 권한 가져오기
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
            // 제목
            builder.setTitle("채팅방 삭제하고 나가기");
            // 알림창 내용 부분
            builder.setMessage("\n나가기를 하면 대화내용이 모두 삭제되고\n채팅방이 삭제됩니다.");
            // 예 버튼
            builder.setPositiveButton("나가기",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAllUser();
                            delete = true;
                        }
                    });
            // 아니오 버튼
            builder.setNegativeButton("아니요",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            // 보이기
            builder.show();
        });

        /* 방 지금 쳐다보는 중 */
        if (!room_join) {
            String msg = JsonMaker.makeCode("in", user_id, room_id);
            Send send = new Send(socket, msg);
            send.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1111 && resultCode == RESULT_OK) {
            Selected = Matisse.obtainResult(data);
            ArrayList<String> images = new ArrayList<>();
            ArrayList<MultipartBody.Part> files = new ArrayList<>();

            for (Uri uri : Selected) {
                String str = getRealPathFromUri(uri);
                images.add(str);
            }

            /* 이미지 */
            for (int i = 0; i < images.size(); i++) {
                File file = new File(images.get(i));
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-date"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" + i, i + "", requestFile);
                files.add(body);
            }

            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<ArrayList<DTO_message>> call = retrofitInterface.writerImageMessage(files);
            call.enqueue(new Callback<ArrayList<DTO_message>>() {
                @Override
                public void onResponse(Call<ArrayList<DTO_message>> call, Response<ArrayList<DTO_message>> response) {
                    if(response.body() != null && response.isSuccessful()){
                        for(DTO_message item : response.body()){
                            String now = null;
                            try {
                                now = DateConverter.setDate();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            /* 서버에 보낼 Json 만들기 */
                            DTO_message dto = new DTO_message("send", room_id, user_id, item.getMessage(), image, now, "img", member,room_title);
                            String msg = DtoToJson(dto);
                            System.out.println("ChatRoom_67 // 보내는 메시지 : " + msg);
                            room_adapter.addItem(dto);

                            /* Send Thread 로 서버로 보내기 */
                            Send send = new Send(socket, msg);
                            send.start();
                            /* 리사이클러뷰 맨 아래로 이동 */
                            bind.ChatRoomRv.scrollToPosition(room_adapter.getItemCount() - 1);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<DTO_message>> call, Throwable t) {

                }
            });

        } ;
    }

    // 절대 경로 가져오기 !!!!!
    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(ChatRoom.this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String url = cursor.getString(columnIndex);
        cursor.close();
        return url;
    }


    private void deleteAllUser() {

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_room> call = retrofitInterface.deleteAll(room_id);
        call.enqueue(new Callback<DTO_room>() {
            @Override
            public void onResponse(Call<DTO_room> call, Response<DTO_room> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String msg = JsonMaker.makeJson("delete", room_id, user_id, "", "", "", "io",room_title);
                    Send send = new Send(socket, msg);
                    send.start();
                }
                finish();
            }

            @Override
            public void onFailure(Call<DTO_room> call, Throwable t) {

            }
        });
    }

    private void delete_reply() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_room> call = retrofitInterface.deleteRoomOut(room_id, user_id);
        call.enqueue(new Callback<DTO_room>() {
            @Override
            public void onResponse(Call<DTO_room> call, Response<DTO_room> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String msg = JsonMaker.makeJson("quit", room_id, user_id, user_id + " 님이 나가셨습니다.", "", "", "io",room_title);
                    Send send = new Send(socket, msg);
                    send.start();
                }
                finish();
            }

            @Override
            public void onFailure(Call<DTO_room> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ chatRoom_나가기 : 에러 : " + t + " ]");
                Log.e("//===========//", "================================================");
            }
        });
    }


    private void getSroll() {
        if (page == 1) {
            page = 2;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_message>> call = retrofitInterface.getRoomScroll(room_id, page, size, last, last_msg);
        call.enqueue(new Callback<ArrayList<DTO_message>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_message>> call, Response<ArrayList<DTO_message>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        page++;
                    }
                    for (DTO_message item : response.body()) {
                        room_adapter.sendItem(item);
                    }
                    scroll = false;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_message>> call, Throwable t) {

            }
        });


    }

    private void getChatData() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_chat> call = retrofitInterface.getRoomData(room_id, page, size, user_id);
        call.enqueue(new Callback<DTO_chat>() {
            @Override
            public void onResponse(Call<DTO_chat> call, Response<DTO_chat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    room_title = response.body().getRoom_name();
                    writer = response.body().getRoom_writer();
                    room_max = response.body().getRoom_max();
                    image = response.body().getUser_image();
                    join_number = response.body().getJoin_number();
                    last = response.body().getLast();
                    last_msg = response.body().getLast_msg();
                    bind.ChatRoomTitle.setText(room_title);
                    room_adapter.setJoin_number(join_number);
                    drawer_adapter.setRoom_id(room_id);
                    drawer_adapter.setRoom_title(room_title);

                    if (user_id.equals(writer)) {
                        bind.chatRoomDrawerOut.setVisibility(View.GONE);
                        bind.chatRoomDrawerDelete.setVisibility(View.VISIBLE);
                    } else {
                        bind.chatRoomDrawerOut.setVisibility(View.VISIBLE);
                        bind.chatRoomDrawerDelete.setVisibility(View.GONE);
                    }

                    /* 메세지 세팅 */
                    for (DTO_message item : response.body().getMessages()) {
                        room_adapter.sendItem(item);
                    }

                    /* 유저 정보 세팅 */
                    for (DTO_message item : response.body().getMembers()) {
                        drawer_adapter.addItem(item);
                    }

                    /* 리사이클러뷰 맨 아래로 이동 */
                    bind.ChatRoomRv.scrollToPosition(room_adapter.getItemCount() - 1);


                    Log.e("//===========//", "================================================");
                    Log.e("", "\n" + "[ ChatRoom : getChatData title : " + room_title + " ]");
                    Log.e("", "\n" + "[ ChatRoom : getChatData writer : " + writer + " ]");
                    Log.e("", "\n" + "[ ChatRoom : getChatData room_max : " + room_max + " ]");
                    Log.e("", "\n" + "[ ChatRoom : getChatData image : " + image + " ]");
                    Log.e("", "\n" + "[ ChatRoom : getChatData join_num : " + join_number + " ]");
                    Log.e("", "\n" + "[ ChatRoom : getChatData last : " + last + " ]");
                    Log.e("//===========//", "================================================");
                }
            }

            @Override
            public void onFailure(Call<DTO_chat> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ ChatRoom : getChatData error" + t + " ]");
                Log.e("//===========//", "================================================");
            }
        });
    }


    private void doDialog(Dialog dialog) {
        dialog.show();
        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */
        // * 주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
        TextView menu_add_theme, menu_update, menu_delete;
        menu_add_theme = dialog.findViewById(R.id.item_dialog_add_theme);
        menu_update = dialog.findViewById(R.id.item_dialog_update_cafe);
        menu_delete = dialog.findViewById(R.id.item_dialog_delete_cafe);
        String user = PreferenceManager.getString(ChatRoom.this, "userId");

        if (writer.equals(user)) {
            menu_add_theme.setText("모임 수정");
            menu_delete.setText("삭제하고 나가기");
            menu_update.setVisibility(View.GONE);
        } else {
            menu_add_theme.setText("방 나가기");
            menu_delete.setVisibility(View.GONE);
            menu_update.setVisibility(View.GONE);
        }
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

    private static String makeJson(String code, String room, String name, String message, String image, String date, String type) {
        /* 서버에 보낼 메세지 데이터 Json 생성 */
        Gson gson = new Gson();
        DTO_message chat = new DTO_message(code, room, name, message, image, date, type);
        String jsonStr = gson.toJson(chat);
        System.out.println("ChatRoom_120 // 변환된 데이터 " + jsonStr);
        return jsonStr;
    }

    /* DTO => JsonString 으로 어디든 보내기 쉽게 */
    private static String DtoToJson(DTO_message item) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(item);
        System.out.println("ChatRoom_ // 변환된 데이터 : " + jsonStr);
        return jsonStr;
    }

    // json String => DTO 로 변경해줌
    private static DTO_message makeDTO(String json) {
        Gson gson = new Gson();
        DTO_message message = gson.fromJson(json, DTO_message.class);
        return message;
    }
}