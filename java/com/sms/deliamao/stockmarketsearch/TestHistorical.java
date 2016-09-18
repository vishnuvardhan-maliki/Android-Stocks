package com.sms.deliamao.stockmarketsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by deliamao on 5/3/16.
 */
public class TestHistorical extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_historical);

        WebView webViewer = (WebView) findViewById(R.id.webView);

        webViewer.loadUrl("fragment_historical.html");

    }
}
