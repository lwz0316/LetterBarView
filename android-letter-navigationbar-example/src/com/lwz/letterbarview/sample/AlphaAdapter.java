package com.lwz.letterbarview.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 字母导航适配器。根据数据源按字母分类，每一个分类间会有该类的字母来分隔。
 * 
 * <p><b>NOTE:</b> 数据源会自动重新按字母排序
 * 
 * <p> 由于该适配器的实现会同样对字母分隔 Item 进行点击事件的监听，故若要使点击事件只针对源数据，则应使用 {@link OnItemClickWrapperListener}. 例如
 * <pre>
listView.setOnItemClickListener(new AlphaAdapter.OnItemClickWrapperListener&ltNews&gt() {

	public void onItemClick(News itemData) {
		// TODO
	}
			
});
</pre>
 * @author lwz
 *
 * @param <T> 数据源类型
 */
public abstract class AlphaAdapter<T extends AlphaWrapper> extends BaseAdapter {
	
	private static final int VIEW_TYPE_ALPHA = 0;
	private static final int VIEW_TYPE_COMMON = 1;
	
	private ArrayList<T> mOriginData;
	private ArrayList<Object> mMergeData;
	private LayoutInflater mLayoutInflater;
	private HashMap<String, Integer> mAlphaMap;

	public AlphaAdapter(Context context, ArrayList<T> data) {
		mLayoutInflater = LayoutInflater.from(context);
		mOriginData = data;
		mergeData();
	}
	
	private void mergeData() {
		Collections.sort(mOriginData, new Comparator<T>() {

			@Override
			public int compare(T lhs, T rhs) {
				return lhs.getAlpha().compareTo(rhs.getAlpha());
			}
			
		});
		mMergeData = new ArrayList<Object>();
		mAlphaMap = new HashMap<String, Integer>();
		final int SIZE = mOriginData.size();
		String lastAlpha = "";
		for( int i=0; i<SIZE; i++ ) {
			T item = mOriginData.get(i);
			if( !lastAlpha.equals(item.getAlpha().substring(0, 1))) {
				lastAlpha = item.getAlpha().substring(0, 1);
				mAlphaMap.put(lastAlpha, mMergeData.size());
				mMergeData.add(lastAlpha);
			}
			mMergeData.add(item);
		}
	}
	
	public ArrayList<Object> getMergeData() {
		return mMergeData;
	}
	
	public boolean containsAlpha(String alpha) {
		return mAlphaMap.containsKey(alpha);
	}
	
	public int getAlphaPosition(String alpha) {
		return mAlphaMap.get(alpha);
	}

	@Override
	public int getCount() {
		return mMergeData.size();
	}

	@Override
	public Object getItem(int position) {
		return mMergeData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		if( mMergeData.get(position) instanceof AlphaWrapper ) {
			return VIEW_TYPE_COMMON;
		}
		return VIEW_TYPE_ALPHA;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( convertView == null ) {
			switch( getItemViewType(position) ) {
				case VIEW_TYPE_ALPHA :
					convertView = mLayoutInflater.inflate(R.layout.item_list_alpha, parent, false);
					break;
				default :
					convertView = mLayoutInflater.inflate(R.layout.item_list_news, parent, false);
			}
		}
		
		bindData(position, convertView);
		return convertView;
	}

	@SuppressWarnings("unchecked")
	private void bindData(int position, View convertView) {
		switch (getItemViewType(position)) {
			case VIEW_TYPE_ALPHA:
				TextView alphaText = ViewHolder.getView(convertView, R.id.alpha);
				alphaText.setText(mMergeData.get(position).toString());
				break;
			default:
				bindOriginData(position, convertView, (T)mMergeData.get(position));
		}
	}
	
	public abstract void bindOriginData(int position, View convertView, T itemData);
	
	/**
	 * 避免 点击字母条响应点击事件。点击事件只针对源数据
	 * @author lwz
	 *
	 * @param <T>
	 */
	public static abstract class OnItemClickWrapperListener<T extends AlphaWrapper> implements AdapterView.OnItemClickListener {

		@SuppressWarnings("unchecked")
		@Override
		public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AlphaAdapter<?> adapter = (AlphaAdapter<?>)parent.getAdapter();
			if( adapter.getItemViewType(position) == VIEW_TYPE_ALPHA ) {
				return;
			}
			onItemClick((T)adapter.getMergeData().get(position));
		}
		
		public abstract void onItemClick(T itemData);
		
	}

}
