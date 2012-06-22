package ru.piter.fm.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ru.piter.fm.radio.RadioFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 15.03.12
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public class RadioAdapter extends FragmentPagerAdapter{

    public List<Fragment> fragments;

    public RadioAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment){
        if (fragments == null)
            fragments = new ArrayList<Fragment>();
        fragments.add(fragment);
    }


}
