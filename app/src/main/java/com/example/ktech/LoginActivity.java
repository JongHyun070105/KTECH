package com.example.ktech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LoginActivity extends BaseActivity {
    public static final String PREFS_NAME = "SchoolPrefs";
    public static final String KEY_SCHOOL_NAME = "schoolName";
    private EditText schoolInput;
    private SchoolNamesProcessor schoolNamesProcessor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        schoolInput = findViewById(R.id.school_input);
        Button loginButton = findViewById(R.id.login_button);
        TextView loginTitle = findViewById(R.id.login_title);
        TextView zeroFoodWasteText = findViewById(R.id.zero_food_waste_text);
        TextView schoolLabel = findViewById(R.id.school_label);

        // Set text colors
        loginTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        schoolLabel.setTextColor(ContextCompat.getColor(this, R.color.black));
        schoolInput.setTextColor(ContextCompat.getColor(this, R.color.black));

        // Set styled text for Zero Food Waste
        String text = "Zero Food Waste";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00ADB5")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Z
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 1, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // ero
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00ADB5")), 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // F
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 6, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // ood
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00ADB5")), 10, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // W
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 11, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // aste
        zeroFoodWasteText.setText(spannableString);

        // Initialize the SchoolNamesProcessor
        schoolNamesProcessor = new SchoolNamesProcessor(this);

        // Check if the user is already logged in
        if (isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String schoolName = schoolInput.getText().toString().trim();
                if (TextUtils.isEmpty(schoolName)) {
                    Toast.makeText(LoginActivity.this, "고등학교를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (schoolNamesProcessor.isSchoolNameValid(schoolName)) {
                    // Save school name to SharedPreferences
                    saveSchoolName(schoolName);
                    Toast.makeText(LoginActivity.this, "학교가 확인되었습니다. 로그인 성공!", Toast.LENGTH_SHORT).show();
                    // Update widget
                    updateWidget();
                    // Start MainActivity
                    navigateToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "존재하지 않는 학교입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isLoggedIn() {
        String schoolName = sharedPreferences.getString(KEY_SCHOOL_NAME, null);
        return !TextUtils.isEmpty(schoolName);
    }

    private void saveSchoolName(String schoolName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SCHOOL_NAME, schoolName);
        editor.apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateWidget() {
        Intent intent = new Intent(MealWidgetProvider.ACTION_UPDATE_WIDGET);
        sendBroadcast(intent);
    }
}
