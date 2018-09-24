package neutrinos.addme.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.utilities.Logger;
import neutrinos.addme.ModelClass.ModelClassChecklist;
import neutrinos.addme.R;
import neutrinos.addme.activity.NewCheckListActivity;

public class ChecklistAdapter extends BaseAdapter {
    ArrayList myList = new ArrayList();
    LayoutInflater inflater;
    List<ModelClassChecklist> modelClassList;
    Activity activity;
    Context context;

    public ChecklistAdapter(Context context, List<ModelClassChecklist> modelClassList, Activity activity) {
        this.context = context;
        this.activity = activity;
        inflater = LayoutInflater.from(this.context);
        this.modelClassList = modelClassList;

    }
    public ChecklistAdapter(){}

    @Override
    public int getCount() {
        return modelClassList.size();
    }

    @Override
    public ModelClassChecklist getItem(int position) {
        return modelClassList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyViewHolder myViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.checklist_adapter, parent, false);
            myViewHolder = new MyViewHolder(convertView);
            myViewHolder.name1.setText(modelClassList.get(position).getName());
            myViewHolder.quantity.setText(modelClassList.get(position).getQuantity()+"-"+modelClassList.get(position).getQuantitySpinser());
            convertView.setTag(myViewHolder);

            myViewHolder.b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modelClassList.remove(position);
                    notifyDataSetChanged();
                }
            });
            myViewHolder.adapterChkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Logger.logD("checkedStatus","status"+isChecked);
                    if(!isChecked){
                        if(context instanceof NewCheckListActivity){
                            ((NewCheckListActivity)context).OurMethod(modelClassList.get(position),position);
                        }
                    }
                }
            });
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        return convertView;

    }


    public class MyViewHolder {
        TextView name1;
        LinearLayout parentLayout;
        CheckBox adapterChkbox;
        private String uniqueId;
        private TextView quantity;
        Button b2;


        public MyViewHolder(final View item) {
            name1 = item.findViewById(R.id.name);
            parentLayout = (LinearLayout) item.findViewById(R.id.parent);
            adapterChkbox = (CheckBox) item.findViewById(R.id.checkbox_adapter_id);
            quantity = (TextView) item.findViewById(R.id.quantity);
            //name1.setPaintFlags(name1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            b2 = item.findViewById(R.id.remove_btn);

        }
    }

}