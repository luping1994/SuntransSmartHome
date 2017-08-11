package net.suntrans.smarthome.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Looney on 2017/7/13.
 */

public class Ammeter3HisEneity {
    @SerializedName("status")
    public int code;

    public String unit;

    @SerializedName("result")
    public List<Map<String,String>> lists;


}
