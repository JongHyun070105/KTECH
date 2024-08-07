package com.example.ktech;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.Calendar;

public class MealWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_UPDATE_WIDGET = "com.example.ktech.UPDATE_WIDGET";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_UPDATE_WIDGET.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MealWidgetProvider.class));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.meal_widget);

        SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String schoolName = sharedPreferences.getString(LoginActivity.KEY_SCHOOL_NAME, null);

        if (schoolName == null) {
            views.setTextViewText(R.id.widget_meal_text1, "로그인 해주세요");
            for (int i = 2; i <= 6; i++) {
                views.setTextViewText(context.getResources().getIdentifier("widget_meal_text" + i, "id", context.getPackageName()), "");
            }
        } else {
            MealDatabaseHelper dbHelper = new MealDatabaseHelper(context);
            Calendar calendar = Calendar.getInstance();
            String date = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

            String menu = dbHelper.getMenuForDate(date);
            if (menu.isEmpty()) {
                views.setTextViewText(R.id.widget_meal_text1, "오늘의 급식 데이터가 없습니다");
                for (int i = 2; i <= 6; i++) {
                    views.setTextViewText(context.getResources().getIdentifier("widget_meal_text" + i, "id", context.getPackageName()), "");
                }
            } else {
                String[] menuItems = menu.split("\n");
                for (int i = 0; i < 6; i++) {
                    String text = (i < menuItems.length) ? menuItems[i] : "";
                    views.setTextViewText(context.getResources().getIdentifier("widget_meal_text" + (i + 1), "id", context.getPackageName()), text);
                }
            }

            String weeklyOrder = getWeeklyOrder(calendar.get(Calendar.WEEK_OF_YEAR));
            views.setTextViewText(R.id.widget_meal_order, weeklyOrder);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private String getWeeklyOrder(int weekOfYear) {
        switch (weekOfYear % 3) {
            case 1:
                return "이번 주 급식 순서: 3 → 2 → 1";
            case 2:
                return "이번 주 급식 순서: 2 → 1 → 3";
            default:
                return "이번 주 급식 순서: 1 → 2 → 3";
        }
    }
}
