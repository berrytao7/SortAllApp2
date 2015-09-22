package com.berry.sortapp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.berry.sortapp.bean.AppInfo;
import com.berry.sortapp.utils.AppCollector;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * App管理者,用来获取设备上的所有app
 * 
 * @author Administrator
 * 
 */
public class AppManager {
    
  //它是通过解析< Intent-filter>标签得到有
  //< action android:name=”android.intent.action.MAIN”/>　　
  //< action android:name=”android.intent.category.LAUNCHER”/>
 //这样的app,所以得到的要比第二种方法少(第二种方法结果包含那种service、previder等app)
	public static ArrayList<AppInfo> getAppList(Context context) {
		
	  ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
		Intent intent =  new Intent(Intent.ACTION_MAIN, null); 
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> mApps =context.getPackageManager().queryIntentActivities(intent, 0); 
		ResolveInfo info = null;
		for (int i = 0; i < mApps.size(); i++) {
		  info = mApps.get(i);
		  String appLabel = info.loadLabel(context.getPackageManager()).toString();  
	      String packagename = info.activityInfo.packageName;  
	      String className = info.activityInfo.name;  
	      Drawable appIcon = info.activityInfo.loadIcon(context.getPackageManager());
	      boolean isUserApp = checkAppType(context,packagename)==1?true:false;
	      ComponentName cn = new ComponentName(packagename, className);
	      // 汉字转换成拼音
	      String byName = AppCollector.getPinYin(appLabel);
          AppInfo appInfo = new AppInfo(appLabel, appIcon,cn,isUserApp,byName);
          appList.add(appInfo);
		}
		return appList;
	}
	
