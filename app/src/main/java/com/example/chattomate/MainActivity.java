package com.example.chattomate;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.fragments.ChatFragment;
import com.example.chattomate.fragments.FriendsFragment;
import com.example.chattomate.fragments.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigationView bnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        bnt = findViewById(R.id.bottom_navigation);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bnt.getMenu().findItem(R.id.chat).setChecked(true);
                        break;
                    case 2:
                        bnt.getMenu().findItem(R.id.friend).setChecked(true);
                        break;
                    case 3:
                        bnt.getMenu().findItem(R.id.more).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bnt.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.chat:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.friend:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.more:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return new FriendsFragment();
                case 2:
                    return new UserFragment();
                case 0:
                default:
                    return new ChatFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppPreferenceManager manager = new AppPreferenceManager(getApplicationContext());
        Log.d("DEBUG", String.valueOf(manager.tokenVaild()));
    }
}