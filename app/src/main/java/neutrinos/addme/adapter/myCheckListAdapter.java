package neutrinos.addme.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import neutrinos.addme.databaseclass.DatabaseHelper;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.ModelClass.ModelClassChecklist;
import neutrinos.addme.R;

import static neutrinos.addme.activity.Checklist.sendUncheckedList;

/**
 * Created by NS_USER on 10-Mar-18.
 */

public class myCheckListAdapter extends BaseAdapter {
    Activity toDoListActivity;
    DatabaseHelper myDb;
    List<ModelClassChecklist> myMainList;
    private static LayoutInflater inflater = null;
    Context context;

    public myCheckListAdapter(Activity toDoListActivity, List<ModelClassChecklist> myMainList) {
        this.toDoListActivity = toDoListActivity;
        this.myMainList = myMainList;
        this.context = toDoListActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sendUncheckedList.clear();

    }

    @Override
    public int getCount() {
        return myMainList.size();
    }

    @Override
    public ModelClassChecklist getItem(int position) {
        return myMainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        //myDb = new DatabaseHelper(context);

        if (convertView == null)
            vi = inflater.inflate(R.layout.activity_checklist, null);

        TextView itemName = (TextView) vi.findViewById(R.id.item_name); // title

        TextView item_qty = (TextView) vi.findViewById(R.id.item_qty); // title
        TextView myCheckDate = (TextView) vi.findViewById(R.id.date); // title
        CheckBox myItemChcck = (CheckBox) vi.findViewById(R.id.check); // title


        itemName.setText(myMainList.get(position).getName());
        myCheckDate.setText(myMainList.get(position).getModifiedDate());
        item_qty.setText(myMainList.get(position).getQuantity());
        Logger.logD("Namecehckingdb",""+myMainList.get(position).getName());



        if (myMainList.get(position).getCheckstatus()) {
            myItemChcck.setChecked(true);
        }
        myItemChcck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Logger.logD("CheckBoxed", "checked" + position);
                    sendUncheckedList.remove(String.valueOf(position));
                    Logger.logD("size", " sendUncheckedList" + sendUncheckedList.size());
                } else {
                    Logger.logD("CheckBoxed", " not checked" + position);
                    sendUncheckedList.put(String.valueOf(position), myMainList.get(position));
                    Logger.logD("size", " sendUncheckedList" + sendUncheckedList.size());
                }
            }
        });


        return vi;
    }


}
