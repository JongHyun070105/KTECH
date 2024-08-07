package com.example.ktech;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 상태 표시줄 색상 설정
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));

        // Zero Food Waste 텍스트 스타일링
        TextView zeroFoodWasteText = findViewById(R.id.zero_food_waste_text);
        String text = "Zero Food Waste";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00ADB5")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Z
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 1, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // ero
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00ADB5")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // F
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 6, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // ood
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00ADB5")), 10, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // W
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 11, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // aste
        zeroFoodWasteText.setText(spannableString);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
