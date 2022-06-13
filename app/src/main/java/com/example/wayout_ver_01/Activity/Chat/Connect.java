package com.example.wayout_ver_01.Activity.Chat;

import android.content.Context;
import android.util.Log;

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
import java.util.Timer;

public class Connect implements Runnable {
    private static Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private static final String HOST = "10.0.2.2"; // 내 로컬 주소를 가르킨다.
    private static final int PORT = 8888; // 해당 accept 할 port 번호
    private final Context context;
    private String user_id;

    public Connect(Socket socket, Context context) {
        Connect.socket = socket;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(HOST, PORT);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            Log.e("Connect_16 ", "소켓 연결 성공!");

            String msg = makeJson("connect","-1",user_id,"","","","");
            user_id = PreferenceManager.getString(context,"userId");
            Log.w("//===========//", "================================================");
            Log.i("", "\n" + "[user_id :: " + user_id + ", msg: "+ msg+" ] ,Service_chat run() ");
            Log.w("//===========//", "================================================");
            Log.w("//===========//", "================================================");
            Log.i("", "\n" + "[context :: " + context + "] ,Service_chat run()");
            Log.w("//===========//", "================================================");

            Receive r = new Receive(socket,context); // 메세지 받는 Thread
            r.start();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Connect_22 ", "소켓 연결 실패!");
            try {
                if (socket != null) {
                    socket.close();
                }
                if (br != null) {
                    br.close();
                }
                if (pw != null) {
                    pw.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        Log.w("//===========//", "================================================");
        Log.i("", "\n" + "[ Connect_Thread,47 :: 서버 연결 ]  ");
        Log.i("", "\n" + "[ Connect_Thread,47 :: Context (run) : "+context+" ]  ");
        Log.w("//===========//", "================================================");
    }
    public static Socket getSocket() {
        return socket;
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
