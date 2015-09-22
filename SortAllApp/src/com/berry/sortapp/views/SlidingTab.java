package com.berry.sortapp.views;

import java.util.Locale;

import com.berry.sortapp.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 顶部随ViewPager滑动view,类似TabHost
 * @author berry
 *
 */
public class SlidingTab extends HorizontalScrollView {

	public interface IconTabProvider {
		public int getPageIconResId(int position);
	}

	 
    public static boolean ACTION_BAR_SHOW_LOGO_ICON = true;//0.不显示;1.显示
    public static boolean ACTION_BAR_SHOW_LOGO_TITLE = true;//0.不显示;1.显示
    //当前tab底部绘制方式
    public static boolean ACTION_BAR_SHOW_INDICATE = true;//0.不显示;1.显示
    public static int ACTION_BAR_INDICATE_TYPE = 0; //0.矩形;1.图片;
    //是否绘制整个tab的底部细线
    public static boolean ACTION_BAR_DRAW_UNDERLINE = false;
    
	private static final String TAG = "SlidingTab";

	/** @formatter:off */
	private static final int[] ATTRS = new int[] { android.R.attr.textSize,
			android.R.attr.textColor };
	/** @formatter:on */

	private final LinearLayout.LayoutParams defaultTabLayoutParams;
	private final LinearLayout.LayoutParams expandedTabLayoutParams;

	private final PageListener pageListener = new PageListener();
	public OnPageChangeListener delegatePageListener;

	private OnPageChangedRefreshMainUIListener mOnPageChangeRefreshMainUIListener = null;
	private OnDoubleClickTabListener mOnDoubleClickTabListener = null;
	private final LinearLayout tabsContainer;
	private ViewPager pager;

	private int tabCount;

	private int weightSum;

	private int currentPosition = 0;
	private int cuttentDispayPosition = 0;
	private float currentPositionOffset = 0f;
	private final Paint rectPaint;
	private final Paint dividerPaint;

	private boolean checkedTabWidths = false;

	private int indicatorColor = 0xFF08C08E;
	private int underlineColor = 0xFFD5D5D6;
	private int dividerColor = 0x1A000000;

	private boolean shouldExpand = false;
	private boolean textAllCaps = true;

	private int scrollOffset = 52;
	private int indicatorHeight = 3;
	private int underlineHeight = 1;
	private int underlinePadding = 0;
	private int dividerPadding = 12;
	private int tabPadding = 18;
	private int dividerWidth = 1;

	private int tabTextSize = 18;

	private int tabTextColor = 0xFF000000;
	private int tabSelectTextColor = 0xFF02AA7C;

	private Typeface tabTypeface = null;
	private int tabTypefaceStyle = Typeface.NORMAL;

	private int lastScrollX = 0;
	private int tabBackgroundResId = R.drawable.pls_pagerslidingtabstrip_tab_bg;
	private Locale locale;

	private Runnable mTabSelector;


	private Context mContext;
	private Bitmap mBitmap;
	private int currentTab;

	public SlidingTab(Context context) {
		this(context, null);
		mContext = context;
	}

	public SlidingTab(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
	}

	public SlidingTab(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		setFillViewport(true);
		setWillNotDraw(false);


		if (ACTION_BAR_INDICATE_TYPE == 1)
			bitmap = BitmapFactory.decodeResource(getContext().getResources(),
					R.drawable.pl_tab_selected_indicater);


		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		addView(tabsContainer);

		DisplayMetrics dm = getResources().getDisplayMetrics();

		scrollOffset = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
		indicatorHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
		underlineHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
		dividerPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
		tabPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
		dividerWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
		tabTextSize = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
		tabTextColor = a.getColor(1, tabTextColor);

		a.recycle();

		a = context.obtainStyledAttributes(attrs,
				R.styleable.SlidingTab);

		indicatorColor = a.getColor(
				R.styleable.SlidingTab_indicatorColor,
				indicatorColor);
		underlineColor = a.getColor(
				R.styleable.SlidingTab_underlineColor,
				underlineColor);
		dividerColor = a
				.getColor(R.styleable.SlidingTab_dividerColor,
						dividerColor);
		indicatorHeight = a.getDimensionPixelSize(
				R.styleable.SlidingTab_indicatorHeight,
				indicatorHeight);
		underlineHeight = a.getDimensionPixelSize(
				R.styleable.SlidingTab_underlineHeight,
				underlineHeight);
		dividerPadding = a.getDimensionPixelSize(
				R.styleable.SlidingTab_dividerPadding,
				dividerPadding);
		tabPadding = a.getDimensionPixelSize(
				R.styleable.SlidingTab_tabPaddingLeftRight,
				tabPadding);
		tabBackgroundResId = a.getResourceId(
				R.styleable.SlidingTab_tabBackground,
				tabBackgroundResId);
		shouldExpand = a
				.getBoolean(R.styleable.SlidingTab_shouldExpand,
						shouldExpand);
		scrollOffset = a
				.getDimensionPixelSize(
						R.styleable.SlidingTab_scrollOffset,
						scrollOffset);
		textAllCaps = a.getBoolean(
				R.styleable.SlidingTab_textAllCaps, textAllCaps);

		a.recycle();

		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setStyle(Style.FILL);

		dividerPaint = new Paint();
		dividerPaint.setAntiAlias(true);
		dividerPaint.setStrokeWidth(dividerWidth);

		defaultTabLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
		expandedTabLayoutParams = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 1.0f);

