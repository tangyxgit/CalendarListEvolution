package com.tangyx.calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.calendarlistview.library.DatePickerController;
import com.andexert.calendarlistview.library.DayPickerView;
import com.andexert.calendarlistview.library.SimpleMonthAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tangyx on 16/3/22.
 *
 */
public class CalendarActivity extends AppCompatActivity implements DatePickerController,View.OnClickListener{
    private DayPickerView mDayPickerView;
    private RelativeLayout mGoLayout;
    private TextView mGo;
    private ImageView mGoDelete;
    private RelativeLayout mBackLayout;
    private TextView mBack;
    private ImageView mBackDelete;
    private boolean isSingle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mGoLayout = (RelativeLayout) findViewById(R.id.go_layout);
        mGo = (TextView) findViewById(R.id.go);
        mGoDelete = (ImageView) findViewById(R.id.go_delete);
        mBackLayout = (RelativeLayout) findViewById(R.id.back_layout);
        mBack = (TextView) findViewById(R.id.back);
        mBackDelete = (ImageView) findViewById(R.id.back_delete);
        mDayPickerView = (DayPickerView) findViewById(R.id.day_picker);
        mDayPickerView.setController(this);
        mGoDelete.setOnClickListener(this);
        mBackDelete.setOnClickListener(this);
        isSingle = getIntent().getBooleanExtra("isSingle",true);
        mDayPickerView.setSingle(isSingle);
        if(isSingle){
            findViewById(R.id.time_layout).setVisibility(View.GONE);
        }
        String date = getIntent().getStringExtra("minDay");
        if(!TextUtils.isEmpty(date)){
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTimeInMillis(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDayPickerView.setMinDay(calendar);
        }
    }

    @Override
    public int getMaxYear() {
        return Calendar.getInstance().get(Calendar.YEAR)+1;
    }

    /**
     * 当日期发生改变每次都会调用的函数
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        if(isSingle){
            Toast.makeText(this,"当前单选一个日期："+year+"-"+(month+1)+"-"+day,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 选中多个的时候触发,但是每次选中或者改变日期的时候都会触发
     * @param selectedDays
     */
    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {
        if(!isSingle){
            //获取第一个选择的日期
            SimpleMonthAdapter.CalendarDay firstDay = selectedDays.getFirst();
            //获取第二个选择日期
            SimpleMonthAdapter.CalendarDay lastDay = selectedDays.getLast();
            //根据需求做对应的逻辑处理，
            //我这里是做了一个往返日期逻辑，得到两个选中的日期后需要进行判断出小的日期肯定是出发日期。
            if(firstDay!=null){
                Calendar firstCalendar = Calendar.getInstance();
                firstCalendar.set(firstDay.year,firstDay.month,firstDay.day);
                mGoLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.color_48c4fe));
                mGoDelete.setVisibility(View.VISIBLE);
                mGo.setText(getString(R.string.select_calendar_go,getYYYY_MM_dd(new Date(firstCalendar.getTimeInMillis())).replace("-","/")));
                mGo.setTag(firstCalendar);
            }else{
                mGoLayout.setBackgroundColor(Color.WHITE);
                mGoDelete.setVisibility(View.INVISIBLE);
                mGo.setText(null);
                mGo.setTag(null);
            }
            if(lastDay!=null){
                Calendar lastCalendar = Calendar.getInstance();
                lastCalendar.set(lastDay.year,lastDay.month,lastDay.day);
                mBackLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.color_48c4fe));
                mBackDelete.setVisibility(View.VISIBLE);
                mBack.setText(getString(R.string.select_calendar_back,getYYYY_MM_dd(new Date(lastCalendar.getTimeInMillis())).replace("-","/")));
                mBack.setTag(lastCalendar);
            }else{
                mBackLayout.setBackgroundColor(Color.WHITE);
                mBackDelete.setVisibility(View.INVISIBLE);
                mBack.setText(null);
                mBack.setTag(null);
            }
            Calendar goCalendar = (Calendar) mGo.getTag();
            Calendar backCalendar = (Calendar) mBack.getTag();
            if(goCalendar!=null && backCalendar!=null
                    && goCalendar.getTimeInMillis()>backCalendar.getTimeInMillis()){
                mGo.setText(getString(R.string.select_calendar_go,getYYYY_MM_dd(new Date(backCalendar.getTimeInMillis())).replace("-","/")));
                mGo.setTag(backCalendar);
                selectedDays.setFirst(lastDay);

                mBack.setText(getString(R.string.select_calendar_back,getYYYY_MM_dd(new Date(goCalendar.getTimeInMillis())).replace("-","/")));
                mBack.setTag(goCalendar);
                selectedDays.setLast(firstDay);
            }
        }
    }

    public String getYYYY_MM_dd(Date date){
        return new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(date);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go_delete:
                mDayPickerView.getSelectedDays().setFirst(null);
                mDayPickerView.getSelectedDays().setLast(null);
                break;
            case R.id.back_delete:
                mDayPickerView.getSelectedDays().setLast(null);
                break;
        }
        //刷新日历
        mDayPickerView.setUpAdapter();
        onDateRangeSelected(mDayPickerView.getSelectedDays());
    }
}
