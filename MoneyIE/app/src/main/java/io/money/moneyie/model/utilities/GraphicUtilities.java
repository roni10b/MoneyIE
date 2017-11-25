package io.money.moneyie.model.utilities;


import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.DefaultXAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.money.moneyie.R;
import io.money.moneyie.model.MoneyFlow;

public abstract class GraphicUtilities {

    public static void dataFilerForCurrentTab(TextView income, TextView expense, TextView overall, List<MoneyFlow> filtererArray, ImageView imgPlusMinus) {
        double inc = 0;
        double exp = 0;
        double ovrallF;

        for (int i = 0; i < filtererArray.size(); i++) {
            if (filtererArray.get(i).getExpense().equalsIgnoreCase("ex")) {
                exp += filtererArray.get(i).getSum();
            } else {
                inc += filtererArray.get(i).getSum();
            }
        }

        ovrallF = inc - exp;
        if(ovrallF > 0){
            imgPlusMinus.setImageResource(R.drawable.ie_progres);
        } else {
            imgPlusMinus.setImageResource(R.drawable.ie_progres_low);
        }
        income.setText(String.format("%.2f", inc));
        expense.setText(String.format("%.2f", exp));
        overall.setText(String.format("%.2f", ovrallF));
    }

    //creating and setting data to the combined chart in the tab fragment FragmentTab_YearGraphic
    public static void combinedBarChart(BarChart chart, List<MoneyFlow> filteredArr, ImageView questionImg){

        if (filteredArr.size() == 0) {
            chart.setVisibility(View.GONE);
            questionImg.setVisibility(View.GONE);
            return;
        }

        chart.setDescription(null);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        ArrayList<String> xVals = new ArrayList();

        xVals.add("Jan");
        xVals.add("Feb");
        xVals.add("Mar");
        xVals.add("Apr");
        xVals.add("May");
        xVals.add("Jun");
        xVals.add("Jul");
        xVals.add("Aug");
        xVals.add("Sep");
        xVals.add("Oct");
        xVals.add("Nov");
        xVals.add("Dec");

        float [] income = {0,0,0,0,0,0,0,0,0,0,0,0};
        float [] expense = {0,0,0,0,0,0,0,0,0,0,0,0};

        //filter the database array to monthly incomes and expenses
        Calendar cal = Calendar.getInstance();
        for(MoneyFlow m : filteredArr){
            if(m.getExpense().equalsIgnoreCase("in")){
                cal.setTimeInMillis(m.getCalendar());
                income[cal.get(Calendar.MONTH)] += m.getSum();
            } else {
                cal.setTimeInMillis(m.getCalendar());
                expense[cal.get(Calendar.MONTH)] += m.getSum();
            }
        }

        ArrayList<BarEntry> yVals1 = new ArrayList(); //expense
        ArrayList<BarEntry> yVals2 = new ArrayList(); //income

        for(int i =0; i<income.length; i++){
            yVals1.add(new BarEntry((float) expense[i], i));
            yVals2.add(new BarEntry((float) income[i], i));
        }

        //draw the graph
        BarDataSet set1, set2;
        set1 = new BarDataSet(yVals2, "Income");
        set1.setColor(Color.GREEN);
        set2 = new BarDataSet(yVals1, "Expense");
        set2.setColor(Color.RED);

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        sets.add(set2);

        BarData data = new BarData(xVals, sets);
        data.setValueFormatter(new LargeValueFormatter());
        chart.setData(data);
        chart.getXAxis().setValueFormatter(new DefaultXAxisValueFormatter());
        chart.animateY(1000);
        chart.invalidate();
    }

    //setting the data to the pie chart used in the tab fragments in the folder graphicsTabs
    public static void pieChart(PieChart pieChart, List<MoneyFlow> utilitiesArray, ImageView questionImg){

        if (utilitiesArray.size() == 0) {
            pieChart.setVisibility(View.GONE);
            questionImg.setVisibility(View.GONE);
            return;
        }

        pieChart.setUsePercentValues(true);
        pieChart.setHoleColor(Color.YELLOW);
        pieChart.setHoleRadius(5);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationEnabled(true);
        Legend leg = pieChart.getLegend();
        leg.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);

