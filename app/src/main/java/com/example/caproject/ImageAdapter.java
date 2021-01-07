package com.example.caproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private final int[] placeholder;

    public ImageAdapter(Context c, int[] placeholder)
    {
        this.mContext = c;
        this.placeholder = placeholder;
    }
    @Override
    public int getCount()
    {
        return placeholder.length;
    }
    //required to override all of the interface's methods even if we don't use them
    @Override
    public Object getItem(int position)
    {
        return null;
    }
    @Override
    public long getItemId(int position)
    {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null)
        {
            gridView = new View(mContext);
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.gridview_single_image, null);
            // set image based on selected text
            ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_image);
            imageView.setImageResource(placeholder[position]);
        }
        else
        {
            gridView = (View) convertView;
        }
        return gridView;
    }
}
