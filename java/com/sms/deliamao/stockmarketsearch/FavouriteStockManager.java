package com.sms.deliamao.stockmarketsearch;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wenjieli on 5/2/16.
 */
public class FavouriteStockManager {
    public FavouriteStockManager(Context context) {
        mContext = context;
    }

    public static final String PREFS_NAME = "MyFavourites";
    public void addOrUpdateFavourite(StockQuote quote) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(quote.getSymbol(), quote.toJSONString());
        editor.commit();

    }
    public boolean isFavourite(String symbol) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        return settings.contains(symbol);
    }
    public void removeFavourite(String symbol) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(symbol);
        editor.commit();
    }

    public ArrayList<StockQuote> getAllFavourites() {
        ArrayList<StockQuote> allFavourites = new ArrayList<StockQuote>();
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        Map<String, ?> allEntries = settings.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String jsonString  = entry.getValue().toString();
            StockQuote quote = StockQuote.fromJSONString(jsonString);
            quote.setSymbol(entry.getKey());
            allFavourites.add(quote);
        }
        return allFavourites;
    }
    private Context mContext;
}
