package com.armjld.eb3tly.Utilites;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.armjld.eb3tly.SupplierProfile.acceptedTab;
import com.armjld.eb3tly.SupplierProfile.dilveredTab;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.SupplierProfile.placedTab;

public class supPageAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_3, R.string.tab_text_4 , R.string.tab_text_5};
    private final Context mContext;

    public supPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
            Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new placedTab();
                break;
            case 1:
                fragment = new acceptedTab();
                break;
            case 2:
                fragment = new dilveredTab();
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
    public int getCount() { return 3; }
}