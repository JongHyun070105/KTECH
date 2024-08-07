package com.example.ktech;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MealDatabaseHelper extends SQLiteOpenHelper {
    public boolean hasMenuForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM meals WHERE date = ?", new String[]{date});
        boolean hasMenu = cursor.moveToFirst();
        cursor.close();
        return hasMenu;
    }
    public String getMenuForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT menu FROM meals WHERE date = ?", new String[]{date});
        String menu = "";
        if (cursor.moveToFirst()) {
            menu = cursor.getString(0);
        }
        cursor.close();
        return menu;
    }
    private static final String DATABASE_NAME = "meal.db";
    private static final int DATABASE_VERSION = 1;
    private Set<String> fixedHolidays;
    private Context context;

    public MealDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        initializeHolidays();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE meals ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "date TEXT NOT NULL, "
                + "menu TEXT NOT NULL);");

        insertMealData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS meals");
        onCreate(db);
    }

    private void insertMealData(SQLiteDatabase db) {
        Calendar start = Calendar.getInstance();
        start.set(2024, Calendar.MARCH, 3);
        Calendar end = Calendar.getInstance();
        end.set(2025, Calendar.JANUARY, 16);

        try {
            // JSON 파일에서 메뉴 데이터를 읽어옵니다.
            InputStream is = context.getAssets().open("menu.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONArray menus = new JSONArray(json);
            int menuIndex = 0;

            while (!start.after(end)) {
                if (!isHolidayOrWeekend(start)) {
                    String date = String.format("%d-%02d-%02d", start.get(Calendar.YEAR), start.get(Calendar.MONTH) + 1, start.get(Calendar.DAY_OF_MONTH));
                    String menu = menus.getJSONObject(menuIndex % menus.length()).getString("menu");
                    db.execSQL("INSERT INTO meals (date, menu) VALUES (?, ?)", new Object[]{date, menu});
                    menuIndex++;
                }
                start.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (Exception e) {
            Log.e("MealDatabaseHelper", "Error reading JSON file", e);
        }
    }

    private boolean isHolidayOrWeekend(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String monthDay = String.format("%d-%d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY || fixedHolidays.contains(monthDay);
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
