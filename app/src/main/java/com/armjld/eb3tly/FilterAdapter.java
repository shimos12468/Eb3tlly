package com.armjld.eb3tly;

import android.content.Context;
import android.widget.ArrayAdapter;

public class FilterAdapter <String> extends ArrayAdapter<String> {
    final int LIMIT = 5;

    public FilterAdapter(Context context, int textViewResourceId, String[] fullList) {
        super(context, textViewResourceId, (String[]) fullList);
    }

    @Override
    public int getCount () {
        return Math.min(LIMIT, super.getCount());
    }

}