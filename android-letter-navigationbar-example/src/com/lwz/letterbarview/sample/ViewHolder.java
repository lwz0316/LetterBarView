package com.lwz.letterbarview.sample;

import android.util.SparseArray;
import android.view.View;

/**
 * 适配器中的 ViewHolder 类
 * 
 * @author lwz
 * @link 参考：http://www.piwai.info/android-adapter-good-practices/#Update
 *
 */
public class ViewHolder {
	
	@SuppressWarnings("unchecked")
	public static <T extends View> T getView(View convertView, int id) {
		
		SparseArray<View> holder = (SparseArray<View>) convertView.getTag();
		if( holder == null ) {
			holder = new SparseArray<View>();
			convertView.setTag(holder);
		}
		
		View view = holder.get(id);
		if( view == null ) {
			view = convertView.findViewById(id);
			holder.put(id, view);
		}
		return (T)view;
	}
}
