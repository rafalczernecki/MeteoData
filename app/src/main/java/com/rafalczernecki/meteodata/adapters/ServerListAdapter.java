package com.rafalczernecki.meteodata.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rafalczernecki.meteodata.R;
import com.rafalczernecki.meteodata.activities.ServerSettingsActivity;
import com.rafalczernecki.meteodata.entities.Server;
import com.rafalczernecki.meteodata.utils.SharedPreferencesHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ViewHolder> {

    private ArrayList<String> serverTags;
    private ArrayList<String> serverIpAddresses;
    private final Context context;
    private SharedPreferencesHelper sharedPrefsHelper;
    private boolean clickable;


    public ServerListAdapter(ArrayList<String> ServerTags, ArrayList<String> ServerIpAddresses, Context context) {
        this.serverTags = ServerTags;
        this.serverIpAddresses = ServerIpAddresses;
        this.context = context;
        sharedPrefsHelper = new SharedPreferencesHelper(context);
        clickable = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.serverTag.setText(serverTags.get(position));
        viewHolder.serverIpAddress.setText(serverIpAddresses.get(position));
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickable) {
                    Server server = new Server(serverTags.get(position), serverIpAddresses.get(position));
                    sharedPrefsHelper.changeCurrentServer(server);
                    updateView(true, false);
                    if (context instanceof ServerSettingsActivity) {
                        ((ServerSettingsActivity) context).setConnectionStatus(Server.CONNECTION_UNDEFINED);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return serverTags.size();
    }

    private void deleteServer(Server server) {
        if (sharedPrefsHelper.deleteServer(server)) {
            updateView(false, true);
        } else {
            Toast.makeText(context, R.string.cant_delete_server, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateView(boolean updateCurrentServerData, boolean updateAllServersData) {
        if (context instanceof ServerSettingsActivity) {
            ((ServerSettingsActivity) context).updateView(updateCurrentServerData, updateAllServersData);
        }
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        @BindView(R.id.serverTagTextView)
        TextView serverTag;
        @BindView(R.id.serverIpAddressTextView)
        TextView serverIpAddress;
        @BindView(R.id.servers_list_item_layout)
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            deleteServer(new Server(serverTag.getText().toString(), serverIpAddress.getText().toString()));
            return true;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem delete = contextMenu.add("Delete");
            delete.setOnMenuItemClickListener(this);
        }
    }
}