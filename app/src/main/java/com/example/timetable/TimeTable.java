package com.example.timetable;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TimeTable extends AppCompatActivity {

    ScrollView screen_time_table;

    String[] days;

    String[] daysNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    int num_periods, num_subjects, lunch_break_period;
    Map<Integer, String> class_timings = new HashMap<>();
    Map<String, String> subjects = new HashMap<>();
    Map<String, ArrayList<String>> teacher_unavailability = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        screen_time_table = findViewById(R.id.screen_time_table);

        Intent intent = getIntent();

        days = intent.getStringArrayExtra("days");
        num_periods = intent.getIntExtra("num_periods", 0);
        num_subjects = intent.getIntExtra("num_subjects", 0);
        lunch_break_period = intent.getIntExtra("lunch_break_period", 0);
        class_timings = (Map<Integer, String>) intent.getSerializableExtra("class_timings");
        subjects = (Map<String, String>) intent.getSerializableExtra("subjects");
        teacher_unavailability = (Map<String, ArrayList<String>>) intent.getSerializableExtra("teacher_unavailability");
        generateTimeTable();
    }

    public static boolean isTeacherAvailable(String teacher, String day, Map<String, ArrayList<String>> teacherUnavailability) {
        ArrayList<String> unavailableDays = teacherUnavailability.get(teacher);
        if (unavailableDays == null) {
            return true;
        }
        return !unavailableDays.contains(day);
    }

    public void generateTimeTable() {
        Map<String, Map<Integer, String>> timetable = new HashMap<>();
        Map<String, Integer> teacherPeriodCount = new HashMap<>();
        Set<String> dayFirstPeriod = new HashSet<>();
        Map<String, Integer> subjectWeekCount = new HashMap<>();
        for (String day : days) {
            Map<Integer, String> periods = new HashMap<>();
            for (int period = 1; period <= num_periods; period++) {
                periods.put(period, "Free");
            }
            timetable.put(day, periods);
        }

        for (String day : days) {
            teacherPeriodCount.clear();
            for (int period = 1; period <= num_periods + 1; period++) {
                int actualPeriod;
                if (period == lunch_break_period + 1) {
                    timetable.get(day).put(period, "Lunch Break");
                    continue;
                } else if (period > lunch_break_period + 1) {
                    actualPeriod = period - 1;
                } else {
                    actualPeriod = period;
                }

                List<Map.Entry<String, String>> availableSubjects = new ArrayList<>(subjects.entrySet());
                Collections.shuffle(availableSubjects);

                for (Map.Entry<String, String> entry : availableSubjects) {
                    String subject = entry.getKey();
                    String teacher = entry.getValue();

                    if (!isTeacherAvailable(teacher, day, teacher_unavailability)) {
                        continue;
                    }

                    if (teacherPeriodCount.getOrDefault(teacher, 0) >= 2) {
                        continue;
                    }

                    if (timetable.get(day).values().contains(subject)) {
                        continue;
                    }

                    if (actualPeriod == 1 && dayFirstPeriod.contains(subject)) {
                        continue;
                    }

                    timetable.get(day).put(period, subject + " (" + teacher + ")");
                    teacherPeriodCount.put(teacher, teacherPeriodCount.getOrDefault(teacher, 0) + 1);
                    subjectWeekCount.put(subject, subjectWeekCount.getOrDefault(subject, 0) + 1);

                    if (actualPeriod == 1) {
                        dayFirstPeriod.add(subject);
                    }
                    break;
                }
            }
        }
        int ctCount = 1;
        for (String class_timing : class_timings.values()) {
            if (!timetable.containsKey("0")) {
                timetable.put("0", new HashMap<>());
            }
            if (ctCount == lunch_break_period + 1) {
                timetable.get("0").put(ctCount, "");
                ctCount++;
            }
            timetable.get("0").put(ctCount, class_timing);
            ctCount++;
        }


        String[][] data = new String[timetable.size()][];
        int i1 = 0;
        for (Map.Entry<String, Map<Integer, String>> dayEntry : timetable.entrySet()) {
            Map<Integer, String> periods = dayEntry.getValue();
            String[] row = new String[periods.size() + 1];
            int j = 0;
            if (i1 != 0 && i1 < daysNames.length) {
                row[j] = daysNames[i1 - 1];
            } else {
                row[j] = "";
            }
            j++;
            for (Map.Entry<Integer, String> periodEntry : periods.entrySet()) {
                String period = periodEntry.getValue();
                row[j] = period;
                j++;
            }
            data[i1] = row;
            i1++;
        }

        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT));
        gridLayout.setRowCount(data.length);
        gridLayout.setColumnCount(data[0].length);

        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.WHITE);
        border.setStroke(1, Color.BLACK);

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                TextView textView = new TextView(this);
                textView.setText(data[i][j]);
                textView.setGravity(Gravity.START);
                textView.setBackground(border);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i, 1f);
                params.columnSpec = GridLayout.spec(j, 1f);
                textView.setLayoutParams(params);
                textView.setPadding(20, 20, 20, 20);

                gridLayout.addView(textView);
            }
        }
        screen_time_table.addView(gridLayout);
    }
}