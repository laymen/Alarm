package com.microsoft.mimickeralarm.appcore;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.ibm.icu.text.SimpleDateFormat;
import com.microsoft.mimickeralarm.R;
import com.microsoft.mimickeralarm.model.DailyList;
import com.microsoft.mimickeralarm.model.Weeks;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * Created by qifan on 2017/3/8.
 */

public class UserHobitsActivity extends AppCompatActivity {

    private RelativeLayout chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_hobits_show);
        chart = (RelativeLayout) findViewById(R.id.chart);
        lineView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.back_up_index);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHobitsActivity.this.finish();
            }
        });
    }

    //折线图
    public void lineView() {
        //同样是需要数据dataset和视图渲染器renderer
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        XYSeries series = new XYSeries("起床时间");

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.setBarWidth(300);
        mRenderer.setLegendHeight(300);
        //图形的显示大小  数值越小 面积越大
//        mRenderer.setBarSpacing(0.99);
        // 上  左  下 右   图表四周的范围
        mRenderer.setMargins(new int[]{280, 80, 30, 60});
        mRenderer.setXLabelsPadding(30);
        //设置图表的X轴的当前方向
        mRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        //设置轴标题文本大小
//        mRenderer.setAxisTitleTextSize(20);
        //设置图表标题
//        mRenderer.setChartTitle("起床时间");
        //设置图表标题文字的大小
        mRenderer.setChartTitleTextSize(50);
        //设置标签的文字大小
        mRenderer.setLabelsTextSize(30);
        //设置图例文本大小
        mRenderer.setLegendTextSize(20);
        //设置点的大小
        mRenderer.setPointSize(5f);
        //设置y轴最小值是0
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(24);
        //设置Y轴刻度个数（貌似不太准确）
        mRenderer.setYLabels(10);
        mRenderer.setXAxisMax(7);
        //显示网格
        mRenderer.setShowGrid(true);
        //将x标签栏目显示如：1,2,3,4替换为显示1月，2月，3月，4月
//        List<String> dates=DateTimeUtilities.getSevenDayDate();
        //
        List<Weeks> weekses = DailyList.get(this).getDaily();//获得数据
        Log.i("mouse2017年3月12号----》", weekses.size() + "");
        for (int i = 0; i < weekses.size(); i++) {
            Log.i("mouse2017年3月12号1----》", weekses.get(i).getmMonthDay());
            Long time = Long.parseLong(weekses.get(i).getmMonthDay());
            Log.i("mouse2017年3月12号2----》", time + "");
            Calendar calendarAlarm = Calendar.getInstance();
            calendarAlarm.setTimeInMillis(time);//是按秒来计算的
            //把月份取出来，把天取出来
            String x = "";
            int hour = calendarAlarm.getTime().getHours();
            int minute = calendarAlarm.getTime().getMinutes();
            String str = "0." + String.valueOf(minute);//拼接
            double y = hour + Double.parseDouble(str);
            BigDecimal b = new BigDecimal(y);
            y = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (i == 0) {//取出月份和天，如03月5日
                SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日");
                x = format1.format(calendarAlarm.getTime());
                Log.i("mouse2017年3月12号x----》", x);

            } else {//其他的只需要取出天就行了
                SimpleDateFormat format2 = new SimpleDateFormat("dd");
                x = format2.format(calendarAlarm.getTime());
            }
            mRenderer.addXTextLabel(i, x);//x轴
            series.add(i, y);//y轴
        }
        mDataset.addSeries(series);
        //设置只显示如1月，2月等替换后的东西，不显示1,2,3等
        mRenderer.setXLabels(0);
        //设置是否可以缩放
        mRenderer.setExternalZoomEnabled(true);
        //设置滑动,这边是横向可以滑动,竖向不可滑动
        mRenderer.setPanEnabled(false, false);
        mRenderer.setPanLimits(new double[]{-1, 0});
        //设置标签的间距
        mRenderer.setYLabelsPadding(10);
        //设置标签倾斜度
        mRenderer.setXLabelsAngle(0);
        mRenderer.setShowGrid(true);
        //设置标签居Y轴的方向,设置刻度线与Y轴之间的相对位置关系
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        //设置画布距数轴之间的颜色
        mRenderer.setMarginsColor(getResources().getColor(R.color.colorPrimary));
        //(类似于一条线对象)
        XYSeriesRenderer r = new XYSeriesRenderer();
        //设置颜色
        r.setColor(Color.BLUE);
        //设置点的样式
        r.setPointStyle(PointStyle.SQUARE);
        //填充点（显示的点是空心还是实心）
        r.setFillPoints(true);
        //将点的值显示出来
        r.setDisplayChartValues(true);
        //显示的点的值与图的距离
        r.setChartValuesSpacing(15);
        //点的值的文字大小
        r.setChartValuesTextSize(40);
        //设置线宽
        r.setLineWidth(4);
        //是否填充折线图的下方
        r.setFillBelowLine(true);
        //填充的颜色，如果不设置就默认与线的颜色一致
        r.setFillBelowLineColor(Color.argb(20, 200, 200, 200));
        mRenderer.addSeriesRenderer(r);
        //getLineChartView()：生成一个View，用户可以自行设置它的显示。
        GraphicalView view = ChartFactory.getLineChartView(this, mDataset, mRenderer);
        //设置view的背景
        view.setBackgroundResource(R.drawable.timg1);
        chart.addView(view);
        view.repaint();
    }

}