		if (locale == null) {
			locale = getResources().getConfiguration().locale;
		}
	}

	public void setViewPager(ViewPager pager) {
		this.pager = pager;

		if (pager.getAdapter() == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}

		pager.addOnPageChangeListener(pageListener);

		notifyDataSetChanged();
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.delegatePageListener = listener;
	}

	public void setmOnPageChangeRefreshMainUIListener(
			OnPageChangedRefreshMainUIListener mOnPageChangeRefreshMainUIListener) {
		this.mOnPageChangeRefreshMainUIListener = mOnPageChangeRefreshMainUIListener;
	}

	public void setmOnDoubleClickTabListener(
			OnDoubleClickTabListener mOnDoubleClickTabListener) {
		this.mOnDoubleClickTabListener = mOnDoubleClickTabListener;
	}

	public void notifyDataSetChanged() {

		tabsContainer.removeAllViews();
		tabCount = pager.getAdapter().getCount();
		weightSum = tabCount;
		tabsContainer.setWeightSum(weightSum);
		// tabsFrameContainer.setTabCount( tabCount );

		for (int i = 0; i < tabCount; i++) {

			if (pager.getAdapter() instanceof IconTabProvider) {
				addIconTab(i,
						((IconTabProvider) pager.getAdapter())
								.getPageIconResId(i));
			} else {
				if (pager != null && pager.getAdapter() != null
						&& pager.getAdapter().getPageTitle(i) != null)
					addTextTab(i, pager.getAdapter().getPageTitle(i).toString());

			}

		}
		updateTabStyles();

		checkedTabWidths = false;

		getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {

						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
							getViewTreeObserver().removeGlobalOnLayoutListener(
									this);
						} else {
							getViewTreeObserver().removeOnGlobalLayoutListener(
									this);
						}

						currentPosition = pager.getCurrentItem();
						scrollToChild(currentPosition, 0);
					}
				});

	}

	private void addTextTab(final int position, String title) {

		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setFocusable(true);
		tab.setGravity(Gravity.CENTER);
		tab.setLayoutParams(defaultTabLayoutParams);
		tab.setSingleLine();

		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pager.getVisibility() == View.VISIBLE) {
					if (isFastDoubleClick() && position == 1
							&& mOnDoubleClickTabListener != null) {
						mOnDoubleClickTabListener
								.onDoubleClickTabToHandle(position);
					} else {
						pager.setCurrentItem(position);
					}
				}

			}
		});

		tabsContainer.addView(tab);
	}

	public void enabledOrNotTabs(boolean b) {

		for (int i = 0; i < tabCount; i++) {
			View v = tabsContainer.getChildAt(i);
			v.setEnabled(b);
		}
	}

	private void addIconTab(final int position, int resId) {

		ImageButton tab = new ImageButton(getContext());
		tab.setFocusable(true);
		tab.setImageResource(resId);

		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.setCurrentItem(position);
			}
		});

		tabsContainer.addView(tab);

	}

	private void updateTabStyles() {


		for (int i = 0; i < tabCount; i++) {

			View v = tabsContainer.getChildAt(i);

			if (v == null)
				continue;

			v.setLayoutParams(defaultTabLayoutParams);
			v.setBackgroundResource(tabBackgroundResId);
			if (shouldExpand) {
				v.setPadding(0, 0, 0, 0);
			} else {
				v.setPadding(tabPadding, 0, tabPadding, 0);
			}

			if (v instanceof TextView) {

				TextView tab = (TextView) v;
				tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
				tab.setTypeface(tabTypeface, tabTypefaceStyle);
				tab.setTextColor(tabTextColor);
				if (i == cuttentDispayPosition) {
					tab.setTextColor(tabSelectTextColor);
				}
			}

		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (!shouldExpand
				|| MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
			return;
		}
		int myWidth = getMeasuredWidth();
		int childWidth = 0;
		for (int i = 0; i < tabCount; i++) {
			childWidth += tabsContainer.getChildAt(i).getMeasuredWidth();
		}

		if (!checkedTabWidths && childWidth > 0 && myWidth > 0) {

			if (childWidth <= myWidth) {
				for (int i = 0; i < tabCount; i++) {
					tabsContainer.getChildAt(i).setLayoutParams(
							expandedTabLayoutParams);
				}
			}

			checkedTabWidths = true;
		}
	}

	private void scrollToChild(int position, int offset) {


		if (tabCount == 0 || tabsContainer == null
				|| tabsContainer.getChildAt(position) == null) {
			return;
		}
		int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0) {
			newScrollX -= scrollOffset;
		}

		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			final int newScrollX2 = newScrollX;
			scrollTo(newScrollX2, 0);
		}

	}

	Bitmap bitmap;
	private float oldLeft = 0;

	// 画矩形
	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);

		if (isInEditMode() || tabCount == 0) {
			return;
		}

		final int height = getHeight();

		// draw indicator line

		rectPaint.setColor(indicatorColor);

		// default: line below current tab
		View currentTab = tabsContainer.getChildAt(currentPosition);
		float lineLeft = currentTab.getLeft();

		float lineRight = currentTab.getRight();

		// if there is an offset, start interpolating left and right coordinates
		// between current and next tab
		if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

			View nextTab = tabsContainer.getChildAt(currentPosition + 1);
			final float nextTabLeft = nextTab.getLeft();
			final float nextTabRight = nextTab.getRight();

			lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset)
					* lineLeft);
			lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset)
					* lineRight);
		}

		float left = lineLeft + underlinePadding;
		if (ACTION_BAR_INDICATE_TYPE == 1) {
			left = lineLeft;
		}
		final float top = height - indicatorHeight;
		final float right = lineRight - underlinePadding;
		final float buttom = height;


		if (ACTION_BAR_INDICATE_TYPE == 1
				&& ACTION_BAR_SHOW_INDICATE) {
			// LogUtils.e( "tabCount = " + tabCount );
			int oneColumnWidth = getWidth() / tabCount;
			float desX = (oneColumnWidth - bitmap.getWidth()) * 0.5f;
			left = left + desX;
			canvas.drawBitmap(bitmap, left, getBottom() - bitmap.getHeight(),
					null);
		} else if (ACTION_BAR_INDICATE_TYPE == 0
				&& ACTION_BAR_SHOW_INDICATE) {
		    float w = right-left;
			canvas.drawRect(left+w/4, top, right-w/4, buttom, rectPaint);
		}


		rectPaint.setColor(underlineColor);
		if (ACTION_BAR_DRAW_UNDERLINE) {
			canvas.drawRect(0, height - underlineHeight,
					tabsContainer.getWidth(), height, rectPaint);
		}
		// draw divider

		dividerPaint.setColor(dividerColor);
		for (int i = 0; i < tabCount - 1; i++) {
			View tab = tabsContainer.getChildAt(i);
			if (tab == null)
				return;
			canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(),
					height - dividerPadding, dividerPaint);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (bitmap != null)
			bitmap.recycle();
	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

			currentPosition = position;
			currentPositionOffset = positionOffset;


			scrollToChild(position, (int) (positionOffset * tabsContainer
					.getChildAt(position).getWidth()));

			invalidate();

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset,
						positionOffsetPixels);
			}

		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				scrollToChild(pager.getCurrentItem(), 0);
			}

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}

		}

		@Override
		public void onPageSelected(int position) {
			if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}
			if (mOnPageChangeRefreshMainUIListener != null) {
				mOnPageChangeRefreshMainUIListener
						.onPageChangedRefreshMainUI(pager.getCurrentItem());
			}

			cuttentDispayPosition = position;
			updateTabStyles();

		}
	}

	public void setIndicatorColor(int indicatorColor) {
		this.indicatorColor = indicatorColor;
		invalidate();
	}

	/**
	 * 设置当前Tab底部横线颜色
	 * 
	 * @param resId
	 */
	public void setIndicatorColorResource(int resId) {
		this.indicatorColor = getResources().getColor(resId);
		invalidate();
	}

	public int getIndicatorColor() {
		return this.indicatorColor;
	}

	public void setIndicatorHeight(int indicatorLineHeightPx) {
		this.indicatorHeight = indicatorLineHeightPx;
		invalidate();
	}

	public int getIndicatorHeight() {
		return indicatorHeight;
	}

	public void setUnderlineColor(int underlineColor) {
		this.underlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineColorResource(int resId) {
		this.underlineColor = getResources().getColor(resId);
		invalidate();
	}

	/**
	 * 设置当前tab字体颜色
	 * @param resId
	 */
	public void setTabSelectTextColor(int resId) {
		this.tabSelectTextColor = getResources().getColor(resId);
		updateTabStyles();
	}

	public int getUnderlineColor() {
		return underlineColor;
	}

	/**
	 * 设置两个tab之间的竖线颜色
	 * @param dividerColor
	 */
	public void setDividerColor(int dividerColor) {
		this.dividerColor = dividerColor;
		invalidate();
	}

	/**
	 * 设置两个tab之间的竖线色值Id
	 * @param resId
	 */
	public void setDividerColorResource(int resId) {
		this.dividerColor = getResources().getColor(resId);
		invalidate();
	}

	public int getDividerColor() {
		return dividerColor;
	}

	public void setUnderlineHeight(int underlineHeightPx) {
		this.underlineHeight = underlineHeightPx;
		invalidate();
	}

	public int getUnderlineHeight() {
		return underlineHeight;
	}

	public void setDividerPadding(int dividerPaddingPx) {
		this.dividerPadding = dividerPaddingPx;
		invalidate();
	}

	public int getDividerPadding() {
		return dividerPadding;
	}

	public void setUnderlinePadding(int underlinePadding) {
		this.underlinePadding = underlinePadding;
		invalidate();
	}

	public void setScrollOffset(int scrollOffsetPx) {
		this.scrollOffset = scrollOffsetPx;
		invalidate();
	}

	public int getScrollOffset() {
		return scrollOffset;
	}

	public void setShouldExpand(boolean shouldExpand) {
		this.shouldExpand = shouldExpand;
		requestLayout();
	}

	public boolean getShouldExpand() {
		return shouldExpand;
	}

	public boolean isTextAllCaps() {
		return textAllCaps;
	}

	public void setAllCaps(boolean textAllCaps) {
		this.textAllCaps = textAllCaps;
	}

	public void setTextSize(int textSizePx) {
		this.tabTextSize = textSizePx;
		updateTabStyles();
	}

	public int getTextSize() {
		return tabTextSize;
	}

	public void setTextColor(int textColor) {
		this.tabTextColor = textColor;
		updateTabStyles();
	}

	/**
	 * 设置除当前tab外其他tab的字体颜色
	 * @param resId
	 */
	public void setTextColorResource(int resId) {
		this.tabTextColor = getResources().getColor(resId);
		updateTabStyles();
	}

	public int getTextColor() {
		return tabTextColor;
	}

	public void setTypeface(Typeface typeface, int style) {
		this.tabTypeface = typeface;
		this.tabTypefaceStyle = style;
		updateTabStyles();
	}

	public void setTabBackground(int resId) {
		this.tabBackgroundResId = resId;
	}

	public int getTabBackground() {
		return tabBackgroundResId;
	}

	public void setTabPaddingLeftRight(int paddingPx) {
		this.tabPadding = paddingPx;
		updateTabStyles();
	}

	public int getTabPaddingLeftRight() {
		return tabPadding;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		currentPosition = savedState.currentPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPosition = currentPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currentPosition;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPosition);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public interface OnPageChangedRefreshMainUIListener {
		void onPageChangedRefreshMainUI(int position);
	}

	public interface OnDoubleClickTabListener {
		void onDoubleClickTabToHandle(int position);
	}

	private static long lastClickTime = 0;

	public static synchronized boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

}
