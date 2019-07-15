package com.rafalczernecki.meteodata.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rafalczernecki.meteodata.R;
import com.rafalczernecki.meteodata.entities.Server;

public class ConnectionStatusIndicatorFragment extends Fragment {
    public static final String ARG_CONNECTION_STATUS = "connectionStatus";
    private int connectionStatus;

    private ProgressBar progressBar;
    private ImageView resultIcon;
    private TextView checkResultTextView;

    public static ConnectionStatusIndicatorFragment newInstance(Integer connectionStatus) {
        ConnectionStatusIndicatorFragment fragment = new ConnectionStatusIndicatorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CONNECTION_STATUS, connectionStatus);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            connectionStatus = getArguments().getInt(ARG_CONNECTION_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connection_status_indicator_fragment, container, false);
        progressBar = view.findViewById(R.id.conn_indicator_frag_indeterminate_bar);
        resultIcon = view.findViewById(R.id.conn_indicator_frag_result_icon);
        checkResultTextView = view.findViewById(R.id.conn_indicator_frag_check_result_text_view);
        setConnectionStatus(this.connectionStatus);
        return view;
    }

    public void setConnectionStatus(Integer connectionStatus) {
        if (connectionStatus == Server.CONNECTION_CHECKING) {
            checkResultTextView.setVisibility(View.VISIBLE);
            checkResultTextView.setText(R.string.checking_connection);
            resultIcon.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else if (connectionStatus == Server.CONNECTION_OK) {
            progressBar.setVisibility(View.GONE);
            resultIcon.setImageResource(R.drawable.ic_baseline_done_48px);
            checkResultTextView.setText(R.string.connection_successful);
            resultIcon.setVisibility(View.VISIBLE);
            checkResultTextView.setVisibility(View.VISIBLE);
        } else if (connectionStatus == Server.CONNECTION_ERROR) {
            progressBar.setVisibility(View.GONE);
            resultIcon.setImageResource(R.drawable.ic_baseline_clear_48px);
            checkResultTextView.setText(R.string.connection_failed);
            resultIcon.setVisibility(View.VISIBLE);
            checkResultTextView.setVisibility(View.VISIBLE);
        } else if (connectionStatus == Server.CONNECTION_UNDEFINED) {
            progressBar.setVisibility(View.GONE);
            resultIcon.setVisibility(View.GONE);
            checkResultTextView.setText("");
            checkResultTextView.setVisibility(View.GONE);
        } else if (connectionStatus == Server.CONNECTION_DOWNLOADING_DATA) {
            checkResultTextView.setText(R.string.downloading_data);
            resultIcon.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            checkResultTextView.setVisibility(View.VISIBLE);
        }
    }
}
