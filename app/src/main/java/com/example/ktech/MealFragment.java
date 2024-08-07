package com.example.ktech;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MealFragment extends Fragment {

    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";
    private static final String ARG_DAY = "day";

    private int year, month, day;
    private GestureDetectorCompat gestureDetector;
    private Set<String> fixedHolidays;
    private MealDatabaseHelper dbHelper;

    public static MealFragment newInstance(int year, int month, int day) {
        MealFragment fragment = new MealFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            year = getArguments().getInt(ARG_YEAR);
            month = getArguments().getInt(ARG_MONTH);
            day = getArguments().getInt(ARG_DAY);
        }
        setHasOptionsMenu(true);

        // 공휴일 설정
        fixedHolidays = new HashSet<>();
        fixedHolidays.add("1-1");
        fixedHolidays.add("3-1"); // 삼일절
        fixedHolidays.add("5-5"); // 어린이날
        fixedHolidays.add("6-6"); // 현충일
        fixedHolidays.add("8-15"); // 광복절
        fixedHolidays.add("10-3"); // 개천절
        fixedHolidays.add("10-9"); // 한글날
        fixedHolidays.add("12-25"); // 성탄절
        // 추가 공휴일을 이곳에 추가

        // 데이터베이스 초기화
        dbHelper = new MealDatabaseHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal, container, false);

        TextView dateTextView = view.findViewById(R.id.date_text);
        dateTextView.setText(String.format("%d월 %d일 급식", month + 1, day));

        ImageView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                GridFragment fragmentGrid = new GridFragment();
                transaction.replace(R.id.fragment_container, fragmentGrid);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        gestureDetector = new GestureDetectorCompat(getContext(), new SwipeGestureListener());

        // Add touch listener to content layout
        LinearLayout contentLayout = view.findViewById(R.id.content_layout);
        contentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        // 급식 데이터 조회 및 표시
        displayMealData(view);

        return view;
    }

    private void displayMealData(View view) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String date = String.format("%d-%02d-%02d", year, month + 1, day);
        Cursor cursor = db.rawQuery("SELECT menu FROM meals WHERE date = ?", new String[]{date});

        TextView menuTextView = view.findViewById(R.id.menu_text);
        if (cursor.moveToFirst()) {
            String menu = cursor.getString(cursor.getColumnIndexOrThrow("menu"));
            menuTextView.setText(menu);
        } else {
            menuTextView.setText("급식 데이터가 없습니다");
        }

        cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            Log.d("Gesture", "onSwipeRight");
                            onSwipeRight();
                        } else {
                            Log.d("Gesture", "onSwipeLeft");
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    private void onSwipeRight() {
        changeDate(-1);
    }

    private void onSwipeLeft() {
        changeDate(1);
    }

    private void changeDate(int dayOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        do {
            calendar.add(Calendar.DAY_OF_MONTH, dayOffset);
        } while (isHolidayOrWeekend(calendar));

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        updateDate(dayOffset);
    }

    private boolean isHolidayOrWeekend(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String monthDay = String.format("%d-%d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY || fixedHolidays.contains(monthDay);
    }

    private void updateDate(int dayOffset) {
        MealFragment newFragment = MealFragment.newInstance(year, month, day);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (dayOffset > 0) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