        HashMap<String, Float> structuredData2 = new HashMap<>();
        ArrayList<Entry> pieDataSave = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        //filtering the data
        for (int z = 0; z < utilitiesArray.size(); z++) {
            if (structuredData2.containsKey(utilitiesArray.get(z).getExpense())) {
                structuredData2.put(utilitiesArray.get(z).getExpense(), structuredData2.get(utilitiesArray.get(z).getExpense()) + (float)utilitiesArray.get(z).getSum());
            } else {
                structuredData2.put(utilitiesArray.get(z).getExpense(), (float)utilitiesArray.get(z).getSum());
            }
        }

        int i = 0;
        for (Iterator<Map.Entry<String, Float>> iterator = structuredData2.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Float> entry = iterator.next();
            pieDataSave.add(new Entry(entry.getValue(), i));
            if (entry.getKey().equalsIgnoreCase("ex")) {
                names.add("Expense");
            } else {
                names.add("Income");
            }
            i++;
        }

        //set some settings of the pie chart
        PieDataSet pieDataSet = new PieDataSet(pieDataSave, "- Income/Expense");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(5);

        pieDataSet.setValueTextSize(15f);
        pieDataSet.setValueTextColor(Color.BLACK);
        PieData pieData = new PieData(names, pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setDescription("");
        pieChart.setData(pieData);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    //setting data to the horizontal bar chart
    public static void horizontalBarChart(HorizontalBarChart horizontalBarChart, List<MoneyFlow> filteredArr, ImageView questionImg){

        if (filteredArr.size() == 0) {
            horizontalBarChart.setVisibility(View.GONE);
            questionImg.setVisibility(View.GONE);
            return;
        }

        //map used to filter the data
        TreeMap<String, Float> structuredData = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });

        ArrayList<BarEntry> horizontalBarChartArr = new ArrayList<>();
        final List<String> names = new ArrayList<String>();

        for (int i = 0; i < filteredArr.size(); i++) {
            if (filteredArr.get(i).getExpense().equalsIgnoreCase("ex")) {
                if (!structuredData.containsKey(filteredArr.get(i).getType())) {
                    structuredData.put(filteredArr.get(i).getType(), (float)filteredArr.get(i).getSum());
                } else {
                    structuredData.put(filteredArr.get(i).getType(), (structuredData.get(filteredArr.get(i).getType())+ (float)filteredArr.get(i).getSum()));
                }
            }
        }

        if (structuredData.isEmpty()) {
            horizontalBarChart.setVisibility(View.INVISIBLE);
            return;
        }
        //filter the names and values of the expenses
        int i = 0;
        ArrayList<Float> values = new ArrayList<>();
        for (Iterator<Map.Entry<String, Float>> iterator = structuredData.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Float> entry = iterator.next();
            names.add(entry.getKey());
            values.add(entry.getValue());
        }

        int pix = 0;
        for (int z = 0; z < values.size(); z++) {
            horizontalBarChartArr.add(new BarEntry(values.get(z), z));
            pix+=50;
        }

        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pix, horizontalBarChart.getContext().getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) horizontalBarChart.getLayoutParams();
        params.height = (int) pixels;
        horizontalBarChart.setLayoutParams(params);

        //set the settings of the horizontal bar char
        BarDataSet bardataset = new BarDataSet(horizontalBarChartArr, "Expenses");
        bardataset.setColor(Color.RED);
        BarData data = new BarData(names, bardataset);
        data.setValueTextSize(12);
        horizontalBarChart.setDoubleTapToZoomEnabled(false);
        horizontalBarChart.setDescription("");
        horizontalBarChart.invalidate();
        horizontalBarChart.animateY(1000);
        horizontalBarChart.setData(data);
    }
}
