package com.transility.welloculus.beans;

import android.app.Activity;

import java.util.List;

/**
 * Class for device info parameters.
 *
 * @author arpit.garg
 */
public class UtilitiesBean {

    private String utilitiesName;
    private String utilitiesDesc;
    private Class<? extends Activity> activityClass;

    public UtilitiesBean(String utilitiesName, String utilitiesDesc, Class<? extends Activity> activityClass){
        this.utilitiesName = utilitiesName;
        this.utilitiesDesc = utilitiesDesc;
        this.activityClass = activityClass;
    }
    public String getUtilitiesName() {
        return utilitiesName;
    }

    public void setUtilitiesName(String utilitiesName) {
        this.utilitiesName = utilitiesName;
    }

    public String getUtilitiesDesc() {
        return utilitiesDesc;
    }

    public void setUtilitiesDesc(String utilitiesDesc) {
        this.utilitiesDesc = utilitiesDesc;
    }

    public Class<? extends Activity> getActivityClass() {
        return activityClass;
    }
}