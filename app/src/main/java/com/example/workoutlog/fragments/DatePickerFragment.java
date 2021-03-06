package com.example.workoutlog.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.workoutlog.R;
import com.example.workoutlog.helpers.Constants;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;


@RequiresApi(api = Build.VERSION_CODES.O)
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private TextView txtDuration, txtStartTime, txtFinishTime;
    private Button btnCancel, btnOk;
    private dateSelect dateListener;
    private LocalDateTime start;
    private LocalDateTime finish;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy' at ' hh:mma");
    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private String type;
    private Switch switchManualSetTime;
    private boolean isManualTime;
    private SharedPreferences prefs;
    private int hours, minutes, seconds;

    public interface dateSelect {
        void setDate(LocalDateTime dateTimeStart, LocalDateTime dateTimeFinish) throws ExecutionException, InterruptedException;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DatePickerFragment(Date start, dateSelect dateListener) {
        this.start = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        this.dateListener = dateListener;
        this.finish = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DatePickerFragment(Date start, Date finish, dateSelect dateListener) {
        this.start = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (finish != null) {
            this.finish = finish.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        this.dateListener = dateListener;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_date_pick, container, false);

        txtDuration = view.findViewById(R.id.dialog_txtDuration);
        txtStartTime = view.findViewById(R.id.dialog_txtStartTime);
        txtFinishTime = view.findViewById(R.id.dialog_txtFinishTime);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnOk = view.findViewById(R.id.btnOk);
        switchManualSetTime = view.findViewById(R.id.switchManualSetTime);

        prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        txtStartTime.setText(start.format(dateTimeFormatter));

        if (finish == null) {
            seconds = (int) Duration.between(start, LocalDateTime.now()).getSeconds();
            hours = seconds / 3600;
            seconds -= hours * 3600;
            minutes = seconds / 60;
            seconds -= minutes * 60;

            txtFinishTime.setText("Running...");
            txtDuration.setText(String.format(getResources().getString(R.string.hours_minutes_seconds_format), hours, minutes, seconds));
        } else {
            txtFinishTime.setText(finish.format(dateTimeFormatter));

            seconds = (int) Duration.between(start, finish).getSeconds();
            hours = seconds / 3600;
            seconds -= hours * 3600;
            minutes = seconds / 60;
            seconds -= minutes * 60;

            txtDuration.setText(String.format(getResources().getString(R.string.hours_minutes_seconds_format), hours, minutes, seconds));
        }

        switchManualSetTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isManualTime = true;

                    finish = finish == null ? LocalDateTime.now() : finish;
                    txtFinishTime.setText(finish.format(dateTimeFormatter));

                    seconds = (int) Duration.between(start, finish).getSeconds();
                    hours = seconds / 3600;
                    seconds -= hours * 3600;
                    minutes = seconds / 60;
                    seconds -= minutes * 60;

                    txtDuration.setText(String.format(getResources().getString(R.string.hours_minutes_seconds_format), hours, minutes, seconds));
                } else {
                    isManualTime = false;
                    finish = null;
                    txtFinishTime.setText("Running...");

                    seconds = (int) Duration.between(start, LocalDateTime.now()).getSeconds();
                    hours = seconds / 3600;
                    seconds -= hours * 3600;
                    minutes = seconds / 60;
                    seconds -= minutes * 60;

                    txtDuration.setText(String.format(getResources().getString(R.string.hours_minutes_seconds_format), hours, minutes, seconds));
                }
            }
        });


        txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = Constants.START_DATE_TIME;
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(DatePickerFragment.this::onDateSet, start.getYear(), start.getMonthValue() - 1, start.getDayOfMonth());
                datePickerDialog.setAccentColor(getResources().getColor(R.color.teal_700));
                //check for night mode
                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
                boolean isNightMode = prefs.getBoolean(Constants.NIGHT_MODE, false);
                if (isNightMode) {
                    datePickerDialog.setThemeDark(true);
                }

                datePickerDialog.show(getActivity().getSupportFragmentManager(), "Datepickerdialog");
            }
        });

        txtFinishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = Constants.FINISH_DATE_TIME;
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(DatePickerFragment.this::onDateSet, start.getYear(), start.getMonthValue() - 1, start.getDayOfMonth());
                datePickerDialog.setAccentColor(getResources().getColor(R.color.teal_700));
                //check for night mode
                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
                boolean isNightMode = prefs.getBoolean(Constants.NIGHT_MODE, false);
                if (isNightMode) {
                    datePickerDialog.setThemeDark(true);
                }

                Calendar minDate = Calendar.getInstance();
                minDate.set(start.getYear(), start.getMonthValue() - 1, start.getDayOfMonth());
                datePickerDialog.setMinDate(minDate);

                datePickerDialog.show(getActivity().getSupportFragmentManager(), "Datepickerdialog");
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    prefsEditor.putBoolean(Constants.MANUAL_TIME, isManualTime);
                    prefsEditor.apply();
                    dateListener.setDate(start, finish);
                    getDialog().dismiss();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        localDate = localDate.of(year, monthOfYear + 1, dayOfMonth);
        switch (type) {
            case Constants.START_DATE_TIME:
                start = localDate.atTime(LocalTime.now());
                txtStartTime.setText(dateTimeFormatter.format(start));
                break;
            case Constants.FINISH_DATE_TIME:
                finish = localDate.atTime(LocalTime.now());
                txtFinishTime.setText(dateTimeFormatter.format(finish));
                break;
            default:
                break;
        }

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this::onTimeSet, start.getHour(), start.getMinute(), false);
        if (type.equals(Constants.FINISH_DATE_TIME) && start.getDayOfMonth() == finish.getDayOfMonth() && start.getMonthValue() == finish.getMonthValue() && start.getYear() == finish.getYear()) {
            timePickerDialog.setMinTime(start.getHour(), start.getMinute(), start.getSecond());
        }
        timePickerDialog.show(getActivity().getSupportFragmentManager(), "timePicker");

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        LocalTime givenTime = LocalTime.of(hourOfDay, minute, second);
        localDateTime = localDate.atTime(givenTime);

        switch (type) {
            case Constants.START_DATE_TIME:
                start = start.with(localDateTime);
                txtStartTime.setText(dateTimeFormatter.format(start));
                if(finish.isBefore(start)){
                    finish = start;
                    txtFinishTime.setText(dateTimeFormatter.format(finish));
                }
                break;
            case Constants.FINISH_DATE_TIME:
                finish = finish.with(localDateTime);
                txtFinishTime.setText(dateTimeFormatter.format(finish));
                break;
            default:
                break;
        }

        seconds = (int) Duration.between(start, finish).getSeconds();
        hours = seconds / 3600;
        seconds -= hours * 3600;
        minutes = seconds / 60;
        seconds -= minutes * 60;

        txtDuration.setText(String.format(getResources().getString(R.string.hours_minutes_seconds_format), hours, minutes, seconds));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getTargetFragment() instanceof dateSelect) {
            dateListener = (dateSelect) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement dateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dateListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs = getActivity().getSharedPreferences(Constants.ARG_PREFS, Context.MODE_PRIVATE);
        isManualTime = prefs.getBoolean(Constants.MANUAL_TIME, false);

        if (isManualTime) {
            switchManualSetTime.setChecked(true);
        }
    }
}
