package com.sms.deliamao.stockmarketsearch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AutoCompleteTextView suggestionTextView;
    ProgressBar refreshProgressBar;
    AsyncTask<Void, Void, Void> hischartTask;
    String quoteJsonString = "";
    String newsJsonString ="";
    Context context;
    FavouriteStockManager mFavouriteStockManager;
    TaskScheduler taskScheduler;
    Runnable autoRefreshTask;
    boolean isLoading = false;
    private boolean flag = false;
    ProgressBar progressBar=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFavouriteStockManager = new FavouriteStockManager(this);
        taskScheduler = new TaskScheduler();

        setContentView(R.layout.activity_main);
//        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.GONE);
      ProgressBar progressbar11=(ProgressBar) findViewById(R.id.progressBar);
      progressbar11.setVisibility(View.GONE);
        context = this;
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate() Restoring previous state");
            /* restore state */
        }

        suggestionTextView=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);

        suggestionTextView.setAdapter(autoCompleteAdapter);
        suggestionTextView.setThreshold(3); // setup how many letter need to call
        Log.d(TAG, "this is test");

        Button clearButton = (Button)findViewById(R.id.clear);
        clearButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProgressBar progressbar11=(ProgressBar) findViewById(R.id.progressBar);
                progressbar11.setVisibility(View.GONE);
                suggestionTextView.setText("");
            }
        });

        Button quoteButton = (Button)findViewById(R.id.getQuote);
        quoteButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProgressBar progressbar11=(ProgressBar) findViewById(R.id.progressBar);
                progressbar11.setVisibility(View.GONE);
                String symbol = suggestionTextView.getText().toString();
                if (verifyQuoteInput()) {
                    getQuote(symbol);
                }
            }
        });

        ImageButton refreshButton = (ImageButton)findViewById(R.id.button_refresh_favourite);
        refreshButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "updateStockFavouriteDataAndView");
                updateStockFavouriteDataAndView();
            }
        });
        autoRefreshTask = new Runnable() {
            @Override
            public void run() {
                updateStockFavouriteDataAndView();
            }
        };
        Switch autoRefreshButton = (Switch)findViewById(R.id.button_auto_refresh_favourite);
        autoRefreshButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Switch autoRefreshButton = (Switch)v;
                if (autoRefreshButton.isChecked()) {
                    Log.d(TAG, "start up auto refresh favourite");
                    taskScheduler.scheduleAtFixedRate(autoRefreshTask, 2000, 15000);
                } else {
                    Log.d(TAG, "stop auto refresh favourite");
                    taskScheduler.stop(autoRefreshTask);
                }
            }
        });

        // Create favourite stocks ListView.
        final FavouriteStockAdapter adapter = new FavouriteStockAdapter(this);
        SlideCutListView favouriteStocksView = (SlideCutListView) findViewById(R.id.favourite_stocks_view);
        favouriteStocksView.setDivider(null);
        favouriteStocksView.setAdapter(adapter);
        favouriteStocksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FavouriteStockAdapter adapter  = (FavouriteStockAdapter)parent.getAdapter();
                StockQuote clickStock = adapter.getItem(position);
                getQuote(clickStock.getSymbol());
            }
        });
        favouriteStocksView.setRemoveListener(new SlideCutListView.RemoveListener() {
            @Override
            public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
                final StockQuote stock = (StockQuote)adapter.getItem(position);
                adapter.hideItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Want to delete " + stock.getName() + "from favourites?")
                        .setCancelable(false)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                adapter.recoverHidedItem();
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                adapter.deleteHidedItem();
                                dialog.cancel();mFavouriteStockManager.removeFavourite(stock.getSymbol());
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        updateStockFavouriteDataAndView();
    }

    private class AutoCompleteAdapter extends ArrayAdapter {
        ArrayList<StockSuggestion> mStockSuggestions;
        class StockSuggestion {
            public String symbol;
            public String name;
            public String exchange;
        }
        public AutoCompleteAdapter(Context context, int textViewResourceId){
            super(context, textViewResourceId);
            mStockSuggestions = new ArrayList<StockSuggestion>();
        }
        @Override
        public Filter getFilter() {
            Filter autoCompleteFilter = new Filter(){
                @Override
                protected FilterResults performFiltering(CharSequence constraint){
                    FilterResults filterResults = new FilterResults();
                    if(constraint != null) {
                        try {
                            mStockSuggestions = new AsyncFetchStockSymbols().execute(new String[]{constraint.toString()}).get();
                        } catch(Exception e) {
                            Log.e("AsyncFetchStockSymbols", e.getMessage());
                        }
                        // Now assign the values and count to the FilterResults object
                        filterResults.values = mStockSuggestions;
                        filterResults.count = mStockSuggestions.size();
                    }
                    return filterResults;
                }
                @Override
                protected void publishResults(CharSequence contraint, FilterResults results) {
                    if(results != null && results.count > 0) {
                        Log.d(TAG, "Result numbers: " + results.count);
                        clear();
                        for (StockSuggestion s : (ArrayList<StockSuggestion>)results.values) {
                            add(s);
                        }
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }

                class AsyncFetchStockSymbols extends AsyncTask<String, Void, ArrayList<StockSuggestion>>{
                    private static final String TAG = "AsyncFetchStockSymbols";

                    @Override
                    protected void onPreExecute() {
                        //ProgressBar progressbar11=(ProgressBar) findViewById(R.id.progressBar);
                        //progressbar11.setVisibility(View.VISIBLE);
                        Log.e("a","a");
                    }


                    @Override

                    protected ArrayList<StockSuggestion> doInBackground(String... constraint) {

                        ArrayList<StockSuggestion> stockSuggestions = new ArrayList<StockSuggestion>();
                        Log.d(TAG, "Fetching symbol: " + constraint[0]);
                        String url = WebFetchHelper.STOCK_SYMBOL_URL + constraint[0];
                        String jsonString = WebFetchHelper.fetchUrl(url);
                        Log.d(TAG, "Auto complete result:" + jsonString);
                        JSONArray jo = null;
                        try {
                            jo = new JSONArray(jsonString);
                            for(int i = 0; i<jo.length();i++ ){
                                JSONObject e = jo.getJSONObject(i);
                                StockSuggestion suggestion = new StockSuggestion();
                                suggestion.symbol = e.getString("Symbol");
                                suggestion.name = e.getString("Name");
                                suggestion.exchange = e.getString("Exchange");
                                stockSuggestions.add(suggestion);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error to parse stock json: " + e.getMessage());
                        }
                        return stockSuggestions;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<StockSuggestion> result) {
                        ProgressBar progressbar11=(ProgressBar) findViewById(R.id.progressBar);
                        progressbar11.setVisibility(View.VISIBLE);

                    }
                }
            };
            return autoCompleteFilter;
        }
        public int getCount() {
            return mStockSuggestions.size();
        }
        public String getItem(int position) {
            return mStockSuggestions.get(position).symbol;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_auto_complete, parent, false);
            }
            TextView  symbolView = (TextView) convertView.findViewById(R.id.auto_complete_symbol);
            TextView nameView = (TextView) convertView.findViewById(R.id.auto_complete_name);

            StockSuggestion suggestion = mStockSuggestions.get(position);
            symbolView.setText(suggestion.symbol);
            nameView.setText(suggestion.name + " (" + suggestion.exchange + ")" );
            return convertView;
        }
    }

    private class FavouriteStockAdapter extends ArrayAdapter<StockQuote> {
        private List<StockQuote> stockList;
        private StockQuote hidedItem;
        private int hidedItemIndex = 0;
        private Context context;

        public FavouriteStockAdapter(Context ctx) {
            super(ctx, R.layout.item_favourite_stock);
            this.context = ctx;
            this.stockList = new ArrayList<StockQuote>();
        }
        public int getCount() {
            return stockList.size();
        }
        public StockQuote getItem(int position) {
            return stockList.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_favourite_stock, parent, false);
            }
            TextView  symbolView = (TextView) convertView.findViewById(R.id.stock_symbol);
            TextView nameView = (TextView) convertView.findViewById(R.id.stock_name);
            TextView priceView = (TextView) convertView.findViewById(R.id.stock_price);
            TextView changeView = (TextView) convertView.findViewById(R.id.stock_change);
            TextView  marketCapView = (TextView) convertView.findViewById(R.id.stock_market_cap);
            StockQuote stock = stockList.get(position);
            symbolView.setText(stock.getSymbol());
            nameView.setText(stock.getName());
            priceView.setText("$ " + NumberFormatHelper.formateDouble(stock.getPrice(), false, false, false));
            changeView.setText(NumberFormatHelper.formateDouble(stock.getChangePercent(), true, true, false));
            if (stock.getChangePercent() == 0) {
                changeView.setBackgroundColor(Color.GRAY);
            } else if (stock.getChangePercent() > 0) {
                changeView.setBackgroundColor(Color.GREEN);
            } else {
                changeView.setBackgroundColor(Color.RED);
            }
            marketCapView.setText("Market Cap: " + NumberFormatHelper.formateDouble(stock.getMarketCap(), false, false, true));
            return convertView;
        }

        public void updateDataSet(ArrayList<StockQuote> newDataSet) {
            findViewById(R.id.favourite_refresh_progress).setVisibility(View.GONE);
            stockList.clear();
            stockList.addAll(newDataSet);
            notifyDataSetChanged();
        }
        public void hideItem(int position) {
            if (position >= stockList.size()) {
                return;
            }
            hidedItemIndex = position;
            hidedItem = stockList.get(position);
            stockList.remove(hidedItemIndex);
            notifyDataSetChanged();
        }
        public void recoverHidedItem() {
            if (hidedItem == null || hidedItemIndex > stockList.size()) {
                return;
            }
            stockList.add(hidedItemIndex, hidedItem);
            notifyDataSetChanged();
        }
        public void deleteHidedItem() {
            hidedItem = null;
        }
    }

    void updateStockFavouriteDataAndView() {
        AsyncTask<ArrayList<StockQuote>, Void, ArrayList<StockQuote>> fetchQuotesTask = new AsyncTask<ArrayList<StockQuote>, Void, ArrayList<StockQuote>> () {
            @Override
            protected ArrayList<StockQuote> doInBackground(ArrayList<StockQuote>... quotes) {
                Log.e("TEST1","TEST1");
                ArrayList<StockQuote> stockQuotes = new ArrayList<StockQuote>();
                for (StockQuote quote : quotes[0]) {
                    //Log.d(TAG, "MMMetching stock for symbol: " + quote.getSymbol());
                    //Log.e("Log", "Fetching stock quote for price: " + quote.getPrice());
                    Log.e("xxxxxx",quote.getSymbol());
                    Log.e("xxxxxx",String.valueOf(quote.getPrice()));
                    String url = WebFetchHelper.STOCK_QUOTE_URL + quote.getSymbol();
                    String jsonString = WebFetchHelper.fetchUrl(url);
                    JSONObject jo = null;
                    StockQuote stockQuote = null;
                    try {
                        stockQuote = new StockQuote(new JSONObject(jsonString));
                    } catch (JSONException e) {
                        Log.e(TAG, "Error to parse stock json: " + e.getMessage());
                    }
                    stockQuotes.add(stockQuote);
                }
                return stockQuotes;
            }
            @Override
            protected void onPostExecute(ArrayList<StockQuote> result) {
                HashMap<String, StockQuote> stockQuoteMap = new HashMap<String, StockQuote>();
                for (StockQuote q : result) {
                    if (q != null) {
                        stockQuoteMap.put(q.getSymbol(), q);
                    }
                }
                ArrayList<StockQuote> newDataSet = new ArrayList<StockQuote>();
                for (StockQuote quote : mFavouriteStockManager.getAllFavourites()) {
                    Log.d(TAG, "update favourite " + quote.getSymbol() + "before message: " + quote.toJSONString());
                    if (stockQuoteMap.containsKey(quote.getSymbol())) {
                        Log.d(TAG, "update favourite " + quote.getSymbol() + "after message: " + stockQuoteMap.get(quote.getSymbol()).toJSONString());
                        mFavouriteStockManager.addOrUpdateFavourite(stockQuoteMap.get(quote.getSymbol()));
                    }
                }
                updateStockFavouriteView();
            }
        };
        ArrayList<StockQuote> favouriteStocks = mFavouriteStockManager.getAllFavourites();
        fetchQuotesTask.execute(favouriteStocks);
        findViewById(R.id.favourite_refresh_progress).setVisibility(View.VISIBLE);
    }
    void updateStockFavouriteView() {
        ListView favouriteStockView = (ListView) findViewById(R.id.favourite_stocks_view);
        FavouriteStockAdapter adapter = (FavouriteStockAdapter) favouriteStockView.getAdapter();
        ArrayList<StockQuote> favouriteStocks = mFavouriteStockManager.getAllFavourites();
        adapter.updateDataSet(favouriteStocks);
    }

    boolean verifyQuoteInput() {
        String symbol = suggestionTextView.getText().toString();
        //validate if the input is blank;
        if(symbol.length()== 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Please enter a Stock Name/Symbol")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return true;
    }
    // handle get quote function
    void getQuote(String symbol) {
        if (isLoading) {
            return;
        }
        isLoading = true;

        // handle get Quote Detial
        /*************************** headle fragment_news feed code end  *******************************/
        AsyncTask<String, Void, String> getNewsTask = new AsyncTask<String, Void, String> () {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... params) {
                return WebFetchHelper.fetchUrl(WebFetchHelper.STOCK_NEWS_URL + params[0]);
            }

            protected void onPostExecute(String result) {
                // TODO(dilinmao):
            }
        };
        getNewsTask.execute(symbol);

        // handle stock detail problem
        AsyncTask<String, Void, String> getQuoteTask = new AsyncTask<String, Void, String> () {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... params) {
                return WebFetchHelper.fetchUrl(WebFetchHelper.STOCK_QUOTE_URL + params[0]);
            }

            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                JSONObject jo = null;
                Log.d(TAG, "getQuote: " + quoteJsonString);
                try {
                    jo = new JSONObject(result);
                    if(jo.has("Message")){
                        Log.d(TAG, "stock detail has Message or not:" + jo.getString("Message"));
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Invalid Symbol")
                                .setCancelable(false)
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }else if(jo.has("Status") && jo.getString("Status").equals("SUCCESS")){
                        Intent intentOfDetail = new Intent(MainActivity.this, ResultActivity.class);
                        intentOfDetail.putExtra("QuoteReturnString", result);
                        intentOfDetail.putExtra("newsReturnString", newsJsonString);
                        startActivity(intentOfDetail);
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("No valid data for this Stock Symbol")
                                .setCancelable(false)
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isLoading = false;
            }
        };
        getQuoteTask.execute(symbol);
    }
    // when the get quote button was click
    // Refresh MainActivity Views. e.g. back button to navigate back.
    public void onResume() {  //
        Log.d(TAG, "onResume");
        super.onResume();
        //Refresh Favourite lists here.
        updateStockFavouriteView();
    }
}

