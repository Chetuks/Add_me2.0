package neutrinos.addme.adapter;

import android.app.Activity;
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

import neutrinos.addme.fragment.GridModelClass;
import neutrinos.addme.R;

/**
 * Created by mahiti on 31/03/18.
 */

public class GridcategoryAdapter extends RecyclerView.Adapter<GridcategoryAdapter.ViewHolder> {

    private Activity activity;
    ArrayList<GridModelClass> detailsListCategory;

    public GridcategoryAdapter(Activity activity, ArrayList<GridModelClass> mAll) {
        this.detailsListCategory=mAll;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.detail_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        setAnimation(viewHolder.itemView, position);
        if(!detailsListCategory.get(position).equals("")){
            Picasso.get()
                    .load(detailsListCategory.get(position).getImageUrl())
                    .placeholder(R.drawable.images)
                    .error(R.drawable.images)
                    .into(viewHolder.imageView);

        }
        viewHolder.textView.setText(detailsListCategory.get(position).getFileName());
       /* viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity,SelectedCategoryActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("id",detailsListCategory.getCategories().get(position).getId());
                Logger.logD("iddd",""+detailsListCategory.getCategories().get(position).getId());
                bundle.putInt("organization_id",detailsListCategory.getCategories().get(position).getOrganizationId());
                Logger.logD("orggggiddd",""+detailsListCategory.getCategories().get(position).getOrganizationId());
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });*/
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
        private  ImageView imageSet;
        private  TextView add_group;


        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image);
            textView = (TextView) view.findViewById(R.id.text);
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
