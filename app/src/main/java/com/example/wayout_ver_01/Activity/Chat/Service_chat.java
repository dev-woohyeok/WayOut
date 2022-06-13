package com.example.wayout_ver_01.Activity.Chat;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_message;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;

public class Service_chat extends Service implements Runnable {

    private static Socket socket = null;
    private BufferedReader br2 = null;
    private PrintWriter pw2 = null;
    private String message;
    private static String user_id;
    private static final String HOST = "10.0.2.2"; // 내 로컬 주소를 가르킨다.
    private static final int PORT = 8888; // 해당 accept 할 port 번호
    private static Context context;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        user_id = PreferenceManager.getString(getApplicationContext(),"userId");
        context = getApplicationContext();
        Log.w("//===========//", "================================================");
        Log.i("", "\n" + "[user_id :: " + user_id + "] ,Service_chat onCreate ");
        Log.w("//===========//", "================================================");


        try {
            Runnable start = new Service_chat();
            Thread t = new Thread(start);
            t.start();

        } catch (Exception e) {
            Log.e("ServerChat,49", "서버 소켓 연결 실패");
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_STICKY; // 서비스가 종료되었을떄도 다시 자동으로 실행함;
        } else {
            return super.onStartCommand(intent, flags, startId);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) {
                socket.close();
            }
            if (br2 != null) {
                br2.close();
            }
            if (pw2 != null) {
                pw2.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(HOST, PORT);
            br2 = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            pw2 = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            String msg = makeJson("connect","",user_id,"","","","");
            pw2.println(msg);
            pw2.flush();
            Log.w("//===========//", "================================================");
            Log.i("", "\n" + "[PrintWriter :: " + pw2 + "] ,Service_chat run() ");
            Log.w("//===========//", "================================================");
            Log.w("//===========//", "================================================");
            Log.i("", "\n" + "[context :: " + context + "] ,Service_chat run()");
            Log.w("//===========//", "================================================");

            Receive r = new Receive(socket,context); // 메세지 받는 Thread
            r.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO [현재 사용중인 최상위 액티비티 명 확인]
    public static String getClassName(Context mContext) {
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
            Log.w("//===========//", "================================================");
            Log.i("", "\n" + "[C_Util >> getNowUseActivity() :: 현재 사용 중인 최상위 액티비티 확인]");
            Log.i("", "\n" + "[className :: " + String.valueOf(className) + "]");
            Log.w("//===========//", "================================================");

            // [리턴 반환 데이터 삽입 실시]
            returnActivityName = className;
        } catch (Exception e) {
            //e.printStackTrace();
            Log.i("---", "---");
            Log.e("//===========//", "================================================");
            Log.i("", "\n" + "[C_Util >> getNowUseActivity() :: 현재 사용 중인 최상위 액티비티 확인]");
            Log.i("", "\n" + "[catch [에러] :: " + String.valueOf(e.getMessage()) + "]");
            Log.e("//===========//", "================================================");
            Log.i("---", "---");
        }

        // [리턴 결과 반환 수행 실시]
        return returnActivityName;
    }

    public static Socket getSocket() {
        return socket;
    }

    private static String makeJson(String code, String room, String name, String message, String image, String date, String type) {
        /* 서버에 보낼 메세지 데이터 Json 생성 */
        Gson gson = new Gson();
        DTO_message chat = new DTO_message(code, room, name, message, image, date, type);
        String jsonStr = gson.toJson(chat);
        System.out.println("ChatRoom_makeJson // 변환된 데이터 " + jsonStr);
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


