package com.berry.sortapp.fragment;

import java.util.ArrayList;

import com.berry.sortapp.R;
import com.berry.sortapp.adapter.AppSortListViewAdapter;
import com.berry.sortapp.bean.SortModel;
import com.berry.sortapp.views.SideBar;
import com.berry.sortapp.views.SideBar.OnTouchingLetterChangedListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 本地应用页面
 * @author berry
 *
 */
public class Fragment1 extends Fragment {
  static Context mContext;
  ListView appSortLV;
  TextView keywordTV;
  SideBar sortSideBar;
  AppSortListViewAdapter listAdapter;
  
  public static Fragment1 newInstance(Context context, Bundle bundle) {
    mContext = context;
    Fragment1 newFragment = new Fragment1();
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
    View root = inflater.inflate(R.layout.fragment_1, container,false);
    Bundle bundle = getArguments();
    ArrayList<SortModel> sortModels = bundle.getParcelableArrayList("sort_app");
    
    appSortLV = (ListView) root.findViewById(R.id.frag_1_sort_lv);
    keywordTV  = (TextView) root.findViewById(R.id.frag_1_keyword);
    sortSideBar = (SideBar) root.findViewById(R.id.frag_1_sidrbar);
    
    sortSideBar.setTextView(keywordTV);
    listAdapter = new AppSortListViewAdapter(mContext, sortModels);
    appSortLV.setAdapter(listAdapter);
    
 // 设置右侧触摸监听
    sortSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

        @Override
        public void onTouchingLetterChanged(String s) {
            // 该字母首次出现的位置
            int position = listAdapter.getPositionForSection(s
                    .charAt(0));
            if (position != -1) {
              appSortLV.setSelection(position);
            }

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
