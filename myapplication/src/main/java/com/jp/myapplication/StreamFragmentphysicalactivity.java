package com.jp.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.database.PhenotypesEvent;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Attribute;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.dpmanager.DPManager;

public class StreamFragmentphysicalactivity extends DemoBase {
    private static final String TAG = StreamFragmentphysicalactivity.class.getName();
    private DPManager dpManager = DPManager.getInstance();
    private Button btnFinish;
    private List<PhenotypesEvent> phenotypesEventList = new ArrayList();
    private LineChart holderBackup = null;
    private boolean isStarted = false;

    private TextView txtValueRecordsVeic;
    private TextView txtValueRecordsBike;
    private TextView txtValueRecordsRunning;
    private TextView txtValueRecordsWalking;
    private TextView txtValueRecordsTilting;
    private TextView txtValueRecordsStill;
    private TextView txtValueRecordsFoot;
    private TextView txtRecordDate;

    private HashMap<Long, Integer> veicValue = new HashMap<>();
    private HashMap<Long, Integer> bikeValue = new HashMap<>();
    private HashMap<Long, Integer> runningValue = new HashMap<>();
    private HashMap<Long, Integer> walkingValue = new HashMap<>();
    private HashMap<Long, Integer> tiltingValue = new HashMap<>();
    private HashMap<Long, Integer> stillValue = new HashMap<>();
    private HashMap<Long, Integer> footValue = new HashMap<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd");
    private SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd-MM-yyyy");

    private ListView lv;

    ArrayList<ChartItem> list = new ArrayList<>();
    ArrayList<Entry> values1 = new ArrayList<>();
    ArrayList<Entry> values2 = new ArrayList<>();
    ArrayList<Entry> values3 = new ArrayList<>();
    ArrayList<Entry> values4 = new ArrayList<>();
    ArrayList<Entry> values5 = new ArrayList<>();
    ArrayList<Entry> values6 = new ArrayList<>();
    ArrayList<Entry> values7 = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the utilities
        Utils.init(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_physicalactivity, viewGroup, false);

        btnFinish = (Button) view.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(clickListener);

        txtValueRecordsVeic = (TextView) view.findViewById(R.id.txtValueRecordsVeic);
        txtValueRecordsBike = (TextView) view.findViewById(R.id.txtValueRecordsBike);
        txtValueRecordsRunning = (TextView) view.findViewById(R.id.txtValueRecordsRunning);
        txtValueRecordsWalking = (TextView) view.findViewById(R.id.txtValueRecordsWalking);
        txtValueRecordsTilting = (TextView) view.findViewById(R.id.txtValueRecordsTilting);
        txtValueRecordsStill = (TextView) view.findViewById(R.id.txtValueRecordsStill);
        txtValueRecordsFoot = (TextView) view.findViewById(R.id.txtValueRecordsFoot);
        txtRecordDate = (TextView) view.findViewById(R.id.txtRecordDate);

        lv = view.findViewById(R.id.listView1);

        isStarted = true;

