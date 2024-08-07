package com.example.ktech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class InfoFragment extends Fragment {
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        // Set status bar color
        requireActivity().getWindow().setStatusBarColor(Color.WHITE);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        sharedPreferences = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);

        TextView schoolNameText = view.findViewById(R.id.school_name_text);
        TextView zeroFoodWasteText = view.findViewById(R.id.zero_food_waste_text);
        Button logoutButton = view.findViewById(R.id.logout_button);
        ImageView instagramIcon = view.findViewById(R.id.instagram_icon);
        ImageView facebookIcon = view.findViewById(R.id.facebook_icon);
        ImageView twitterIcon = view.findViewById(R.id.twitter_icon);
        ImageView linkedinIcon = view.findViewById(R.id.linkedin_icon);
        ImageView youtubeIcon = view.findViewById(R.id.youtube_icon);

        String schoolName = sharedPreferences.getString(LoginActivity.KEY_SCHOOL_NAME, "Unknown School");
        schoolNameText.setText("학교명 : " + schoolName);

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

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        instagramIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("https://www.instagram.com/h_.yun07");
            }
        });

        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("https://www.instagram.com/tae0_735");
            }
        });

        twitterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("https://www.instagram.com/sinjaeu134");
            }
        });

        linkedinIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("https://www.instagram.com/j_hwanhee");
            }
        });

        youtubeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("https://www.instagram.com/j._.siu0o");
            }
        });

        return view;
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(LoginActivity.KEY_SCHOOL_NAME);
        editor.apply();

        // 위젯 업데이트 브로드캐스트 발송
        Intent intent = new Intent(MealWidgetProvider.ACTION_UPDATE_WIDGET);
        requireContext().sendBroadcast(intent);

        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
        getActivity().finish();
    }

    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
