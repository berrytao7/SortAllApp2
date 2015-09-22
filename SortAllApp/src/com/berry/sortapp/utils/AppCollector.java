package com.berry.sortapp.utils;

import java.util.ArrayList;
import java.util.Locale;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.berry.sortapp.bean.AppInfo;
import com.berry.sortapp.bean.SortModel;


/**
 *  把开头字母相通的app收集起来
 * 
 * @author berry
 * 
 */
public class AppCollector {

	private static final String[] letters = { "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
			"T", "U", "V", "W", "X", "Y", "Z", "#" };

	/**
	 * 收集字母分类app
	 * @param appInfos
	 * @return
	 */
	public static ArrayList<SortModel> collect(ArrayList<AppInfo> appInfos) {
	  ArrayList<SortModel> sortModels = new ArrayList<SortModel>();
		for (int i = 0; i < letters.length; i++) {

		  ArrayList<AppInfo> apps = getAppInfos(letters[i], appInfos);
			if (apps.size() > 0) {
				SortModel sortModel = new SortModel();
				sortModel.setSortLetters(letters[i]);
				sortModel.setApps(apps);
				sortModels.add(sortModel);
			}
		}
		return sortModels;
	}

	/**
	 * 获取该首字母为letter的所有的app信息
	 * 
	 * @param letter
	 * @param appLetters
	 * @return
	 */
	private static ArrayList<AppInfo> getAppInfos(String letter,
	  ArrayList<AppInfo> appInfos) {
	  ArrayList<AppInfo> appInfoList = new ArrayList<AppInfo>();
		for (AppInfo appInfo : appInfos) {

			if (letter.equals("#") && appInfo.getByName().matches("[0-9](.)*")) {
				appInfoList.add(appInfo);
			} else {
			  String sortString = appInfo.getByName().substring(0, 1).toUpperCase(Locale.getDefault());

				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					if (letter.equals(sortString)) {
						appInfoList.add(appInfo);
					}
				}
			}
		}
		return appInfoList;
	}
	
	
	/**
	   * 获取字符串对应的拼音
	   * 
	   * @param string
	   * @return
	   */

	  public static String getPinYin(String src) {
	    char[] t1 = null;
	    t1 = src.toCharArray();
	    String[] t2 = new String[t1.length];
	    // 设置汉字拼音输出的格式
	    HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
	    t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	    t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	    t3.setVCharType(HanyuPinyinVCharType.WITH_V);
	    String t4 = "";
	    int t0 = t1.length;
	    try {
	      for (int i = 0; i < t0; i++) {
	        // 判断能否为汉字字符
	        if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
	          // 将汉字的几种全拼都存到t2数组中
	          t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
	          // 取出该汉字全拼的第一种读音并连接到字符串t4后
	          t4 += t2[0] + " ";
	        } else {
	          // 如果不是汉字字符，间接取出字符并连接到字符串t4后
	          t4 += Character.toString(t1[i]);
	        }
	      }
	    } catch (BadHanyuPinyinOutputFormatCombination e) {
	      e.printStackTrace();
	    }
	    return t4;
	  }
}
