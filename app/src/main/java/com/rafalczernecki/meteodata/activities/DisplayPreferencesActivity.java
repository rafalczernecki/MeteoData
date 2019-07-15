package com.rafalczernecki.meteodata.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.rafalczernecki.meteodata.R;
import com.rafalczernecki.meteodata.entities.Server;
import com.rafalczernecki.meteodata.entities.SingleQuantityMeasure;
import com.rafalczernecki.meteodata.fragments.ConnectionStatusIndicatorFragment;
import com.rafalczernecki.meteodata.fragments.DateTimeDialogFragment;
import com.rafalczernecki.meteodata.interfaces.MeasuresReceivable;
import com.rafalczernecki.meteodata.interfaces.ServerConnectionCheckReceiver;
import com.rafalczernecki.meteodata.network.CommunicationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DisplayPreferencesActivity extends AppCompatActivity implements MeasuresReceivable, ServerConnectionCheckReceiver {
    private static final String TAG = "DateAndTimePicker";
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    public static final String ARG_START_TIMESTAMP_IN_MILLIS = "startTimestampInMillis";
    public static final String ARG_END_TIMESTAMP_IN_MILLIS = "endTimestampInMillis";
    public static final String ARG_JSON_MEASURES_LIST = "jsonMeasuresList";

    private DateTimeDialogFragment dateTimeDialog;
    private long startTimestampInMillis;
    private long endTimestampInMillis;
    private CommunicationHelper communicationHelper;
    private ConnectionStatusIndicatorFragment connectionStatusFragment;
    private Integer connectionStatus;

    @BindView(R.id.start_date_button)
    Button pickStartDateBtn;
    @BindView(R.id.end_date_button)
    Button pickEndDateBtn;
    @BindView(R.id.display_preferences_ok_button)
    Button okBtn;
    @BindView(R.id.start_date_text_view)
    TextView startDateTextView;
    @BindView(R.id.end_date_text_view)
    TextView endDateTextView;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_preferences);
        ButterKnife.bind(this);
        initializeDateAndTimePicker();
        communicationHelper = new CommunicationHelper(getApplicationContext(), this, this);
        if (savedInstanceState == null) connectionStatus = Server.CONNECTION_UNDEFINED;
        else {
            connectionStatus = savedInstanceState.getInt(ARG_CONNECTION_STATUS);
            startTimestampInMillis = savedInstanceState.getLong(ARG_START_TIMESTAMP_IN_MILLIS);
            endTimestampInMillis = savedInstanceState.getLong(ARG_END_TIMESTAMP_IN_MILLIS);
            if (connectionStatus == Server.CONNECTION_CHECKING)
                communicationHelper.testConnectionWithServer();
            else if (connectionStatus == Server.CONNECTION_DOWNLOADING_DATA) {
                communicationHelper.getMeteoDataFromServer(getTypeOfData(), startTimestampInMillis, endTimestampInMillis);
            }
        }
        connectionStatusFragment = ConnectionStatusIndicatorFragment.newInstance(connectionStatus);
        addIndicatorFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CONNECTION_STATUS, connectionStatus);
        outState.putLong(ARG_START_TIMESTAMP_IN_MILLIS, startTimestampInMillis);
        outState.putLong(ARG_END_TIMESTAMP_IN_MILLIS, endTimestampInMillis);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_prefs_action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, ServerSettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.start_date_button)
    public void handleStartDateBtn() {
        pickStartDateBtn.setEnabled(false);
        showDateTimePickerFragment(TIME_TYPE.START_TIME);
    }

    @OnClick(R.id.end_date_button)
    public void handleEndDateBtn() {
        pickEndDateBtn.setEnabled(false);
        showDateTimePickerFragment(TIME_TYPE.END_TIME);
    }

    @OnClick(R.id.display_preferences_ok_button)
    public void handleOkBtn() {
        if (checkUserInput()) {
            okBtn.setEnabled(false);
            setConnectionStatus(Server.CONNECTION_CHECKING);
        }
    }

    private void initializeDateAndTimePicker() {
        dateTimeDialog = DateTimeDialogFragment.newInstance(
                getString(R.string.picker_label),
                getString(android.R.string.ok),
                getString(android.R.string.cancel),
                null);
        dateTimeDialog.set24HoursMode(true);
        try {
            dateTimeDialog.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMM dd"));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private enum TIME_TYPE {
        START_TIME,
        END_TIME
    }

    private void showDateTimePickerFragment(final TIME_TYPE timeType) {
        dateTimeDialog.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                if (timeType == TIME_TYPE.START_TIME) {
                    long startTime = calendar.getTimeInMillis();
                    if (startTime >= endTimestampInMillis && endTimestampInMillis != 0) {
                        Toast.makeText(getApplicationContext(),
                                R.string.start_time_before_end_time,
                                Toast.LENGTH_LONG).show();
                    } else {
                        setStartTimestampInMillis(calendar.getTimeInMillis());
                        startDateTextView.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date));
                    }

                } else {
                    long endTime = calendar.getTimeInMillis();
                    if (endTime < startTimestampInMillis) {
                        Toast.makeText(getApplicationContext(),
                                R.string.end_time_before_start_time,
                                Toast.LENGTH_LONG).show();
                    } else {
                        setEndTimestampInMillis(endTime);
                        endDateTextView.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date));
                    }
                }
            }

            @Override
            public void onNegativeButtonClick(Date date) {
            }

        });

        dateTimeDialog.startAtCalendarView();
        dateTimeDialog.setDefaultDateTime(new GregorianCalendar().getTime());
        dateTimeDialog.setMaximumDateTime(new Date(Calendar.getInstance().getTimeInMillis()));
        dateTimeDialog.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
    }

    @Override
    public void receiveMeasures(ArrayList<SingleQuantityMeasure> measures) {
        setConnectionStatus(Server.CONNECTION_UNDEFINED);
        if (measures.size() != 0) {
            Intent i = new Intent(getApplicationContext(), DisplayActivity.class);
            Gson gson = new Gson();
            String jsonMeasuresList = gson.toJson(measures);
            i.putExtra(ARG_JSON_MEASURES_LIST, jsonMeasuresList);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(),
                    R.string.no_data_to_display,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void receiveServerConnectionStatus(Integer connectionStatus) {
        okBtn.setEnabled(true);
        setConnectionStatus(connectionStatus);
    }

    @Override
    public void setConnectionStatus(Integer connectionStatus) {
        this.connectionStatus = connectionStatus;
        if (connectionStatusFragment != null) {
            connectionStatusFragment.setConnectionStatus(connectionStatus);
        }
        if (connectionStatus == Server.CONNECTION_OK) {
            this.connectionStatus = Server.CONNECTION_DOWNLOADING_DATA;
            if (connectionStatusFragment != null) {
                connectionStatusFragment.setConnectionStatus(Server.CONNECTION_DOWNLOADING_DATA);
            }
            getMeteoDataFromServer();
        } else if (connectionStatus == Server.CONNECTION_CHECKING) {
            communicationHelper.testConnectionWithServer();
        }
    }

    public void setStartTimestampInMillis(Long startTimestampInMillis) {
        this.startTimestampInMillis = startTimestampInMillis;
    }

    public void setEndTimestampInMillis(Long endTimestampInMillis) {
        this.endTimestampInMillis = endTimestampInMillis;
    }

    private Integer getTypeOfData() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.air_pressure_radio_button:
                return SingleQuantityMeasure.AIR_PRESSURE;
            case R.id.humidity_radio_button:
                return SingleQuantityMeasure.HUMIDITY;
            default:
                return SingleQuantityMeasure.TEMPERATURE;
        }
    }

    private boolean checkUserInput() {
        if (radioGroup.getCheckedRadioButtonId() == -1 ||
                TextUtils.isEmpty(startDateTextView.getText().toString()) ||
                TextUtils.isEmpty(endDateTextView.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.missing_data_update, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getMeteoDataFromServer() {
        communicationHelper.getMeteoDataFromServer(getTypeOfData(),
                startTimestampInMillis, endTimestampInMillis);
    }

    private void addIndicatorFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.disp_prefs_connection_indicator_fragment_container, connectionStatusFragment)
                .commit();
    }

    public void enableDatePick() {
        pickStartDateBtn.setEnabled(true);
        pickEndDateBtn.setEnabled(true);
    }
}
