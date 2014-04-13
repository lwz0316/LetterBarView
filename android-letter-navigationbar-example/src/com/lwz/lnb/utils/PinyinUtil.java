package com.lwz.lnb.utils;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.content.ContentValues;

/**
 * 拼音工具类
 * @author lwz
 *
 */
public class PinyinUtil {
		// 暂存区。将转换过的[字符串，拼音/英文首字母]放入其中
	private HashMap<String, String> mIndexTemp = new HashMap<String, String>();	
		// 汉语拼音输出格式
	private HanyuPinyinOutputFormat outputFormat;	
	
	/**
	 * 
	 * 将字符串的第一个汉字转化为拼音
	 * @param values 
	 * 			为ContentValues对象
	 * @param getKey
	 * 			为values中欲要将首字转换为拼音的键(key)
	 * @param putKey
	 * 			将首字转换为拼音后将第一个字母放入values的键(key)
	 * @return String
	 * 			拼音/英文	
	 */
	public String getPingyin( ContentValues values , String getKey, String putKey){
		String str = values.getAsString(getKey);
		String result = mIndexTemp.get(str);
		if( result == null ){
			result = getPinyin(str);
			mIndexTemp.put(str, result);
		}
		values.put(putKey, String.valueOf(result.charAt(0)));
		return result;
		
	}
	
	/**
	 * 将字符串的第一个汉字转化为拼音
	 * <br>若该字符串的第一个字为英文字母，则原样返回
	 * @param str
	 * 			欲要将首字转换为拼音的字符串
	 * @return String
	 * 		拼音/英文
	 */
	public String getPinyin(String str){
		if( outputFormat == null ){
			outputFormat = new HanyuPinyinOutputFormat();	// 设置格式
			outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 没有音标
			outputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE); // 输出拼音为大写字母
		}
		// 去除中英文标点及空格
		str = str.trim().replaceAll("\\p{P}", "").trim();
		String[] strs = null;
		try {
			strs = PinyinHelper.toHanyuPinyinStringArray(str.charAt(0),outputFormat);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			;
		} 
		if( strs != null ){
			str = strs[0];
		}
		return str;
	}
}
