package com.example.ktech;

import android.content.Context;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class SchoolNamesProcessor {
    private Context context;
    private Set<String> schoolNamesSet;

    public SchoolNamesProcessor(Context context) {
        this.context = context;
        this.schoolNamesSet = new HashSet<>();
        loadSchoolNamesFromCSV();
    }

    private void loadSchoolNamesFromCSV() {
        InputStream inputStream = context.getResources().openRawResource(R.raw.school_names);

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 0) {
                    String schoolName = nextLine[0].trim();
                    schoolNamesSet.add(schoolName);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    public boolean isSchoolNameValid(String schoolName) {
        return schoolNamesSet.contains(schoolName);
    }
}
