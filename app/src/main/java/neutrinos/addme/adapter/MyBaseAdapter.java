package neutrinos.addme.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.utilities.Logger;
import neutrinos.addme.ModelClass.CategoryModelClass;
import neutrinos.addme.R;
import neutrinos.addme.activity.SubCategoryListActivity;

/**
 * Created by NS_USER on 21-Mar-18.
 */

public class MyBaseAdapter extends BaseAdapter {
    ArrayList myList = new ArrayList();
    LayoutInflater inflater;
    Context context;
    List<CategoryModelClass> myCategoryModelClassList;
    Activity activity;




    public MyBaseAdapter(Context context, List<CategoryModelClass> myCategoryModelClassList, Activity activity) {
        this.myList = myList;
        this.context = context;
        this.activity = activity;
        inflater = LayoutInflater.from(this.context);

        this.myCategoryModelClassList = myCategoryModelClassList;
    }
    @Override
    public int getCount() {
        return myCategoryModelClassList.size();
    }
    @Override
    public CategoryModelClass getItem(int position) {
        return myCategoryModelClassList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.near_me_adapter, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            mViewHolder.name.setText(myCategoryModelClassList.get(position).getName());
            //mViewHolder.image.setImageResource(R.drawable.supermarket_img);
            //Picasso.with(this.activity).load(mViewHolder.image);


            convertView.setTag(mViewHolder);

        } else {

            mViewHolder = (MyViewHolder) convertView.getTag();

        }

        //mViewHolder.name.setText(myCategoryModelClassList.get(position).getName());
        //mViewHolder.image.setImageResource(R.drawable.supermarket_img);
        //Picasso.with(this.activity).load(mViewHolder.getImageUri()).into((ImageView) convertView);
        new DownloadImageTask(mViewHolder.image).execute(myCategoryModelClassList.get(position).getImage());


        mViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, myCategoryModelClassList.get(position).getName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, String.valueOf(myCategoryModelClassList.get(position).getId()), Toast.LENGTH_SHORT).show();
               /* try {
                    CategoryModelClass myCategoryModelClass = new CategoryModelClass("id", "name", "image", "description", "date");
                    Intent mIntent = new Intent(activity, SubCategoryListActivity.class);
                    mIntent.putExtra("ModelBean", myCategoryModelClass);
                    activity.startActivity(mIntent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }*/
                Intent intent = new Intent(context, SubCategoryListActivity.class);
                Bundle b = new Bundle();
                b.putString("id",myCategoryModelClassList.get(position).getId());
                Logger.logD("check",""+String.valueOf(myCategoryModelClassList.get(position).getId()));
                intent.putExtras(b);
               // intent.putExtra("id", String.valueOf(myCategoryModelClassList.get(position).getId()));
                context.startActivity(intent);
            }
        });
        return convertView;
    }


    private class MyViewHolder {
        TextView name;
        ImageView image;
        LinearLayout parentLayout;
        public MyViewHolder(View item) {
            name = (TextView) item.findViewById(R.id.name);
            image=(ImageView) item.findViewById(R.id.image_view_adapter_id);
            parentLayout = (LinearLayout) item.findViewById(R.id.parent);
        }


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}