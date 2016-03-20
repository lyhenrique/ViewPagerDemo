package com.ericlau.realizeviewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.ericlau.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;
    List<String> mTitles = Arrays.asList("短信","收藏","分享","短信1","收藏2","分享3","短信4","收藏5","分享6");
    List<VpFragment> mContent = new ArrayList<VpFragment>();
    FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        
        initView();
        
        initDatas();

        mIndicator.setTabVisibleCount(4);
        mIndicator.setTabItemTitles(mTitles);

        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager,0);

    }

    private void initDatas() {
        for (String title : mTitles) {
           VpFragment fragment =  VpFragment.newInstance(title);
            mContent.add(fragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContent.get(position);
            }

            @Override
            public int getCount() {
                return mContent.size();
            }
        };

    }

    private void initView() {
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mIndicator = (ViewPagerIndicator)findViewById(R.id.id_indicator);
    }
}
