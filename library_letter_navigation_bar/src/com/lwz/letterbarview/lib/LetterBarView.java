package com.lwz.letterbarview.lib;

import com.lwz.lnb.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 字母导航条
 * 
 * <p>
 * 可用于需要按照字母排序的应用中. <br>
 * 若排序的 item 首字为中文时，需要将每个 item 的首个汉字转化为拼音然后进行排序.<br>将汉字转换为拼音可用 pinyin4j.jar 来转换
 * </p>
 * <p>
 * 当点击导航条的字母时，会触发监听事件,若要监听，则可以使用 {@link #setOnLetterSelectListener(OnLetterSelectListener)} 来设置
 * </p>
 * 
 */
public class LetterBarView extends View {
	
	/**
	 * 选中字母监听
	 */
	public interface OnLetterSelectListener {
		/**
		 * @param s 被选中的字母
		 */
		public void onLetterSelect(String s);
	}

	private OnLetterSelectListener mOnLetterSelectListener;

	private String[] mLetters = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private int mCount = mLetters.length;
	private int mLastIndex = -1;

	// 是否绘制字母导航栏背景。 当用户点击时绘制
	private boolean mLetterBarFocus = false;
	private float mLetterBarWidth;
	
	// 导航栏在 x 轴的位移
	private float mLetterBarXOffset;
	
	private Paint mPaint;
	private float mLetterSpaceHeight;
	
	private Drawable mLetterBarBackground;
	private ColorStateList mLetterBarTexColorStateList;
	private Drawable mOverlayBackground;
	
	private int mOverlayTextColor = Color.WHITE;
	private float mOverlayTextSize = 120;
	
	public LetterBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public LetterBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public LetterBarView(Context context) {
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs) {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		setBackgroundColor(Color.TRANSPARENT);
		
		setLetterBarBackgrond(Color.parseColor("#88000000"), Color.parseColor("#66000000"));
		setLetterBarTextColor(Color.BLUE, Color.WHITE);
		setOverlayBackgroundColor(Color.parseColor("#88000000"));
		
		if( attrs != null ) {
			TypedArray a = getResources().obtainAttributes(attrs, R.styleable.LetterBar);
			
			if( a.hasValue(R.styleable.LetterBar_lbLetterBarBackground) ) {
				mLetterBarBackground = a.getDrawable(R.styleable.LetterBar_lbLetterBarBackground);
			}
			if( a.hasValue(R.styleable.LetterBar_lbLetterBarTextColor) ) {
				mLetterBarTexColorStateList = a.getColorStateList(R.styleable.LetterBar_lbLetterBarTextColor);
			}
			if( a.hasValue(R.styleable.LetterBar_lbOverlayBackground) ) {
				mOverlayBackground = a.getDrawable(R.styleable.LetterBar_lbOverlayBackground);
			}
			mOverlayTextColor = a.getColor(R.styleable.LetterBar_lbOverlayTextColor, mOverlayTextColor);
			mOverlayTextSize = a.getDimension(R.styleable.LetterBar_lbOverlayTextSize, mOverlayTextSize);
			
			a.recycle();
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if( changed ) {
			invalidate();
		}
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	protected void drawableStateChanged() {
		if( mLetterBarBackground != null && mLetterBarBackground.isStateful() ) {
			mLetterBarBackground.setState(getDrawableState());
		}
		super.drawableStateChanged();
	}
	
	@Override
	protected boolean verifyDrawable(Drawable who) {
		return who == mLetterBarBackground || super.verifyDrawable(who);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		
		final float y = event.getY();
		final int currentIndex = (int) (y / getHeight() * mCount);

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				if( mLetterBarXOffset > event.getX()  ) {
					invalidate();
					return false;
				}
				mLetterBarFocus = true;
			case MotionEvent.ACTION_MOVE:
				if( !mLetterBarFocus ) 
					return false;
				
				if (mLastIndex != currentIndex && currentIndex >= 0 && currentIndex < mCount) {
					mLastIndex = currentIndex;
					final String letter = mLetters[currentIndex];
					if (mOnLetterSelectListener != null) {
						mOnLetterSelectListener.onLetterSelect(letter);
					}
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				mLetterBarFocus = false;
				invalidate();
				dismissLetterOverlay();
				break;
			}
		return true;
	}
	
	/**
	 * 消失字母弹出层
	 */
	private void dismissLetterOverlay() {
		postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mLastIndex = -1;
				invalidate();
			}
		}, 500);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		calculateLetterBarParams();
		
		drawLetterBarBackground(canvas);
		drawLetters(canvas, mLetterBarWidth, mLetterSpaceHeight);
		drawOverlayLetter(canvas, mLastIndex < 0 ? null : mLetters[mLastIndex]);
	}
	
	/**
	 * 计算字母导航条的参数
	 * <p> 根据字母的高度来计算 导航条的宽度 和 其在 X 轴的位置
	 * @param letterSpaceHeight 根据字母的高度
	 */
	private void calculateLetterBarParams() {
		calculateLetterSpaceHeight();
		if( mLetterBarWidth <= 0 ) {
			mLetterBarWidth = mLetterSpaceHeight;
		}
		mLetterSpaceHeight = Math.min(mLetterBarWidth, mLetterSpaceHeight);
		// 计算 导航栏在 x 轴的位移
		mLetterBarXOffset = getWidth() - mLetterBarWidth;
	}
	
	/**
	 * 计算每个字母所占空间的高度
	 */
	private void calculateLetterSpaceHeight() {
		if( mLetterSpaceHeight <= 0f ) {
			mLetterSpaceHeight = (getHeight() - getPaddingTop() - getPaddingBottom()) / (float)mCount;
		}
	}
	
	/**
	 * 绘制导航栏背景
	 * @param canvas 
	 * @param width	该 View 的宽度
	 * @param height 该 View 的高度
	 */
	private void drawLetterBarBackground(Canvas canvas) {
		// 若不设置，则 会导致 StateListDrawable 无效果
		setFocusable(mLetterBarFocus);
		setSelected(mLetterBarFocus);
		
		canvas.save();
		canvas.translate(mLetterBarXOffset, 0);
		mLetterBarBackground.setBounds(0, 0, (int)FloatMath.ceil(mLetterBarWidth), getHeight());
		mLetterBarBackground.draw(canvas);
		canvas.restore();
	}
	
	/**
	 * 绘制字母
	 * @param canvas
	 * @param letterBarWidth	导航栏宽度
	 * @param letterSpaceHeight 字母的固定高度
	 */
	private void drawLetters(Canvas canvas, float letterBarWidth, float letterSpaceHeight ) {
		resetPaintForLetter(letterSpaceHeight);
		float lastYPos = getPaddingTop() + calculateTextVerticalOffset(letterSpaceHeight, mPaint);
		for (int i = 0; i < mCount; i++) {
			mPaint.setColor(mLetterBarTexColorStateList.getColorForState(
					i == mLastIndex ? FOCUSED_STATE_SET : EMPTY_STATE_SET, Color.WHITE));
			// 设置字的位置为居中显示
			float xPos = mLetterBarXOffset + letterBarWidth / 2;
			canvas.drawText(mLetters[i], xPos, lastYPos, mPaint);
			lastYPos += letterSpaceHeight;
		}
	}
	
	/**
	 * 绘制弹出层上的字母
	 * @param canvas
	 * @param letter 要绘制的字母
	 */
	private void drawOverlayLetter(Canvas canvas, String letter) {
		if( TextUtils.isEmpty(letter) ) {
			return;
		}
		drawOverlayBackground(canvas);
		resetPaintForOverlayLetter();
		canvas.drawText(letter, getWidth() / 2, calculateTextVerticalOffset(getHeight(), mPaint) , mPaint);
	}
	
	/**
	 * 绘制选中字母的弹出层背景
	 * @param canvas
	 */
	private void drawOverlayBackground(Canvas canvas) {
		int dWidth = mOverlayBackground.getIntrinsicWidth();
		int dHeight = mOverlayBackground.getIntrinsicHeight();
		if( dWidth == 0 || dHeight == 0) {
			dWidth = dHeight = 200;
		}
		// 设置弹出层为屏幕中心
		canvas.save();
		canvas.translate(
				(getWidth() - getPaddingLeft() - getPaddingRight() - dWidth) / 2,
				(getHeight() - getTop() - getPaddingBottom() - dHeight) / 2);
		// drawable 要设置 bounds, 否则画不出来
		mOverlayBackground.setBounds(0, 0, dWidth, dHeight);
		mOverlayBackground.draw(canvas);
		canvas.restore();
	}
	
	/**
	 * 设置字母选中监听
	 * @param l
	 */
	public void setOnLetterSelectListener(OnLetterSelectListener l) {
		this.mOnLetterSelectListener = l;
	}
	
	/**
	 * 重设画笔。绘制字母
	 * @param letterSpaceHeight	字母空间的高度。用此来计算 字体大小
	 */
	private void resetPaintForLetter(float letterSpaceHeight) {
		mPaint.reset();
		mPaint.setTextSize( (letterSpaceHeight > mLetterBarWidth ? mLetterBarWidth : letterSpaceHeight ) * 0.6f);
		mPaint.setTextAlign(Align.CENTER);
	}
	
	/**
	 * 重设画笔。绘制弹出层上的字母
	 */
	private void resetPaintForOverlayLetter() {
		mPaint.reset();
		mPaint.setTextSize(mOverlayTextSize);
		mPaint.setColor(mOverlayTextColor);
		mPaint.setTextAlign(Align.CENTER);
	}
	
	/**
	 * 计算文字垂直居中的偏移量
	 * @param spaceHeight	文字所占空间的高度
	 * @param paint	当前绘制该文字的画笔.用来获取 {@link android.graphics.Paint.FontMetrics}
	 * @return
	 */
	private float calculateTextVerticalOffset(float spaceHeight, Paint paint) {
		FontMetrics metrics = paint.getFontMetrics();
		return spaceHeight / 2 + (metrics.bottom - metrics.top) / 4;
	}
	
	/**
	 * 设置字母导航条里的字母集
	 * @param letters
	 */
	public void setLetterSet(String[] letters) {
		mLetters = letters;
		mCount = mLetters.length;
	}
	
	/**
	 * 设置字母条的宽度
	 * @param width
	 */
	public void setLetterBarWidth(float width) {
		mLetterBarWidth = width;
	}
	
	/**
	 * 设置字母条的背景
	 * @param drawableRes 
	 */
	public void setLetterBarBackground(int drawableRes) {
		mLetterBarBackground = getResources().getDrawable(drawableRes);
		mLetterBarBackground.setCallback(this);
	}
	
	/**
	 * 设置字母条的背景
	 * @param focusedColor 获得焦点的颜色
	 * @param unfocusedColor 未获得焦点的颜色
	 */
	private void setLetterBarBackgrond(int focusedColor, int unfocusedColor) {
		StateListDrawable sld = new StateListDrawable();
		Drawable focusedOrSelectedDrawable = new ColorDrawable(focusedColor);
		sld.addState(FOCUSED_STATE_SET, focusedOrSelectedDrawable);
		sld.addState(EMPTY_STATE_SET, new ColorDrawable(unfocusedColor));
		mLetterBarBackground = sld;
		mLetterBarBackground.setCallback(this);
	}
	
	/**
	 * 设置字母的颜色
	 * @param colorRes
	 */
	public void setLetterBarTextColor(int colorRes) {
		mLetterBarTexColorStateList = getResources().getColorStateList(colorRes);
	}
	
	/**
	 * 设置字母的颜色
	 * @param focusedColor 未选中的颜色
	 * @param unfoucsedColor 选中的颜色
	 */
	public void setLetterBarTextColor(int focusedColor, int unfoucsedColor) {
		mLetterBarTexColorStateList = new ColorStateList(
				new int[][]{FOCUSED_STATE_SET, EMPTY_STATE_SET}, 
				new int[]{focusedColor, unfoucsedColor});
	}
	
	/**
	 * 设置字母弹出层的背景
	 * @param drawableRes
	 */
	public void setOverlayBackground(int drawableRes) {
		mOverlayBackground = getResources().getDrawable(drawableRes);
	}
	
	/**
	 * 设置字母弹出层的背景颜色
	 * @param color
	 */
	public void setOverlayBackgroundColor(int color) {
		mOverlayBackground = new ColorDrawable(color);
	}
	
	/**
	 * 设置字母弹出层的文字颜色
	 * @param color
	 */
	public void setOverlayTextColor(int color) {
		mOverlayTextColor = color;
	}
	
	/**
	 * 设置字母弹出层的文字大小
	 * @param size
	 */
	public void setOverlayTextSize(float size) {
		mOverlayTextSize = size;
	}
	
	/**
	 * 设置字母弹出层的文字大小 
	 * @param unit	单位 .See {@link TypedValue} for the possible dimension units.
	 * @param size	单位下的大小
	 */
	public void setOVerlayTextSize(int unit, float size) {
		Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();
        setOverlayTextSize(TypedValue.applyDimension(
                unit, size, r.getDisplayMetrics()));
	}
}
