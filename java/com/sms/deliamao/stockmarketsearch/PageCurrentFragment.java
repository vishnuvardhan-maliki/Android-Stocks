package com.sms.deliamao.stockmarketsearch;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by deliamao on 5/2/16.
 */
public class PageCurrentFragment extends Fragment {
    private static final String TAG = "PageCurrentFragment";
    private StockQuote mQuote;

    public static PageCurrentFragment newInstance(StockQuote quote) {
        PageCurrentFragment fragment = new PageCurrentFragment();
        fragment.mQuote = quote;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current, container, false);
        ListView stockDetailView = (ListView) view.findViewById(R.id.stock_detail_list_view);
        stockDetailView.setAdapter(new StockDetailAdapter(getContext()));
        setListViewSize(stockDetailView);

        final ImageView stockImgView = (ImageView) view.findViewById(R.id.stock_detail_image);

        AsyncTask<String, Void, Bitmap> loadStocImgTask = new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    URL urlConnection = new URL(WebFetchHelper.STOCK_DETAIL_IMG_URL + params[0]);
                    HttpURLConnection connection = (HttpURLConnection) urlConnection
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                stockImgView.setImageBitmap(result);
            }
        };
        loadStocImgTask.execute(mQuote.getSymbol());
        return view;
    }
    public static void setListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }

    private class StockDetailAdapter extends ArrayAdapter<String> {
        private Context context;
        class StockDetailItem {
            public String name;
            public String value;
            public boolean withIcon;
            public int iconId;
            public StockDetailItem() {
                name = "";
                value = "";
                withIcon = false;
                iconId = 0;
            }
            public String toString() {
                return name + "," + value + "," + withIcon + "," + iconId;
            }
        }
        private List<StockDetailItem> stockDetails;

        public StockDetailAdapter(Context ctx) {
            super(ctx, R.layout.item_favourite_stock);
            this.context = ctx;
            this.stockDetails = new ArrayList<StockDetailItem>();
            // Name
            StockDetailItem item = new StockDetailItem();
            item.name = "NAME";
            item.value = mQuote.getName();
            item.withIcon = false;
            stockDetails.add(item);
            // Symbol
            item = new StockDetailItem();
            item.name = "SYMBOL";
            item.value = mQuote.getSymbol();
            stockDetails.add(item);
            // Price
            item = new StockDetailItem();
            item.name = "LASTPRICE";
            item.value = "$ "+String.format("%.2f", mQuote.getPrice());
            stockDetails.add(item);
            // Change
            item = new StockDetailItem();
            item.name = "CHANGE";
            item.value = NumberFormatHelper.formateDouble(mQuote.getChange(), true, false, false) +
                    "(" + NumberFormatHelper.formateDouble(mQuote.getChangePercent(), true, true, false) + ")";

            if (mQuote.getChangePercent() > 0) {
                item.withIcon = true;
                item.iconId = R.drawable.ic_arrow_up;
            } else if (mQuote.getChangePercent() < 0){
                item.withIcon = true;
                item.iconId = R.drawable.ic_arrow_down;
            }
            stockDetails.add(item);
            // Timstamp
            item = new StockDetailItem();
            item.name = "TIMESTAMP";
            item.value = mQuote.getTimestamp();
            if (mQuote.getTimestamp().length() != 0) {
                String timestamp = mQuote.getTimestamp();
                String year = timestamp.substring(timestamp.length() - 4, timestamp.length());
                String time = timestamp.substring(timestamp.length() - 23, timestamp.length() - 15);
                String date = timestamp.substring(4, timestamp.length() - 24);
                item.value = date + " " + year + ", " + time;
            }

            stockDetails.add(item);
            // Market Cap
            item = new StockDetailItem();
            item.name = "MARKETCAP";
            item.value = String.format("%.2f Billion", mQuote.getMarketCap() / 1000000000);
            stockDetails.add(item);
            // volum
            item = new StockDetailItem();
            item.name = "VOLUME";
            item.value = String.format("%.0f", mQuote.getVolume());
            stockDetails.add(item);
            // Change YTD
            item = new StockDetailItem();
            item.name = "CHANGEYTD";
            item.value = NumberFormatHelper.formateDouble(mQuote.getChangeYTD(), false, false, false) +
                    "(" + NumberFormatHelper.formateDouble(mQuote.getChangePercentYTD(), true, true, false) + ")";
            if (mQuote.getChangePercentYTD() > 0) {
                item.withIcon = true;
                item.iconId = R.drawable.ic_arrow_up;
            } else if (mQuote.getChangePercentYTD() < 0) {
                item.withIcon = true;
                item.iconId = R.drawable.ic_arrow_down;
            }
            stockDetails.add(item);
            // HIGH
            item = new StockDetailItem();
            item.name = "HIGH";
            item.value = String.format("%.2f", mQuote.getHigh());
            stockDetails.add(item);
            // LOW
            item = new StockDetailItem();
            item.name = "LOW";
            item.value = String.format("%.2f", mQuote.getLow());
            stockDetails.add(item);
            // OPEN
            item = new StockDetailItem();
            item.name = "OPEN";
            item.value = String.format("%.2f", mQuote.getOpen());
            stockDetails.add(item);
        }

        public int getCount() {
            return stockDetails.size();
        }
        public String getItem(int position) {
            return stockDetails.get(position).name;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_stock_detail, parent, false);
            }
            TextView nameView = (TextView) convertView.findViewById(R.id.stock_detail_name);
            TextView valueView = (TextView) convertView.findViewById(R.id.stock_detail_value);
            ImageView imgView = (ImageView) convertView.findViewById(R.id.stock_detail_image);

            StockDetailItem stockDetailItem = stockDetails.get(position);
            nameView.setText(stockDetailItem.name);
            valueView.setText(stockDetailItem.value);
            if (stockDetailItem.withIcon) {
                imgView.setVisibility(View.VISIBLE);
                imgView.setImageResource(stockDetailItem.iconId);
            } else {
                imgView.setVisibility(View.GONE);
            }

            return convertView;
        }
    }
}
