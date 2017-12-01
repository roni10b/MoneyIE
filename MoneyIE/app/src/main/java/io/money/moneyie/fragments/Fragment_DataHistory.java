package io.money.moneyie.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.money.moneyie.R;
import io.money.moneyie.model.utilities.PageAdapter;


public class Fragment_DataHistory extends Fragment {

    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_datahistory, container, false);
        initialise();
        startTabView();
        return view;
    }

    private void initialise() {
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
    }

    //start tab view
    private void startTabView() {
        //adding names of tabs
        tabLayout.addTab(tabLayout.newTab().setText(R.string.DAY));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.MONTH));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.YEAR));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.PERIOD));
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final PageAdapter adapter = new PageAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
