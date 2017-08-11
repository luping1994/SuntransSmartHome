package net.suntrans.smarthome.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Looney on 2017/7/13.
 */

public class AmmeterHisEneity {
    @SerializedName("status")
    public int code;
    @SerializedName("result")
    public List<HisItem> lists;
    public String unit;


    public static class HisItem{
        public String data;
        public String created_at;
    }
}
