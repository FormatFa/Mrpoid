package mrper.formatfa.mrper.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import mrper.formatfa.mrper.R;
import mrper.formatfa.mrper.adapter.FileAdapter;
import mrper.formatfa.mrper.adapter.OnListClickListener;

public class FileViewFragment extends Fragment implements View.OnClickListener {


    RecyclerView list;

    File[] files;
    File nowPath;

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
      View view = inflater.inflate(R.layout.fileview,null);

      list = view.findViewById(R.id.fileview_list);

      view.findViewById(R.id.fileview_back).setOnClickListener(this);
      list.setLayoutManager(new LinearLayoutManager((this.getContext())));


      init();
        return view;
    }

    private void init() {
        nowPath = Environment.getExternalStorageDirectory();

        upFileList(nowPath);
    }


    private void upFileList(File  path)
    {
        if(path.canRead() == false)
        {
            Toast.makeText(this.getContext(),"file read fail:"+path.getAbsolutePath(),Toast.LENGTH_LONG).show();
            return;
        }

        files = path.listFiles(new FileFilter(){
            @Override
            public boolean accept(File file) {
                if(file.isFile())
                {
                    if(file.getName().endsWith(".mrp") == false)return false;
                }
                return true;
            }
        });

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                String name1 = file.getName();
                String name2 = t1.getName();
                if(file.isDirectory()&&(t1.isDirectory()==false))
                return 1;
                if(file.isFile()&&(!t1.isFile()))
                return -1;
                else
                    return name1.compareTo(name2);

            }
        });
        FileAdapter adapter = new FileAdapter(this.getActivity(),files);

        adapter.setOnListClickListener(new OnListClickListener() {
            @Override
            public void onItemClick(Object object, View view, int position) {
                File file = (File)object;
                if(file.isDirectory())
                {
                    upFileList(file);
                }
            }
        });
        list.setAdapter(adapter);
        nowPath = path;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.fileview_back:
                upFileList(nowPath.getParentFile());
                break;
        }
    }
}
