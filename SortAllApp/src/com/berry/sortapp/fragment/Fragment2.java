package com.berry.sortapp.fragment;


import java.util.ArrayList;

import com.berry.sortapp.AppManager;
import com.berry.sortapp.R;
import com.berry.sortapp.R.layout;
import com.berry.sortapp.adapter.AppGridViewAdapter;
import com.berry.sortapp.bean.AppInfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 内容待定
 * @author berry
 *
 */
public class Fragment2 extends Fragment {
  static Context mContext;
  Bundle meBundle;
  
  GridView gridView;
  TextView nullTView;
  AppGridViewAdapter gridAdapter;
  View broswerView,gameView,storeView;

  public static Fragment2 newInstance(Context context, Bundle bundle) {
    mContext = context;
    Fragment2 newFragment = new Fragment2();
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
    View  root = inflater.inflate(R.layout.fragment_2, container,false);
    
     gridView = (GridView) root.findViewById(R.id.frag_2_open);
     nullTView = (TextView) root.findViewById(R.id.frag_2_open_null);
     broswerView = root.findViewById(R.id.search_layout_broswer);
     gameView = root.findViewById(R.id.search_layout_game);
     storeView = root.findViewById(R.id.search_layout_appstore);
     
     
     ArrayList<AppInfo> apps = AppManager.getTaskList(mContext, 9);
     if (apps==null || apps.size()==0) {
      nullTView.setVisibility(View.VISIBLE);
      gridView.setVisibility(View.GONE);
    }else {
      nullTView.setVisibility(View.GONE);
      gridView.setVisibility(View.VISIBLE);
      gridAdapter = new AppGridViewAdapter(mContext, apps);
      gridView.setAdapter(gridAdapter);
    }
     
     gridView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view!=null) {
          AppInfo info = (AppInfo) view.getTag(R.id.gridview_item_app);
          Intent intent = new Intent();
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.setComponent(info.getCn());
          mContext.startActivity(intent);
        }
        
      }
       
     });
     
     broswerView.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        AppManager.startWebSearch(mContext, null);
        
      }
    });
     
     gameView.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        AppManager.startGameSearch(mContext, null);
        
      }
    });
     storeView.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        AppManager.startStoreSearch(mContext, null);
        
      }
    });
        
    return root;
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
    super.onSaveInstanceState(outState);
  }

}
