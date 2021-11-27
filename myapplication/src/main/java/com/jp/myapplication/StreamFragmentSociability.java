package com.jp.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.database.Phenotypes;
import br.ufma.lsdi.digitalphenotyping.dpmanager.DPManager;

public class StreamFragmentSociability extends Fragment {
    private static final String TAG = StreamFragmentSociability.class.getName();
    private DPManager dpManager = DPManager.getInstance();
    private CardView cardView;
    private Button btnFinish;
    private TextView txtValueRecords;
    private List<Phenotypes> phenotypesList = new ArrayList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sociability, viewGroup, false);
        btnFinish = (Button) view.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(clickListener);
        txtValueRecords = (TextView) view.findViewById(R.id.txtValueRecords);

        try {
            phenotypesList = dpManager.getInstance().getPhenotypesList("Sociability");
            setSettings();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    public void setSettings(){
        if(phenotypesList == null){
            txtValueRecords.setText(0);
            return;
        }
        String value = String.valueOf(phenotypesList.size());
        txtValueRecords.setText(value);
    }

    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnFinish: {
                    try {
                        List<String> dataProcessorsName = new ArrayList();
                        dataProcessorsName.add("Sociability");
                        dpManager.getInstance().stopDataProcessors(dataProcessorsName);
                        Toast.makeText(getContext(), "Finish situation of interest: sociability",Toast.LENGTH_SHORT).show();
                        btnFinish.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };
}