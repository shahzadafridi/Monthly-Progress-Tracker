package com.signin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.signin.RoomDB.Model.ProgressHistory;
import com.signin.RoomDB.Repository.HistoryRepository;
import com.signin.constants;
import com.signin.login.MainActivity;
import com.signin.login.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.signin.constants.userEmail;

public class progresstracker extends AppCompatActivity implements View.OnClickListener {
    ImageView back;
    ProgressBar pb;
    TextView CDays;
    EditText weightEt;
    Button updateWeight;
    HistoryRepository historyRepository;

    ArrayList<ILineDataSet> weightDataSet = new ArrayList<>();
    ArrayList<Entry> values = new ArrayList<>();
    LineChart weightChart;
    int weight[] = {R.color.weight};
    int var_year=0,var_month=0,var_day = 0;
    Calendar cal;
    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progresstracker);
        calendarView = (CalendarView) findViewById(R.id.calender);
        weightChart = (LineChart) findViewById(R.id.weightChart_current);
        weightChart.getDescription().setText("");
        historyRepository = new HistoryRepository(this);
        back = findViewById(R.id.back);
        pb = findViewById(R.id.progressBar);
        CDays = findViewById(R.id.consectiveDays);
        weightEt = findViewById(R.id.weight_et);
        updateWeight = findViewById(R.id.update_weight);
        updateWeight.setOnClickListener(this);
        back.setOnClickListener(this);
        setCdays();
        if (!(MainActivity.myExercise.size() < 1)) {

            int left = MainActivity.myExercise.size();

            if (left > 4)
                left = 4;
            left = 4 - left;
            left = (int) (((float) left / (float) 4) * 100);
            pb.setProgress(left);

        }

        Date today = new Date();
        cal = Calendar.getInstance();
        cal.setTime(today);
        final int month = cal.get(Calendar.MONTH) + 1; // months start with 0...11

        historyRepository.getTasksMonthWise(month).observe(progresstracker.this, new Observer<List<ProgressHistory>>() {
            @Override
            public void onChanged(List<ProgressHistory> progressHistories) {
                Log.e("History", new Gson().toJson(progressHistories));
                if (progressHistories.size() > 0){
                    values.clear();
                    for (ProgressHistory history: progressHistories) {
                        int day = Integer.parseInt(history.getDay());
                        float progress = (float) history.getProgress();
                        Log.e("Progress", ""+progress);
                        values.add(new Entry(day,progress));
                    }
                    loadGraph();
                }
            }
        });

//        i = year , i1 = month , i2 = day

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                i1 = i1 + 1;
                var_day = i2;
                var_month = i1;
                var_year = i;
            }
        });
    }

    private void loadGraph() {
        if (values.size() > 10){
            weightChart.setScaleMinima(2.5f, 1f);
        }
        // no description text
        weightChart.getDescription().setEnabled(false);
        // enable touch gestures
        weightChart.setTouchEnabled(true);
        weightChart.setDragDecelerationFrictionCoef(0.9f);
        // enable scaling and dragging
        weightChart.setDragEnabled(true);
        weightChart.setScaleEnabled(true);
        weightChart.setDrawGridBackground(false);
        weightChart.setHighlightPerDragEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        weightChart.setPinchZoom(true);
        // get the legend (only possible after setting data)


        LineDataSet weightData = new LineDataSet(values, "Testingn");
        weightData.setColors(weight, progresstracker.this);
        weightData.setLineWidth(3);
        weightData.setValueTextSize(11);
        weightData.setDrawFilled(true);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fad_red);
        weightData.setFillDrawable(drawable);
        weightDataSet.add(weightData);
        LineData currentLineData = new LineData(weightDataSet);

        XAxis xAxis = weightChart.getXAxis();
        xAxis.setAxisMaximum(31f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1.0f);

        YAxis leftAxis = weightChart.getAxisLeft();
        leftAxis.setAxisMaximum(150f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(15.0f);

        YAxis rightAxis = weightChart.getAxisRight();
        rightAxis.setEnabled(false);

        weightChart.animateY(1000);
        weightChart.setDrawMarkers(true);
        weightChart.setData(currentLineData);
        weightChart.invalidate();
    }

    private void setCdays() {
        String userName = userEmail.split("@")[0];
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        Save(userName, date);
        int Consective_Days = Retrieve(userName).size();
        CDays.setText("Consective Days = " + Consective_Days);

    }

    public boolean Save(String key, String date) {

        try {

            ArrayList<String> retrieve = Retrieve(key);

            for (String singleDate : retrieve) {

                if (singleDate.equalsIgnoreCase(date)) {
                    return false;
                }
            }
            retrieve.add(date);
            SharedPreferences mPrefs = getSharedPreferences("CDAYS", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(retrieve);
            prefsEditor.putString(key, json);
            prefsEditor.apply();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public ArrayList<String> Retrieve(String Key) {
        SharedPreferences mPrefs = getSharedPreferences("CDAYS", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(Key, null);
        try {
            ArrayList<String> obj = gson.fromJson(json, ArrayList.class);
            if (obj == null)
                return new ArrayList<>();
            return obj;
        } catch (Exception e) {

            return new ArrayList<>();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            onBackPressed();
        } else if (v == updateWeight) {
            double progress = Double.parseDouble(weightEt.getText().toString());
            if (var_year == 0){
                var_year = cal.get(Calendar.YEAR);
            }
            if (var_month == 0){
                var_month = cal.get(Calendar.MONTH) + 1;
            }
            if (var_day ==  0){
                var_day = cal.get(Calendar.DAY_OF_MONTH);
            }

            Log.e("history", historyRepository.searcTask(var_month, var_day) ? "exists" : "not exists");

            if (historyRepository.searcTask(var_month, var_day)) {
                ProgressHistory progressHistory = new ProgressHistory();
                progressHistory.setProgress(progress);
                progressHistory.setDay(String.valueOf(var_day));
                progressHistory.setMonth(String.valueOf(var_month));
                progressHistory.setYear(String.valueOf(var_year));
                historyRepository.updateTask(progress,var_month,var_day);
            } else {
                ProgressHistory progressHistory = new ProgressHistory();
                progressHistory.setProgress(progress);
                progressHistory.setDay(String.valueOf(var_day));
                progressHistory.setMonth(String.valueOf(var_month));
                progressHistory.setYear(String.valueOf(var_year));
                historyRepository.insertTask(progressHistory);
            }
        }
    }


}
