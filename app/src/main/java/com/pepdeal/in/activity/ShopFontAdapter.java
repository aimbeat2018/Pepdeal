package com.pepdeal.in.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pepdeal.in.R;

public class ShopFontAdapter extends BaseAdapter {


    LayoutInflater inflter;
    int[] shopfont;
    String[] fontNames;
    Context context;

    public ShopFontAdapter(Context applicationContext, int[] shopfont, String[] fontNames) {
       
        this.context = applicationContext;
        this.shopfont = shopfont;
        this.fontNames = fontNames;
        inflter = (LayoutInflater.from(applicationContext));

    }

    @Override
    public int getCount() {
        return shopfont.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.custom_spinner_shopfont, null);
        TextView font = (TextView) convertView.findViewById(R.id.shopfont);
        TextView fontnames = (TextView) convertView.findViewById(R.id.fontname);
        font.setBackgroundResource(shopfont[position]);
        fontnames.setText(fontNames[position]);
        return convertView;    }
}
