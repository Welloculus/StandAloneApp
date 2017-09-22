package com.transility.welloculus.ui;

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

import com.transility.welloculus.R;
import com.transility.welloculus.utils.UnitConverter;

/**
 * Activity to show list of devices associated with the user.
 */
public class IdealWeightActivity extends BaseActivity implements Button.OnClickListener{

    public static final float BMI_LOW = 18.5F;
    public static final float BMI_HIGH = 24.9F;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ideal_weight);
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

    EditText editHeight;
    Spinner spinHeightUnit;
    Button btnBMI;
    TextView textBMIResult;
    TextView textBMIDesc;
    @Override
    protected void initUI() {
        editHeight = (EditText) findViewById(R.id.edit_height);
        spinHeightUnit = (Spinner) findViewById(R.id.spin_height_unit);
        btnBMI = (Button) findViewById(R.id.btn_ideal_weight);
        textBMIResult = (TextView) findViewById(R.id.text_result);
        textBMIDesc = (TextView) findViewById(R.id.text_desc);
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
            float heightAmount = Float.parseFloat(editHeight.getText().toString());
            String heightUnit = spinHeightUnit.getSelectedItem().toString();
            float heightInMeters = UnitConverter.getHeightInMeters(heightAmount, heightUnit);
            float minWeightInKg = BMI_LOW*heightInMeters*heightInMeters;
            float maxWeightInKg = BMI_HIGH*heightInMeters*heightInMeters;
            String bmiDesc = "As per your height, your weight should be in below range.";

            String bmiString = String.format("%.2f To %.2f Kg.",minWeightInKg, maxWeightInKg);
            textBMIResult.setText(bmiString);
            textBMIDesc.setText(bmiDesc);
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            if(getCurrentFocus()!=null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }catch (Exception e){
            textBMIResult.setText(" ");
            textBMIDesc.setText(" ");
            Log.e("Welloculus","Error occurred: "+e.getMessage());
        }
    }
}