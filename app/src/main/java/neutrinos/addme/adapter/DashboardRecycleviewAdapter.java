package neutrinos.addme.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.ModelClass.CategoryModelClass;
import neutrinos.addme.R;
import neutrinos.addme.activity.CategoryDashboardActivity;

public class DashboardRecycleviewAdapter extends RecyclerView.Adapter<DashboardRecycleviewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    //vars
   // private ArrayList<String> mNames = new ArrayList<>();
   // private ArrayList<String> mImageUrls = new ArrayList<>();
    List<CategoryModelClass> mAll;
    private Context context;

    public DashboardRecycleviewAdapter(Context context, ArrayList<CategoryModelClass> mall) {
        //this.mNames = mNames;
        //this.mImageUrls = mImageUrls;
        this.context = context;
        this.mAll=mall;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       /* Glide.with(context)
                .asBitmap()
                .load(mAll.get(position).getImage())
                .into(holder.circleImageView);*/

        holder.name.setText(mAll.get(position).getName());
        new DownloadImageTask(holder.circleImageView).execute(mAll.get(position).getImage());
        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,CategoryDashboardActivity.class);
                context.startActivity(intent);
                Toast.makeText(context,"mage",Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return mAll.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView circleImageView;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circle_image);
            name = itemView.findViewById(R.id.circle_text);
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
