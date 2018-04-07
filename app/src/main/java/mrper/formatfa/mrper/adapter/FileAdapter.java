package mrper.formatfa.mrper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrpoid.core.Emulator;

import java.io.File;

import mrper.formatfa.mrper.R;
import mrper.formatfa.mrper.mrp.MrpReader;


public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {



    Context context;
    File[] files;

    private OnListClickListener clickListener;
    private OnListLongClickListener longClickListener;
    public void setOnListClickListener(OnListClickListener list)
    {
        clickListener = list;
    }
    public void setOnListLongClickListener(OnListLongClickListener list)
    {
        longClickListener= list;
    }
    public FileAdapter(Context context,File[] files)
    {
        this.context = context;
        this.files = files;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fileitem,null);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        File file = files[position];
        if(file.isDirectory())
        {
            holder.icon.setImageResource(R.drawable.ic_folder);
        }
        else
            holder.icon.setImageResource(R.drawable.ic_launcher);

        holder.fileName.setText(file.getName());

        if(clickListener!=null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(files[position],holder.itemView,position);
                }
            });
        }
        if(longClickListener!=null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    longClickListener.onItemLongClick(files[position],holder.itemView,position);
                }
            });
        }

        if(file.getName().endsWith(".mrp"))
        {
            MrpReader reader = MrpReader.readFile(file.getAbsolutePath());

            holder.other.setText(reader.getName());
            holder.mrpinfo.setText(reader.getInfo());
        }
    }

    @Override
    public int getItemCount() {
        return files.length;
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {

        ImageView icon;
        TextView fileName;

        TextView other;
        TextView mrpinfo;
        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.fileview_icon);
            fileName = itemView.findViewById(R.id.fileview_name);
            other = itemView.findViewById(R.id.fileview_other);
            mrpinfo = itemView.findViewById(R.id.fileview_mrpinfo);
        }
    }
}
