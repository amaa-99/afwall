package dev.ukanth.ufirewall.log;

import static dev.ukanth.ufirewall.Api.TAG;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.ukanth.ufirewall.Api;
import dev.ukanth.ufirewall.R;

/**
 * Created by ukanth on 25/7/16.
 */
public class LogRecyclerViewAdapter extends RecyclerView.Adapter<LogRecyclerViewAdapter.ViewHolder> {

    private final List<LogData> logData;
    private final Context context;
    private final RecyclerItemClickListener recyclerItemClickListener;

    public LogRecyclerViewAdapter(final Context context, RecyclerItemClickListener recyclerItemClickListener) {
        this.context = context;
        logData = new ArrayList<>();
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public void updateData(List<LogData> logDataList) {
        logData.clear();
        logData.addAll(logDataList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_recycle_item, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LogData data = logData.get(position);
        holder.bind(logData.get(position), recyclerItemClickListener);
        try {
            Drawable applicationIcon = Api.getApplicationIcon(context, data.getUid());
            holder.icon.setBackground(applicationIcon);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data.getTimestamp());
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            holder.lastDenied.setText(dateFormat.format(calendar.getTime()));
        } catch (Exception e) {
            holder.lastDenied.setText("-");
        }
        holder.appName.setText(data.getAppName() != null ? data.getAppName() + "(" + data.getUid() + ")" : context.getString(R.string.log_deletedapp));
        if (data.getCount() > 1) {
            holder.dataDenied.setText(context.getString(R.string.log_denied) + " " + data.getCount() + " " + context.getString(R.string.log_times));
        } else {
            holder.dataDenied.setText(context.getString(R.string.log_denied) + " " + data.getCount() + " " + context.getString(R.string.log_time));
        }
        holder.icon.invalidate();
    }

    @Override
    public int getItemCount() {
        return logData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView icon;
        final TextView appName;
        final TextView lastDenied;
        final TextView dataDenied;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            lastDenied = itemView.findViewById(R.id.last_denied);
            dataDenied = itemView.findViewById(R.id.data_denied);
        }

        public void bind(final LogData item, final RecyclerItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
