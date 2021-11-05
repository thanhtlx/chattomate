package com.example.chattomate;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.chattomate.fragments.ChatFragment;
import com.example.chattomate.fragments.FriendsFragment;
import com.example.chattomate.fragments.UserFragment;
import com.example.chattomate.helper.AppPreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppPreferenceManager appManager;
    private NotificationCompat.Builder notiBuilder;

//    private ViewPager viewPager;
//    private TabLayout tabLayout = null;
//    private FloatingActionButton addAction;
//    public static String CHAT_LIST_FRAGMENT = "CHAT_LIST";
//    public static String FRIENDS_FRAGMENT = "FRIENDS";
//    public static String USER_FRAGMENT = "USER";
//
//    private ViewPagerAdapter adapter;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        appManager = new AppPreferenceManager(getApplicationContext());

        notiBuilder = new NotificationCompat.Builder(this);
        notiBuilder.setSmallIcon(R.drawable.logo);
        notiBuilder.setContentTitle("");
        notiBuilder.setContentText("");
        notiBuilder.setAutoCancel(true);



//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        if(toolbar != null) {
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setTitle("RivChat");
//        }
//
//        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        initTab();

    }
//
//    //Khoi tao 3 tab
//    private void initTab() {
//        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorIndivateTab));
//        setupViewPager(viewPager);
//        tabLayout.setupWithViewPager(viewPager);
//
//        int[] tabIcons = {R.drawable.ic_tab_person, R.drawable.ic_tab_group, R.drawable.ic_tab_infor};
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//    }
//
//    private void setupViewPager(ViewPager viewPager) {
//        adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFrag(new ChatFragment(), CHAT_LIST_FRAGMENT);
//        adapter.addFrag(new FriendsFragment(), FRIENDS_FRAGMENT);
//        adapter.addFrag(new UserFragment(), USER_FRAGMENT);
//        addAction.setOnClickListener(((FriendsFragment) adapter.getItem(0)).onClickFloatButton.getInstance(this));
//        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(3);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                ServiceUtils.stopServiceFriendChat(MainActivity.this.getApplicationContext(), false);
//                if (adapter.getItem(position) instanceof FriendsFragment) {
//                    addAction.setVisibility(View.VISIBLE);
//                    addAction.setOnClickListener(((FriendsFragment) adapter.getItem(position)).onClickFloatButton.getInstance(MainActivity.this));
//                    addAction.setImageResource(R.drawable.plus);
//                } else if (adapter.getItem(position) instanceof GroupFragment) {
//                    addAction.setVisibility(View.VISIBLE);
//                    addAction.setOnClickListener(((GroupFragment) adapter.getItem(position)).onClickFloatButton.getInstance(MainActivity.this));
//                    addAction.setImageResource(R.drawable.ic_float_add_group);
//                } else {
//                    addAction.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.about) {
//            Toast.makeText(this, "chattomate", Toast.LENGTH_LONG).show();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    //Adapter hien thi tab
//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFrag(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            // return null to display only the icon
//            return null;
//        }
//    }

}