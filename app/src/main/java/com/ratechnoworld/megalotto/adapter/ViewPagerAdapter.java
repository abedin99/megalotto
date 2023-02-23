package com.ratechnoworld.megalotto.adapter;

import android.content.Context;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private final ArrayList<String> mFragmentTitleList = new ArrayList<>();

    public Context context;
    public ViewPager viewPager;
    public TabLayout tabLayout;

    public ViewPagerAdapter(FragmentManager manager, Context context, ViewPager viewPager, TabLayout tabLayout) {
        super(manager);
        this.context = context;
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}
