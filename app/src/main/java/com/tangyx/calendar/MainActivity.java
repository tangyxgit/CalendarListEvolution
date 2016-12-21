package com.tangyx.calendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.andexert.calendarlistview.library.DayPickerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.single).setOnClickListener(this);
        findViewById(R.id.choice).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,CalendarActivity.class);
        switch (v.getId()){
            case R.id.single:
                intent.putExtra("isSingle",true);
                break;
            case R.id.choice:
                intent.putExtra("isSingle",false);
                break;
        }
        startActivity(intent);
    }
}
