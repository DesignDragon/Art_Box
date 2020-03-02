package com.example.ArtBox;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class viewPager_adapter extends FragmentPagerAdapter {
    private Fragment[] childs;
    public viewPager_adapter(FragmentManager fm) {
        super(fm);
        childs=new Fragment[]{
                new posts(),
                new auction()
        };
    }

    @Override
    public Fragment getItem(int position) {
        return childs[position];
    }

    @Override
    public int getCount() {
        return childs.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title= getItem(position).getClass().getName();
        return title.subSequence(title.lastIndexOf(".")+1,title.length());
    }
}
