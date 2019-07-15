package com.rafalczernecki.meteodata.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rafalczernecki.meteodata.R;
import com.rafalczernecki.meteodata.adapters.ServerListAdapter;
import com.rafalczernecki.meteodata.entities.Server;
import com.rafalczernecki.meteodata.fragments.ConnectionStatusIndicatorFragment;
import com.rafalczernecki.meteodata.interfaces.ServerConnectionCheckReceiver;
import com.rafalczernecki.meteodata.network.CommunicationHelper;
import com.rafalczernecki.meteodata.utils.SharedPreferencesHelper;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ServerSettingsActivity extends AppCompatActivity implements ServerConnectionCheckReceiver {

    private SharedPreferencesHelper sharedPrefsHelper;
    private CommunicationHelper communicationHelper;
    private ConnectionStatusIndicatorFragment connectionStatusFragment;
    private Integer connectionStatus;

    @BindView(R.id.current_server_tag_text_view)
    TextView currentServerTagTextView;
    @BindView(R.id.current_server_ip_address_text_view)
    TextView currentServerIpAddressTextView;
    @BindView(R.id.new_server_ip_address_edit_text)
    EditText newServerIpAddressEditText;
    @BindView(R.id.new_server_tag_edit_text)
    EditText newServerTagEditText;
    @BindView(R.id.serversListRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.server_settings_layout)
    ConstraintLayout rootView;
    @BindView(R.id.check_server_connection_btn)
    Button checkServerConnectionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);
        Context context = getApplicationContext();
        sharedPrefsHelper = new SharedPreferencesHelper(context);
        communicationHelper = new CommunicationHelper(context, this);
        updateView(true, true);
        if (savedInstanceState == null) connectionStatus = Server.CONNECTION_UNDEFINED;
        else setConnectionStatus(savedInstanceState.getInt(ARG_CONNECTION_STATUS));
        connectionStatusFragment = ConnectionStatusIndicatorFragment.newInstance(connectionStatus);
        addIndicatorFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CONNECTION_STATUS, connectionStatus);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.check_server_connection_btn)
    public void handleCheckServerConnectionBtn() {
        setConnectionStatus(Server.CONNECTION_CHECKING);
    }

    @OnClick(R.id.add_new_server_ip_btn)
    public void handleAddNewServerBtn() {
        Server server = new Server(newServerTagEditText.getText().toString(), newServerIpAddressEditText.getText().toString());
        if (!sharedPrefsHelper.getCurrentServerTag().equals(server.getServerTag())) {
            if (validateServerData(server)) {
                sharedPrefsHelper.addNewServer(server);
                newServerIpAddressEditText.setText("");
                newServerTagEditText.setText("");
                if (this.getCurrentFocus() == newServerTagEditText) {
                    newServerTagEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                } else if (this.getCurrentFocus() == newServerIpAddressEditText) {
                    newServerIpAddressEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    newServerTagEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                }
                updateView(false, true);
                Toast.makeText(getApplicationContext(), R.string.adding_successful, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.override_curr_server, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateServerData(Server server) {
        boolean valid = true;
        if (!Server.validateIPAddress(server.getServerIpAddress())) {
            valid = false;
            newServerIpAddressEditText.setError(getString(R.string.invalid_ip));
        }
        if (TextUtils.isEmpty(server.getServerTag())) {
            valid = false;
            newServerTagEditText.setError(getString(R.string.empty_tag));
        } else if (server.getServerTag().length() > 20) {
            valid = false;
            newServerTagEditText.setError(getString(R.string.tag_to_long));
        }
        return valid;
    }

    public void updateView(boolean updateCurrentServerData, boolean updateAllServersData) {
        if (updateCurrentServerData) {
            currentServerTagTextView.setText(sharedPrefsHelper.getCurrentServerTag());
            currentServerIpAddressTextView.setText(sharedPrefsHelper.getCurrentServerIpAddress());
        }
        if (updateAllServersData) {
            Map<String, ?> servers = sharedPrefsHelper.getAllServersData();
            ServerListAdapter adapter = new ServerListAdapter(Server.getServerTagsArrayList(servers),
                    Server.getServerIpAddressesArrayList(servers), this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public void receiveServerConnectionStatus(Integer connectionStatus) {
        setConnectionStatus(connectionStatus);
        enableUserInteraction();
    }

    @Override
    public void setConnectionStatus(Integer connectionStatus) {
        this.connectionStatus = connectionStatus;
        if (connectionStatusFragment != null)
            connectionStatusFragment.setConnectionStatus(connectionStatus);
        if (connectionStatus == Server.CONNECTION_CHECKING) {
            communicationHelper.testConnectionWithServer();
            disableUserInteraction();
        }
    }

    private void disableUserInteraction() {
        ((ServerListAdapter) recyclerView.getAdapter()).setClickable(false);
        checkServerConnectionButton.setEnabled(false);
    }

    private void enableUserInteraction() {
        ((ServerListAdapter) recyclerView.getAdapter()).setClickable(true);
        checkServerConnectionButton.setEnabled(true);
    }

    private void addIndicatorFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_conn_indicator_frag_container, connectionStatusFragment)
                .commit();
    }
}