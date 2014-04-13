package com.lwz.lnb.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.widget.ListView;

import com.lwz.lnb.adpt.CustomListAdapter;
import com.lwz.lnb.utils.PinyinUtil;
import com.lwz.lnb.widget.LetterBarView;
import com.lwz.lnb.widget.LetterBarView.OnLetterTouchListener;

public class MainActivity extends Activity {

	LetterBarView mLetterBar;
	private ListView mList;
	private List<ContentValues> mData;
	private List<String> mTitles;
	private CustomListAdapter mAdpt;
	private Map<String, Integer> mAlphaIndexer ;
	
	private String WILD_CARD = "#";
	private String[] letters = { WILD_CARD, "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
			"X", "Y", "Z" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
		initData();
	}

	private void findView() {
		mList = (ListView)findViewById(R.id.list);
		mLetterBar = (LetterBarView) findViewById(R.id.letter_bar);
		mLetterBar.setOnLetterTouchListener(new OnLetterTouchListener() {
			
			@Override
			public void onLetterTouch(String s) {
				setListSelection(s);
			}
		});
	}
	
	private void initData(){
		mTitles = new ArrayList<String>();
		mTitles.add("Facebook发布新版页面 旨在向广告模式转变");
		mTitles.add("移动领域：96%的恶意软件针对Android平台");
		mTitles.add("潘多拉Q4移动营收增长111% 每股亏0.09美元");
		mTitles.add("谷歌出游应用Field Trip推出iPhone版本");
		mTitles.add("传统实体店引进NFC技术 欲重塑购物方式");
		mTitles.add("新调查显示iOS正在蚕食安卓企业市场份额");
		mTitles.add("测试：苹果地图导航优于谷歌地图和Waze");
		mTitles.add("唯美版神庙逃亡上架 本周精品游戏汇总");
		mTitles.add("坐着躺着我们照样踢 试玩手指足球游戏");
		mTitles.add("分析师下调苹果股票评级 谷歌股价创新高");
		mTitles.add("图：Google员工公布他们所享受的奇葩福利");
		mTitles.add("增量依然惊人 但国内越狱比例呈下降趋势");
		mTitles.add("半天假期如何过 三八节妇女专属应用推荐");
		mTitles.add("杨元庆:有能力做自有手机系统但条件不成熟");
		mTitles.add("向微信学习 私密社交Path增加私信功能");
		mTitles.add("WP8设备增长迅猛 用户增长量接近Android");
		mTitles.add("谷歌街景地图在欧洲覆盖更多城市和国家");
		mTitles.add("调查称WP用户增速创新高 月增长首超0.2%");
		mTitles.add("移动互联网摩尔定律加速 从18个月缩短到6个月");
		mTitles.add("BaaS产品推荐：移动开发必备的后端云服务");
		mTitles.add("移动阅读的变革：智能、个性、社交将是标配");
		mTitles.add("移动广告遇标准瓶颈 未来靠手游广告推动");
		mTitles.add("几个靠谱的微信创业方向:媒体和CRM成先行者");
		mTitles.add("好大夫王航：移动医疗 医生资源是核心因素");
		mTitles.add("“吸金王”Puzzle & Dragons是如何炼成的");
		
		mData = new ArrayList<ContentValues>();
		for(int i=0; i<mTitles.size(); i++){
			ContentValues values = new ContentValues();
			values.put(CustomListAdapter.KEY_TITLE, mTitles.get(i));
			mData.add(values);
		}
		final PinyinUtil pinyinUtil = new PinyinUtil();
		// 将数据中的内容进行排序进行排序
		Collections.sort(mData, new Comparator<ContentValues>() {

			public int compare(ContentValues val1, ContentValues val2) {
				return pinyinUtil.getPingyin(val1, CustomListAdapter.KEY_TITLE, CustomListAdapter.KEY_ALPHA)
							.compareTo(pinyinUtil.getPingyin(val2, CustomListAdapter.KEY_TITLE, CustomListAdapter.KEY_ALPHA));
			}
		});
		setAdapter();
	}
	
	private void setAdapter(){
		mAdpt = new CustomListAdapter(this, mData);
		mAlphaIndexer = mAdpt.getAlphaIndexer();
		mList.setAdapter(mAdpt);
	}
	
	public void setListSelection(CharSequence s) {
		int position = 0;
		// 当s是通配符时，则回到list的顶部
		if( WILD_CARD.equals( s ) ){
			mList.setSelection(position);	// 设置list显示的位置
			return;
		}
		if( mAlphaIndexer.containsKey(s) ) {
			position = mAlphaIndexer.get(s);
			mList.setSelection(position);	// 设置list显示的位置
		} 
		
	}
	
	

}
