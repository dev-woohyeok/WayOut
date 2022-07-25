package com.example.wayout_ver_01.Activity.Chat;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_message;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.Channel;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Receive extends Thread implements Runnable {
    private BufferedReader br2 = null;
    private PrintWriter pw = null;
    private Socket sc = null;
    private String request = null;
    private String user_id;
    private Context context;
    private int temp = 0;
    private NotificationCompat.Builder builder = null;

    public Receive(Socket socket, Context context) {
        this.sc = socket;
        this.context = context;
        try {
            this.br2 = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            String NOTIFICATION_CHANNEL_ID = "test1";

            /* TODO : 노티 채널  */
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "channel_test1", NotificationManager.IMPORTANCE_HIGH);

                /*  채널 설정 */
                channel.setDescription("Channel description");
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.setVibrationPattern(new long[]{0, 1000});
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }


            while (true) {
                request = br2.readLine();
                DTO_message chat = makeDTO(request);
                String name = PreferenceManager.getString(context, "userId");
                String image = chat.getImage(); // 채팅이미지
                String message = chat.getMessage(); // 채팅 내용
                String title = chat.getRoom(); // 채팅방 이름 (고유값)
                String code = chat.getCode(); //  채팅방 코드 어떤상태인지
                String writer = chat.getName(); // 채팅 작성자 이름
                String date = chat.getDate(); // 채팅 작성일자
                String room_name = chat.getRoom_name(); // 채팅 방 이름
                String type = chat.getType(); // 메세지인지, 이미지인지, 출입 메세지인지 구분
                int noti_id = Integer.parseInt(title); // 숫자로 변환 한 채팅방 고유값

                Log.w("//===========//", "================================================");
                Log.i("", "\n" + "[ Receive_Thread,47 :: 서버에서 받은 msg : " + request + "]  ");
                Log.w("//===========//", "================================================");

                /* 어디를 보고있는지 */
                String actName = getNowUseActivity(context);
                Log.w("//===========//", "================================================");
                Log.i("", "\n" + "[ Receive_thread_현재 위치 가져오기 :: " + actName + "]");
                Log.w("//===========//", "================================================");

                /* 현재 위치 알아보기 */
                switch (actName) {
                    case "com.example.wayout_ver_01.Activity.Chat.ChatRoom":
                        Log.w("//===========//", "================================================");
                        Log.i("", "\n" + "[ Receive_Thread,76 메세지 전송 :: ChatRoom 일때 broad ]  ");
                        Log.w("//===========//", "================================================");
                        Intent intent2 = new Intent("msgReceive_room");
                        intent2.putExtra("msg", request);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
                        break;
                    default:
                        Log.w("//===========//", "================================================");
                        Log.i("", "\n" + "[ Receive_Thread,67 메세지 전송 :: HomeActivity 일때 broad ]  ");
                        Log.w("//===========//", "================================================");
                        Intent intent = new Intent("msgReceive_home");
                        intent.putExtra("msg", request);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                /* 알림 */
                /* 알람 전체 끄고 켜기 */
                if (PreferenceManager.getBoolean(context, "alarm")) {
                    Log.e("//===========//", "================================================");
                    Log.e("", "\n" + "[ Receive : 전체 알람 꺼짐 ]");
                    Log.e("//===========//", "================================================");
                    continue;
                }
                /* 일부 방만 끄고 켜기 */
                if (PreferenceManager.getBoolean(context, "alarm" + title)) {
                    Log.e("//===========//", "================================================");
                    Log.e("", "\n" + "[ Receive : " + title + " 방 알람 꺼짐 ]");
                    Log.e("//===========//", "================================================");
                    continue;
                }
                if (code.equals("in")) {
                    Log.e("//===========//", "================================================");
                    Log.e("", "\n" + "[ Receive : 방 in 알람 꺼짐 ]");
                    Log.e("//===========//", "================================================");
                    continue;
                }
                if (code.equals("out")) {
                    Log.e("//===========//", "================================================");
                    Log.e("", "\n" + "[ Receive : 방 out 알람 꺼짐 ]");
                    Log.e("//===========//", "================================================");
                    continue;
                }


                /* 채팅방에서 send, join, quit 알림 안받음 */
                if (actName.equals("com.example.wayout_ver_01.Activity.Chat.ChatRoom") && code.equals("send") && title.equals(PreferenceManager.getString(context, "nowRoom"))) {
                    continue;
                }
                if (code.equals("join")) {
                    if (code.equals("join") && name.equals(writer) && date.equals("invite")) {
                        message = "채팅방에 초대되셨습니다.";
                    } else {
                        continue;
                    }
                }
                if (code.equals("quit")) {
                    continue;
                }
                if (code.equals("delete")) {
                    message = "모임이 삭제되었습니다.";
                    if (name.equals(writer)) {
                        message = "모임을 삭제하셨습니다.";
                    }
                }
                if (type.equals("img")) {
                    message = "이미지가 전송되었습니다.";
                }
                if (code.equals("kick") && name.equals(writer)) {
                    message = "모임에서 강퇴되셨습니다.";
                }
                if (code.equals("kick") && !name.equals(writer)) {
                    continue;
                }

                /* 노티피케이션 기본 상단 알림 구현 */
                NotificationCompat.Builder builder = null;
                /* 안드로이드 오레오 이상부터는 Channel 을 꼭 생성해야댐 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

                    /* 알림 클릭시 동작 추가하기 */
                    Intent intent1 = new Intent(context, ChatRoom.class);
                    intent1.putExtra("room_id", title);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT );

                    Log.e("//===========//", "================================================");
                    Log.e("", "\n" + "[ Noti code : " + code + "  ]");
                    Log.e("", "\n" + "[ Noti message : " + message + "  ]");
                    Log.e("", "\n" + "[ Noti writer : " + writer + "  ]");
                    Log.e("", "\n" + "[ Noti title : " + room_name + "  ]");
                    Log.e("//===========//", "================================================");

                    /* builder 에 알림 내용과 아이콘을 설정 */
                    builder.setContentTitle(room_name) // 제목텍스트 *생략가능
                            .setContentText(message) // 본문 텍스트 *생략가능
                            .setSmallIcon(R.drawable.exit) // 알림시 보여지는 아이콘, 필수
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
//                            .setTimeoutAfter(5000) // 지정한 시간 이후 알림이 취소된다.
                            .setPriority(NotificationCompat.PRIORITY_HIGH) // 헤드업 알림을 위한 중요도 설정 High 이상으로 해야됨 7.1 이상버전에선 Max까지
                            .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE);

                    /* 기존에 방과 같은 방인지 다른 방인지지 */
                    if (noti_id == temp) {
                                            /*
                                            같은 방이면 그대로
                                            builder 의 build() 를 통해 Notification 객체를 생성하고,
                                               알림을 표시 하기 위한 NotificationManagerCompat.notify() 를
                                               호출하여 알림의 고유 ID와 함께 전달 */
                        Notification notification = builder.build();
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                        notificationManagerCompat.notify(noti_id, notification);
                        temp = noti_id;
                    } else {
                        notificationManager.cancel(temp);
                        temp = noti_id;
                                             /* builder 의 build() 를 통해 Notification 객체를 생성하고,
                                                알림을 표시 하기 위한 NotificationManagerCompat.notify() 를
                                                호출하여 알림의 고유 ID와 함께 전달 */
                        Notification notification = builder.build();
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                        notificationManagerCompat.notify(noti_id, notification);
                    }
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            Log.w("//===========//", "================================================");
            Log.i("", "\n" + "[ Receive_Thr :: Receive Thread 종료 ]");
            Log.w("//===========//", "================================================");
        }

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

    // 로그 찍기
    private static void consoleLog(String log) {
        System.out.println(log);
    }

    // TODO [현재 사용중인 최상위 액티비티 명 확인]
    public static String getNowUseActivity(Context mContext) {

        /**
         * 참고 : [특정 클래스에서 본인 클래스명 확인 방법]
         * getClass().getName()
         * */

        // [초기 리턴 결과 반환 변수 선언 실시]
        String returnActivityName = "";
        try {
            ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

            String className = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                className = String.valueOf(manager.getAppTasks().get(0).getTaskInfo().topActivity.getClassName());
            } else {
                List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
                ComponentName componentName = info.get(0).topActivity;
                className = componentName.getClassName();
            }


            // [리턴 반환 데이터 삽입 실시]
            returnActivityName = className;
        } catch (Exception e) {
            //e.printStackTrace();

            Log.e("//===========//", "================================================");
            Log.i("", "\n" + "[C_Util >> getNowUseActivity() :: 현재 사용 중인 최상위 액티비티 확인]");
            Log.i("", "\n" + "[catch [에러] :: " + String.valueOf(e.getMessage()) + "]");
            Log.e("//===========//", "================================================");
        }

        // [리턴 결과 반환 수행 실시]
        return returnActivityName;
    }

}

