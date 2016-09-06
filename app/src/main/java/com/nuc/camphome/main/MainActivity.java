package com.nuc.camphome.main;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.nuc.camphome.R;
import com.nuc.camphome.beans.Personnel;

/**
 * Created by 景贝贝 on 2016/8/18.
 */
public class MainActivity extends AppCompatActivity {
    private SimpleFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Personnel personnel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      personnel= (Personnel) getIntent().getSerializableExtra("personnel");
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this,personnel);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.addTab(tabLayout.newTab().setText("主页"));
//        tabLayout.addTab(tabLayout.newTab().setText("1"));
//        tabLayout.addTab(tabLayout.newTab().setText("2"));

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab one = tabLayout.getTabAt(0);
        TabLayout.Tab two = tabLayout.getTabAt(1);
        TabLayout.Tab three = tabLayout.getTabAt(2);
//        one.setIcon(R.drawable.selector_menu_home);
//        two.setIcon(R.mipmap.ic_launcher);
        one.setCustomView(R.layout.item_tab_layout_custom);
        two.setCustomView(R.layout.item_tab_two_layout_custom);
        three.setCustomView(R.layout.item_tab_three_layout_custom);


    }

}
