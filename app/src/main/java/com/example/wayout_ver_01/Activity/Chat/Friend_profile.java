package com.example.wayout_ver_01.Activity.Chat;

import static com.example.wayout_ver_01.Class.JsonMaker.DtoToJson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_follow;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_message;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_room;
import com.example.wayout_ver_01.Activity.Chat.Chat.MyFriend;
import com.example.wayout_ver_01.Activity.MyPage.MyLikeCafe;
import com.example.wayout_ver_01.Activity.MyPage.MyLikeTheme;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivityFriendProfileBinding;

import java.net.Socket;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Friend_profile extends AppCompatActivity {
    private ActivityFriendProfileBinding bind;
    private String user_id, user_image, user_index, follow_id;
    private int follower_num , follow_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityFriendProfileBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        /* 인텐트 가져오기 */
        Intent intent = getIntent();
        follow_id = intent.getStringExtra("user_id"); // 해당 프로필 ID
        user_image = intent.getStringExtra("user_image"); // 해당 프로필 이미지
        user_index = intent.getStringExtra("user_index"); // 해당 프로필 고유값
        user_id = PreferenceManager.getString(getBaseContext(),"userId"); // 현재 로그인한 아이디 주인


        /* 팔로우 버튼 */
        bind.friendProfileBtn.setOnClickListener(v -> {
            String str = bind.friendProfileBtn.getText().toString();
            switch (str) {
                case "팔로우":
                    bind.friendProfileBtn.setText("팔로잉");
                    bind.friendProfileBtn.setBackgroundResource(R.drawable.category_check_end);
                    writeFollow();
                    break;
                case "팔로잉":
                    bind.friendProfileBtn.setText("팔로우");
                    bind.friendProfileBtn.setBackgroundResource(R.drawable.category_check);
                    deleteFollow();
                    break;
                default:
            }
        });

        /* 1:1 채팅 */
        bind.friendProfileBtnMsg.setOnClickListener(v -> {
            newChat();
        });

        /* 관심 매장 */
        bind.friendProfileCafe.setOnClickListener(v -> {
            Intent intent1 = new Intent(getBaseContext(), MyLikeCafe.class);
            intent1.putExtra("user_index", user_index);
            startActivity(intent1);
        });

        /* 관심 테마 */
        bind.friendProfileTheme.setOnClickListener(v -> {
            Intent intent1 = new Intent(getBaseContext(), MyLikeTheme.class);
            intent1.putExtra("user_index", user_index);
            startActivity(intent1);
        });

        /* 팔로우 클릭 */
        bind.friendProfileFollowerNum.setOnClickListener(v -> {
            Intent intent1 = new Intent(getBaseContext(), MyFriend.class);
            intent1.putExtra("follow", true);
            intent1.putExtra("user_id", follow_id);
            intent1.putExtra("friend", true);
            startActivity(intent1);
        });

        /* 팔로잉 클릭 */
        bind.friendProfileFollowingNum.setOnClickListener(v -> {
            Intent intent1 = new Intent(getBaseContext(), MyFriend.class);
            intent1.putExtra("follow", false);
            intent1.putExtra("user_id", follow_id);
            intent1.putExtra("friend", true);
            startActivity(intent1);
        });
    }

    private void newChat() {
        /* 1:1 채팅방 구현 절차 */
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_room> call = retrofitInterface.writeSingleMsg(user_id, follow_id);
        call.enqueue(new Callback<DTO_room>() {
            @Override
            public void onResponse(Call<DTO_room> call, Response<DTO_room> response) {

                if(response.isSuccessful() && response.body() != null) {
                    Socket socket = Service_chat.getSocket();
                    String room_id = response.body().getRoom_id();
                    Log.e("test1", "" + response.body().isNew_msg());

                    if(response.body().isNew_msg()){
                        /* 채팅방 입장 메세지 */
                        DTO_message data = new DTO_message("join", room_id, user_id, user_id+"님이 입장하셨습니다.", "nope", follow_id, "io", 0, user_id);
                        String msg = DtoToJson(data);
                        System.out.println("ChatWrite_1 // 보내는 메시지 : " + msg);
                        /* Send Thread 로 서버로 보내기 */
                        Send send = new Send(socket, msg);
                        send.start();

                        /* 처리시간 확보 */
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(response.body().isOld_msg()){
                        /* 채팅방 입장 메세지 */
                        DTO_message data = new DTO_message("join", room_id, user_id, user_id+"님이 입장하셨습니다.", "nope", follow_id, "io", 0, user_id);
                        String msg = DtoToJson(data);
                        System.out.println("ChatWrite_1 // 보내는 메시지 : " + msg);
                        /* Send Thread 로 서버로 보내기 */
                        Send send = new Send(socket, msg);
                        send.start();

                        /* 처리시간 확보 */
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent intent = new Intent(Friend_profile.this,ChatRoom.class);
                    intent.putExtra("room_id", room_id);
                    intent.putExtra("room_join", true);
                    intent.putExtra("friend", true);
                    intent.putExtra("follow_id", follow_id);
                    Log.e("test5",room_id);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<DTO_room> call, Throwable t) {

            }
        });
    }

    private void writeFollow() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_message> call = retrofitInterface.writeFollow(user_id,follow_id);
        call.enqueue(new Callback<DTO_message>() {
            @Override
            public void onResponse(Call<DTO_message> call, Response<DTO_message> response) {
                if (response.isSuccessful() && response.body() != null) {
                }
            }

            @Override
            public void onFailure(Call<DTO_message> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ friend_profile, writeFollow 에러 : " + t + "  ]");
                Log.e("//===========//", "================================================");
            }
        });
    }

    private void deleteFollow() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_message> call = retrofitInterface.deleteFollow(user_id,follow_id);
        call.enqueue(new Callback<DTO_message>() {
            @Override
            public void onResponse(Call<DTO_message> call, Response<DTO_message> response) {
                if (response.isSuccessful() && response.body() != null) {
                }
            }
            @Override
            public void onFailure(Call<DTO_message> call, Throwable t) {
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ Friend_Profile, deleteFollow 에러 : " + t + "  ]");
                Log.e("//===========//", "================================================");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_follow> call = retrofitInterface.getFriendProfile(user_index,follow_id,user_id);
        call.enqueue(new Callback<DTO_follow>() {
            @Override
            public void onResponse(Call<DTO_follow> call, Response<DTO_follow> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bind.friendProfileName.setText(follow_id);
                    Glide.with(getBaseContext()).load(user_image).into(bind.friendProfileImage);
                    bind.friendProfileFollowerNum.setText(response.body().getFollow_num());
                    bind.friendProfileFollowingNum.setText(response.body().getFollowing_num());

                    int follow_state = response.body().getFollow_state();
                    if(follow_state == 0){
                        bind.friendProfileBtn.setText("팔로잉");
                        bind.friendProfileBtn.setBackgroundResource(R.drawable.category_check_end);
                    }else{
                        bind.friendProfileBtn.setText("팔로우");
                        bind.friendProfileBtn.setBackgroundResource(R.drawable.category_check);
                    }
                }
            }

            @Override
            public void onFailure(Call<DTO_follow> call, Throwable t) {
                Log.e("//===========//","================================================");
                Log.e("","\n"+"[ Friend_Profile, getDate : 실패로그 "+t+"  ]");
                Log.e("//===========//","================================================");
            }
        });
    }
}