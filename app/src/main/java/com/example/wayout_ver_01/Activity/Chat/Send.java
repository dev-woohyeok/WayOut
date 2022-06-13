package com.example.wayout_ver_01.Activity.Chat;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Send extends Thread {
    private Socket sc = null;
    private String message = null;
    private PrintWriter pw = null;

    public Send(Socket socket, String message) {
        try {
            this.sc = socket;
            this.pw = new PrintWriter(new OutputStreamWriter(sc.getOutputStream(), StandardCharsets.UTF_8));
            this.message = message;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        /* 받은 intent 데이터를 서버로 전송    */
        if (message != null) {
            pw.println(message);
            pw.flush();
            Log.w("//===========//", "================================================");
            Log.i("", "\n" + "[ sendThread :: message : + " + message + " ]");
            Log.w("//===========//", "================================================");
        }
    }
}
