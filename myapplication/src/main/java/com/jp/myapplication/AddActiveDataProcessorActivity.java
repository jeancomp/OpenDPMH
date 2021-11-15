package com.jp.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessor;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessorManager;

public class AddActiveDataProcessorActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = AddActiveDataProcessorActivity.class.getName();
    private ListDataProcessorManager listDataProcessorManager = ListDataProcessorManager.getInstance();
    private List<ListDataProcessor> listDataProcessors = new ArrayList();
    private RecyclerView recycler_List;
    private Button btnActiveDataProcessor;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dataprocessor);
        context = this;
        getSupportActionBar().setTitle("Add situation of interest");

        btnActiveDataProcessor = (Button) findViewById(R.id.btnActiveDataProcessor);
        btnActiveDataProcessor.setOnClickListener(this);
        recycler_List = (RecyclerView) findViewById(R.id.recycler_List);

        try {
            new AddItemTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processValue(List<ListDataProcessor> myValue) {
        List<String> dataProcessorBackup = new ArrayList();
        dataProcessorBackup.add("Empty");

        ListDataProcessorAdapter adapter = new ListDataProcessorAdapter(context, myValue, dataProcessorBackup);

        recycler_List.setLayoutManager(new LinearLayoutManager(context));
        recycler_List.setAdapter(adapter);
    }

    private class AddItemTask extends AsyncTask<Void, Void, List<ListDataProcessor>> {
        @Override
        protected List<ListDataProcessor> doInBackground(Void... params) {
            List<ListDataProcessor> l = new ArrayList();
            l = listDataProcessorManager.getInstance().select();
            return l;
        }

        @Override
        protected void onPostExecute(List<ListDataProcessor> result) {
            processValue(result);
        }
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
    public void onPointerCaptureChanged(boolean hasCapture) { }
}