	//通过解析AndroidManifest.xml的< application>标签中得到的，所以它能得到所有的app;未使用的原因是获取到的className,packageName有可能为空
	public static List<AppInfo> getAppList2(Context context) {
      List<PackageInfo> installedPackages = context.getPackageManager()
              .getInstalledPackages(0);
      
      List<AppInfo> appList = new ArrayList<AppInfo>();
      for (PackageInfo packageInfo : installedPackages) {
          ApplicationInfo applicationInfo = packageInfo.applicationInfo;
          if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
              // 系统app
          } else {
              String appName = applicationInfo.loadLabel(context.getPackageManager()).toString();
              String className = applicationInfo.className;
              String pkgName = applicationInfo.packageName;
              Drawable appIcon = applicationInfo.loadIcon(context.getPackageManager());
              ComponentName cn = new ComponentName(pkgName, className);
              boolean isUserApp = checkAppType(context,pkgName)==1?true:false;
              String byName = AppCollector.getPinYin(appName);
              AppInfo appInfo = new AppInfo(appName, appIcon,cn,isUserApp,byName);
              appList.add(appInfo);
          }
          
      }
      return appList;
  }
	
	//未知软件类型  
    public static final int UNKNOW_APP = 0;  
    //用户软件类型  
    public static final int USER_APP = 1;  
    //系统软件  
    public static final int SYSTEM_APP = 2;  
    //系统升级软件  
    public static final int SYSTEM_UPDATE_APP = 4;  
    //系统+升级软件  
    public static final int SYSTEM_REF_APP = SYSTEM_APP | SYSTEM_UPDATE_APP;  
	/** 
     * 检查app是否是系统rom集成的 
     * @param pname 
     * @return 
     */  
    private static int checkAppType(Context context,String pname) {  
        try {  
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(pname, 0);  
            // 是系统软件或者是系统软件更新  
            if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {  
                return SYSTEM_REF_APP;  
            } else {  
                return USER_APP;  
            }  
  
        } catch (NameNotFoundException e) {  
          return UNKNOW_APP; 
        }  
    }  
      
    /** 
     * 是否是系统软件或者是系统软件的更新软件 
     * @return 
     */  
    public static boolean isSystemApp(PackageInfo pInfo) {  
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);  
    }  
  
    public static boolean isSystemUpdateApp(PackageInfo pInfo) {  
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);  
    }  
  
    public boolean isUserApp(PackageInfo pInfo) {  
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));  
    }  
    
    
    
    private static final String SCHEME = "package";
    /**
    * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
    */
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    /**
    * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
    */
    private static final String APP_PKG_NAME_22 = "pkg";
    /**
    * InstalledAppDetails所在包名
    */
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    /**
    * InstalledAppDetails类名
    */
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
    /**
    * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
    * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
    *
    * @param context
    *
    * @param packageName
    * 应用程序的包名
    */
    public static void showInstalledAppDetails(Context context, String packageName) {
    Intent intent = new Intent();
    final int apiLevel = Build.VERSION.SDK_INT;
    if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts(SCHEME, packageName, null);
    intent.setData(uri);
    } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
    // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
    final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
    : APP_PKG_NAME_21);
    intent.setAction(Intent.ACTION_VIEW);
    intent.setClassName(APP_DETAILS_PACKAGE_NAME,
    APP_DETAILS_CLASS_NAME);
    intent.putExtra(appPkgName, packageName);
    }
    context.startActivity(intent);
    }
    
    /**
     * 获取近期执行任务(Home键)
     * 
     * @param maxNum 获取打开任务数量
     */
    
    public static ArrayList<AppInfo> getTaskList(Context context,int maxNum) {    
      ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);    
      PackageManager pm = context.getPackageManager();   
      ArrayList<AppInfo> apps = new ArrayList<AppInfo>();
      try {    
          List<RecentTaskInfo> list = am.getRecentTasks(maxNum, 1); 
          for (RecentTaskInfo ti : list) {    
              Intent intent = ti.baseIntent;    
              ResolveInfo info = pm.resolveActivity(intent, 0);    
              String appLabel = info.loadLabel(context.getPackageManager()).toString();  
              String packagename = info.activityInfo.packageName;  
              if (!packagename.contentEquals("com.jui.launcher3")) {
                String className = info.activityInfo.name;  
                Drawable appIcon = info.activityInfo.loadIcon(context.getPackageManager());
                ComponentName cn = new ComponentName(packagename, className);
                AppInfo appInfo = new AppInfo();  
                appInfo.setAppName(appLabel);
                appInfo.setCn(cn);
                appInfo.setAppIcon(appIcon);
                apps.add(appInfo);
              }
              
          }  
          return apps;  
      } catch (SecurityException se) {   
          return null;  
      }    
  } 
    
    private static final String NATIVE_DEFAUTL_URL = "http://m.yz.sm.cn/s?q=%s&from=wm882235";
    private static final String ABROAD_DEFAUTL_URL = "http://dh.atuyou.com/get?pid=9873&q=%s";
    private static final String DEFAULT_URL = "http://go.uc.cn/page/hao/business?source=yg";
    /**
     * 启动浏览器进行搜索
     * 
     * @param context
     * @param query
     */    
    public static void startWebSearch(Context context,String query){
      String searchUri="";
      if (query == null || query.trim().equals("")) {
        searchUri = DEFAULT_URL; 
      }else {
        SharedPreferences sp = context.getSharedPreferences("SearchSettings", Context.MODE_PRIVATE);
        String search_engine_url = "";
        boolean isAbroad = context.getResources().getBoolean(R.bool.channel_abroad);
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
            context.startActivity(webIntent);
            //add umeng analysis for search event
//            MobclickAgent.onEvent(this, UmengAnalysisConstants.EVENT_ID_SEARCH_BROWSER);
        } catch (ActivityNotFoundException e) {
             Intent AllWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUri));                 
             AllWebIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             try {
                 context.startActivity(AllWebIntent);
                 //add umeng analysis for search event
//                 MobclickAgent.onEvent(this, UmengAnalysisConstants.EVENT_ID_SEARCH_BROWSER);
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(context, context.getString(R.string.search_in_web_error), Toast.LENGTH_LONG).show();
            }
             
        }
    }
    
    /**
     * 启动游戏中心进行搜索
     * 
     * @param context
     * @param query
     */
    public static void startGameSearch(Context context,String query){
       //1.判断是否安装合适版本的app(未安装进行下载)
      
      //2.判断query是否为空(若为空进入首页)
      
      //3.所有都正常显示搜索结果
      //com.pada.gamecenter
      //com.pada.gamecenter.activity.SplashActivity
      Toast.makeText(context, "游戏中心搜索待定", Toast.LENGTH_LONG).show();
    }
    
    /**
     * 启动应用商店进行搜索
     * 
     * @param context
     * @param query
     */
    public static void startStoreSearch(Context context,String query){
      
      //1.判断是否安装合适版本的app(未安装进行下载)
      
      //2.判断query是否为空(若为空进入首页)
      
      //3.所有都正常显示搜索结果
      
      //com.pada.appstore
      //com.pada.appstore.activity.SplashActivity
      Toast.makeText(context, "应用中心搜索待定", Toast.LENGTH_LONG).show();
  }
}
