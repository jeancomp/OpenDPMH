package com.jp.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessor;

public class AddActiveDataProcessorActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = AddActiveDataProcessorActivity.class.getName();
    private RecyclerView recycler_List;
    private Button btnActiveDataProcessor;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dataprocessor);
        context = this;

        // below line is to change
        // the title of our action bar.
        getSupportActionBar().setTitle("Add situation of interest");
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add situation of interest");*/

        btnActiveDataProcessor = (Button) findViewById(R.id.btnActiveDataProcessor);
        btnActiveDataProcessor.setOnClickListener(this);
        recycler_List = (RecyclerView) findViewById(R.id.recycler_List);

        // Display the fragment as the main content.
        /*FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        AddActiveDataProcessorFragment addActiveDataProcessorFragment = new AddActiveDataProcessorFragment();
        mFragmentTransaction.replace(R.id.card_view, addActiveDataProcessorFragment);
        mFragmentTransaction.commit();*/


        List<ListDataProcessor> listDataProcessors = new ArrayList();
        /*ListDataProcessorManager ldpm = ListDataProcessorManager.getInstance();
        listDataProcessors = ldpm.selectAll();*/

        List<String> dataProcessorBackup = new ArrayList();
        dataProcessorBackup.add("Empty");

        ListDataProcessorAdapter adapter = new ListDataProcessorAdapter(context, listDataProcessors, dataProcessorBackup);

        recycler_List.setLayoutManager(new LinearLayoutManager(context));
        recycler_List.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}