package framgia.vn.photoSketch.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.adapter.ViewPagerAdapter;
import framgia.vn.photoSketch.fragment.ListPhotoFragment;
import framgia.vn.photoSketch.fragment.ListVideoFragment;

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
