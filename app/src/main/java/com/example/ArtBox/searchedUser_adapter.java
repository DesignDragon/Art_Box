package com.example.ArtBox;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class searchedUser_adapter extends FragmentPagerAdapter {
    private final Bundle bundle;
    public searchedUser_adapter(FragmentManager fm,Bundle data)
    {
        super(fm);
        bundle=data;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                posts p=new posts();
                p.setArguments(bundle);
                return p;
            case 1:
                auction a=new auction();
                a.setArguments(bundle);
                return a;
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title= getItem(position).getClass().getName();
        return title.subSequence(title.lastIndexOf(".")+1,title.length());
    }
}
