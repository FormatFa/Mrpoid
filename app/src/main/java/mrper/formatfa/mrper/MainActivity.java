package mrper.formatfa.mrper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mrpoid.app.EmulatorActivity;
import com.mrpoid.app.MrpoidSettingsActivity;
import com.mrpoid.core.EmuUtils;
import com.mrpoid.core.Emulator;
import com.mrpoid.core.MrpRunner;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.widget.TextView;
import android.widget.Toast;

import mrper.formatfa.mrper.fragment.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragManager;
    NavigationView nagationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    TextView tip;

    Fragment nowFragment;
    OnLineFragment onLineFragment;
    LocalFragment localFragment;

    FileViewFragment fileViewFragment;

    int request = 120;
    Emulator emulator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EmulatorActivity emu;
//        findViewById(R.id.test).setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                MrpRunner.runMrp(MainActivity.this,"/sdcard/test.mrp");
//            }
//        });
        //  MrpRunner.runMrp(MainActivity.this,"/storage/emulated/0/test.mrp");


        init();
    }

    private void init() {

        nagationView = findViewById(R.id.main_navigation);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);

        tip = nagationView.getHeaderView(0).findViewById(R.id.tip);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.draweropen, R.string.drawerclose);
        drawerLayout.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nagationView.setNavigationItemSelectedListener(this);
        fragManager = getSupportFragmentManager();


        onLineFragment = new OnLineFragment();
        localFragment = new LocalFragment();
        fileViewFragment = new FileViewFragment();

        emulator = Emulator.getInstance();

        tip.setText("vm path:" + emulator.getVmFullPath());
        checkPermissiom();
    }

    private void checkPermissiom() {
        String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        boolean hasAllPermisson = true;
//        for(String permission:permissions)
//        {
//
//
//        if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)
//        {
//            hasAllPermisson = false;
//            break;
//        }
//
//
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, request);
        }
    }

    private void openOnLine() {
        if (nowFragment == onLineFragment) return;
        nowFragment = onLineFragment;
        FragmentTransaction tran = fragManager.beginTransaction();

        tran.replace(R.id.main_contain, onLineFragment);
        tran.commitNow();
    }

    private void openLocal() {
        if (nowFragment == localFragment) return;
        nowFragment = localFragment;
        FragmentTransaction tran = fragManager.beginTransaction();

        tran.replace(R.id.main_contain, localFragment);

        tran.commitNow();
    }

    private void openFile() {
        if (nowFragment == fileViewFragment) return;
        nowFragment = fileViewFragment;
        FragmentTransaction tran = fragManager.beginTransaction();
        tran.replace(R.id.main_contain, fileViewFragment);

        tran.commitNow();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.localgame:
                openLocal();
                break;

            case R.id.online:
                openOnLine();
                break;
            case R.id.fileview:
                openFile();
                break;

            case R.id.setting:
                Intent i = new Intent(this, MrpoidSettingsActivity.class);
                startActivity(i);
                break;
            case R.id.about:
                Intent iq = new Intent(this, AboutActivity.class);
                startActivity(iq);
                break;
        }
        drawerLayout.closeDrawers();
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == request) {
            if(grantResults.length>0)
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission dine", Toast.LENGTH_LONG).show();
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                EmuUtils.subScriberID = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
                        .getSubscriberId();
                Log.e("FormatFa","id:"+EmuUtils.subScriberID);
                Toast.makeText(this,"getSubsriberID:"+EmuUtils.subScriberID,Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
