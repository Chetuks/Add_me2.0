package neutrinos.addme.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import neutrinos.addme.R;
import neutrinos.addme.ModelClass.ToDoList;
import neutrinos.addme.activity.Checklist;
import neutrinos.addme.activity.ToDoListActivity;

/**
 * Created by NS_USER on 10-Mar-18.
 */

public class myItemAdapter extends BaseAdapter{
    ToDoListActivity toDoListActivity;
    List<ToDoList> myMainList;
    private static LayoutInflater inflater=null;
    Context context;

    public myItemAdapter(ToDoListActivity toDoListActivity, List<ToDoList> myMainList) {
        this.toDoListActivity=toDoListActivity;
        this.myMainList=myMainList;
        this.context=toDoListActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("MymainCheckList adapter",myMainList.size()+"");
    }

    public myItemAdapter(Checklist checklist, List<ToDoList> myMainList) {
    }

    @Override
    public int getCount() {
        return myMainList.size();
    }

    @Override
    public ToDoList getItem(int position) {
        return myMainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView itemQty = (TextView)vi.findViewById(R.id.item_qty); // title
        TextView itemName = (TextView)vi.findViewById(R.id.item_name); // title
        Button removeItem = (Button)vi.findViewById(R.id.remove); // title
        itemName.setText(myMainList.get(position).getitemText());
        itemQty.setText(myMainList.get(position).getItemQTy());
        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMainList.remove(position);
                notifyDataSetChanged();
            }
        });
        return vi;
    }
}
