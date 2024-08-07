package com.example.ktech;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "meal_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "com.example.ktech.SEND_NOTIFICATION".equals(intent.getAction())) {
            sendNotification(context);
        }
    }

    private void sendNotification(Context context) {
        MealDatabaseHelper dbHelper = new MealDatabaseHelper(context);
        Calendar calendar = Calendar.getInstance();
        String date = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        String menu = dbHelper.getMenuForDate(date);
        if (menu.isEmpty()) {
            return; // 급식 데이터가 없는 경우 알림을 보내지 않음
        }

        String[] menuItems = menu.split("\n");
        StringBuilder menuText = new StringBuilder();
        for (String item : menuItems) {
            menuText.append("• ").append(item.replaceAll("<|>", "")).append("\n");
        }

        String weeklyOrder = getWeeklyOrder(calendar.get(Calendar.WEEK_OF_YEAR));
        String notificationText = "⭐ 이번 주 급식 순서 : " + weeklyOrder + "\n" + menuText.toString().trim();

        createNotificationChannel(context);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);

        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_food)
                .setContentTitle("오늘의 급식 🍱")
                .setContentText(notificationText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private String getWeeklyOrder(int weekOfYear) {
        switch (weekOfYear % 3) {
            case 1:
                return "3 → 2 → 1";
            case 2:
                return "2 → 1 → 3";
            default:
                return "1 → 2 → 3";
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Meal Notification";
            String description = "Channel for Meal notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void setDailyAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar7AM = Calendar.getInstance();
        calendar7AM.set(Calendar.HOUR_OF_DAY, 7);
        calendar7AM.set(Calendar.MINUTE, 0);
        calendar7AM.set(Calendar.SECOND, 0);
        calendar7AM.set(Calendar.MILLISECOND, 0);

        Calendar calendar12PM = Calendar.getInstance();
        calendar12PM.set(Calendar.HOUR_OF_DAY, 12);
        calendar12PM.set(Calendar.MINUTE, 0);
        calendar12PM.set(Calendar.SECOND, 0);
        calendar12PM.set(Calendar.MILLISECOND, 0);

        PendingIntent alarmIntent7AM = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationReceiver.class).setAction("com.example.ktech.SEND_NOTIFICATION"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent alarmIntent12PM = PendingIntent.getBroadcast(context, 1, new Intent(context, NotificationReceiver.class).setAction("com.example.ktech.SEND_NOTIFICATION"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar7AM.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent7AM);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar12PM.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent12PM);
    }
}
