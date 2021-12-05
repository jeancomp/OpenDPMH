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

public class StreamFragmentPhysicalSociability extends DemoBase {
    private static final String TAG = StreamFragmentPhysicalSociability.class.getName();
    private DPManager dpManager = DPManager.getInstance();
    private Button btnFinish;
    private List<PhenotypesEvent> phenotypesEventList = new ArrayList();
    private LineChart holderBackup = null;
    private boolean isStarted = false;

    private TextView txtValueRecords;
    private TextView txtRecordDate;

    private HashMap<Long, Integer> audioValue = new HashMap<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd");
    private SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd-MM-yyyy");

    private ListView lv;

    ArrayList<ChartItem> list = new ArrayList<>();
    ArrayList<Entry> values1 = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the utilities
        Utils.init(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_physical_sociability, viewGroup, false);
        btnFinish = (Button) view.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(clickListener);
        txtValueRecords = (TextView) view.findViewById(R.id.txtValueRecords);
        txtRecordDate = (TextView) view.findViewById(R.id.txtRecordDate);

        lv = view.findViewById(R.id.listView1);

        isStarted = true;

        try {
            phenotypesEventList = dpManager.getInstance().getPhenotypesList("Physical_Sociability");
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
                        dataProcessorsName.add("Physical_Sociability");
                        dpManager.getInstance().stopDataProcessors(dataProcessorsName);
                        Toast.makeText(getContext(), "Finish situation of interest: Physical_Sociability",Toast.LENGTH_SHORT).show();
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
        String value = String.valueOf(phenotypesEventList.size());
        txtValueRecords.setText(value);

        List<DigitalPhenotypeEvent> digitalPhenotypeEventList = new ArrayList();

        for(int i = 0; i < phenotypesEventList.size(); i++){
            DigitalPhenotypeEvent dpe = new DigitalPhenotypeEvent();
            String str = phenotypesEventList.get(i).getPhenotypeEvent();
            dpe = phenotypesEventList.get(i).getObjectFromString(str);
            digitalPhenotypeEventList.add(dpe);
        }

        long audioLastRecord = 0;
        for(int i=0; i < digitalPhenotypeEventList.size(); i++){
            if(digitalPhenotypeEventList.get(i).getSituation().getLabel().equals("Physical_Sociability")){
                List<Attribute> attributeList = new ArrayList();
                attributeList = digitalPhenotypeEventList.get(i).getAttributes();
                for(int j=0; j < attributeList.size(); j++) {
                    if(attributeList.get(j).getType().contains("Date")){
                        String str = attributeList.get(j).getValue();
                        long val = Long.valueOf(str);
                        if(val > audioLastRecord){
                            audioLastRecord = val;
                        }
                        //values1.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(val))),1));
                        addAudio(val);
                    }
                }
            }
        }
        if(audioLastRecord != 0){
            txtRecordDate.setText(String.valueOf(dateFormat.format(audioLastRecord)));
        }
    }


    public void addAudio(Long timeStamp) throws ParseException {
        try {
            String aux1 = String.valueOf(dateFormat3.format(timeStamp));
            Date dat = (Date) dateFormat3.parse(aux1);
            long aux2 = dat.getTime();
            if (!audioValue.containsKey(aux2)) {
                audioValue.put(aux2, 1);
                //return true;
            } else { //
                Integer value = audioValue.get(aux2);
                value = value + 1;
                audioValue.put(aux2, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void setListviewSettings(){
        list.add(new LineChartItem(generateDataLine(3 + 1), getContext()));

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
        //ArrayList<Entry> values1 = new ArrayList<>();

    /*for (int i = 0; i < 12; i++) {
        values1.add(new Entry(i, (int) (Math.random() * 65) + 40));
    }*/
        for (HashMap.Entry<Long, Integer> entry : audioValue.entrySet()) {
            System.out.printf("#### Audio: %s -> %s%n", String.valueOf(dateFormat2.format(entry.getKey())), entry.getValue());
            values1.add(new Entry(Integer.valueOf(String.valueOf(dateFormat2.format(entry.getKey()))), entry.getValue()));
        }

        d1 = new LineDataSet(values1, "Audio");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
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