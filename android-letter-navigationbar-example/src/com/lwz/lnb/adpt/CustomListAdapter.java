package com.lwz.lnb.adpt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lwz.lnb.example.R;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomListAdapter extends BaseAdapter {

	// 其中的数据包括标题，以及标题中第一个汉字的拼音/英文的首字母
	// key分别为 title、alpha
	private List<ContentValues> mData;
	private LayoutInflater mInflater;
	// 存放存在的汉语拼音/英文首字母和与之对应的列表位置
	private Map<String, Integer> mAlphaIndexer;
	private int mDataCount;

	public static final String KEY_TITLE = "title";
	public static final String KEY_ALPHA = "alpha";

	public CustomListAdapter(Context context, List<ContentValues> data) {
		mInflater = LayoutInflater.from(context);
		mData = data;
		mDataCount = data.size();
		mAlphaIndexer = new HashMap<String, Integer>();
		String lastAlpha = "";
		for (int i = 0; i < mDataCount; i++) {
			String currentAlpha = data.get(i).getAsString(KEY_ALPHA);
			if (!currentAlpha.equals(lastAlpha)) {
				mAlphaIndexer.put(currentAlpha, i);
			}
			lastAlpha = currentAlpha;
		}
	}

	/**
	 * 获得包含对应的字母在list中的位置信息的Map集合
	 * 
	 * @return Map<String, Integer> ， String 为字母，Integer 为对应的位置
	 */
	public Map<String, Integer> getAlphaIndexer() {
		return mAlphaIndexer;
	}

	public int getCount() {
		return mDataCount;
	}

	public Object getItem(int position) {
		return mData.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list, null);
			holder = new ViewHolder();
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ContentValues cv = mData.get(position);
		holder.title.setText(cv.getAsString(KEY_TITLE));
		String currentAlpha = mData.get(position).getAsString(KEY_ALPHA);
		String previewAlpha = (position - 1) >= 0 ? mData.get(position - 1)
				.getAsString(KEY_ALPHA) : " ";
		if (!previewAlpha.equals(currentAlpha)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentAlpha);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView alpha;
		TextView title;
	}
}
