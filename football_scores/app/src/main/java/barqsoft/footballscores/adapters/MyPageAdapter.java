package barqsoft.footballscores.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import barqsoft.footballscores.MainScreenFragment;
import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class MyPageAdapter extends FragmentPagerAdapter{

    public static final int NUM_PAGES = 5;
    private SparseArray<String> stringDates;
    private SparseArray<String> stringNameDays;
    private Date today;
    private Context mContext;

    @Override
    public Fragment getItem(int i) {
        // Return a MainScreenFragment with the corresponding date.
        return MainScreenFragment.newInstance(stringDates.get(i));
    }

    @Override
    public int getCount()
    {
        return NUM_PAGES;
    }

    public MyPageAdapter(FragmentManager fm,Context context)
    {
        super(fm);
        mContext = context;
        stringDates = new SparseArray<>();
        stringNameDays = new SparseArray<>();
        today = new Date(System.currentTimeMillis());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < NUM_PAGES; i++) {
            Date fragmentDate = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
            stringDates.put(i, mformat.format(fragmentDate));
            stringNameDays.put(i, dayFormat.format(fragmentDate));
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position){
            case 0:return stringNameDays.get(position);
            case 1:return mContext.getString(R.string.yesterday);
            case 2:return mContext.getString(R.string.today);
            case 3:return mContext.getString(R.string.tomorrow);
            case 4:return stringNameDays.get(position);
        }
        return null;
    }
}
