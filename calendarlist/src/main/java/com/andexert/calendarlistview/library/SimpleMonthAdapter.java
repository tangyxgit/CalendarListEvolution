/***********************************************************************************
 * The MIT License (MIT)

 * Copyright (c) 2014 Robin Chutaux

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ***********************************************************************************/
package com.andexert.calendarlistview.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SimpleMonthAdapter extends RecyclerView.Adapter<SimpleMonthAdapter.ViewHolder> implements SimpleMonthView.OnDayClickListener {

    protected static final int MONTHS_IN_YEAR = 12;
    private final TypedArray typedArray;
	private final Context mContext;
	private final DatePickerController mController;
    private final Calendar calendar;
    private final SelectedDays<CalendarDay> selectedDays;
    private final Integer firstMonth;
    private final Integer lastMonth;
    private boolean isSingle;
    private Calendar minDay;
    private Calendar trainDay;
    private Bitmap trainMap;

	public SimpleMonthAdapter(Context context, DatePickerController datePickerController, TypedArray typedArray) {
        this.typedArray = typedArray;
        calendar = Calendar.getInstance();
        firstMonth = typedArray.getInt(R.styleable.DayPickerView_firstMonth, calendar.get(Calendar.MONTH));
        lastMonth = typedArray.getInt(R.styleable.DayPickerView_lastMonth, (calendar.get(Calendar.MONTH)) % MONTHS_IN_YEAR);
        isSingle = typedArray.getBoolean(R.styleable.DayPickerView_selectSingle,false);
        selectedDays = new SelectedDays<>();
		mContext = context;
		mController = datePickerController;
        trainMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.calendar_train_blue);
        trainMap = onScaleImage(trainMap, 0.5f);
		init();
	}

    private Bitmap onScaleImage(Bitmap bitmap, float scale){
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final SimpleMonthView simpleMonthView = new SimpleMonthView(mContext, typedArray);
        simpleMonthView.setMinDay(minDay);
        simpleMonthView.setMaxYear(mController.getMaxYear());
        simpleMonthView.setLastMonth(lastMonth);
        return new ViewHolder(simpleMonthView, this);
    }

    public void setMinDay(Calendar minDay) {
        this.minDay = minDay;
    }

    public void setTrainDay(Calendar trainDay){
        this.trainDay = trainDay;
    }

    public void setSingle(boolean isSingle){
        this.isSingle = isSingle;
        if(isSingle){
            selectedDays.setLast(new CalendarDay(System.currentTimeMillis()));
        }else{
            selectedDays.setLast(null);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        final SimpleMonthView v = viewHolder.simpleMonthView;
        final HashMap<String, Integer> drawingParams = new HashMap<String, Integer>();
        int month;
        int year;

        month = (firstMonth + (position % MONTHS_IN_YEAR)) % MONTHS_IN_YEAR;
        year = position / MONTHS_IN_YEAR + calendar.get(Calendar.YEAR) + ((firstMonth + (position % MONTHS_IN_YEAR)) / MONTHS_IN_YEAR);

        int selectedFirstDay = -1;
        int selectedLastDay = -1;
        int selectedFirstMonth = -1;
        int selectedLastMonth = -1;
        int selectedFirstYear = -1;
        int selectedLastYear = -1;

        if (selectedDays.getFirst() != null)
        {
            selectedFirstDay = selectedDays.getFirst().day;
            selectedFirstMonth = selectedDays.getFirst().month;
            selectedFirstYear = selectedDays.getFirst().year;
        }

        if (selectedDays.getLast() != null && !isSingle)
        {
            selectedLastDay = selectedDays.getLast().day;
            selectedLastMonth = selectedDays.getLast().month;
            selectedLastYear = selectedDays.getLast().year;
        }

        v.reuse();

        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_YEAR, selectedFirstYear);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_YEAR, selectedLastYear);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_MONTH, selectedFirstMonth);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_MONTH, selectedLastMonth);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_BEGIN_DAY, selectedFirstDay);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_SELECTED_LAST_DAY, selectedLastDay);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_WEEK_START, calendar.getFirstDayOfWeek());
        v.setMonthParams(drawingParams);
        v.invalidate();
    }

    public long getItemId(int position) {
		return position;
	}

    @Override
    public int getItemCount()
    {
        int itemCount = (((mController.getMaxYear() - calendar.get(Calendar.YEAR)) + 1) * MONTHS_IN_YEAR);

        if (firstMonth != -1)
            itemCount -= firstMonth;

        if (lastMonth != -1)
            itemCount -= (MONTHS_IN_YEAR - lastMonth) - 1;

        return itemCount;
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        final SimpleMonthView simpleMonthView;

        ViewHolder(View itemView, SimpleMonthView.OnDayClickListener onDayClickListener)
        {
            super(itemView);
            simpleMonthView = (SimpleMonthView) itemView;
            simpleMonthView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            simpleMonthView.setClickable(true);
            simpleMonthView.setOnDayClickListener(onDayClickListener);
        }
    }

	protected void init() {
        if (typedArray.getBoolean(R.styleable.DayPickerView_currentDaySelected, false)){
            onDayTapped(new CalendarDay(System.currentTimeMillis()));
        }
        if(isSingle){
            selectedDays.setLast(new CalendarDay(System.currentTimeMillis()));
        }
    }

	public void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay) {
		if (calendarDay != null) {
			onDayTapped(calendarDay);
        }
	}

	private void onDayTapped(CalendarDay calendarDay) {
		mController.onDayOfMonthSelected(calendarDay.year, calendarDay.month, calendarDay.day);
		setSelectedDay(calendarDay);
	}

	private void setSelectedDay(CalendarDay calendarDay) {
        if (selectedDays.getFirst() != null && selectedDays.getLast() == null) {
            selectedDays.setLast(calendarDay);
            if (selectedDays.getFirst().month < calendarDay.month) {
                for (int i = 0; i < selectedDays.getFirst().month - calendarDay.month - 1; ++i)
                    mController.onDayOfMonthSelected(selectedDays.getFirst().year, selectedDays.getFirst().month + i, selectedDays.getFirst().day);
            }
        } else if (selectedDays.getLast() != null) {
            selectedDays.setFirst(calendarDay);
            if(!isSingle){
                selectedDays.setLast(null);
            }
        } else{
            selectedDays.setFirst(calendarDay);
        }
        mController.onDateRangeSelected(selectedDays);
		notifyDataSetChanged();
	}

	public static class CalendarDay implements Serializable {
        private static final long serialVersionUID = -5456695978688356202L;
        private Calendar calendar;

		public int day;
		public int month;
		public int year;

		public CalendarDay() {
			setTime(System.currentTimeMillis());
		}

		public CalendarDay(int year, int month, int day) {
			setDay(year, month, day);
		}

		public CalendarDay(long timeInMillis) {
			setTime(timeInMillis);
		}

		public CalendarDay(Calendar calendar) {
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
		}

		private void setTime(long timeInMillis) {
			if (calendar == null) {
				calendar = Calendar.getInstance();
            }
			calendar.setTimeInMillis(timeInMillis);
			month = this.calendar.get(Calendar.MONTH);
			year = this.calendar.get(Calendar.YEAR);
			day = this.calendar.get(Calendar.DAY_OF_MONTH);
		}

		public void set(CalendarDay calendarDay) {
		    year = calendarDay.year;
			month = calendarDay.month;
			day = calendarDay.day;
		}

		public void setDay(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}

        public Date getDate()
        {
            if (calendar == null) {
                calendar = Calendar.getInstance();
            }
            calendar.set(year, month, day);
            return calendar.getTime();
        }

        @Override
        public String toString()
        {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{ year: ");
            stringBuilder.append(year);
            stringBuilder.append(", month: ");
            stringBuilder.append(month);
            stringBuilder.append(", day: ");
            stringBuilder.append(day);
            stringBuilder.append(" }");

            return stringBuilder.toString();
        }
    }

    public SelectedDays<CalendarDay> getSelectedDays()
    {
        return selectedDays;
    }

    public static class SelectedDays<K> implements Serializable
    {
        private static final long serialVersionUID = 3942549765282708376L;
        private K first;
        private K last;

        public K getFirst()
        {
            return first;
        }

        public void setFirst(K first)
        {
            this.first = first;
        }

        public K getLast()
        {
            return last;
        }

        public void setLast(K last)
        {
            this.last = last;
        }
    }
}