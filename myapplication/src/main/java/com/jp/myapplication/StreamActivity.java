package com.jp.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class StreamActivity extends AppCompatActivity {
    private static final String TAG = StreamActivity.class.getName();
    private String dataProcessorName;
    //private ActionBar actionBar = getActionBar();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        getSupportActionBar().setTitle("Stream");
        dataProcessorName = getIntent().getStringExtra("dataprocessorname");
        getSupportActionBar().setSubtitle(dataProcessorName);

        setFragment(dataProcessorName);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"#### g1");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,"#### g2");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"#### g3");
    }


    public String getMyData() {
        return dataProcessorName;
    }


    public void setFragment(String dataProcessorName){
        if(dataProcessorName.equals("Physical_Sociability")){
            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager
                    .beginTransaction();
            StreamFragmentPhysicalSociability streamFragmentPhysicalSociability = new StreamFragmentPhysicalSociability();
            mFragmentTransaction.replace(R.id.frag_physical_sociability, streamFragmentPhysicalSociability);
            mFragmentTransaction.commit();
        }
        else if(dataProcessorName.equals("Online_Sociability")){
            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager
                    .beginTransaction();
            StreamFragmentOnlineSociability streamFragmentOnlineSociability = new StreamFragmentOnlineSociability();
            mFragmentTransaction.replace(R.id.frag_online_sociability, streamFragmentOnlineSociability);
            mFragmentTransaction.commit();
        }
        else if(dataProcessorName.equals("PhysicalActivity")){
            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager
                    .beginTransaction();
            StreamFragmentphysicalactivity streamFragmentphysicalactivity = new StreamFragmentphysicalactivity();
            mFragmentTransaction.replace(R.id.frag_physicalactivity, streamFragmentphysicalactivity);
            mFragmentTransaction.commit();
        }
        else if(dataProcessorName.equals("Mobility")){
            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager
                    .beginTransaction();
            StreamFragmentMobility streamFragmentMobility = new StreamFragmentMobility();
            mFragmentTransaction.replace(R.id.frag_mobility, streamFragmentMobility);
            mFragmentTransaction.commit();
        }
        else if(dataProcessorName.equals("Sleep")){
            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager
                    .beginTransaction();
            StreamFragmentSleep streamFragmentSleep = new StreamFragmentSleep();
            mFragmentTransaction.replace(R.id.frag_sleep, streamFragmentSleep);
            mFragmentTransaction.commit();
        }
    }
}
