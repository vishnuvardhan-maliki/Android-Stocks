package com.sms.deliamao.stockmarketsearch;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class PageNewsFragment extends Fragment {

    ListView nlv;
    List<NewsClass> newsArrList = new ArrayList<NewsClass>();
    String symboldata;

    public PageNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        symboldata = getArguments().getString("symbol");
        View v =inflater.inflate(R.layout.fragment_news,container,false);
        nlv = (ListView)v.findViewById(R.id.newslistView);

        new JSONTask().execute();
        return v;
    }

    public class JSONTask extends AsyncTask<URL, String, String> {


        @Override
        protected String doInBackground(URL... params) {
            try {
                String url = "http://ninja-1.crpqbfpuau.us-west-2.elasticbeanstalk.com/?news="+symboldata;
                Log.e("STOCK", "\nSending 'GET' request to URL : " + url + "***");
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.connect();
                InputStream stream = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                StringBuffer response = new StringBuffer();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();

                JSONArray jsonarr = new JSONArray(response.toString());
                for (int i = 0; i < jsonarr.length(); i++) {
                    JSONObject jobj = new JSONObject(jsonarr.get(i).toString());
                    NewsClass nobj = new  NewsClass();
                    nobj.title = jobj.getString("Title");
                    nobj.url = jobj.getString("Url");
                    nobj.description = jobj.getString("Description");
                    nobj.publisher = jobj.getString("Source");
                    nobj.date = jobj.getString("Date");
                    newsArrList.add(nobj);

                }



                return null;

            } catch (Exception e) {
                Log.e("STOCK", "errrorrr");
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PageNewsAdapter adapter = new PageNewsAdapter(getActivity().getBaseContext(),newsArrList);
            if(adapter != null) {
                //newslv = (ListView) v.findViewById(R.id.newslistView);
                if (nlv!= null){
                    nlv.setAdapter(adapter);}else{
                    Log.e("Stock","adapter issue");}
            }
        }

    }
}
