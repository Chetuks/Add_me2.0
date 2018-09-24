/**************************************************************************
 *
 * Copyright (C) 2012-2015 Alex Taradov <alex@taradov.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *************************************************************************/

package neutrinos.addme.alarmclass;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import neutrinos.addme.utilities.DateTime;
import neutrinos.addme.R;

public class AlarmListAdapter extends BaseAdapter {
    private final String TAG = "AlarmMe";

    private Context mContext;
    private DataSource mDataSource;
    private LayoutInflater mInflater;
    private DateTime mDateTime;
    private int mColorOutdated;
    private int mColorActive;
    private AlarmManager mAlarmManager;
    private final int EDIT_ALARM_ACTIVITY = 1;

    final CharSequence[] items = { "Delete"};


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public AlarmListAdapter(Context context) {
        mContext = context;
        mDataSource = DataSource.getInstance(context);

        Log.i(TAG, "AlarmListAdapter.create()");

        mInflater = LayoutInflater.from(context);
        mDateTime = new DateTime(context);

        mColorOutdated = mContext.getResources().getColor(R.color.alarm_title_outdated);
        mColorActive = mContext.getResources().getColor(R.color.alarm_title_active);

        mAlarmManager = (AlarmManager) context.getSystemService(mContext.ALARM_SERVICE);


        dataSetChanged();
    }

    public void save() {
        mDataSource.save();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void update(Alarm alarm) {
        mDataSource.update(alarm);
        dataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateAlarms() {
        Log.i(TAG, "AlarmListAdapter.updateAlarms()");
        for (int i = 0; i < mDataSource.size(); i++)
            mDataSource.update(mDataSource.get(i));
        dataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void add(Alarm alarm) {
        mDataSource.add(alarm);
        dataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void delete(int index) {
        cancelAlarm(mDataSource.get(index));
        mDataSource.remove(index);
        dataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onSettingsUpdated() {
        mDateTime.update();
        dataSetChanged();
    }

    public int getCount() {
        return mDataSource.size();
    }

    public Alarm getItem(int position) {
        return mDataSource.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Alarm alarm = mDataSource.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            holder.details = (TextView) convertView.findViewById(R.id.item_details);
            holder.setAlaramFlag = (Switch) convertView.findViewById(R.id.set_alaram_status);
            holder.parentContainer = (LinearLayout) convertView.findViewById(R.id.parentcontainer);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(alarm.getTitle());
        holder.details.setText(mDateTime.formatDetails(alarm) + (alarm.getEnabled() ? "" : " [disabled]"));

        if (alarm.getOutdated()) {
            holder.title.setTextColor(mColorOutdated);
            holder.setAlaramFlag.setChecked(false);
        } else {
            holder.title.setTextColor(mColorActive);
            holder.setAlaramFlag.setChecked(true);

        }
        holder.parentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                       /* if (item == 0) {
                            Intent intent = new Intent(mContext, EditAlarm.class);
                            Alarm mCurrentAlarm = new Alarm(mContext);
                            AlarmListAdapter mAlarmListAdapter = new AlarmListAdapter(mContext);
                            mCurrentAlarm = mAlarmListAdapter.getItem(position);
                            mCurrentAlarm.toIntent(intent);
                            Activity activity = (Activity) mContext;
                            activity.startActivityForResult(intent, EDIT_ALARM_ACTIVITY);
                        } else*/ if (item == 0) {
                            delete(position);
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void dataSetChanged() {
        for (int i = 0; i < mDataSource.size(); i++)
            setAlarm(mDataSource.get(i));

        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setAlarm(Alarm alarm) {
        PendingIntent sender;
        Intent intent;

        if (alarm.getEnabled() && !alarm.getOutdated()) {
            intent = new Intent(mContext, AlarmReceiver.class);
            alarm.toIntent(intent);
            sender = PendingIntent.getBroadcast(mContext, (int) alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getDate(), sender);
            Log.i(TAG, "AlarmListAdapter.setAlarm(" + alarm.getId() + ", '" + alarm.getTitle() + "', " + alarm.getDate() + ")");
        }
    }

    private void cancelAlarm(Alarm alarm) {
        PendingIntent sender;
        Intent intent;

        intent = new Intent(mContext, AlarmReceiver.class);
        sender = PendingIntent.getBroadcast(mContext, (int) alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(sender);
    }

    static class ViewHolder {
        TextView title;
        TextView details;
        Switch setAlaramFlag;
        LinearLayout parentContainer;
    }
}