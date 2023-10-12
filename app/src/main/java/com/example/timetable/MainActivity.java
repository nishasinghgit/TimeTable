package com.example.timetable;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText days_entryET, num_periods_entryET, num_subjects_labelET, lunch_break_periodET;
    Button button_nextButton;

    LinearLayout screen_one, screen_two, screen_three;

    int[] class_timings_1;
    List<List<Integer>> subjects_1 = new ArrayList<>();
    Map<String, Integer> days_1 = new HashMap<>();
    int currentScreen = 1;


    // TimeTable Transfers
    String[] days;
    int num_periods, num_subjects, lunch_break_period;
    HashMap<Integer, String> class_timings = new HashMap<>();
    HashMap<String, String> subjects = new HashMap<>();
    HashMap<String, ArrayList<String>> teacher_unavailability = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screen_one = findViewById(R.id.screen_one);
        screen_two = findViewById(R.id.screen_two);
        screen_three = findViewById(R.id.screen_three);


        days_entryET = findViewById(R.id.days_entry);
        num_periods_entryET = findViewById(R.id.num_periods_entry);
        num_subjects_labelET = findViewById(R.id.num_subjects_entry);
        lunch_break_periodET = findViewById(R.id.lunch_break_period_entry);
        button_nextButton = findViewById(R.id.button_next);
        button_nextButton.setOnClickListener(view -> {
            switch (currentScreen) {
                case 1:
                    proceedToScreen2();
                    break;
                case 2:
                    proceedToScreen3();
                    break;
                case 3:
                    generateTimeTable();
                    break;
                default:
                    Toast.makeText(this, "Wrong Option", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    public void proceedToScreen2() {
        days = days_entryET.getText().toString().trim().split(",");
        if (days_entryET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter Valid Days", Toast.LENGTH_SHORT).show();
            return;
        }
        if (num_periods_entryET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter Valid Periods", Toast.LENGTH_SHORT).show();
            return;
        }
        if (num_subjects_labelET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter Valid Subjects", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lunch_break_periodET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter Valid Lunch Break Period", Toast.LENGTH_SHORT).show();
            return;
        }
        num_periods = Integer.parseInt(num_periods_entryET.getText().toString().trim());
        num_subjects = Integer.parseInt(num_subjects_labelET.getText().toString().trim());
        lunch_break_period = Integer.parseInt(lunch_break_periodET.getText().toString().trim());
        if (num_periods <= 0) {
            Toast.makeText(this, "Enter Valid Periods", Toast.LENGTH_SHORT).show();
            return;
        }
        if (num_subjects <= 0) {
            Toast.makeText(this, "Enter Valid Subjects", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lunch_break_period <= 0) {
            Toast.makeText(this, "Enter Valid Lunch Break Period", Toast.LENGTH_SHORT).show();
            return;
        }
        class_timings_1 = new int[num_periods];
        for (int i = 0; i < num_periods; i++) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(String.format("Enter the timing for period %d (e.g., '9:00 - 10:00')", i + 1));

            EditText editText = new EditText(this);
            editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            int id = View.generateViewId();
            class_timings_1[i] = id;
            editText.setId(id);

            screen_two.addView(textView);
            screen_two.addView(editText);
        }
        for (int i = 0; i < num_subjects; i++) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(String.format("Subject %d:", i + 1));
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(16);

            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView1.setText("Enter subject name");

            EditText editText = new EditText(this);
            editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            int subject_id = View.generateViewId();
            editText.setId(subject_id);

            TextView textView2 = new TextView(this);
            textView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView2.setText("Enter teacher name");

            EditText editText1 = new EditText(this);
            editText1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            int teacher_id = View.generateViewId();
            editText1.setId(teacher_id);

            List<Integer> newEntry = new ArrayList<>();
            newEntry.add(subject_id);
            newEntry.add(teacher_id);
            subjects_1.add(newEntry);

            screen_two.addView(textView);
            screen_two.addView(textView1);
            screen_two.addView(editText);
            screen_two.addView(textView2);
            screen_two.addView(editText1);
        }
        screen_one.setVisibility(View.GONE);
        screen_two.setVisibility(View.VISIBLE);
        screen_three.setVisibility(View.GONE);
        currentScreen++;
    }

    public void proceedToScreen3() {
        for (int i = 0; i < class_timings_1.length; i++) {
            String class_time = ((EditText) findViewById(class_timings_1[i])).getText().toString().trim();
            if (class_time.isEmpty()) {
                Toast.makeText(this, "Enter Valid Class Time", Toast.LENGTH_SHORT).show();
                return;
            }
            class_timings.put(i, class_time);
        }
        for (int i = 0; i < subjects_1.size(); i++) {
            String subject_name = ((EditText) findViewById(subjects_1.get(i).get(0))).getText().toString().trim();
            String teacher_name = ((EditText) findViewById(subjects_1.get(i).get(1))).getText().toString().trim();
            if (subject_name.isEmpty()) {
                Toast.makeText(this, "Enter Valid Subject Name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (teacher_name.isEmpty()) {
                Toast.makeText(this, "Enter Valid Teacher Name", Toast.LENGTH_SHORT).show();
                return;
            }
            subjects.put(subject_name, teacher_name);
        }
        for (String teacher : subjects.values()) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(String.format("Enter the days %s is unavailable separated by commas", teacher));

            EditText editText = new EditText(this);
            editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            int id = View.generateViewId();
            days_1.put(teacher, id);
            editText.setId(id);

            screen_three.addView(textView);
            screen_three.addView(editText);
        }
        screen_one.setVisibility(View.GONE);
        screen_two.setVisibility(View.GONE);
        screen_three.setVisibility(View.VISIBLE);
        currentScreen++;
    }


    public void generateTimeTable() {
        for (Map.Entry<String, Integer> entry : days_1.entrySet()) {
            ArrayList<String> unavailability = new ArrayList<>(Arrays.asList(((EditText) findViewById(entry.getValue())).getText().toString().trim().split(",")));
            teacher_unavailability.put(entry.getKey(), unavailability);
        }


//        TODO: JUST FOR TESTING
//        days = new String[]{"1", "2", "3", "4", "5"};
//        num_periods = 6;
//        num_subjects = 5;
//        lunch_break_period = 3;
//        class_timings.put(1, "10:00 - 11:00");
//        class_timings.put(2, "11:00 - 12:00");
//        class_timings.put(3, "12:00 - 13:00");
//        class_timings.put(4, "13:00 - 14:00");
//        class_timings.put(5, "14:00 - 15:00");
//        class_timings.put(6, "15:00 - 16:00");
//        subjects.put("English", "Ms. Charu");
//        subjects.put("Maths", "Mr. Chintu");
//        subjects.put("IP", "Mr. Faarukh");
//        subjects.put("Eco", "Ms. Ruchi");
//        subjects.put("Accounts", "Ms. Preeti");
//        teacher_unavailability.put("Ms. Charu", new ArrayList<>(Arrays.asList("1", "3")));

        Intent intent = new Intent(this, TimeTable.class);
        intent.putExtra("days", days);
        intent.putExtra("num_periods", num_periods);
        intent.putExtra("num_subjects", num_subjects);
        intent.putExtra("lunch_break_period", lunch_break_period);
        intent.putExtra("class_timings", class_timings);
        intent.putExtra("subjects", subjects);
        intent.putExtra("teacher_unavailability", teacher_unavailability);
        startActivity(intent);
    }
}