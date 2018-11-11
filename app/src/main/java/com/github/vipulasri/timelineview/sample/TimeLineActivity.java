package com.github.vipulasri.timelineview.sample;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.vipulasri.timelineview.sample.Unit.UTime;
import com.github.vipulasri.timelineview.sample.base.BaseActivity;
import com.github.vipulasri.timelineview.sample.model.OrderStatus;
import com.github.vipulasri.timelineview.sample.model.Orientation;
import com.github.vipulasri.timelineview.sample.model.TimeLineModel;

import org.litepal.LitePal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private FloatingActionButton flaction;
    private TimeLineLocation timeLineLocation;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        timeLineLocation = new TimeLineLocation(this);

        mOrientation = Orientation.VERTICAL;
        mWithLinePadding = false;
        setTitle(R.string.tile);
        mRecyclerView = findViewById(R.id.recyclerView);
        flaction = findViewById(R.id.floatingActionButton);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);
        initLocation();
        initView();
    }

    @SuppressLint({"CheckResult", "MissingPermission"})
    private void initLocation() {


    }

    private LinearLayoutManager getLinearLayoutManager() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        } else {
            return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
    }

    private void initView() {
        setDataListItems();
        bindFloatButton();
        initToolBar();
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    private void initToolBar() {
        toolbar.setTitle("----测试用的title");
        toolbar.setOnClickListener(v -> {
            v.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            onYearMonthDayPicker(v);
        });
    }

    @SuppressLint("MissingPermission")
    private void bindFloatButton() {
        flaction.setOnClickListener(v -> {
            Location location = timeLineLocation.getLocation();
            String msg = "无法获取地理位置";
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                msg = "lng=" + longitude + "  lat=" + latitude;
            }
            String address = timeLineLocation.formatAddress();
            if (address != null) {
                msg = address;
            }
            TimeLineModel timeLineModel = new TimeLineModel(msg,
                    UTime.nowDateTime(),
                    OrderStatus.COMPLETED);
            mDataList.add(0, timeLineModel);

            timeLineModel.save();
            mTimeLineAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
        });
    }

    private void setDataListItems() {
        mDataList = LitePal.findAll(TimeLineModel.class);
        if (mDataList != null && !mDataList.isEmpty()) {
            Collections.sort(mDataList);
        }
    }

    public void onYearMonthDayPicker(View view) {
        final DatePicker picker = new DatePicker(this);

        LocalDateTime today = LocalDateTime.now();
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 2));
        picker.setRangeEnd(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
//        picker.setRangeStart(2016, 8, 29);
        picker.setSelectedItem(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        picker.setResetWhileWheel(false);
        picker.setHalfScreen(true);
//        picker.setTextSizeAutoFit(true);
        picker.setTextSize(ConvertUtils.toPx(this, 10));
        picker.setTitleTextSize(ConvertUtils.toPx(this, 10));
        picker.setOnDatePickListener((DatePicker.OnYearMonthDayPickListener) (year, month, day) -> {
            Month month1 = Month.of(Integer.valueOf(month));
            LocalDateTime localDateTime = LocalDateTime.of(Integer.valueOf(year), month1, Integer.valueOf(day), 0, 0);
//            view.setTitle(UTime.formatDate(localDateTime));
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });

        picker.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menu
        switch (item.getItemId()) {
            //When home is clicked
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (mOrientation != null)
            savedInstanceState.putSerializable(MainActivity.EXTRA_ORIENTATION, mOrientation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MainActivity.EXTRA_ORIENTATION)) {
                mOrientation = (Orientation) savedInstanceState.getSerializable(MainActivity.EXTRA_ORIENTATION);
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
