package com.ericlau.realizeviewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Eric.Lau on 16/3/20.
 */
public class VpFragment extends Fragment {

    private String mTitle;
    public static final String BUNDLE_TLTLE = "title";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(BUNDLE_TLTLE);
        }

        TextView tv = new TextView(getActivity());
        tv.setText(mTitle);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    public static VpFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TLTLE, title);
        VpFragment fragment = new VpFragment();
        fragment.setArguments(bundle);
        return fragment;

    }
}
