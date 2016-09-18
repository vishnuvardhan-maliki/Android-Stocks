package com.sms.deliamao.stockmarketsearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookSdk;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import  com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;

import java.io.IOException;

/**
 * Created by deliamao on 5/2/16.
 */
public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MenuItem mFavouriteButton;
    private FavouriteStockManager mFavouriteStockManager;
    private JSONObject mStockQuoteJSON;
    private String mStockId;
    private String mStockName;
    private String mStockLastPrice;
    private StockQuote mCurrentStockQuote;
    private String historicalHtmlContent;
    Bundle extras;

    ListView stockList;

    String quoteJson;
    String  newJson;
    //facebook post function
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // facebook function:
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        // historical chart part
        // update the historical string

        // Init any table here.
        mFavouriteStockManager = new FavouriteStockManager(this);
        extras = getIntent().getExtras();
        if (extras == null) {
            Log.e(TAG, "Expect QuoteReturnString, but no Extras found.");
            finish();
        } else {
            try {
                mStockQuoteJSON = new JSONObject(extras.getString("QuoteReturnString"));
                mCurrentStockQuote = new StockQuote(mStockQuoteJSON);
                mStockId = mStockQuoteJSON.getString("Symbol");
                mStockName = mStockQuoteJSON.getString("Name");
                mStockLastPrice = mStockQuoteJSON.getString("LastPrice");
                // update the historical string
                try {
                    historicalHtmlContent = IOUtils.toString(getAssets().open("historical.html")).replaceAll("%test%", mStockId);
                } catch (IOException ex) {
                    System.out.println(ex.toString());
                }
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse QuoteReturnString.");
                finish();
            }
        }
        Log.d(TAG, mStockQuoteJSON.toString());

        // Init any UI views in following.
        setContentView(R.layout.activity_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mStockName);
        // you may need to set up toolbar in here later

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        ViewPagerAdapter  viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),getApplicationContext());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout)findViewById(R.id.tabs);

        // if without below code it won't stop
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        /*************************** headle fragment_news feed code end  *******************************/
        if(extras != null){
            Log.d(TAG, "none object " );
            newJson = extras.getString("newsReturnString");
            // get the whole fragment_news feed;
            Log.d(TAG, "newsReturnString: " );
        }
        //test
        //Intent intentOfDetail2 = new Intent(ResultActivity.this, TestHistorical.class);
        //startActivity(intentOfDetail2);


        /*************************** headle fragment_news feed  code end  *******************************/
// Crazy ViewList problem
        /*************************** headle stock detail code *******************************/
        String[] rcontent = new String[11];
        String [] rtitle = {"NAME","SYMBOL","LASTPRICE","CHANGE","TIMESTAPM","MARKETCAP","VOLUME","CHANGEYTD","HIGH","LOW","OPEN"};
        if (extras!= null){
            quoteJson = extras.getString("QuoteReturnString");
            Log.d(TAG, "extras de quote result" + quoteJson);
            Log.d(TAG, "extras de quote result" + rtitle[5]);
            JSONObject quoteObject = null;
            try{
                quoteObject = new JSONObject(quoteJson);
                rcontent[0] = quoteObject.getString("Name");
                rcontent[1] = quoteObject.getString("Symbol");
                rcontent[2] = quoteObject.getString("LastPrice");
                rcontent[3] = quoteObject.getString("Change");
                rcontent[4] = quoteObject.getString("Timestamp");
                rcontent[5] = quoteObject.getString("MarketCap");
                rcontent[6] = quoteObject.getString("Volume");
                rcontent[7] = quoteObject.getString("ChangeYTD");
                rcontent[8] = quoteObject.getString("High");
                rcontent[9] = quoteObject.getString("Low");
                rcontent[10] = quoteObject.getString("Open");
                Log.d(TAG, "extra de quote result" + rcontent[5]);
                //D New Actioin
                /*
                Intent intentOfDetail = new Intent(ResultActivity.this, GenerateCurrent.class);
                intentOfDetail.putExtra("stockTitle", rtitle);
                intentOfDetail.putExtra("stockContent", rcontent);
                startActivity(intentOfDetail);
                */
                // D New Action
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    // new Action

    /*************************** headle stock detail code end  *******************************/
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    // Add favourite and facebook share button
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_view, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onPrepareOptionsMenu (Menu menu) {
        mFavouriteButton = menu.findItem(R.id.action_favorite);
        if (mFavouriteStockManager.isFavourite(mStockId)) {
            mFavouriteButton.setIcon(R.drawable.ic_filled_star);
        } else {
            mFavouriteButton.setIcon(R.drawable.ic_star_outline);
        }
        return true;
    }

    public boolean is_favourite = false;

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Log.d(TAG, "action_favorite");
                if (mFavouriteStockManager.isFavourite(mStockId)) {
                    mFavouriteStockManager.removeFavourite(mStockId);
                    item.setIcon(R.drawable.ic_star_outline);
                } else {
                    mFavouriteStockManager.addOrUpdateFavourite(mCurrentStockQuote);
                    item.setIcon(R.drawable.ic_filled_star);
                    Toast.makeText(ResultActivity.this, "Bookmarked "+mStockName+"!!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_fb:
                // User chose the "Favorite" action, mark the fragment_current item
                // as a favorite...

                // facebook function
                String stockImg = "http://chart.finance.yahoo.com/t?s="+mStockId+"&lang=en-US&width=300&height=300";
                String title = "Current Stock Price of "+ mStockName+ " is $" + mStockLastPrice;
                String subhead = "Stock Information of "+mStockName;
                String yahooURL ="http://finance.yahoo.com/q?s=" + mStockId;

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(title)
                            .setContentDescription(subhead)
                            .setContentUrl(Uri.parse(yahooURL))
                            .setImageUrl(Uri.parse(stockImg))
                            .build();

                    shareDialog.show(linkContent);
                }
                Log.d(TAG, "action_fb");
                return true;

            default:


                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    /// facebook Function

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(ResultActivity.this, "You shared this post", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(ResultActivity.this, "You didn't share this post", Toast.LENGTH_SHORT).show();
        }


    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private String fragments [] = {"CURRENT", "HISTORICAL", "NEWS"};
        public ViewPagerAdapter(FragmentManager supportFragmentManger, Context applicationContext){
            super(supportFragmentManger);

        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    //PageCurrentFragment cf = PageCurrentFragment.newInstance(mCurrentStockQuote, extras);
                    //cf.setArguments(extras);
                    //return  cf;
                    return PageCurrentFragment.newInstance(mCurrentStockQuote);
                case 1:
                    return  PageHistoryFragment.newInstance(historicalHtmlContent);

                case 2:
                    PageNewsFragment nf = new PageNewsFragment();
                    nf.setArguments(extras);
                    return nf;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }
}

