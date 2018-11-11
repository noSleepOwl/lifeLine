package com.github.vipulasri.timelineview.sample.base;

import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG = this.getClass().getName();

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @CallSuper
    @Override
    public void onStop() {
        super.onStop();
    }

    public void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void logI(String msg) {
        Log.i(this.getClass().getSimpleName(), msg);
    }
}
