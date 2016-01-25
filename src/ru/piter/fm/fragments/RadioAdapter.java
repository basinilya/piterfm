package ru.piter.fm.fragments;

import static ru.piter.fm.radio.RadioFactory.*;
import ru.piter.fm.radio.RadioFactory;
import ru.piter.fm.radio.TomskStation;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 15.03.12
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public class RadioAdapter extends FragmentPagerAdapter{

    private RadioFragment primaryFragment;

    public RadioAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        primaryFragment = (RadioFragment)object;
    }

    public RadioFragment getPrimaryFragment() {
        return primaryFragment;
    }

    public static String getCity(int position) {
        String s = position == 0 ? FAVOURITE : TomskStation.cities[position-1];
        return s;
    }
    
    @Override
    public Fragment getItem(int position) {
        return new RadioFragment(RadioFactory.getRadio(getCity(position)));
    }

    @Override
    public int getCount() {
        return TomskStation.cities.length + 1;
    }

}
