package com.ericlau.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ericlau.realizeviewpager.R;

import java.util.List;

/**
 * Created by Eric.Lau on 16/3/20.
 */
public class ViewPagerIndicator extends LinearLayout {
    private Paint mPaint;
    private Path mPath;
    private int mTriangleWidth;
    private int mTriangleHeight;
    private static final float TRIANGLE_BOTTOM_LENGTH = 1 / 6F;
    /**
     * 设置三角形底边最大宽度
     */
    private final int DIMENSION_TRIANGLE_WIDTH_MAX = (int) (getScreenWidth()/3*TRIANGLE_BOTTOM_LENGTH);
    private int mInitTranslationX;
    private int mTranslationX;

    private int mTabVisibleCount;
    private static final int COUNT_DEFAULT = 4;

    private List<String> mTitles;

    private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
    private static final int COLOR_TEXT_HIGHLIGHT = 0xFFFFFFFF;

    private ViewPager mViewPager;

    public interface PageOnChangeListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);


        public void onPageSelected(int position);


        public void onPageScrollStateChanged(int state);

    }

    public PageOnChangeListener mListener;

    public void setPageOnChangeListener(PageOnChangeListener listener) {
        mListener = listener;
    }



    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);


        //获取可见Tab的数量
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);

        mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_visible_tab_count,COUNT_DEFAULT);

        if(mTabVisibleCount<0) {
            mTabVisibleCount = COUNT_DEFAULT;
        }
        a.recycle();

        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(2));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();

        canvas.translate(mInitTranslationX + mTranslationX, getHeight() + 2);
        canvas.drawPath(mPath, mPaint);

        canvas.restore();

        super.dispatchDraw(canvas);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTriangleWidth = (int) (w / mTabVisibleCount * TRIANGLE_BOTTOM_LENGTH);
        mTriangleWidth = Math.min(mTriangleWidth,DIMENSION_TRIANGLE_WIDTH_MAX);
        mInitTranslationX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;

        initTriangle();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int cCount = getChildCount();
        if (cCount == 0) return;

        for (int i=0;i<cCount;i++) {
            View v = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) v.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth()/mTabVisibleCount;
            v.setLayoutParams(lp);
        }
        setItemClickEvent();
    }

    /**
     * 获得屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /*
    *初始化三角形
    */
    private void initTriangle() {

        mTriangleHeight = mTriangleWidth / 2;

        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();

    }

    /**
     * 指示器跟随手指进行滚动
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {


        //三角形的联动
        int tabWidth = getWidth()/mTabVisibleCount;
        mTranslationX = (int) (tabWidth * (offset + position));


        //容器上的tab的联动  当tab处于最后一个时
        if(position >= mTabVisibleCount-2 && offset>0 && getChildCount()>mTabVisibleCount && (position) != (getChildCount()-2)) {
            if(mTabVisibleCount != 1) {
                this.scrollTo((int) ((position - (mTabVisibleCount - 2) + offset) * tabWidth), 0);
            }else {
                this.scrollTo((int) (position*tabWidth+offset*tabWidth),0);
            }
        }

        //让三角形重绘的方法
        invalidate();
    }

    public void setTabItemTitles(List<String> titles) {
        if(titles != null && titles.size() > 0) {
            this.removeAllViews();
            mTitles = titles;
            for(String title : mTitles) {
                addView(generateTextView(title));
            }
            setItemClickEvent();
        }
    }

    /**
     * 让用户设置可见显示几列,但一定要在setTabItemTitles之前调用
     * 因为在setTabItemTitles里用到了mTabVisibleCount
     * @param count
     */
    public void setTabVisibleCount(int count){
        mTabVisibleCount = count;
    }

    /**
     * 根据title创建tab
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth()/mTabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setLayoutParams(lp);
        return tv;
    }


    /**
     * 设置关联的ViewPager
     * @param viewPager
     * @param pos
     */
    public void setViewPager(ViewPager viewPager,int pos) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mListener != null) {
                    mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                //偏移量是tabWidth*positionOffset+position*tabWidth
                scroll(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPageSelected(position);
                }

                highlightTextView(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });
        mViewPager.setCurrentItem(pos);
        highlightTextView(pos);
    }

    /**
     * 重置Tab文本颜色
     */
    private void resetTextColor() {
        for (int i=0;i<getChildCount();i++) {
            View view = getChildAt(i);
            if(view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    /**
     * 高亮某个tab的文本
     * @param pos
     */
    private void highlightTextView(int pos) {
        resetTextColor();
        View view = getChildAt(pos);
        if(view instanceof TextView) {

            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHT);
        }
    }

    /**
     * 设置tab的点击事件
     */
    public void setItemClickEvent(){
        int cCount = getChildCount();
        for (int i=0;i<cCount;i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }
}
