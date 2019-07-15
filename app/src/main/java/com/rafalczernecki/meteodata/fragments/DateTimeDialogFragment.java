package com.rafalczernecki.meteodata.fragments;

import android.content.DialogInterface;
import android.os.Bundle;


import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.rafalczernecki.meteodata.activities.DisplayPreferencesActivity;

public class DateTimeDialogFragment extends SwitchDateTimeDialogFragment {

    private static final String TAG_LABEL = "LABEL";
    private static final String TAG_POSITIVE_BUTTON = "POSITIVE_BUTTON";
    private static final String TAG_NEGATIVE_BUTTON = "NEGATIVE_BUTTON";
    private static final String TAG_NEUTRAL_BUTTON = "NEUTRAL_BUTTON";

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (this.getActivity() instanceof DisplayPreferencesActivity) {
            ((DisplayPreferencesActivity) this.getActivity()).enableDatePick();
        }
    }

    public static DateTimeDialogFragment newInstance(String label, String positiveButton, String negativeButton, String neutralButton) {
        DateTimeDialogFragment dateTimeDialogFragment = new DateTimeDialogFragment();
        Bundle args = new Bundle();
        args.putString(TAG_LABEL, label);
        args.putString(TAG_POSITIVE_BUTTON, positiveButton);
        args.putString(TAG_NEGATIVE_BUTTON, negativeButton);
        if (neutralButton != null) {
            args.putString(TAG_NEUTRAL_BUTTON, neutralButton);
        }
        dateTimeDialogFragment.setArguments(args);

        return dateTimeDialogFragment;
    }

}
