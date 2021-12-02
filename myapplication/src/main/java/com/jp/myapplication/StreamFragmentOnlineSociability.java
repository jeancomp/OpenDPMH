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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.database.PhenotypesEvent;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Attribute;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.dpmanager.DPManager;

public class StreamFragmentOnlineSociability extends Fragment {
    private static final String TAG = StreamFragmentOnlineSociability.class.getName();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private CardView cardView;
    private DPManager dpManager = DPManager.getInstance();
    private Button btnFinish;
    private List<PhenotypesEvent> phenotypesEventList = new ArrayList();

    private TextView txtEntradaCall;
    private TextView txtSaidaCall;
    private TextView txtPerdidaCall;

    private TextView txtEntradaSMS;
    private TextView txtSaidaSMS;

    private TextView txtCallRecordDate;
    private TextView txtSMSRecordDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_sociability, viewGroup, false);
        btnFinish = (Button) view.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(clickListener);

        txtEntradaCall = (TextView) view.findViewById(R.id.txtEntradaCall);
        txtSaidaCall = (TextView) view.findViewById(R.id.txtSaidaCall);
        txtPerdidaCall = (TextView) view.findViewById(R.id.txtPerdidaCall);

        txtEntradaSMS = (TextView) view.findViewById(R.id.txtEntradaSMS);
        txtSaidaSMS = (TextView) view.findViewById(R.id.txtSaidaSMS);

        txtCallRecordDate = (TextView) view.findViewById(R.id.txtCallRecordDate);
        txtSMSRecordDate = (TextView) view.findViewById(R.id.txtSMSRecordDate);

        try {
            phenotypesEventList = dpManager.getInstance().getPhenotypesList("Online_Sociability");
            setSettings();
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    public void setSettings(){
        List<DigitalPhenotypeEvent> digitalPhenotypeEventList = new ArrayList();

        for(int i = 0; i < phenotypesEventList.size(); i++){
            DigitalPhenotypeEvent dpe = new DigitalPhenotypeEvent();
            String str = phenotypesEventList.get(i).getPhenotypeEvent();
            dpe = phenotypesEventList.get(i).getObjectFromString(str);
            digitalPhenotypeEventList.add(dpe);
        }

        int total_entrada_call_record = 0;
        int total_saida_call_record = 0;
        int total_perdida_call_record = 0;
        int total_entrada_sms_record = 0;
        int total_saida_sms_record = 0;
        long callLastRecord = 0;
        long SMSRecordDate = 0;
        for(int i=0; i < digitalPhenotypeEventList.size(); i++){
            if(digitalPhenotypeEventList.get(i).getSituation().getLabel().equals("PhoneCall_Online_Sociability")){
                List<Attribute> attributeList = new ArrayList();
                attributeList = digitalPhenotypeEventList.get(i).getAttributes();
                for(int j=0; j < attributeList.size(); j++) {
                    if (attributeList.get(j).getValue().contains("Incoming_call")) {
                        total_entrada_call_record = total_entrada_call_record + 1;
                    } else if (attributeList.get(j).getValue().contains("Outgoing_call")) {
                        total_saida_call_record = total_saida_call_record + 1;
                    } else if (attributeList.get(j).getValue().contains("Missed_call")) {
                        total_perdida_call_record = total_perdida_call_record + 1;
                    }
                    if(attributeList.get(j).getType().contains("Date")){
                        String str = attributeList.get(j).getValue();
                        long val = Long.valueOf(str);
                        if(val > callLastRecord){
                            callLastRecord = val;
                        }
                    }
                }
            }
            else if(digitalPhenotypeEventList.get(i).getSituation().getLabel().equals("SMS_Online_Sociability")){
                List<Attribute> attributeList = new ArrayList();
                attributeList = digitalPhenotypeEventList.get(i).getAttributes();
                for(int j=0; j < attributeList.size(); j++) {
                    if (attributeList.get(j).getValue().contains("Sms_incoming")) {
                        total_entrada_sms_record = total_entrada_sms_record + 1;
                    } else if (attributeList.get(j).getValue().contains("Sms_outgoing")) {
                        total_saida_sms_record = total_saida_sms_record + 1;
                    }
                    if(attributeList.get(j).getType().contains("Date")){
                        long val = Long.parseLong(attributeList.get(j).getValue());
                        if(val > SMSRecordDate){
                            SMSRecordDate = val;
                        }
                    }
                }
            }
        }

        txtEntradaCall.setText(String.valueOf(total_entrada_call_record));
        txtSaidaCall.setText(String.valueOf(total_saida_call_record));
        txtPerdidaCall.setText(String.valueOf(total_perdida_call_record));

        txtEntradaSMS.setText(String.valueOf(total_entrada_sms_record));
        txtSaidaSMS.setText(String.valueOf(total_saida_sms_record));

        if(callLastRecord != 0){
            txtCallRecordDate.setText(String.valueOf(dateFormat.format(callLastRecord)));
        }
        if(SMSRecordDate != 0){
            txtSMSRecordDate.setText(String.valueOf(dateFormat.format(SMSRecordDate)));
        }
    }

    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnFinish: {
                    try {
                        List<String> dataProcessorsName = new ArrayList();
                        dataProcessorsName.add("Online_Sociability");
                        dpManager.getInstance().stopDataProcessors(dataProcessorsName);
                        Toast.makeText(getContext(), "Finish situation of interest: Online_Sociability",Toast.LENGTH_SHORT).show();
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