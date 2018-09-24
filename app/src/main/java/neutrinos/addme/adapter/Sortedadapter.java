package neutrinos.addme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.R;
import neutrinos.addme.ModelClass.SortedModelclass;

public class Sortedadapter extends BaseAdapter{
    private Context context;
    private List<SortedModelclass> sortedcategorylist;

    public Sortedadapter(Context context, ArrayList<SortedModelclass> sortedcategorylist) {
        this.context=context;
        this.sortedcategorylist=sortedcategorylist;
    }

    @Override
    public int getCount() {
        return sortedcategorylist.size();
    }

    @Override
    public Object getItem(int position) {
        return sortedcategorylist.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

   /* @Override
    public long getItemId(int position) {
        return sortedcategorylist.get(position).getId();
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_listitem, null);
        ImageView circleImageView =view. findViewById(R.id.circle_image);
        TextView name = view.findViewById(R.id.circle_text);
        name.setText(sortedcategorylist.get(position).getCategoryname());
        Picasso.get()
                .load( sortedcategorylist.get(position).getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(circleImageView);
        return view;
    }
}
