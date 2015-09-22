package com.berry.sortapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.berry.sortapp.adapter.SearchResultAdapter;
import com.berry.sortapp.adapter.ViewPagerAdapter;
import com.berry.sortapp.bean.AppInfo;
import com.berry.sortapp.bean.SortModel;
import com.berry.sortapp.fragment.Fragment1;
import com.berry.sortapp.fragment.Fragment2;
import com.berry.sortapp.fragment.Fragment3;
import com.berry.sortapp.utils.AppCollector;
import com.berry.sortapp.utils.PinyinComparator;
import com.berry.sortapp.views.SearchEditText;
import com.berry.sortapp.views.SlidingTab;
import com.jui.material.widgets.ProgressDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends FragmentActivity {
  private SearchEditText search_edit;
  private SlidingTab search_tab;
  private ViewPager search_paper;
  private ArrayList<Fragment> fragmentsList;
  private View searchResult,broswerView,gameView,storeView;
  private ListView searchResultLV;
  private SearchResultAdapter resultAdapter;
  private ArrayList<AppInfo> infos;
  
  //根据拼音来排列ListView里面的数据类
  private PinyinComparator pinyinComparator;
  private ArrayList<SortModel> sortModels;
  @Override
  protected void onCreate(Bundle arg0) {
    super.onCreate(arg0);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_search);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    new MyTask().execute("");
  }
  
  @SuppressLint("ResourceAsColor")
  private void initView(){
    search_edit = (SearchEditText) findViewById(R.id.search_edit);
    search_tab = (SlidingTab) findViewById(R.id.search_layout_tab);
    searchResult = findViewById(R.id.search_layout_result);
    broswerView = findViewById(R.id.search_result_item_broswer);
    gameView = findViewById(R.id.search_result_item_game);
    storeView = findViewById(R.id.search_result_item_store);
    searchResultLV = (ListView) findViewById(R.id.search_result_lv);
    infos = new ArrayList<AppInfo>();
    resultAdapter = new SearchResultAdapter(this, infos);
    searchResultLV.setAdapter(resultAdapter);
    
    search_tab.setIndicatorColorResource(R.color.search_selected_tab_color);
    search_tab.setTabSelectTextColor(R.color.search_selected_tab_color);
    search_tab.setTextColorResource(R.color.search_unselected_tab_color);
    search_tab.setDividerColorResource(R.color.search_tab_divider_color);
    
    //重写软键盘
    search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT ||
            actionId == EditorInfo.IME_ACTION_DONE || 
            actionId == EditorInfo.IME_ACTION_GO || 
            actionId == EditorInfo.IME_ACTION_SEARCH) {
          /*隐藏软键盘*/  
          InputMethodManager imm = (InputMethodManager) v  
                  .getContext().getSystemService(  
                          Context.INPUT_METHOD_SERVICE);  
          if (imm.isActive()) {  
              imm.hideSoftInputFromWindow(  
                      v.getApplicationWindowToken(), 0);  
          }
          if (search_edit.getText()==null) {
            AppManager.startWebSearch(SearchActivity.this,null);
          }else {
            AppManager.startWebSearch(SearchActivity.this,search_edit.getText().toString().trim());
          }
          
          return true;
        }
        return false;
      }
    });
    
 // 根据输入框输入值的改变来过滤搜索
    search_edit.addTextChangedListener(new TextWatcher() {
      
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
        changeSearchResult(s.toString());
        
      }
      
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub
        if (s!=null&& searchResult.getVisibility()==View.GONE) {
          searchResult.setVisibility(View.VISIBLE);
        }
      }
      
      @Override
      public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
        if ((s==null || s.toString().trim().isEmpty())&& searchResult.getVisibility()==View.VISIBLE) {
          searchResult.setVisibility(View.GONE);
        }
      }
    });
    
    
    broswerView.setOnClickListener(new ResultClickListener(1));
    gameView.setOnClickListener(new ResultClickListener(2));
    storeView.setOnClickListener(new ResultClickListener(3));
    searchResultLV.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view!=null) {
          AppInfo info = (AppInfo) view.getTag(R.id.listview_item_app);
          Intent intent = new Intent();
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.setComponent(info.getCn());
          startActivity(intent);
        }
        
      }
      
    });
  }
  
 private void initViewPager(){
    
    search_paper = (ViewPager) findViewById(R.id.search_paper);
    
    fragmentsList = new ArrayList<Fragment>();
    Bundle bundle = new Bundle();
    bundle.putParcelableArrayList("sort_app", sortModels);
    Fragment1 fragment0 = Fragment1.newInstance(SearchActivity.this, bundle);
    bundle = new Bundle();
    Fragment2 fragment1 = Fragment2.newInstance(SearchActivity.this, bundle);
    Fragment3 fragment2 = Fragment3.newInstance(SearchActivity.this, bundle);
    fragmentsList.add(fragment0);
    fragmentsList.add(fragment1);
    fragmentsList.add(fragment2);
    
    search_paper.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), SearchActivity.this, fragmentsList));
    search_paper.setCurrentItem(0);
    
    search_tab.setViewPager(search_paper);
    search_tab.setOnPageChangeListener(new MyOnPageChangeListener());
  }
  
  private void initData() {
    // TODO Auto-generated method stub
    pinyinComparator = new PinyinComparator();

    sortModels = AppCollector.collect(AppManager.getAppList(this));
    // 根据a-z进行排序源数据
    Collections.sort(sortModels, pinyinComparator);
}
  
  class MyTask extends AsyncTask<String, Void, Void>{
    ProgressDialog dialog;
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      dialog = new ProgressDialog(SearchActivity.this, null, "正在加载");
      dialog.show();
    }
    @Override
    protected Void doInBackground(String... params) {
      initData();
      return null;
    }
    @Override
    protected void onPostExecute(Void result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      initView();
      initViewPager();
      dialog.dismiss();
    }
    
  }
  
  class ResultClickListener implements View.OnClickListener{
    int key;
    public ResultClickListener(int key){
      this.key = key;
    }
    @Override
    public void onClick(View v) {
      switch (this.key) {
        case 1:
          //broswser
          if (search_edit.getText()==null) {
            AppManager.startWebSearch(SearchActivity.this, null);
          }else {
            AppManager.startWebSearch(SearchActivity.this, search_edit.getText().toString());
          }
          break;
        case 2:
          //game
          if (search_edit.getText()==null) {
            AppManager.startGameSearch(SearchActivity.this, null);
          }else {
            AppManager.startGameSearch(SearchActivity.this, search_edit.getText().toString());
          }
          break;
        case 3:
          //appstore
          if (search_edit.getText()==null) {
            AppManager.startStoreSearch(SearchActivity.this, null);
          }else {
            AppManager.startStoreSearch(SearchActivity.this, search_edit.getText().toString());
          }
          break;

        default:
          break;
      }
      
    }
    
  }
  
  
  class TabOnClickListener implements View.OnClickListener {
    private int index = 0;

    public TabOnClickListener(int i) {
        index = i;
    }

    @Override
    public void onClick(View v) {
        search_paper.setCurrentItem(index);
    }
};

  class MyOnPageChangeListener implements OnPageChangeListener {

    @Override
    public void onPageSelected(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }
}

  private void changeSearchResult(String s){
  infos = new ArrayList<AppInfo>();
  String input = s.trim();
  if (!input.isEmpty()) {
    for (SortModel m : sortModels) {
      List<AppInfo> apps = m.getApps();
      for (AppInfo p : apps) {
        String appName = p.getAppName();
        String byName = p.getByName();
        if (appName.contains(input)
            ||byName.contains(input) 
            || byName.toLowerCase(Locale.getDefault()).contains(input.toLowerCase(Locale.getDefault()))) {
          infos.add(p);
        }
      }
    }
  }
  resultAdapter.updateData(infos);
  }

  @Override
  public void onBackPressed() {
    if (searchResult.getVisibility()==View.VISIBLE) {
      searchResult.setVisibility(View.GONE);
    }else {
      super.onBackPressed();
    }
  }

  
  

}
