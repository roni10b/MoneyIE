package io.money.moneyie.fragments;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.money.moneyie.R;
import io.money.moneyie.model.utilities.PageAdapterGraphic;

public class Fragment_Statistics extends Fragment {

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistics, container, false);
        initialise();
        startTabView();
        return view;
    }

    private void initialise() {
        tabLayout = view.findViewById(R.id.tabLayoutGraphics);
        viewPager = view.findViewById(R.id.viewPagerGraphics);
    }

    private void startTabView() {
        //adding names of tabs
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.DAY)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.MONTH)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.YEAR)));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.PERIOD));
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final PageAdapterGraphic adapter = new PageAdapterGraphic(getChildFragmentManager(), tabLayout.getTabCount());
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
