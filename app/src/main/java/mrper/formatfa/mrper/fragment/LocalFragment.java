package mrper.formatfa.mrper.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mrpoid.core.Emulator;
import com.mrpoid.core.MrpRunner;

import java.io.File;
import java.io.FileFilter;

import mrper.formatfa.mrper.R;
import mrper.formatfa.mrper.adapter.FileAdapter;
import mrper.formatfa.mrper.adapter.OnListClickListener;
import mrper.formatfa.mrper.mrp.MrpReader;

public class LocalFragment extends Fragment implements OnListClickListener{


    Emulator emulator =Emulator.getInstance();
    RecyclerView list;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
        {
            boolean isHidden = savedInstanceState.getBoolean("isHidden");
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if(isHidden)
                transaction.hide(this);
            else
                transaction.show(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isHidden",isHidden());
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localgame,null);


        list = view.findViewById(R.id.locallist);
        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        init();
        return view;
    }

    File vmPath;
    File[] localFiles;
    private void init() {
        vmPath = new File(emulator.getVmRootPath(),emulator.getVmWorkPath());
        refresh();
    }

    private void refresh() {
        if(vmPath.exists()==false)
        {
            Toast.makeText(this.getContext(),"本地文件夹不存在"+vmPath.getAbsolutePath(),Toast.LENGTH_LONG).show();
            return;
        }
        localFiles = vmPath.listFiles(new FileFilter(){

            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".mrp");
            }
        });
        FileAdapter adapter = new FileAdapter(this.getContext(),localFiles);
        adapter.setOnListClickListener(this);
        list.setAdapter(adapter);

    }

    @Override
    public void onItemClick(Object object, View view, final int position) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this.getContext());
        MrpReader reader = MrpReader.readFile(localFiles[position].getAbsolutePath());
        ab.setTitle(reader.getName());
        ab.setMessage(reader.getInfo());
        ab.setPositiveButton("启动", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MrpRunner.runMrp(getActivity(),localFiles[position].getAbsolutePath());
            }
        });
        ab.setNegativeButton("取消",null);

        ab.show();
    }
}
