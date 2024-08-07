package com.example.ktech;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.HashSet;

public class GridFragment extends Fragment {

    private CalendarView calendarView;
    private HashSet<String> fixedHolidays;
    private MealDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);

        // 데이터베이스 초기화
        dbHelper = new MealDatabaseHelper(getContext());

        initializeHolidays();
        calendarView = view.findViewById(R.id.calendarView);

        // 커스텀 날짜 선택 리스너
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                String selectedDateString = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
                if (isWeekendOrHoliday(selectedDate) || !dbHelper.hasMenuForDate(selectedDateString)) {
                    Toast.makeText(getContext(), "급식이 없는 날입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    MealFragment mealFragment = MealFragment.newInstance(year, month, dayOfMonth);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    transaction.replace(R.id.fragment_container, mealFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        return view;
    }

    private boolean isWeekendOrHoliday(Calendar date) {
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
            return true;
        }

        String monthDay = String.format("%d-%d", date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
        return fixedHolidays.contains(monthDay);
    }

    private void initializeHolidays() {
        fixedHolidays = new HashSet<>();
        // 매년 같은 날짜에 발생하는 공휴일 추가 (월-일 형식)
        fixedHolidays.add("1-1"); // 신정
        fixedHolidays.add("3-1"); // 삼일절
        fixedHolidays.add("5-5"); // 어린이날
        fixedHolidays.add("6-6"); // 현충일
        fixedHolidays.add("8-15"); // 광복절
        fixedHolidays.add("10-3"); // 개천절
        fixedHolidays.add("10-9"); // 한글날
        fixedHolidays.add("12-25"); // 성탄절
        // 추가 공휴일 필요 시 여기에 추가
    }
}
