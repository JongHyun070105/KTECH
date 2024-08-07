package com.example.ktech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private MealDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize database helper
        dbHelper = new MealDatabaseHelper(getContext());

        // Find the ImageView and set an image resource
        ImageView menuImage = view.findViewById(R.id.menu_image);
        menuImage.setImageResource(R.drawable.meal_image_placeholder); // 이미지 리소스 설정

        // Find the TextView for the menu
        TextView menuText = view.findViewById(R.id.menu_text);

        // Get today's date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(calendar.getTime());

        // Get today's menu from the database
        String todayMenu = dbHelper.getMenuForDate(todayDate);
        menuText.setText(todayMenu);

        return view;
    }
}
