package com.armjld.eb3tly.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.armjld.eb3tly.LocationManeger.MyLocation;
import com.armjld.eb3tly.R;

import java.util.ArrayList;

import Model.LocationDataType;

public class LocationsAdapter extends ArrayAdapter<LocationDataType> {
    Context context;
    public LocationsAdapter(Context context, ArrayList<LocationDataType> locations) {
        super(context, 0, locations);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LocationDataType locations = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_locations, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvGov = convertView.findViewById(R.id.tvGov);
        TextView tvAddress = convertView.findViewById(R.id.tvAddress);

        assert locations != null;
        tvName.setText(locations.getTitle());
        tvGov.setText(locations.getState() + " - " + locations.getRegion());
        tvAddress.setText(locations.getAddress());

        convertView.setOnClickListener(v-> {
            MyLocation.type = "Edit";
            Intent editInt = new Intent(context, MyLocation.class);
            editInt.putExtra("locID", locations.getId());
            context.startActivity(editInt);
        });

        return convertView;
    }
}