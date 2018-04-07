package mrper.formatfa.mrper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mrper.formatfa.mrper.R;
import mrper.formatfa.mrper.mrp.MrpItem;


public class MrpAdapter extends BaseAdapter {

    Context context;
    List<MrpItem> mrpItems;

    public MrpAdapter(Context context, List<MrpItem> mrpItems) {
        this.context = context;
        this.mrpItems = mrpItems;
    }

    @Override
    public int getCount() {
        return mrpItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mrpItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mrpItems.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View myview = inflater.inflate(R.layout.mrpitem,null);

        TextView name = (TextView)myview.findViewById(R.id.mrpitem_name);
        TextView download = (TextView)myview.findViewById(R.id.mrpitem_download);
        TextView size = (TextView)myview.findViewById(R.id.mrpitem_size);

        name.setText(mrpItems.get(i).getName());
        download.setText(mrpItems.get(i).getDonwload());
        size.setText(mrpItems.get(i).getSize());
        return myview;
    }
}
