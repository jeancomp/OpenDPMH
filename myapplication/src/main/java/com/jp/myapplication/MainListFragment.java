package com.jp.myapplication;

import android.content.Context;
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

public class MainListFragment extends Fragment {
    private static final String TAG = MainListFragment.class.getName();
    private RecyclerViewAdapter adapter;
    private MainService myService;
    private DPManager dpManager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textLoad;
    private Context context;
    private View button_init;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, viewGroup, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview_fragment_main_list);
        button_init = view.findViewById(R.id.button_init);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        textLoad = (TextView) view.findViewById(R.id.error_msg);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        ActiveDataProcessor adp = new ActiveDataProcessor();
        adp.setDataProcessorName("No data processor");
        List<ActiveDataProcessor> activeDataProcessors = new ArrayList();
        //activeDataProcessors.add(adp);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(context, activeDataProcessors);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        final int tempoDeEspera = 1000;
            new Thread(new Runnable() {
                @Override
                public void run() {
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
                }
            }).start();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
