package neutrinos.addme.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import neutrinos.addme.R;
import neutrinos.addme.activity.Offers;
import neutrinos.addme.fragment.DashboardFragment;
import neutrinos.addme.fragment.GridModelClass;
import neutrinos.addme.utilities.Logger;

/**
 * Created by mahiti on 31/03/18.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Activity activity;
    ArrayList<GridModelClass> detailsListCategory;

    public CategoryAdapter(Activity activity, ArrayList<GridModelClass> mAll, DashboardFragment dashboardFragment) {
        this.detailsListCategory = mAll;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.favourites_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        setAnimation(viewHolder.itemView, position);
        if (!detailsListCategory.get(position).equals("")) {
            Picasso.get()
                    .load(detailsListCategory.get(position).getImageUrl())
                    .placeholder(R.drawable.logo)
                    .fit()
                    .error(R.drawable.logo)
                    .into(viewHolder.imageView);

        }
        viewHolder.textView.setText(detailsListCategory.get(position).getFileName());
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.logD("imagecheck", "" + String.valueOf(detailsListCategory.get(position).getImageUrl()));
                Bundle bundle = new Bundle();
                bundle.putString("image", String.valueOf(detailsListCategory.get(position).getImageUrl()));
                Intent intent = new Intent(activity, Offers.class);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });
    }

    private int mLastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > mLastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return detailsListCategory.size();
    }


    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;
        LikeButton favbtn;


        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.grid_image);
            textView = (TextView) view.findViewById(R.id.grid_text);
            favbtn = view.findViewById(R.id.favourites_id);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