        try {
            phenotypesEventList = dpManager.getInstance().getPhenotypesList("PhysicalActivity");
            setCardviewSettings();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            setListviewSettings();
        }
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        isStarted = false;
        super.onStop();
    }


    @Override
    public void onDestroy(){
        isStarted = false;
        super.onDestroy();
    }


    public boolean getIsStarted(){
        return this.isStarted;
    }


    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnFinish: {
                    try {
                        List<String> dataProcessorsName = new ArrayList();
                        dataProcessorsName.add("PhysicalActivity");
                        dpManager.getInstance().stopDataProcessors(dataProcessorsName);
                        Toast.makeText(getContext(), "Finish situation of interest: PhysicalActivity",Toast.LENGTH_SHORT).show();
                        btnFinish.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };


    public void setCardviewSettings() throws ParseException {
        List<DigitalPhenotypeEvent> digitalPhenotypeEventList = new ArrayList();

        for(int i = 0; i < phenotypesEventList.size(); i++){
            DigitalPhenotypeEvent dpe = new DigitalPhenotypeEvent();
            String str = phenotypesEventList.get(i).getPhenotypeEvent();
            dpe = phenotypesEventList.get(i).getObjectFromString(str);
            digitalPhenotypeEventList.add(dpe);
        }

        int totalRecordsVeic = 0;
        int totalRecordsBike = 0;
        int totalRecordsRunning = 0;
        int totalRecordsWalking = 0;
        int totalRecordsTilting = 0;
        int totalRecordsStill = 0;
        int totalRecordsFoot = 0;

        long lastRecordVeic = 0;
        long lastRecordBike = 0;
        long lastRecordRunning = 0;
        long lastRecordWalking = 0;
        long lastRecordTilting = 0;
        long lastRecordStill = 0;
        long lastRecordFoot = 0;
        long lastRecord = 0;
        for(int i=0; i < digitalPhenotypeEventList.size(); i++){
            String value = digitalPhenotypeEventList.get(i).getSituation().getLabel();
            if(value.equals("In_Vehicle")) {
                totalRecordsVeic = totalRecordsVeic + 1;

                List<Attribute> attributeList = new ArrayList();
                attributeList = digitalPhenotypeEventList.get(i).getAttributes();
                for(int j=0; j < attributeList.size(); j++) {
                    if(attributeList.get(j).getType().contains("Date")){
                        String str = attributeList.get(j).getValue();
                        long val = Long.valueOf(str);
                        if(val > lastRecordVeic){
                            lastRecordVeic = val;
                        }
                        //values1.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(val))),1));
                        addVeic(val);
                    }
                }
            }
            else if(value.equals("On_Bicycle")) {
                totalRecordsBike = totalRecordsBike + 1;

                List<Attribute> attributeList = new ArrayList();
                attributeList = digitalPhenotypeEventList.get(i).getAttributes();
                for(int j=0; j < attributeList.size(); j++) {
                    if(attributeList.get(j).getType().contains("Date")){
                        String str = attributeList.get(j).getValue();
                        long val = Long.valueOf(str);
                        if(val > lastRecordBike){
                            lastRecordBike = val;
                        }
                        //values1.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(val))),1));
                        addBike(val);
                    }
                }
            }
            else if(value.equals("Running")) {
                totalRecordsRunning = totalRecordsRunning + 1;

                List<Attribute> attributeList = new ArrayList();
                attributeList = digitalPhenotypeEventList.get(i).getAttributes();
                for(int j=0; j < attributeList.size(); j++) {
                    if(attributeList.get(j).getType().contains("Date")){
                        String str = attributeList.get(j).getValue();
                        long val = Long.valueOf(str);
                        if(val > lastRecordRunning){
                            lastRecordRunning = val;
                        }
                        //values1.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(val))),1));
                        addRunning(val);
                    }
                }
            }
            else if(value.equals("On_Foot")) {
                totalRecordsFoot = totalRecordsFoot + 1;

                List<Attribute> attributeList = new ArrayList();
                attributeList = digitalPhenotypeEventList.get(i).getAttributes();
                for(int j=0; j < attributeList.size(); j++) {
                    if(attributeList.get(j).getType().contains("Date")){
                        String str = attributeList.get(j).getValue();
                        long val = Long.valueOf(str);
                        if(val > lastRecordFoot){
                            lastRecordFoot = val;
                        }
                        //values1.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(val))),1));
                        addFoot(val);
                    }
                }
            }
            else if(value.equals("Still")) {
                totalRecordsStill = totalRecordsStill + 1;

                List<Attribute> attributeList = new ArrayList();
                attributeList = digitalPhenotypeEventList.get(i).getAttributes();
                for(int j=0; j < attributeList.size(); j++) {
                    if(attributeList.get(j).getType().contains("Date")){
                        String str = attributeList.get(j).getValue();
                        long val = Long.valueOf(str);
                        if(val > lastRecordStill){
                            lastRecordStill = val;
                        }
                        //values1.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(val))),1));
                        addStill(val);
                    }
                }
            }
            else if(value.equals("Tilting")) {
                totalRecordsTilting = totalRecordsTilting + 1;

                List<Attribute> attributeList = new ArrayList();
                attributeList = digitalPhenotypeEventList.get(i).getAttributes();
                for(int j=0; j < attributeList.size(); j++) {
                    if(attributeList.get(j).getType().contains("Date")){
                        String str = attributeList.get(j).getValue();
                        long val = Long.valueOf(str);
                        if(val > lastRecordTilting){
                            lastRecordTilting = val;
                        }
                        //values1.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(val))),1));
                        addTilting(val);
                    }
                }
            }
            else if(value.equals("Walking")) {
                totalRecordsWalking = totalRecordsWalking + 1;

                List<Attribute> attributeList = new ArrayList();
                attributeList = digitalPhenotypeEventList.get(i).getAttributes();
                for(int j=0; j < attributeList.size(); j++) {
                    if(attributeList.get(j).getType().contains("Date")){
                        String str = attributeList.get(j).getValue();
                        long val = Long.valueOf(str);
                        if(val > lastRecordWalking){
                            lastRecordWalking = val;
                        }
                        //values1.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(val))),1));
                        addWalking(val);
                    }
                }
            }
        }
        if(totalRecordsVeic != 0){
            txtValueRecordsVeic.setText(String.valueOf(totalRecordsVeic));
            if(lastRecordVeic > lastRecord){
                lastRecord = lastRecordVeic;
            }
        }
        if(totalRecordsBike != 0){
            txtValueRecordsBike.setText(String.valueOf(totalRecordsBike));
            if(lastRecordBike > lastRecord){
                lastRecord = lastRecordBike;
            }
        }
        if(totalRecordsRunning != 0){
            txtValueRecordsRunning.setText(String.valueOf(totalRecordsRunning));
            if(lastRecordRunning > lastRecord){
                lastRecord = lastRecordRunning;
            }
        }
        if(totalRecordsWalking != 0){
            txtValueRecordsWalking.setText(String.valueOf(totalRecordsWalking));
            if(lastRecordWalking > lastRecord){
                lastRecord = lastRecordWalking;
            }
        }
        if(totalRecordsTilting != 0){
            txtValueRecordsTilting.setText(String.valueOf(totalRecordsTilting));
            if(lastRecordTilting > lastRecord){
                lastRecord = lastRecordTilting;
            }
        }
        if(totalRecordsStill != 0){
            txtValueRecordsStill.setText(String.valueOf(totalRecordsStill));
            if(lastRecordStill > lastRecord){
                lastRecord = lastRecordStill;
            }
        }
        if(totalRecordsFoot != 0){
            txtValueRecordsFoot.setText(String.valueOf(totalRecordsFoot));
            if(lastRecordFoot > lastRecord){
                lastRecord = lastRecordFoot;
            }
        }
        if(lastRecord != 0){
            txtRecordDate.setText(String.valueOf(dateFormat.format(lastRecord)));
        }
    }


    public void addVeic(Long timeStamp) throws ParseException {
        try {
            String aux1 = String.valueOf(dateFormat3.format(timeStamp));
            Date dat = (Date) dateFormat3.parse(aux1);
            long aux2 = dat.getTime();
            if (!veicValue.containsKey(aux2)) {
                veicValue.put(aux2, 1);
                //return true;
            } else { //
                Integer value = veicValue.get(aux2);
                value = value + 1;
                veicValue.put(aux2, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void addBike(Long timeStamp) throws ParseException {
        try {
            String aux1 = String.valueOf(dateFormat3.format(timeStamp));
            Date dat = (Date) dateFormat3.parse(aux1);
            long aux2 = dat.getTime();
            if (!bikeValue.containsKey(aux2)) {
                bikeValue.put(aux2, 1);
                //return true;
            } else { //
                Integer value = bikeValue.get(aux2);
                value = value + 1;
                bikeValue.put(aux2, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void addRunning(Long timeStamp) throws ParseException {
        try {
            String aux1 = String.valueOf(dateFormat3.format(timeStamp));
            Date dat = (Date) dateFormat3.parse(aux1);
            long aux2 = dat.getTime();
            if (!runningValue.containsKey(aux2)) {
                runningValue.put(aux2, 1);
                //return true;
            } else { //
                Integer value = runningValue.get(aux2);
                value = value + 1;
                runningValue.put(aux2, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void addWalking(Long timeStamp) throws ParseException {
        try {
            String aux1 = String.valueOf(dateFormat3.format(timeStamp));
            Date dat = (Date) dateFormat3.parse(aux1);
            long aux2 = dat.getTime();
            if (!walkingValue.containsKey(aux2)) {
                walkingValue.put(aux2, 1);
                //return true;
            } else { //
                Integer value = walkingValue.get(aux2);
                value = value + 1;
                walkingValue.put(aux2, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void addTilting(Long timeStamp) throws ParseException {
        try {
            String aux1 = String.valueOf(dateFormat3.format(timeStamp));
            Date dat = (Date) dateFormat3.parse(aux1);
            long aux2 = dat.getTime();
            if (!tiltingValue.containsKey(aux2)) {
                tiltingValue.put(aux2, 1);
                //return true;
            } else { //
                Integer value = tiltingValue.get(aux2);
                value = value + 1;
                tiltingValue.put(aux2, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void addStill(Long timeStamp) throws ParseException {
        try {
            String aux1 = String.valueOf(dateFormat3.format(timeStamp));
            Date dat = (Date) dateFormat3.parse(aux1);
            long aux2 = dat.getTime();
            if (!stillValue.containsKey(aux2)) {
                stillValue.put(aux2, 1);
                //return true;
            } else { //
                Integer value = stillValue.get(aux2);
                value = value + 1;
                stillValue.put(aux2, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void addFoot(Long timeStamp) throws ParseException {
        try {
            String aux1 = String.valueOf(dateFormat3.format(timeStamp));
            Date dat = (Date) dateFormat3.parse(aux1);
            long aux2 = dat.getTime();
            if (!footValue.containsKey(aux2)) {
                footValue.put(aux2, 1);
                //return true;
            } else { //
                Integer value = footValue.get(aux2);
                value = value + 1;
                footValue.put(aux2, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void setListviewSettings(){
        list.add(new LineChartItem(generateDataLine(7), getContext()));

        ChartDataAdapter cda = new ChartDataAdapter(getContext(), list);
        lv.setAdapter(cda);
    }


    @Override
    protected void saveToGallery() {
        saveToGallery(holderBackup, "Graphic");
    }


    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //noinspection ConstantConditions
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            ChartItem ci = getItem(position);
            return ci != null ? ci.getItemType() : 0;
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }


    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Line data
     */
    private LineData generateDataLine(int cnt) {
        LineDataSet d1 = null;
        LineDataSet d2 = null;
        LineDataSet d3 = null;
        LineDataSet d4 = null;
        LineDataSet d5 = null;
        LineDataSet d6 = null;
        LineDataSet d7 = null;

        for (HashMap.Entry<Long, Integer> entry : veicValue.entrySet()) {
            System.out.printf("#### veicValue: %s -> %s%n", String.valueOf(dateFormat2.format(entry.getKey())), entry.getValue());
            values1.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(entry.getKey()))), entry.getValue()));
        }
        for (HashMap.Entry<Long, Integer> entry : bikeValue.entrySet()) {
            System.out.printf("#### bikeValue: %s -> %s%n", String.valueOf(dateFormat2.format(entry.getKey())), entry.getValue());
            values2.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(entry.getKey()))), entry.getValue()));
        }
        for (HashMap.Entry<Long, Integer> entry : runningValue.entrySet()) {
            System.out.printf("#### runningValue: %s -> %s%n", String.valueOf(dateFormat2.format(entry.getKey())), entry.getValue());
            values3.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(entry.getKey()))), entry.getValue()));
        }
        for (HashMap.Entry<Long, Integer> entry : walkingValue.entrySet()) {
            System.out.printf("#### walkingValue: %s -> %s%n", String.valueOf(dateFormat2.format(entry.getKey())), entry.getValue());
            values4.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(entry.getKey()))), entry.getValue()));
        }
        for (HashMap.Entry<Long, Integer> entry : tiltingValue.entrySet()) {
            System.out.printf("#### tiltingValue: %s -> %s%n", String.valueOf(dateFormat2.format(entry.getKey())), entry.getValue());
            values5.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(entry.getKey()))), entry.getValue()));
        }
        for (HashMap.Entry<Long, Integer> entry : stillValue.entrySet()) {
            System.out.printf("#### stillValue: %s -> %s%n", String.valueOf(dateFormat2.format(entry.getKey())), entry.getValue());
            values6.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(entry.getKey()))), entry.getValue()));
        }
        for (HashMap.Entry<Long, Integer> entry : footValue.entrySet()) {
            System.out.printf("#### footValue: %s -> %s%n", String.valueOf(dateFormat2.format(entry.getKey())), entry.getValue());
            values7.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(entry.getKey()))), entry.getValue()));
        }

        d1 = new LineDataSet(values1, "Vehicle");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        d2 = new LineDataSet(values2, "Bike");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 244, 0));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);

        d3 = new LineDataSet(values3, "Running");
        d3.setLineWidth(2.5f);
        d3.setCircleRadius(4.5f);
        d3.setHighLightColor(Color.rgb(0, 244, 207));
        d3.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        d3.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        d3.setDrawValues(false);

        d4 = new LineDataSet(values4, "Walking");
        d4.setLineWidth(2.5f);
        d4.setCircleRadius(4.5f);
        d4.setHighLightColor(Color.rgb(0, 57, 244));
        d4.setColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        d4.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        d4.setDrawValues(false);

        d5 = new LineDataSet(values5, "Tilting");
        d5.setLineWidth(2.5f);
        d5.setCircleRadius(4.5f);
        d5.setHighLightColor(Color.rgb(244, 117, 117));
        d5.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        d5.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        d5.setDrawValues(false);

        d6 = new LineDataSet(values6, "Still");
        d6.setLineWidth(2.5f);
        d6.setCircleRadius(4.5f);
        d6.setHighLightColor(Color.rgb(199, 0, 244));
        d6.setColor(ColorTemplate.VORDIPLOM_COLORS[4]);
        d6.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[4]);
        d6.setDrawValues(false);

        d7 = new LineDataSet(values7, "Foot");
        d7.setLineWidth(2.5f);
        d7.setCircleRadius(4.5f);
        d7.setHighLightColor(Color.rgb(244, 134, 0));
        /*d7.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d7.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);*/
        d7.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);
        sets.add(d3);
        sets.add(d4);
        sets.add(d5);
        sets.add(d6);
        sets.add(d7);
        LineData ld = new LineData(sets);
        return ld;
    }


    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Bar data
     */
    private BarData generateDataBar(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, (int) (Math.random() * 70) + 30));
        }

        BarDataSet d = new BarDataSet(entries, "New DataSet " + cnt);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);

        BarData cd = new BarData(d);
        cd.setBarWidth(0.9f);
        return cd;
    }


    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Pie data
     */
    private PieData generateDataPie() {

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "Quarter " + (i+1)));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        return new PieData(d);
    }


    @SuppressWarnings("unused")
    public abstract class ChartItem {

        static final int TYPE_BARCHART = 0;
        static final int TYPE_LINECHART = 1;
        static final int TYPE_PIECHART = 2;

        ChartData<?> mChartData;

        ChartItem(ChartData<?> cd) {
            this.mChartData = cd;
        }

        public abstract int getItemType();

        public abstract View getView(int position, View convertView, Context c);
    }


    public class LineChartItem extends ChartItem {

        private final Typeface mTf;

        public LineChartItem(ChartData<?> cd, Context c) {
            super(cd);

            mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Bold.ttf");
        }

        @Override
        public int getItemType() {
            return TYPE_LINECHART;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, Context c) {

            LineChartItem.ViewHolder holder;

            if (convertView == null) {

                holder = new LineChartItem.ViewHolder();

                convertView = LayoutInflater.from(c).inflate(
                        R.layout.list_item_linechart, null);
                holder.chart = convertView.findViewById(R.id.chart);

                convertView.setTag(holder);

            } else {
                holder = (LineChartItem.ViewHolder) convertView.getTag();
            }

            // apply styling
            // holder.chart.setValueTypeface(mTf);
            holder.chart.getDescription().setEnabled(false);
            holder.chart.setDrawGridBackground(false);

            IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(holder.chart);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTypeface(mTf);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f); // only intervals of 1 day
            xAxis.setLabelCount(7);
            xAxis.setValueFormatter((ValueFormatter) xAxisFormatter);

            IAxisValueFormatter custom = new MyAxisValueFormatter();

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setTypeface(mTf);
            leftAxis.setLabelCount(8, false);
            leftAxis.setValueFormatter((ValueFormatter) custom);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

            Legend l = holder.chart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setForm(Legend.LegendForm.SQUARE);
            l.setFormSize(9f);
            l.setTextSize(11f);
            l.setXEntrySpace(4f);

            XYMarkerView mv = new XYMarkerView(getContext(), xAxisFormatter);
            mv.setChartView(holder.chart); // For bounds control
            holder.chart.setMarker(mv); // Set the marker to the chart

            // set data
            holder.chart.setData((LineData) mChartData);
            holderBackup = (LineChart)holder.chart;

            // do not forget to refresh the chart
            // holder.chart.invalidate();
            holder.chart.animateX(750);

            return convertView;
        }

        private class ViewHolder {
            LineChart chart;
        }
    }


    public class DayAxisValueFormatter extends ValueFormatter {

        private final String[] mMonths = new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        private final BarLineChartBase<?> chart;

        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            int days = (int) value;

            int year = determineYear(days);

            int month = determineMonth(days);
            String monthName = mMonths[month % mMonths.length];
            String yearName = String.valueOf(year);

            if (chart.getVisibleXRange() > 30 * 6) {

                return monthName + " " + yearName;
            } else {

                int dayOfMonth = determineDayOfMonth(days, month + 12 * (year - 2016));

                String appendix = "th";

                switch (dayOfMonth) {
                    case 1:
                        appendix = "st";
                        break;
                    case 2:
                        appendix = "nd";
                        break;
                    case 3:
                        appendix = "rd";
                        break;
                    case 21:
                        appendix = "st";
                        break;
                    case 22:
                        appendix = "nd";
                        break;
                    case 23:
                        appendix = "rd";
                        break;
                    case 31:
                        appendix = "st";
                        break;
                }

                return dayOfMonth == 0 ? "" : dayOfMonth + appendix + " " + monthName;
            }
        }

        private int getDaysForMonth(int month, int year) {

            // month is 0-based

            if (month == 1) {
                boolean is29Feb = false;

                if (year < 1582)
                    is29Feb = (year < 1 ? year + 1 : year) % 4 == 0;
                else if (year > 1582)
                    is29Feb = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);

                return is29Feb ? 29 : 28;
            }

            if (month == 3 || month == 5 || month == 8 || month == 10)
                return 30;
            else
                return 31;
        }

        private int determineMonth(int dayOfYear) {

            int month = -1;
            int days = 0;

            while (days < dayOfYear) {
                month = month + 1;

                if (month >= 12)
                    month = 0;

                int year = determineYear(days);
                days += getDaysForMonth(month, year);
            }

            return Math.max(month, 0);
        }

        private int determineDayOfMonth(int days, int month) {

            int count = 0;
            int daysForMonths = 0;

            while (count < month) {

                int year = determineYear(daysForMonths);
                daysForMonths += getDaysForMonth(count % 12, year);
                count++;
            }

            return days - daysForMonths;
        }

        private int determineYear(int days) {

            if (days <= 366)
                return 2016;
            else if (days <= 730)
                return 2017;
            else if (days <= 1094)
                return 2018;
            else if (days <= 1458)
                return 2019;
            else
                return 2020;

        }
    }


    public class MyAxisValueFormatter extends ValueFormatter {

        private final DecimalFormat mFormat;

        public MyAxisValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value) + " $";
        }
    }


    @SuppressLint("ViewConstructor")
    public class XYMarkerView extends MarkerView {

        private final TextView tvContent;
        private final IAxisValueFormatter xAxisValueFormatter;

        private final DecimalFormat format;

        public XYMarkerView(Context context, IAxisValueFormatter xAxisValueFormatter) {
            super(context, R.layout.custom_marker_view);

            this.xAxisValueFormatter = xAxisValueFormatter;
            tvContent = findViewById(R.id.tvContent);
            format = new DecimalFormat("###.0");
        }

        // runs every time the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            tvContent.setText(String.format("x: %s, y: %s", xAxisValueFormatter.getFormattedValue(e.getX(), null), format.format(e.getY())));

            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }
    }
}