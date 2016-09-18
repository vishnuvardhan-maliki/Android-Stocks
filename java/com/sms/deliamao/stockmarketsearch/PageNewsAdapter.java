package com.sms.deliamao.stockmarketsearch;

/**
 * Created by Anusha on 5/5/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PageNewsAdapter extends BaseAdapter {
    private Context mContext;
    List<NewsClass> newslist = new ArrayList<NewsClass>();
    private static LayoutInflater inflater=null;
    public PageNewsAdapter(Context c,List nl)
    {
        super();
        mContext=c;
        newslist = nl;
        inflater = ( LayoutInflater )mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount()
    {
        // return the number of records in cursor
        return newslist.size();
    }
    public class Holder{
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
    }

    // getView method is called for each item of ListView
    public View getView(int position, View view, ViewGroup parent)
    {
        Holder holder=new Holder();
        View rowView;
        //inflater = ( LayoutInflater )mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.newsrow, null);
        holder.tv1=(TextView) rowView.findViewById(R.id.newsdesc);
        holder.tv2=(TextView) rowView.findViewById(R.id.newstitle);
        holder.tv3=(TextView) rowView.findViewById(R.id.newspublisher);
        holder.tv4=(TextView) rowView.findViewById(R.id.newsdate);
        holder.tv1.setText(Html.fromHtml("<a href=\"" + newslist.get(position).url + "\">" + newslist.get(position).title + "</a>"));
        holder.tv1.setClickable(true);
        holder.tv1.setMovementMethod (LinkMovementMethod.getInstance());
        // holder.tv1.setText(newslist.get(position).title);
        holder.tv2.setText(newslist.get(position).description);
        holder.tv3.setText("Publisher : "+newslist.get(position).publisher);
        holder.tv4.setText("Date : "+newslist.get(position).date);
        return rowView;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}

