package neutrinos.addme.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import neutrinos.addme.ModelClass.NearbyModelClass;
import neutrinos.addme.R;
import neutrinos.addme.utilities.Logger;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.ViewHolder>{
    private Activity activity;
    ArrayList<NearbyModelClass> nearbyModelClassArrayList;

    public NearbyAdapter(Activity newNearByActivity, ArrayList<NearbyModelClass> nearbyList) {
        this.nearbyModelClassArrayList = nearbyList;
        this.activity = newNearByActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.wishlist_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        setAnimation(holder.itemView, position);
        if (!nearbyModelClassArrayList.get(position).equals("")) {
            Picasso.get()
                    .load(nearbyModelClassArrayList.get(position).getContentImage())
                    .placeholder(R.drawable.logo)
                    .fit()
                    .error(R.drawable.logo)
                    .into(holder.imageView);
        }
        holder.textView.setText(nearbyModelClassArrayList.get(position).getFilename());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.logD("imagecheck", "" + String.valueOf(nearbyModelClassArrayList.get(position).getContentImage()));
                Bundle bundle = new Bundle();
                bundle.putString("image", String.valueOf(nearbyModelClassArrayList.get(position).getContentImage()));
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
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.wishlist_image_adapter);
            textView = (TextView) view.findViewById(R.id.wishlist_text);
        }
    }
}
