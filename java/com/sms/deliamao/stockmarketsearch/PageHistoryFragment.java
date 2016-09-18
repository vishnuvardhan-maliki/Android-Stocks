package com.sms.deliamao.stockmarketsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by deliamao on 5/2/16.
 */
public class PageHistoryFragment extends Fragment {
    private String content;

    public static PageHistoryFragment newInstance(String S) {
        PageHistoryFragment fragment = new PageHistoryFragment();
        fragment.content = S;
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historical, container, false);
        WebView wv = (WebView) view.findViewById(R.id.webView);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.loadDataWithBaseURL("file:///android_asset/historical.html", content, "text/html", "UTF-8", null);
        return view;
    }
}

