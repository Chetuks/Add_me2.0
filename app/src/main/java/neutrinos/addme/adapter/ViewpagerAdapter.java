package neutrinos.addme.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.R;
import neutrinos.addme.activity.PremiumContent;
import neutrinos.addme.fragment.PremiumModelClass;

public class ViewpagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    List<PremiumModelClass> premiumList;

    public ViewpagerAdapter(Context context, ArrayList<PremiumModelClass> premiumList) {
        this.context = context;
        this.premiumList = premiumList;
    }

    @Override
    public int getCount() {
        return premiumList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_premiumcontent);
        Picasso.get()
                .load(premiumList.get(position).getImageUrl())
                .fit()
                .into(imageView);
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("image",premiumList.get(position).getImageUrl());
                Intent intent=new Intent(context, PremiumContent.class);
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
