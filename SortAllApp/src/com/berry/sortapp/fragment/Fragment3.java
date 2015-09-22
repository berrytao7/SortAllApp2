package com.berry.sortapp.fragment;

import com.berry.sortapp.R;
import com.berry.sortapp.R.layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 新闻(广告)页面
 * @author berry
 *
 */
public class Fragment3 extends Fragment {
  static Context mContext;
  Bundle meBundle;

  public static Fragment3 newInstance(Context context, Bundle bundle) {
    mContext = context;
    Fragment3 newFragment = new Fragment3();
    newFragment.setArguments(bundle);
    return newFragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    return inflater.inflate(R.layout.fragment_3, container,false);
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
    super.onSaveInstanceState(outState);
  }
}
