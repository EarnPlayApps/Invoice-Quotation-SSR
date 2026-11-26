package com.earnplayapps.ssr;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient.FileChooserParams;
import android.webkit.WebResourceRequest;
import android.webkit.JavascriptInterface;
import android.content.Context;

public class MainActivity extends Activity {
    private WebView web;
    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_REQUEST = 4102;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(0xFF101D32);
        getWindow().setNavigationBarColor(0xFF101D32);
        web = new WebView(this);
        web.setBackgroundColor(0xFFEEF1F5);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        web.addJavascriptInterface(new Object() {
            @JavascriptInterface public void printPage() {
                runOnUiThread(() -> {
                    PrintManager manager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
                    if (manager != null) {
                        String jobName = "SSR Document";
                        manager.print(jobName, web.createPrintDocumentAdapter(jobName),
                            new PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                            .setResolution(new android.print.PrintAttributes.Resolution("ssr", "SSR", 300, 300))
                            .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build());
                    }
                });
            }
        }, "SSR");
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("file".equals(uri.getScheme())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                catch (Exception ignored) { }
                return true;
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                Intent intent = params.createIntent();
                try { startActivityForResult(intent, FILE_REQUEST); }
                catch (Exception e) { fileCallback = null; callback.onReceiveValue(null); return false; }
                return true;
            }
        });
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST && fileCallback != null) {
            Uri[] results = (resultCode == RESULT_OK && data != null && data.getData() != null)
                ? new Uri[]{data.getData()} : null;
            fileCallback.onReceiveValue(results);
            fileCallback = null;
        }
    }

    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
