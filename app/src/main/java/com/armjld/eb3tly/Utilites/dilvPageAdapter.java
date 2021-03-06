package com.armjld.eb3tly.Utilites;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.armjld.eb3tly.CaptinProfile.capAcceptedTab;
import com.armjld.eb3tly.CaptinProfile.capDelvTab;
import com.armjld.eb3tly.CaptinProfile.captinRecived;
import com.armjld.eb3tly.R;

public class dilvPageAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_6,R.string.tab_text_2};
    private final Context mContext;

    public dilvPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
       Fragment fragment = null;
       switch(position){
           case 0:
               fragment = new capAcceptedTab();
               break;
           case 1:
               fragment = new captinRecived();
               break;
           case 2:
               fragment = new capDelvTab();
               break;
       }
        assert fragment != null;
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 3;
    }
}