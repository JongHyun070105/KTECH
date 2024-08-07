package com.example.ktech;

import static com.example.ktech.LoginActivity.KEY_SCHOOL_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends FragmentActivity {
    private SharedPreferences sharedPreferences;
    private ViewPager2 viewPager;
    private FragmentAdapter fragmentAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (id == R.id.navigation_grid) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (id == R.id.navigation_info) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 로케일을 한국어로 설정
        Locale locale = new Locale("ko");
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);

        // Check if the user is already logged in
        if (!isLoggedIn()) {
            navigateToLoginActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        fragmentAdapter = new FragmentAdapter(this);
        viewPager.setAdapter(fragmentAdapter);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    navView.setSelectedItemId(R.id.navigation_home);
                } else if (position == 1) {
                    navView.setSelectedItemId(R.id.navigation_grid);
                } else if (position == 2) {
                    navView.setSelectedItemId(R.id.navigation_info);
                }
            }
        });

        // Set default fragment
        if (savedInstanceState == null) {
            viewPager.setCurrentItem(0);
        }
    }

    private boolean isLoggedIn() {
        String schoolName = sharedPreferences.getString(KEY_SCHOOL_NAME, null);
        return !TextUtils.isEmpty(schoolName);
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_SCHOOL_NAME);
        editor.apply();

        // 위젯 업데이트 브로드캐스트 발송
        Intent intent = new Intent(MealWidgetProvider.ACTION_UPDATE_WIDGET);
        sendBroadcast(intent);

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
