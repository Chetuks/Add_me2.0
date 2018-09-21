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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import neutrinos.addme.PreferenceClass.Preferences;
import neutrinos.addme.R;
import neutrinos.addme.activity.MainActivity;

//  Toast.makeText(getApplicationContext(), "Delete" + index, Toast.LENGTH_SHORT).show();

public class AlarmMe extends Activity
{
  private final String TAG = "AlarmMe";

  private ListView mAlarmList;
  private AlarmListAdapter mAlarmListAdapter;
  private Alarm mCurrentAlarm;

  private final int NEW_ALARM_ACTIVITY = 0;
  private final int EDIT_ALARM_ACTIVITY = 1;
  private final int PREFERENCES_ACTIVITY = 2;
  private final int ABOUT_ACTIVITY = 3;

  private final int CONTEXT_MENU_EDIT = 0;
  private final int CONTEXT_MENU_DELETE = 1;
  private final int CONTEXT_MENU_DUPLICATE = 2;

  private Toolbar mToolbar;
  private SharedPreferences sharedPreferences;

  @SuppressLint("NewApi")
  @Override
  public void onCreate(Bundle bundle)
  {
    super.onCreate(bundle);
    setContentView(R.layout.main);
    Log.i(TAG, "AlarmMe.onCreate()");

    mAlarmList = (ListView)findViewById(R.id.alarm_list_one);

    mAlarmListAdapter = new AlarmListAdapter(this);
    mAlarmList.setAdapter(mAlarmListAdapter);
    mAlarmList.setOnItemClickListener(mListOnItemClickListener);
    registerForContextMenu(mAlarmList);

    mCurrentAlarm = null;
  }



  @Override
  public void onDestroy()
  {
    super.onDestroy();
    Log.i(TAG, "AlarmMe.onDestroy()");
//    mAlarmListAdapter.save();
  }

  @SuppressLint("NewApi")
  @Override
  public void onResume()
  {
    super.onResume();
    Log.i(TAG, "AlarmMe.onResume()");
    mAlarmListAdapter.updateAlarms();
  }

  public void onAddAlarmClick(View view)
  {
    Intent intent = new Intent(getBaseContext(), EditAlarm.class);
    mCurrentAlarm = new Alarm(this);
    mCurrentAlarm.toIntent(intent);
    AlarmMe.this.startActivityForResult(intent, NEW_ALARM_ACTIVITY);
  }

  @SuppressLint("NewApi")
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    if (requestCode == NEW_ALARM_ACTIVITY)
    {
      if (resultCode == RESULT_OK)
      {
        mCurrentAlarm.fromIntent(data);
        mAlarmListAdapter.add(mCurrentAlarm);
      }
      mCurrentAlarm = null;
    }
    else if (requestCode == EDIT_ALARM_ACTIVITY)
    {
      if (resultCode == RESULT_OK)
      {
        mCurrentAlarm.fromIntent(data);
        mAlarmListAdapter.update(mCurrentAlarm);
      }
      mCurrentAlarm = null;
    }
    else if (requestCode == PREFERENCES_ACTIVITY)
    {
      mAlarmListAdapter.onSettingsUpdated();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    if (R.id.menu_settings == item.getItemId())
    {
      Intent intent = new Intent(getBaseContext(), Preferences.class);
      startActivityForResult(intent, PREFERENCES_ACTIVITY);
      return true;
    }
      else if (R.id.menu_tone == item.getItemId())
    {
      Intent intent = new Intent(getBaseContext(), MainActivity.class);
      startActivity(intent);
      return true;
    }
    else
    {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
  {
    if (v.getId() == R.id.alarm_list_one)
    {
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
      menu.setHeaderTitle(mAlarmListAdapter.getItem(info.position).getTitle());
      menu.add(Menu.NONE, CONTEXT_MENU_EDIT, Menu.NONE, "Edit");
      menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE, "Delete");
      menu.add(Menu.NONE, CONTEXT_MENU_DUPLICATE, Menu.NONE, "Duplicate");
    }
  }

  @SuppressLint("NewApi")
  @Override
  public boolean onContextItemSelected(MenuItem item)
  {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    int index = item.getItemId();

    if (index == CONTEXT_MENU_EDIT)
    {
      Intent intent = new Intent(getBaseContext(), EditAlarm.class);

      mCurrentAlarm = mAlarmListAdapter.getItem(info.position);
      mCurrentAlarm.toIntent(intent);
      startActivityForResult(intent, EDIT_ALARM_ACTIVITY);
    }
    else if (index == CONTEXT_MENU_DELETE)
    {
      mAlarmListAdapter.delete(info.position);
    }
    else if (index == CONTEXT_MENU_DUPLICATE)
    {
      Alarm alarm = mAlarmListAdapter.getItem(info.position);
      Alarm newAlarm = new Alarm(this);
      Intent intent = new Intent();

      alarm.toIntent(intent);
      newAlarm.fromIntent(intent);
      newAlarm.setTitle(alarm.getTitle() + " (copy)");
      mAlarmListAdapter.add(newAlarm);
    }

    return true;
  }

  private AdapterView.OnItemClickListener mListOnItemClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
      Intent intent = new Intent(getBaseContext(), EditAlarm.class);

      mCurrentAlarm = mAlarmListAdapter.getItem(position);
      mCurrentAlarm.toIntent(intent);
      AlarmMe.this.startActivityForResult(intent, EDIT_ALARM_ACTIVITY);
    }
  };

}

