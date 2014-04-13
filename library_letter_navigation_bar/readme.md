ANDROID LETTER NAVIGATION BAR
===
实现了类似于android手机中联系人查找的右边字母导航功能

Activity
---
	
		public class MainActivity extends LetterNavigationBarBaseActivity {
	
		private ListView mList;
		private List<ContentValues> mData;
		private List<String> mTitles;
		private CustomListAdapter mAdpt;
		private Map<String, Integer> mAlphaIndexer ;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			
			findView();
			initData();
		}
	
		private void findView() {
			mList = (ListView)findViewById(R.id.list);
		}
		
		private void initData(){
			mTitles = new ArrayList<String>();
			
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
		
		@Override
		public void setListSelection(String s) {
			int position = 0;
			// 当s是通配符时，则回到list的顶部
			if( CustomLetterNavigationBar.WILD_CARD .equals( s ) ){
				mList.setSelection(position);	// 设置list显示的位置
				return;
			}
			if( mAlphaIndexer.containsKey(s) ) {
				position = mAlphaIndexer.get(s);
				mList.setSelection(position);	// 设置list显示的位置
			} 
		}
	}


Fragment
---
	继承LetterNavigationBarBaseFragment，之后的代码与Activity类似

----