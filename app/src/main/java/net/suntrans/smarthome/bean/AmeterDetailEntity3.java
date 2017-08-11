package net.suntrans.smarthome.bean;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by Looney on 2017/7/23.
 */

public class AmeterDetailEntity3 {

    @SerializedName("status")
    public int code;

    @SerializedName("result")
    public Map<String, String> data;


    public static class Ameter3Detail {
        public String id;
        public String status;
        public String updated_at;

        public String user_id;
        public String dev_id;
        public String vtype;
        public String sno;


        public String EletricityValue;


        public String VolA;
        public String VolB;
        public String VolC;
        public String IA;
        public String IB;
        public String IC;
        public String ActivePower;
        public String ActivePowerA;
        public String ActivePowerB;
        public String ActivePowerC;
        public String ReactivePower;
        public String ReactivePowerA;
        public String ReactivePowerB;
        public String ReactivePowerC;
        public String PowerFactor;
        public String PowerFactorA;
        public String PowerFactorB;
        public String PowerFactorC;
        public String EletricitySharp;
        public String EletricityPeak;
        public String EletricityFlat;
        public String EletricityValley;


    }
}
