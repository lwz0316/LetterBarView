package com.lwz.letterbarview.sample;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lwz.letterbarview.lib.LetterBarView;
import com.lwz.letterbarview.lib.LetterBarView.OnLetterSelectListener;

public class MainActivity extends Activity {

	final String[] CONTENTS = {
		"2013年美国互联网广告收入首次超过广播电视广告"	,
		"高德地图新版在Android端推出“离线导航”，同时在杭州试推“实时公交”功能",
		"“小区”概念兴起，专为某个生活圈服务的创业公司开始涌现",
		"登陆KickStarter仅3天，第一款真正面向大众的3D打印机项目Micro已经募资220万美元",
		"微信国际版WeChat在美国市场受挫？称目前不会将精力放在变现上",
		"每周一氪（4月11日）：不是体育节目，也可以“更高更快更强”的嘛",
		"【 I Got It!】在线酒店预订的新突破口在钟点房",
		"对高德，阿里为何非得全资收购？",
		"亚马逊或将于今年6月推出智能手机，支持裸眼3D效果",
		"8点1氪：高德软件正式与阿里巴巴集团达成并购协议",
		"没有团队，没有投资，只有一个想法的老陈，该如何他实现打造互联网汽车的弘愿？",
		"Google翻译新增“改善翻译”按钮，让任何人都能为Google翻译质量的提高做贡献",
		"还没体验过Chromebook？别急，Chrome Tablet就要来了",
		"Amazon将在今年6月底正式推出带4个摄像头的智能手机，9月发售",
		"IAC集团宣布回购Tinder 11%股权，使得这款约会应用估值达5亿美元",
		"【WISE Talk -- 科技改变音乐总结篇】音乐和科技已经发生的关系比想象中浅薄，新硬件可能是接下来的方向",
		"把自己变成“章鱼船长”Davy Jones，特效拍摄应用Epica上线不到一周，冲入多个国家应用榜单前20",
		"幕后大起底：OpenSSL“Heartbleed”漏洞消息发布前的惊魂72小时",
		"上市在即的Box推出开源计划“Box Open Source”，多项内部技术通过GitHub与开发者共享",
		"聚美优品提交IPO申请书，拟最多融资4亿美元，将用于营销及品牌建设",
		"微信广告自助平台几周后将正式上线，经认证的公众账号及广告主可申请接入",
		"苹果Mac的“情景”营销",
		"速成工具不能让人变艺术，但至少不会表达无能，WeComics又一个漫画自制工具",
		"当乐器也开始可穿戴：Mi.Mu手套让音乐家打破与乐器的隔阂",
		"采用机器学习和算法处理，iPIN利用网上的过剩信息揭露公司的薪资，升职空间等用人信息#36氪开放日广州站#",
		"网脊运维通推在线SaaS 运维平台，让企业享受网银级的安全运维服务#36氪开放日广州站#"
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ListView listView = (ListView) findViewById(R.id.listview);
		
		ArrayList<News> data = new ArrayList<News>();
		for( int i=0; i<CONTENTS.length; i++ ) {
			data.add(new News(CONTENTS[i]));
		}
		
		final AlphaAdapter<News> adapter = new AlphaAdapter<News>(this, data) {

			@Override
			public void bindOriginData(int position, View convertView, News itemData) {
				TextView titleText = ViewHolder.getView(convertView, R.id.news_title);
				titleText.setText(itemData.getTitle());
			}
		};
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AlphaAdapter.OnItemClickWrapperListener<News>() {

			@Override
			public void onItemClick(News itemData) {
				Toast.makeText(MainActivity.this, itemData.getTitle(), Toast.LENGTH_SHORT).show();
			}
			
		});
		
		LetterBarView letterBar = (LetterBarView) findViewById(R.id.letter_bar);
		letterBar.setOnLetterSelectListener(new OnLetterSelectListener() {
			
			@Override
			public void onLetterSelect(String s) {
				if(s.equalsIgnoreCase("#")) {
					listView.setSelection(0);
				} else {
					if( adapter.containsAlpha(s) ) {
						listView.setSelection( adapter.getAlphaPosition(s) );
					}
				}
			}
		});
	}
	
	
}
