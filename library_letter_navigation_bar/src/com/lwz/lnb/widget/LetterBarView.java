package com.lwz.lnb.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
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
 * 当点击导航条的字母时，会触发监听事件,若要监听，则可以使用 {@link #setOnLetterTouchListener(OnLetterTouchListener)} 来设置
 * </p>
 * 
 */
public class LetterBarView extends View {
	
	/**
	 * 触摸字母监听
	 */
	public interface OnLetterTouchListener {
		/**
		 * @param s 被触摸的字母
		 */
		public void onLetterTouch(String s);
	}

	private OnLetterTouchListener mOnLetterTouchListener;

	private String[] mLetters = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private int mCount = mLetters.length;
	private int mLastIndex = -1;

	// 是否绘制字母导航栏背景。 当用户点击时绘制
	private boolean mLetterBarFocus = false;
	private float mLetterBarWidth = 40;
	private final float LETTER_BAR_MAX_WIDTH = 80;
	
	// 导航栏在 x 轴的位移
	private float mLetterBarXOffset = 0;
	private String mSelectedLetter;
	
	private Paint mPaint;
	private RectF mOverlayRect ;
	private float mLetterSpaceHight = 0;
	
	private int mLetterBarColor = Color.parseColor("#66000000");
	private int mLetterBarFocusedColor = Color.parseColor("#88000000");
	private int mLetterColor = Color.WHITE;
	private int mLetterFocusedColor = Color.BLUE;
	private int mOverlayColor = Color.parseColor("#88000000");
	private int mOverlayTextColor = Color.WHITE;
	
	private float mOverlayTextSize = 120;
	
	public LetterBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LetterBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LetterBarView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mOverlayRect = new RectF(0, 0, 200, 200);
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
					if (mOnLetterTouchListener != null) {
						mOnLetterTouchListener.onLetterTouch(letter);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				mLetterBarFocus = false;
				mLastIndex = -1;
				dismissSelctedLetterOverlay();
				break;
			}
		invalidate();
		return true;
	}
	
	private void dismissSelctedLetterOverlay() {
		postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mSelectedLetter = null;
				invalidate();
			}
		}, 500);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		calculateLetterBarParams();
		
		drawLetterBarBackground(canvas);
		drawLetters(canvas, mLetterBarWidth, mLetterSpaceHight);
		drawOverlayLetter(canvas);
	}
	
	private void calculateLetterBarParams() {
		calculateLetterSpaceHight();
		
		// 导航栏的宽度
		mLetterBarWidth = Math.min( Math.max(mLetterSpaceHight, mLetterBarWidth), LETTER_BAR_MAX_WIDTH);
		// 计算 导航栏在 x 轴的位移
		mLetterBarXOffset = getWidth() - mLetterBarWidth;
	}
	
	private void calculateLetterSpaceHight() {
		if( mLetterSpaceHight <= 0f ) {
			mLetterSpaceHight = (getHeight() - getPaddingTop() - getPaddingBottom()) / (float)mCount;
		}
	}
	
	/**
	 * 绘制导航栏背景
	 * @param canvas 
	 * @param width	该 View 的宽度
	 * @param height 该 View 的高度
	 */
	private void drawLetterBarBackground(Canvas canvas) {
		resetPaintForLetterBar(mLetterBarFocus);
		canvas.drawRect(mLetterBarXOffset, 0, getWidth(), getHeight(), mPaint);
	}
	
	/**
	 * 绘制字母
	 * @param canvas
	 * @param letterBarWidth	导航栏宽度
	 * @param letterSpaceHight 字母的固定高度
	 */
	private void drawLetters(Canvas canvas, float letterBarWidth, float letterSpaceHight ) {
		resetPaintForLetter(letterSpaceHight);
		float lastYPos = getPaddingTop() + calculateTextVerticalOffset(letterSpaceHight, mPaint);
		for (int i = 0; i < mCount; i++) {
			boolean isLetterFocus = ( i == mLastIndex );
			String letter = mLetters[i];
			if( isLetterFocus ) {
				mPaint.setColor(mLetterFocusedColor);
			}
			// 设置字的位置为居中显示
			float xPos = mLetterBarXOffset + letterBarWidth / 2;
			canvas.drawText(letter, xPos, lastYPos, mPaint);
			lastYPos += letterSpaceHight;
			if( isLetterFocus ) {
				mSelectedLetter = mLetters[i];
				mPaint.setColor(Color.WHITE);
			}
		}
	}
	
	private void drawOverlayLetter(Canvas canvas) {
		if( TextUtils.isEmpty(mSelectedLetter) ) {
			return;
		}
		drawOverlayBackground(canvas);
		resetPaintForOverlaySelectLetter();
		canvas.drawText(mSelectedLetter, getWidth() / 2, calculateTextVerticalOffset(getHeight(), mPaint) , mPaint);
	}
	
	private void drawOverlayBackground(Canvas canvas) {
		resetPaintForOverlayBackground();
		mOverlayRect.offsetTo((getWidth() - mOverlayRect.right + mOverlayRect.left) / 2 ,
				(getHeight() - mOverlayRect.bottom + mOverlayRect.top) / 2);
		canvas.drawRoundRect(mOverlayRect, 20, 20, mPaint);
	}
	
	public void setOnLetterTouchListener(OnLetterTouchListener l) {
		this.mOnLetterTouchListener = l;
	}
	
	private void resetPaintForLetter(float letterHeight) {
		mPaint.reset();
		mPaint.setTextSize( (letterHeight > mLetterBarWidth ? mLetterBarWidth : letterHeight ) * 0.6f);
		mPaint.setColor(mLetterColor);
		mPaint.setTextAlign(Align.CENTER);
	}
	
	private void resetPaintForLetterBar(boolean isFocus) {
		mPaint.reset();
		// 若导航栏被焦点，则高亮显示
		mPaint.setColor( isFocus ? mLetterBarFocusedColor : mLetterBarColor);
	}
	
	private void resetPaintForOverlaySelectLetter() {
		mPaint.reset();
		mPaint.setTextSize(mOverlayTextSize);
		mPaint.setColor(mOverlayTextColor);
		mPaint.setTextAlign(Align.CENTER);
	}
	
	private void resetPaintForOverlayBackground() {
		mPaint.reset();
		mPaint.setColor(mOverlayColor);
	}
	
	private float calculateTextVerticalOffset(float containerHeight, Paint paint) {
		FontMetrics metrics = paint.getFontMetrics();
		return containerHeight / 2 + (metrics.bottom - metrics.top) / 4;
	}
	
	public void setLetterSet(String[] letters) {
		mLetters = letters;
		mCount = mLetters.length;
	}
	
	public void setSelectLetterOverlaySize(int width, int height) {
		mOverlayRect.set(0, 0, width, height);
	}
	
	public void setLetterBarWidth(float width) {
		mLetterBarWidth = width;
	}
	
	public void setLetterBarColor(int normalColor, int focusedColor) {
		mLetterBarColor = normalColor;
		mLetterBarFocusedColor = focusedColor;
	}
	
	public void setLetterColor(int normalColor, int focusedColor) {
		mLetterColor = normalColor;
		mLetterFocusedColor = focusedColor;
	}
	
	public void setOverlayColor(int color) {
		mOverlayColor = color;
	}
	
	public void setOverlayTextColor(int color) {
		mOverlayTextColor = color;
	}
	
	public void setOverlayTextSize(float size) {
		mOverlayTextSize = size;
	}
	
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
