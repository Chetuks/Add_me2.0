package neutrinos.addme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import neutrinos.addme.beenclass.ItemBeen;
import neutrinos.addme.R;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyContactAdapterViews> {
    Context context;
    List<ItemBeen> itemBeens;


    public ItemAdapter(Context context, List<ItemBeen> itemBeens) {
        this.context = context;
        this.itemBeens = itemBeens;

    }
    @Override
    public MyContactAdapterViews onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_history_item, parent, false);
        return new MyContactAdapterViews(view);
    }

    @Override
    public void onBindViewHolder(final MyContactAdapterViews holder, final int position) {
        try{
            ItemBeen itemBeen = itemBeens.get(position);

            holder.statusTv.setText(itemBeen.getStatus());
            holder.ammountTv.setText(itemBeen.getAmmount());
            holder.dateTv.setText(itemBeen.getDate());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return itemBeens.size();
    }

    public class MyContactAdapterViews extends RecyclerView.ViewHolder
    {
        TextView statusTv;
        TextView ammountTv;
        TextView dateTv;

        public MyContactAdapterViews(View itemView) {
            super(itemView);
            statusTv = (TextView) itemView.findViewById(R.id.status);
            ammountTv = (TextView) itemView.findViewById(R.id.amount);
            dateTv = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
