package com.sms.deliamao.stockmarketsearch;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wenjieli on 5/3/16.
 */
public class StockQuote {
    private String symbol;
    private String name;
    private String timestamp;
    private double price;
    private double change;
    private double changePercent;
    private double changePercentYTD;
    private double changeYTD;
    private double marketCap;
    private double volume;
    private double high;
    private double low;
    private double open;


    public StockQuote() {}
    public StockQuote(JSONObject json) {
        parseFromJSONObject(json);
    }

    public void parseFromJSONObject(JSONObject json) {
        try {
            symbol = json.getString("Symbol");
            name = json.getString("Name");
            price = json.getDouble("LastPrice");
            change = json.getDouble("Change");
            changePercent = json.getDouble("ChangePercent");
            changeYTD = json.getDouble("ChangeYTD");
            changePercentYTD = json.getDouble("ChangePercentYTD");

            marketCap = json.getDouble("MarketCap");
            timestamp = json.getString("Timestamp");
            volume = json.getDouble("Volume");
            high = json.getDouble("High");
            low = json.getDouble("Low");
            open = json.getDouble("Open");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static StockQuote fromJSONString(String jsonString) {
        StockQuote quote = new StockQuote();
        try {
            JSONObject json = new JSONObject(jsonString);
            quote.parseFromJSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return quote;
    }
    public String toJSONString() {
        JSONObject json = new JSONObject();
        try {
            json.accumulate("Symbol", symbol);
            json.accumulate("Name", name);
            json.accumulate("LastPrice", price);
            json.accumulate("Change", change);
            json.accumulate("ChangePercent", changePercent);
            json.accumulate("ChangeYTD", changeYTD);
            json.accumulate("ChangePercentYTD", changePercentYTD);
            json.accumulate("MarketCap", marketCap);
            json.accumulate("Timestamp", timestamp);
            json.accumulate("Volume", volume);
            json.accumulate("High", high);
            json.accumulate("Low", low);
            json.accumulate("Open", open);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public double getChangeYTD() {
        return changeYTD;
    }

    public double getVolume() {
        return volume;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getOpen() {
        return open;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getMarketCap() {
        return marketCap;
    }
    public double getChange() { return change;}
    public double getChangePercent() {
        return changePercent;
    }
    public double getChangePercentYTD() {
        return changePercentYTD;
    }

}
