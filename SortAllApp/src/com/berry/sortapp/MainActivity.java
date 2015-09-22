package com.berry.sortapp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.berry.sortapp.adapter.AppSortListViewAdapter;
import com.berry.sortapp.bean.AppInfo;
import com.berry.sortapp.bean.SortModel;
import com.berry.sortapp.utils.AppCollector;
import com.berry.sortapp.utils.PinyinComparator;
import com.berry.sortapp.views.SearchEditText;
import com.berry.sortapp.views.SideBar;
import com.berry.sortapp.views.SideBar.OnTouchingLetterChangedListener;
import com.jui.material.widgets.ProgressDialog;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    // 搜索框
    private SearchEditText searchEt;
    // 排序listview
    private ListView appSortLv;

    // 中间字母提示
    private TextView dialog;

    // 右边字母
    private SideBar sideBar;


    // 根据拼音来排列ListView里面的数据类
    private PinyinComparator pinyinComparator;

    private AppSortListViewAdapter appSortListViewAdapter;

    private List<SortModel> sortModels;
    
    View broswerView,gameView,appstoreView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

//        this.initData();
//        this.initView();
        new MyTask().execute("");
    }

    class MyTask extends AsyncTask<String, Void, Void>{
      ProgressDialog dialog;
      @Override
      protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(MainActivity.this, null, "正在加载");
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
        dialog.dismiss();
      }
      
    }
    private void initData() {
        // TODO Auto-generated method stub
        pinyinComparator = new PinyinComparator();

        sortModels = AppCollector.collect(AppManager.getAppList(this));
        // 根据a-z进行排序源数据
        Collections.sort(sortModels, pinyinComparator);
    }

    private void initView() {
        searchEt = (SearchEditText) findViewById(R.id.search_et);
        appSortLv = (ListView) findViewById(R.id.app_sort_lv);

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        appSortListViewAdapter = new AppSortListViewAdapter(this, sortModels);
        appSortLv.setAdapter(appSortListViewAdapter);
        
        broswerView = findViewById(R.id.search_layout_broswer);
        gameView = findViewById(R.id.search_layout_game);
        appstoreView = findViewById(R.id.search_layout_appstore);
        
        broswerView.setOnClickListener(new OnClickListener() {
          
          @Override
          public void onClick(View v) {
            // TODO Auto-generated method stub
            if (searchEt.getText()==null) {
              startWebSearch(null);
            }else {
              startWebSearch(searchEt.getText().toString().trim());
            }
          }
        });
        
        gameView.setOnClickListener(new OnClickListener() {
          
          @Override
          public void onClick(View v) {
            // TODO Auto-generated method stub
            if (searchEt.getText()==null) {
              startGameSearch(null);
            }else {
              startGameSearch(searchEt.getText().toString().trim());
            }
          }
        });
        
        appstoreView.setOnClickListener(new OnClickListener() {
          
          @Override
          public void onClick(View v) {
            // TODO Auto-generated method stub
            if (searchEt.getText()==null) {
              startStoreSearch(null);
            }else {
              startStoreSearch(searchEt.getText().toString().trim());
            }
          }
        });
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = appSortListViewAdapter.getPositionForSection(s
                        .charAt(0));
                if (position != -1) {
                    appSortLv.setSelection(position);
                }

            }
        });


        // 根据输入框输入值的改变来过滤搜索
        searchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          
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
              if (searchEt.getText()==null) {
                startWebSearch(null);
              }else {
                startWebSearch(searchEt.getText().toString().trim());
              }
              
              return true;
            }
            return false;
          }
        });
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * 
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = sortModels;
        } else {
          String input = filterStr.toString().trim();
            filterDateList.clear();
            for (SortModel sortModel : sortModels) {
                List<AppInfo> newAppInfos = new ArrayList<AppInfo>();
                // 获取该sortmodel中的app列表
                List<AppInfo> apps = sortModel.getApps();
                for (AppInfo appInfo : apps) {
                    String appName = appInfo.getAppName();
                    String byName = appInfo.getByName();
                    if (appName.contains(input)
                        ||byName.contains(input) 
                        || byName.toLowerCase(Locale.getDefault()).contains(input.toLowerCase(Locale.getDefault()))) {
                      newAppInfos.add(appInfo);
                    }
                }
                if (newAppInfos.size() > 0) {
                    SortModel newSortModel = new SortModel();
                    newSortModel.setSortLetters(sortModel.getSortLetters());
                    newSortModel.setApps(newAppInfos);
                    
                    filterDateList.add(newSortModel);
                }
            }
        }
        appSortListViewAdapter.updateListView(filterDateList);
    }



    private static final String NATIVE_DEFAUTL_URL = "http://m.yz.sm.cn/s?q=%s&from=wm882235";
    private static final String ABROAD_DEFAUTL_URL = "http://dh.atuyou.com/get?pid=9873&q=%s";
    private static final String DEFAULT_URL = "http://go.uc.cn/page/hao/business?source=yg";
    
    protected void startWebSearch(String query){
      String searchUri="";
      if (query == null || query.trim().equals("")) {
        searchUri = DEFAULT_URL; 
      }else {
        SharedPreferences sp = this.getSharedPreferences("SearchSettings", Context.MODE_PRIVATE);
        String search_engine_url = "";
        boolean isAbroad = getResources().getBoolean(R.bool.channel_abroad);
        if(!isAbroad){
            search_engine_url = sp.getString("search_engine_url", NATIVE_DEFAUTL_URL);
        }else{
            search_engine_url = sp.getString("search_engine_url", ABROAD_DEFAUTL_URL);
        }
        try {
            String queryStr =URLEncoder.encode(query, "UTF-8");
            searchUri = String.format(search_engine_url, queryStr);
        } catch (UnsupportedEncodingException e) {
             searchUri = DEFAULT_URL;
        }
      }
        
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUri));
        webIntent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        try {
            startActivity(webIntent);
            //add umeng analysis for search event
//            MobclickAgent.onEvent(this, UmengAnalysisConstants.EVENT_ID_SEARCH_BROWSER);
        } catch (ActivityNotFoundException e) {
             Intent AllWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUri));                 
             AllWebIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             try {
                 startActivity(AllWebIntent);
                 //add umeng analysis for search event
//                 MobclickAgent.onEvent(this, UmengAnalysisConstants.EVENT_ID_SEARCH_BROWSER);
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(MainActivity.this, getString(R.string.search_in_web_error), Toast.LENGTH_LONG).show();
            }
             
        }
    }
    
    protected void startGameSearch(String query){
       //1.判断是否安装合适版本的app(未安装进行下载)
      
      //2.判断query是否为空(若为空进入首页)
      
      //3.所有都正常显示搜索结果
      
      Toast.makeText(MainActivity.this, "游戏中心搜索待定", Toast.LENGTH_LONG).show();
    }
    
    protected void startStoreSearch(String query){
      
      //1.判断是否安装合适版本的app(未安装进行下载)
      
      //2.判断query是否为空(若为空进入首页)
      
      //3.所有都正常显示搜索结果
      Toast.makeText(MainActivity.this, "应用中心搜索待定", Toast.LENGTH_LONG).show();
  }
    
    
    


}
