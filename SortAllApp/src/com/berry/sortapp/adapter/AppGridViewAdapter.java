package com.berry.sortapp.adapter;

import java.util.List;

import com.berry.sortapp.R;
import com.berry.sortapp.bean.AppInfo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AppGridViewAdapter extends BaseAdapter {
	
	private List<AppInfo> apps;
	private Context context;

	public AppGridViewAdapter(Context context, List<AppInfo> apps) {
		this.context = context;
		this.apps = apps;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return apps.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return apps.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.app_gridview_item, null);
			holder = new Holder();
			holder.appIv = (ImageView) convertView
					.findViewById(R.id.app_iv);
			holder.appNameTv = (TextView) convertView.findViewById(R.id.appName_tv);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final AppInfo appInfo = apps.get(position);
		holder.appIv.setBackground(appInfo.getAppIcon());
		holder.appNameTv.setText(appInfo.getAppName());
		convertView.setTag(R.id.gridview_item_app, appInfo);
		return convertView;
	}

	class Holder {
		ImageView appIv;
		TextView appNameTv;
	}
}
