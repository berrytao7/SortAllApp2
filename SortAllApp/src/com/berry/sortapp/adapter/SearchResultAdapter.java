package com.berry.sortapp.adapter;

import java.util.ArrayList;

import com.berry.sortapp.R;
import com.berry.sortapp.bean.AppInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchResultAdapter extends BaseAdapter {

  private Context mContext;
  private ArrayList<AppInfo> mInfos;
  
  public SearchResultAdapter(Context context,ArrayList<AppInfo> infos){
    this.mContext = context;
    this.mInfos = infos;
  }
  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return mInfos.size();
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return mInfos.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView==null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_search_result_list_item, null);
      holder = new ViewHolder();
      holder.icon = (ImageView) convertView.findViewById(R.id.search_result_lv_item_icon);
      holder.title = (TextView) convertView.findViewById(R.id.search_result_lv_item_title);
      convertView.setTag(holder);
    }else {
      holder = (ViewHolder) convertView.getTag();
    }
    AppInfo info = mInfos.get(position);
    convertView.setTag(R.id.listview_item_app, info);
    
    holder.icon.setImageDrawable(info.getAppIcon());
    holder.title.setText(info.getAppName());
    return convertView;
  }
  
   
  
  public void updateData(ArrayList<AppInfo> infos){
    this.mInfos = infos;
    notifyDataSetChanged();
  }
  
  class ViewHolder{
    ImageView icon;
    TextView title;
  }

}
