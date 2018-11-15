package com.github.vipulasri.timelineview.sample;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.vipulasri.timelineview.sample.Unit.Util;
import com.github.vipulasri.timelineview.sample.base.BaseActivity;
import com.github.vipulasri.timelineview.sample.model.DateseModel;
import com.github.vipulasri.timelineview.sample.model.Orientation;
import com.github.vipulasri.timelineview.sample.model.TimeLineModel;

import org.litepal.LitePal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public TextView title;
    //当前列表展示的时间
    public LocalDateTime currentListTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.show_time_title);
        setSupportActionBar(toolbar);

        timeLineLocation = new TimeLineLocation(this);

        mOrientation = Orientation.VERTICAL;
        mWithLinePadding = false;
        setTitle(R.string.tile);
        mRecyclerView = findViewById(R.id.recyclerView);
        flaction = findViewById(R.id.floatingActionButton);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);
        initView();
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
        title.setText(Util.nowDate());
        title.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        toolbar.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        title.setOnClickListener(this::onYearMonthDayPicker);
    }

    @SuppressLint("MissingPermission")
    private void bindFloatButton() {
        DateseModel dataModel = new DateseModel();

        flaction.setOnClickListener(v -> {
            if (Util.isFastClick()) {
                return;
            }
            Location location = timeLineLocation.getLocation();
            String msg = "无法获取地理位置";
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                msg = "lng=" + longitude + "  lat=" + latitude;
                dataModel.setLat(latitude);
                dataModel.setLng(longitude);
            }
            String address = timeLineLocation.formatAddress();
            if (address != null) {
                msg = address;
            }
//            timeLineModel.semMessage(msg);
            dataModel.setMessage(msg);
            dataModel.setCreateDate(Util.nowDate());
            dataModel.setCreateTime(Util.nowTime());
//            timeLineModel.setStatus(OrderStatus.ACTIVE.ordinal());
            dataModel.assignBaseObjId(0);
            dataModel.save();
            mDataList.clear();
            mDataList.addAll(findLineByDate(Util.nowDate()));

            mTimeLineAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
            title.setText(Util.nowDate());
        });
    }

    private void setDataListItems() {

        List<DateseModel> dateseModel = findModelByDate(Util.nowDate());
        mDataList = coverDataToLine(dateseModel);

        if (mDataList != null && !mDataList.isEmpty()) {
            Collections.sort(mDataList);
        }
    }


    private List<DateseModel> findModelByDate(String createDate) {
        return LitePal.where(" createDate=?", createDate)
                .order("createTime desc")
                .find(DateseModel.class);
    }

    private List<TimeLineModel> coverDataToLine(List<DateseModel> models) {
        if (models == null || models.isEmpty()) {
            return null;
        }
        return models.stream().map(TimeLineModel::new).collect(Collectors.toList());
    }

    private List<TimeLineModel> findLineByDate(String date) {
        List<DateseModel> models = findModelByDate(date);
        return coverDataToLine(models);
    }

    public void onYearMonthDayPicker(View view) {
        final DatePicker picker = new DatePicker(this);

        LocalDateTime today = LocalDateTime.now();
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 2));
        picker.setRangeEnd(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        picker.setSelectedItem(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        picker.setResetWhileWheel(false);
        picker.setHalfScreen(true);
        picker.setTextSize(ConvertUtils.toPx(this, 10));
        picker.setTitleTextSize(ConvertUtils.toPx(this, 10));
        picker.setOnDatePickListener((DatePicker.OnYearMonthDayPickListener) (year, month, day) -> {
            Month month1 = Month.of(Integer.valueOf(month));
            LocalDateTime localDateTime = LocalDateTime.of(Integer.valueOf(year), month1, Integer.valueOf(day), 0, 0);
            title.setText(Util.formatDate(localDateTime));
            List<DateseModel> models = findModelByDate(Util.formatDate(localDateTime));
            mDataList.clear();
            if (models.isEmpty()) {
                mTimeLineAdapter.notifyDataSetChanged();
            } else {
                mDataList.addAll(coverDataToLine(models));
                mTimeLineAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(0);
            }

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

    
}
