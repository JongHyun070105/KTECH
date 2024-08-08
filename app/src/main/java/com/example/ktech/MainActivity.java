package com.example.ktech;

import static com.example.ktech.LoginActivity.KEY_SCHOOL_NAME;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends FragmentActivity {
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 100;

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
            } else if (id == R.id.navigation_grid) {
                viewPager.setCurrentItem(1);
            } else if (id == R.id.navigation_info) {
                viewPager.setCurrentItem(2);
            }
            return true;
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

        // 권한 확인 및 요청
        checkAndRequestExactAlarmPermission();
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

    private void checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!getSystemService(AlarmManager.class).canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM);
            } else {
                NotificationReceiver.setDailyAlarms(this);
            }
        } else {
            NotificationReceiver.setDailyAlarms(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationReceiver.setDailyAlarms(this);
            } else {
                // 권한이 거부된 경우 사용자에게 알림
                Toast.makeText(this, "정확한 알람을 설정하려면 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
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
