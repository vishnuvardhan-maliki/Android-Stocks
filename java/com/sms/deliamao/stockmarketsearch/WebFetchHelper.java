package com.sms.deliamao.stockmarketsearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wenjieli on 5/3/16.
 */
public class WebFetchHelper {
    private static final String TAG = "WebFetchHelper";
    public static final String STOCK_SYMBOL_URL = "http://deliancapp-env.us-west-1.elasticbeanstalk.com/index.php/index.php?input=";
    public static final String STOCK_QUOTE_URL = "http://deliancapp-env.us-west-1.elasticbeanstalk.com/index.php/index.php?symbolVal=";
    public static final String STOCK_NEWS_URL = "http://deliancapp-env.us-west-1.elasticbeanstalk.com/index.php/index.php?bingVal=";
    public static final String STOCK_DETAIL_IMG_URL = "http://chart.finance.yahoo.com/t?&lang=en-US&width=550&height=400&s=";
    public static String fetchUrl(String url) {
        String response = "";
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("content-length", "0");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);
            conn.connect();
            int status = conn.getResponseCode();
            if (status == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                response = sb.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch url: " + url + ". Message: " + e.toString());
        }
        return response;
    }
}
