package io.money.moneyie.fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.money.moneyie.R;
import io.money.moneyie.model.utilities.PageAdapterGraphic;

public class Fragment_Statistics extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistics, container, false);

        startTabView();

        return view;
    }

    private void startTabView() {
        //adding names of tabs
        final TabLayout tabLayout = view.findViewById(R.id.tabLayoutGraphics);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.DAY)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.MONTH)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.YEAR)));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.PERIOD));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = view.findViewById(R.id.viewPagerGraphics);
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
