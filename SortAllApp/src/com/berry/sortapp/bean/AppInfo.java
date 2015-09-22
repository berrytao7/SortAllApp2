package com.berry.sortapp.bean;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

/**
 * 保存app信息模型
 * 
 * @author berry
 * 
 */
public class AppInfo {

  private Drawable appIcon;
  private String appName;
  private ComponentName cn;
  private boolean isUserApp;
  private String byName;//拼音名称
  
  public AppInfo() {
  }

  public AppInfo(String appName, Drawable appIcon, ComponentName cn,boolean isUserApp,String byName) {
    super();
    this.appName = appName;
    this.appIcon = appIcon;
    this.cn = cn;
    this.isUserApp = isUserApp;
    this.byName = byName;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public Drawable getAppIcon() {
    return appIcon;
  }

  public void setAppIcon(Drawable appIcon) {
    this.appIcon = appIcon;
  }

  public ComponentName getCn() {
    return cn;
  }

  public void setCn(ComponentName cn) {
    this.cn = cn;
  }

  public boolean isUserApp() {
    return isUserApp;
  }

  public void setUserApp(boolean isUserApp) {
    this.isUserApp = isUserApp;
  }

  public String getByName() {
    return byName;
  }

  public void setByName(String byName) {
    this.byName = byName;
  }

 

}
