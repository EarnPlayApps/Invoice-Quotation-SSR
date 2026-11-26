package com.earnplayapps.ssr;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.view.View;

public class MainActivity extends Activity {
 @Override public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); getWindow().setStatusBarColor(0xFF101D32); getWindow().setNavigationBarColor(0xFF101D32); WebView web = new WebView(this); web.setBackgroundColor(0xFFEEF1F5); WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setAllowFileAccess(true); web.setWebChromeClient(new WebChromeClient()); setContentView(web); web.loadUrl("file:///android_asset/index.html"); }
 @Override public void onBackPressed() { super.onBackPressed(); }
}
