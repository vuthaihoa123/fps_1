package framgia.vn.photo_sketch.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import framgia.vn.photo_sketch.R;
import framgia.vn.photo_sketch.adapter.ViewPagerAdapter;
import framgia.vn.photo_sketch.fragment.ListPhotoFragment;
import framgia.vn.photo_sketch.fragment.ListVideoFragment;

public class ListImageActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TabLayout mTabLayoutListPhoto;
    private ViewPager mViewPagerListPhoto;
    private ViewPagerAdapter mViewpagerAdapter;
    private static final String TITLE_PHOTO = "Photo";
    private static final String TITLE_VIDEO = "Video";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image);
        initView();
        setUpViewPager();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTabLayoutListPhoto = (TabLayout) findViewById(R.id.tablayout_list_photo);
        mViewPagerListPhoto = (ViewPager) findViewById(R.id.viewpager_list_photo);
        mToolbar.setTitle(getString(R.string.title_activity_list_photo));
        setSupportActionBar(mToolbar);
    }

    private void setUpViewPager() {
        mViewpagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment listPhotoFragment =  new ListPhotoFragment();
        mViewpagerAdapter.addFragment(listPhotoFragment, TITLE_PHOTO);
        Fragment listVideoFragment = new ListVideoFragment();
        mViewpagerAdapter.addFragment(listVideoFragment, TITLE_VIDEO);
        mViewPagerListPhoto.setAdapter(mViewpagerAdapter);
        mTabLayoutListPhoto.setupWithViewPager(mViewPagerListPhoto);
    }
}
