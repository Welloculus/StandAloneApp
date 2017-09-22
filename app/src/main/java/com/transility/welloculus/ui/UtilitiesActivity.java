package com.transility.welloculus.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.transility.welloculus.R;
import com.transility.welloculus.adapter.UtilitiesListAdapter;
import com.transility.welloculus.app.receiver.UtilitySelectListener;
import com.transility.welloculus.beans.UtilitiesBean;

import java.util.ArrayList;

/**
 * Activity to show list of devices associated with the user.
 */
public class UtilitiesActivity extends BaseActivity implements UtilitySelectListener{
    protected RecyclerView mRecyclerView;
    private UtilitiesListAdapter mAdapter;
    private Context mContext;
    protected ArrayList<UtilitiesBean> utilitiesInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_utilities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            toolbar.setNavigationIcon(R.drawable.back_arrow_white);
            setSupportActionBar(toolbar);
            if(null!=getSupportActionBar()){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        mContext = this;
        initUI();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        fillUtilities();
        mAdapter.notifyDataSetChanged();
    }

    private void fillUtilities() {
        UtilitiesBean bmiUtility = new UtilitiesBean("BMI Calculator", "Calculate your BMI using your weight and height", BMIActivity.class);
        UtilitiesBean idealWeightUtility = new UtilitiesBean("Ideal Weight Calculator", "Calculate ideal weight range as per your height", IdealWeightActivity.class);
        utilitiesInfoList.add(bmiUtility);
        utilitiesInfoList.add(idealWeightUtility);
    }


    @Override
    protected void initUI() {
        mRecyclerView = (RecyclerView) findViewById(R.id.utilities_list);
        mAdapter = new UtilitiesListAdapter(utilitiesInfoList, this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onUtilitySelected(UtilitiesBean utilitiesBean) {
        Intent utilityActivity = new Intent(this, utilitiesBean.getActivityClass());
        startActivity(utilityActivity);
    }
}
