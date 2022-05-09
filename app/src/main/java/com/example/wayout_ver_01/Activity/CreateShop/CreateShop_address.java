package com.example.wayout_ver_01.Activity.CreateShop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wayout_ver_01.databinding.ActivityCreateShopAddressBinding;

public class CreateShop_address extends AppCompatActivity {
    ActivityCreateShopAddressBinding binding;

    private WebView webView;

    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data){
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("address", data);
            intent.putExtras(extra);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateShopAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        webView = binding.CreateShopWeb;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        webView.loadUrl("http://3.39.57.242/daum.html");
    }
}