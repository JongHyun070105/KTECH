package com.example.ktech;

import static com.example.ktech.LoginActivity.KEY_SCHOOL_NAME;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends FragmentActivity {
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 100;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101;

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

        Locale locale = new Locale("ko");
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);

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

        if (savedInstanceState == null) {
            viewPager.setCurrentItem(0);
        }

        checkAndRequestNotificationPermission();
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

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, REQUEST_CODE_POST_NOTIFICATIONS);
            } else {
                checkAndRequestExactAlarmPermission();
            }
        } else {
            checkAndRequestExactAlarmPermission();
        }
    }

    private void checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!getSystemService(AlarmManager.class).canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
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
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkAndRequestExactAlarmPermission();
            } else {
                showNotificationPermissionDeniedDialog();
            }
        } else if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationReceiver.setDailyAlarms(this);
            } else {
                Toast.makeText(this, "정확한 알람을 설정하려면 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showNotificationPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("알림 권한 필요")
                .setMessage("급식 알림을 보내려면 권한이 필요합니다. 설정에서 알림 권한을 활성화해주세요.")
                .setPositiveButton("설정으로 이동", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(intent);
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_SCHOOL_NAME);
        editor.apply();

        Intent intent = new Intent(MealWidgetProvider.ACTION_UPDATE_WIDGET);
        sendBroadcast(intent);

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
