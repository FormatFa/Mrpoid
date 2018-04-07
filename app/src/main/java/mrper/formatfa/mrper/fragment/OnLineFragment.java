package mrper.formatfa.mrper.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import mrper.formatfa.mrper.R;
import mrper.formatfa.mrper.adapter.MrpAdapter;
import mrper.formatfa.mrper.mrp.MrpItem;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.edroid.common.dl.Downloader;
import com.edroid.common.dl.IDownloadListener;
import com.edroid.common.utils.HttpUtils;
import com.mrpoid.core.Emulator;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;



public class OnLineFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{


    List<MrpItem> mrpItems;
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
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.search:
                String s = searchText.getText().toString();
                searchTask task = new searchTask();
                task.execute(URLEncoder.encode( s));
                break;

        }

    }
    private EditText searchText;

    ListView list;
    Emulator emulator;

    ContentLoadingProgressBar b;
    ProgressDialog d;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.onlinegame,null);

       view.findViewById(R.id.search).setOnClickListener(this);
       searchText = view.findViewById(R.id.searchtext);

       list = view.findViewById(R.id.searchresult);

       list.setOnItemClickListener(this);

       emulator = Emulator.getInstance();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        confirmDownload(mrpItems.get(i));
    }


    class searchTask extends AsyncTask<String,Integer,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            parseJson(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            String result = HttpUtils.get("http://app.formatfa.top/mrp/mrpersearch.php","mrpname="+strings[0]);

            return result;
        }


    }


    private void confirmDownload(final MrpItem mrp)
    {
        final File path =emulator.getVmFullFilePath(mrp.getId()+mrp.getFilename());
        if(path.exists())
        {
            Toast.makeText(this.getContext(),"之前已经下载了。。。。",Toast.LENGTH_LONG).show();

            return;
        }
        AlertDialog.Builder ab = new AlertDialog.Builder(this.getContext());

        ab.setTitle(">_<");
        StringBuilder sb = new StringBuilder();
        sb.append("名字:"+mrp.getName());
        sb.append("\n");
        sb.append("下载:"+ "http://app.formatfa.top/mrp/mrpfiles/" + mrp.getId()+mrp.getFilename());
        sb.append("\n");
        sb.append("下载目录:"+path.getAbsolutePath());
        sb.append("\n");

        sb.append("介绍:"+mrp.getInfo());
        sb.append("大小:"+mrp.getSize());
        sb.append("\n");
        ab.setMessage(sb.toString());

        ab.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                new downloadTask(path,mrp).execute(0);
            }
        });
        ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ab.show();
    }

    class downloadTask extends AsyncTask{
        File save;
        MrpItem mrp;

        public downloadTask(File save, MrpItem mrp) {
            this.save = save;
            this.mrp = mrp;
        }
      // AlertDialog.Builder ab;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            ab = new AlertDialog.Builder(OnLineFragment.this.getContext());
//            //ab.setView(pd);
//            ab.setMessage("下载中...");
//            ab.setPositiveButton("完成",null);
//            ab.show();
        }

        @Override
        protected void onPostExecute(Object o) {

            if((boolean)o)
            {
                tip("下载完成");
            }
            else
                tip("下载失败");
            //ab.create().dismiss();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

           return  HttpUtils.download("http://app.formatfa.top/mrp/mrpfiles/" + mrp.getId()+mrp.getFilename(),save);

        }
    }

    private  void tip(String str)
    {
        AlertDialog.Builder ab = new AlertDialog.Builder(this.getContext());
        ab.setTitle(">_*");
        ab.setMessage(str);
        ab.setPositiveButton("ok",null);
        ab.show();
    }
    private void  parseJson(String str)
    {
        mrpItems = new ArrayList<>();
        try {
            JSONArray ja = new JSONArray(str);


            if(ja==null)
                return;
            for(int i = 0 ; i< ja.length() ;i+=1)
            {
                MrpItem mrpitem = new MrpItem();
                JSONArray item = ja.getJSONArray(i);
                mrpitem.setId(item.getString(0));
                mrpitem.setFilename(item.getString(1));
                mrpitem.setName(item.getString(2));
                mrpitem.setSize(item.getString(3));
                mrpitem.setDonwload(item.getString(4));
                mrpitem.setInfo(item.getString(5));
                mrpItems.add(mrpitem);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        MrpAdapter adapter = new MrpAdapter(this.getContext(),mrpItems);
        list.setAdapter(adapter);
    }
}
