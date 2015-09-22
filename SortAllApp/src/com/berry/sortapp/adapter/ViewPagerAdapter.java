package com.berry.sortapp.adapter;

import java.util.ArrayList;

import com.berry.sortapp.R;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
  private ArrayList<Fragment> fragments;
  private Context mContext;

  public ViewPagerAdapter(FragmentManager fm,Context context,ArrayList<Fragment> fragments) {
    super(fm);
    this.mContext = context;
    this.fragments = fragments;
  }

  @Override
  public Fragment getItem(int position) {
    return this.fragments.get(position);
  }

  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return this.fragments.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
      String tabLabel = null;
      switch (position) {
          case 0:
              tabLabel = mContext.getString(R.string.tab_1);
              break;
          case 1:
              tabLabel = mContext.getString(R.string.tab_2);
              break;
          case 2:
              tabLabel = mContext.getString(R.string.tab_3);
              break;
      }
      return tabLabel;
  }
}
