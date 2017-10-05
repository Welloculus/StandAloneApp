package com.transility.tracker.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.transility.tracker.R;
import com.transility.tracker.utils.UnitConverter;

/**
 * Activity to show list of devices associated with the user.
 */
public class BMIActivity extends BaseActivity implements Button.OnClickListener{

    public static final float BMI_LOW = 18.5F;
    public static final float BMI_HIGH = 24.9F;

    public static final String LOW_BMI_DESC = "Your BMI is lower then the normal range. You need to put on some weight to get into the healthy range.";
    public static final String NORMAL_BMI_DESC = "Your BMI falls under the normal range. Keep up the activity and stay fit.";
    public static final String HIGH_BMI_DESC = "Your BMI is higher then the normal range. You need to loose some weight to get into the healthy range.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bmi);
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

        initUI();
    }

    EditText editWeight;
    EditText editHeight;
    Spinner spinWeightUnit;
    Spinner spinHeightUnit;
    Button btnBMI;
    TextView textBMIResult;
    TextView textBMIDesc;
    @Override
    protected void initUI() {
        editWeight = (EditText) findViewById(R.id.edit_weight);
        editHeight = (EditText) findViewById(R.id.edit_height);
        spinWeightUnit = (Spinner) findViewById(R.id.spin_weight_unit);
        spinHeightUnit = (Spinner) findViewById(R.id.spin_height_unit);
        btnBMI = (Button) findViewById(R.id.btn_bmi);
        textBMIResult = (TextView) findViewById(R.id.text_bmi_result);
        textBMIDesc = (TextView) findViewById(R.id.text_bmi_desc);
        btnBMI.setOnClickListener(this);
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
    public void onClick(View view) {
        try {
            float weightAmount = Float.parseFloat(editWeight.getText().toString());
            float heightAmount = Float.parseFloat(editHeight.getText().toString());
            String weightUnit = spinWeightUnit.getSelectedItem().toString();
            String heightUnit = spinHeightUnit.getSelectedItem().toString();
            float weightInKg = UnitConverter.getWeightInKG(weightAmount, weightUnit);
            float heightInMeters = UnitConverter.getHeightInMeters(heightAmount, heightUnit);
            float bmi = weightInKg/(heightInMeters*heightInMeters);
            String bmiDesc;
            if(bmi<BMI_LOW){
                bmiDesc = LOW_BMI_DESC;
            }else if(bmi<BMI_HIGH){
                bmiDesc = NORMAL_BMI_DESC;
            }else {
                bmiDesc = HIGH_BMI_DESC;
            }
            String bmiString = String.format("Your BMI is %.2f",bmi);
            textBMIResult.setText(bmiString);
            textBMIDesc.setText(bmiDesc);
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            if(getCurrentFocus()!=null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }catch (Exception e){
            textBMIResult.setText(" ");
            textBMIDesc.setText(" ");
            Log.e("Welloculus","Error occured: "+e.getMessage());
        }
    }
}
