package com.example.chattomate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.chattomate.activities.ChangePasswordActivity;
import com.example.chattomate.activities.LoginActivity;
import com.example.chattomate.activities.SetupProfileActivity;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.fragments.ChatFragment;
import com.example.chattomate.fragments.FriendsFragment;
import com.example.chattomate.fragments.UserFragment;
import com.example.chattomate.models.Conversation;
import com.example.chattomate.service.ServiceAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ViewPager viewPager;
    BottomNavigationView bnt;
    Toolbar toolbar;
    SearchView searchView;
    AppPreferenceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        manager = new AppPreferenceManager(getApplicationContext());

        viewPager = findViewById(R.id.view_pager);
        bnt = findViewById(R.id.bottom_navigation);
        searchView = findViewById(R.id.search_view);
        searchView.setQueryHint("Tìm kiếm");
        searchView.setOnQueryTextListener(this);

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

    @Override
    protected void onStart() {
        super.onStart();
        AppPreferenceManager manager = new AppPreferenceManager(getApplicationContext());
        Log.d("DEBUG", String.valueOf(manager.tokenVaild()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.add_new_message:
                //Tạo tin nhắn mới
                manager.clear();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
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
}