package io.money.moneyie.model.utilities;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.money.moneyie.fragments.graphicsTabs.FragmentTab_PeriodGraphic;
import io.money.moneyie.fragments.graphicsTabs.FragmentTab_YearGraphic;
import io.money.moneyie.fragments.graphicsTabs.FragmentTab_DayGraphic;
import io.money.moneyie.fragments.graphicsTabs.FragmentTab_MonthGraphic;

//page adapter used for the sliding tabs in the fragment Fragment_Graphics
public class PageAdapterGraphic extends FragmentPagerAdapter {

    private int numberOfTabs;

    public PageAdapterGraphic(FragmentManager fm, int tabs) {
        super(fm);
        if(tabs >= 0) {
            this.numberOfTabs = tabs;
        } else {
            this.numberOfTabs = 0;
        }
    }

    @Override
    public Fragment getItem(int position) {
        //getting the items with switch/case construction
        switch (position) {
            case 0:
                FragmentTab_DayGraphic day = new FragmentTab_DayGraphic();
                return day;
            case 1:
                FragmentTab_MonthGraphic month = new FragmentTab_MonthGraphic();
                return month;
            case 2:
                FragmentTab_YearGraphic year = new FragmentTab_YearGraphic();
                return year;
            case 3:
                FragmentTab_PeriodGraphic period = new FragmentTab_PeriodGraphic();
                return period;
            default:
                return null;
        }
    }

    @Override
    //get the number of the tabs loaded
    public int getCount() {
        return numberOfTabs;
    }
}
