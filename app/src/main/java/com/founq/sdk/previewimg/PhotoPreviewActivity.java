package com.founq.sdk.previewimg;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.founq.sdk.previewimg.widget.MyViewPager;

import java.util.ArrayList;
import java.util.List;

public class PhotoPreviewActivity extends AppCompatActivity implements PreviewFragment.OnFragmentInteractionListener {

    private List<String> imgURLs = new ArrayList<>();

    private MyViewPager mViewPager;

    private int index;

    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        mViewPager = findViewById(R.id.view_pager);
        imgURLs = getIntent().getStringArrayListExtra("imgURLs");
        index = getIntent().getIntExtra("index", 0);
        mFragments = new ArrayList<>();
        if (imgURLs != null && imgURLs.size() > 0) {
            for (int i = 0; i < imgURLs.size(); i++) {
                mFragments.add(PreviewFragment.newInstance(imgURLs.get(i)));
            }
        }
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), mFragments));
        mViewPager.setCurrentItem(index);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        String text = uri.toString();
        if (text.matches(".*next.*")) {
            int item = mViewPager.getCurrentItem();
            if (item + 1 < mFragments.size()) {
                mViewPager.setCurrentItem(item + 1);
            }
        } else {
            int item = mViewPager.getCurrentItem();
            if (item - 1 >= 0) {
                mViewPager.setCurrentItem(item - 1);
            }
        }
    }

    private class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mlistViews;

        public TabFragmentPagerAdapter(FragmentManager fm, List<Fragment> listViews) {
            super(fm);
            this.mlistViews = listViews;
        }

        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            return mlistViews.get(arg0);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mlistViews.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

    }
}
