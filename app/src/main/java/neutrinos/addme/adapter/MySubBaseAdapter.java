package neutrinos.addme.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.activity.DetailsCategoryListActivity;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.ModelClass.CategoryModelClass;
import neutrinos.addme.R;

/**
 * Created by NS_USER on 27-Mar-18.
 */

public class MySubBaseAdapter extends BaseAdapter {
    ArrayList myList = new ArrayList();
    LayoutInflater inflater;
    Context context;
    List<CategoryModelClass> mySubCategoryModelClassList;
    Activity activity;


    public MySubBaseAdapter(Context context, List<CategoryModelClass> mySubCategoryModelClassList, Activity activity) {
        this.myList = myList;
        this.context = context;
        this.activity = activity;
        inflater = LayoutInflater.from(this.context);
        this.mySubCategoryModelClassList = mySubCategoryModelClassList;
    }
    @Override
    public int getCount() {
        return mySubCategoryModelClassList.size();
    }
    @Override
    public CategoryModelClass getItem(int position) {
        return mySubCategoryModelClassList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MySubViewHolder mySubViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sub_category_adapter, parent, false);
            mySubViewHolder = new MySubViewHolder(convertView);
            mySubViewHolder.name.setText(mySubCategoryModelClassList.get(position).getName());
            Picasso.get().load(mySubCategoryModelClassList.get(position).getImage()).fit().centerCrop()
                    .placeholder(R.mipmap.addme_logo)
                    .error(R.mipmap.addme_logo)
                    .into(mySubViewHolder.image);
            convertView.setTag(mySubViewHolder);
        } else {
            mySubViewHolder = (MySubViewHolder) convertView.getTag();
        }

        mySubViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, mySubCategoryModelClassList.get(position).getName(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, String.valueOf(mySubCategoryModelClassList.get(position).getId()), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, DetailsCategoryListActivity.class);
                Bundle b = new Bundle();
                Logger.logD("Getting ID frm mySubCategoryModelClassList",mySubCategoryModelClassList.get(position).getId()+"");
                b.putString("id", String.valueOf(mySubCategoryModelClassList.get(position).getId()));
                b.putString("organization_id", String.valueOf(mySubCategoryModelClassList.get(position).getOrganizationid()));
                intent.putExtras(b);
                activity.startActivity(intent);
            }
        });
        return convertView;
    }

    private class MySubViewHolder {
        TextView name;
        ImageView image;
        LinearLayout parentLayout;
        public MySubViewHolder(View item) {
            name = (TextView) item.findViewById(R.id.name);
            image = (ImageView) item.findViewById(R.id.sub_category_adapter_id);
            parentLayout = (LinearLayout) item.findViewById(R.id.parent_layout);
        }
    }
}
