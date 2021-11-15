package com.jp.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.dpmanager.DPManager;
import br.ufma.lsdi.digitalphenotyping.mainservice.MainService;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessor;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessorManager;

public class MainListFragment extends Fragment {
    private static final String TAG = MainListFragment.class.getName();
    private ActiveDataProcessorManager activeDataProcessorManager;// = ActiveDataProcessorManager.getInstance();
    private RecyclerViewAdapter adapter;
    private MainService myService;
    private DPManager dpManager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textLoad;
    private TextView text_primary_empty;
    private TextView text_secondary_empty;
    private View button_init;
    private View pag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, viewGroup, false);
        pag = view;
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview_fragment_main_list);
        button_init = view.findViewById(R.id.button_init);
        textLoad = (TextView) view.findViewById(R.id.error_msg);
        text_primary_empty = (TextView) view.findViewById(R.id.text_primary_empty);
        text_secondary_empty = (TextView) view.findViewById(R.id.text_secondary_empty);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        return view;
    }

    public void processValue(List<ActiveDataProcessor> myValue) {
        adapter = new RecyclerViewAdapter(getContext(), myValue);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        if(myValue.size() > 0){
            text_primary_empty.setVisibility(View.INVISIBLE);
            text_secondary_empty.setVisibility(View.INVISIBLE);
        }
    }

    private class AddItemTaskFrag extends AsyncTask<Void, Void, List<ActiveDataProcessor>> {
        @Override
        protected List<ActiveDataProcessor> doInBackground(Void... params) {
            List<ActiveDataProcessor> l = new ArrayList();
            l = activeDataProcessorManager.getInstance().select();
            return l;
        }

        @Override
        protected void onPostExecute(List<ActiveDataProcessor> result) {
            processValue(result);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        final int tempoDeEspera = 1000;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    pag.setEnabled(false);
                    SystemClock.sleep(tempoDeEspera);
                    Log.i(TAG,"#### Aguardando1");
                    SystemClock.sleep(tempoDeEspera);
                    Log.i(TAG,"#### Aguardando2");
                    SystemClock.sleep(tempoDeEspera);
                    Log.i(TAG,"#### Aguardando3");
                    SystemClock.sleep(tempoDeEspera);
                    Log.i(TAG,"#### Aguardando4");
                    SystemClock.sleep(tempoDeEspera);
                    progressBar.setVisibility(View.INVISIBLE);
                    textLoad.setVisibility(View.INVISIBLE);
                    pag.setEnabled(true);
                    new AddItemTaskFrag().execute();
                }
            }).start();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
