package com.armjld.eb3tly;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import Model.LocationDataType;

public class LocationSpinnerAdapter extends ArrayAdapter<LocationDataType> {

    private final ArrayList<LocationDataType> locData;
    private Context mContext;

    public LocationSpinnerAdapter(ArrayList<LocationDataType> locData, Context mContext) {
        super(mContext,android.R.layout.simple_dropdown_item_1line, locData);

        this.locData = locData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return locData.size();
    }

    @Override
    public LocationDataType getItem(int i) {
        return locData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
        TextView locTitle;
        if(view != null) {
            locTitle = (TextView) view;
        } else {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            locTitle = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line,null);
        }
        locTitle.setText(locData.get(i).getTitle());
        return locTitle;
    }

    @Override
    public View getDropDownView (int position, View view, ViewGroup viewGroup) {
        TextView locTitle;
        if(view != null) {
            locTitle = (TextView) view;
        } else {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            locTitle = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line,null);
        }
        locTitle.setText(locData.get(position).getTitle());
        return locTitle;
    }

}
