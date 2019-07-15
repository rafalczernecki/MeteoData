package com.rafalczernecki.meteodata.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.rafalczernecki.meteodata.R;
import com.rafalczernecki.meteodata.entities.SingleQuantityMeasure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DisplayActivity extends AppCompatActivity {
    public static final Integer MILLIS_IN_DAY = 86400000;
    public static final String MEASURES_ARRAY_LIST = "jsonMeasuresList";

    private GraphView graph;
    private ArrayList<SingleQuantityMeasure> measures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        Gson gson = new Gson();
        measures = gson.fromJson(getIntent().getStringExtra(MEASURES_ARRAY_LIST), new TypeToken<ArrayList<SingleQuantityMeasure>>() {
        }.getType());
        graph = findViewById(R.id.graph);
        configureAndDrawGraph();
    }

    private void configureAndDrawGraph() {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        if (measures.size() == 1) {
            series.setDrawDataPoints(true);
        }
        series.setDrawBackground(true);
        series.setBackgroundColor(R.color.colorGraphBackground);
        for (SingleQuantityMeasure measure : measures) {
            series.appendData(new DataPoint(measure.getMeasureTimeInMillis(), measure.getValue()), true, measures.size());
        }
        graph.addSeries(series);

        final Date date = new Date();
        final DateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        final Long firstRecordMillisDate = measures.get(0).getMeasureTimeInMillis();
        Long lastRecordMillisDate = measures.get(measures.size() - 1).getMeasureTimeInMillis();
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    date.setTime((long) value);
                    return hourFormat.format(date);
                }
                return super.formatLabel(value, isValueX);
            }
        });

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(firstRecordMillisDate);
        if (lastRecordMillisDate - firstRecordMillisDate > MILLIS_IN_DAY) {
            graph.getViewport().setMaxX(firstRecordMillisDate + MILLIS_IN_DAY);
        } else graph.getViewport().setMaxX(lastRecordMillisDate);
        graph.getViewport().setScalable(true);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                final Date date = new Date();
                final DateFormat df = new SimpleDateFormat("dd.MM.yyy HH:mm", Locale.ENGLISH);
                date.setTime((long) dataPoint.getX());
                Toast.makeText(getApplicationContext(), df.format(date) + "  /  " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}