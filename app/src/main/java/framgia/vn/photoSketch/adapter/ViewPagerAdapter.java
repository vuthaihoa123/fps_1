package framgia.vn.photoSketch.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nghicv on 02/05/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mListFragment;
    private List<String> mListTitle;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mListFragment = new ArrayList<>();
        mListTitle = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mListFragment.get(position);
    }

    @Override
    public int getCount() {
        return mListFragment.size();
    }

    public void addFragment(Fragment fragment,String title) {
        mListFragment.add(fragment);
        mListTitle.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mListTitle.get(position);
    }
}
