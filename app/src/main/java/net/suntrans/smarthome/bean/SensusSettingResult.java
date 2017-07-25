package net.suntrans.smarthome.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Looney on 2017/4/27.
 */

public class SensusSettingResult {
    private String code;
    private Param param;
    private String command;
    private String device;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;

    @Override
    public String toString() {
        return "SensusSettingResult{" +
                "code='" + code + '\'' +
                ", param=" + param.toString() +
                ", command='" + command + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public static class Param extends BaseObservable {


        private double v1 = 0;

        private double v2 = 0;

        private double v3 = 0;
        private double v4 = 0;
        private double v5 = 0;

        @Bindable
        public double getV1() {
            return v1;
        }

        public void setV1(double v1) {
            this.v1 = v1;
        }

        @Bindable
        public double getV2() {
            return v2;
        }

        public void setV2(double v2) {
            this.v2 = v2;
        }

        @Bindable
        public double getV3() {
            return v3;
        }

        public void setV3(double v3) {
            this.v3 = v3;
        }

        @Bindable
        public double getV4() {
            return v4;
        }

        public void setV4(double v4) {
            this.v4 = v4;
        }

        @Bindable
        public double getV5() {
            return v5;
        }

        public void setV5(double v5) {
            this.v5 = v5;
        }


        public JSONObject getJsonParm(){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("v1", v1);
                jsonObject.put("v2", v2);
                jsonObject.put("v3", v3);
                jsonObject.put("v4", v4);
                jsonObject.put("v5", v5);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
        @Override
        public String toString() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("v1", v1);
                jsonObject.put("v2", v2);
                jsonObject.put("v3", v3);
                jsonObject.put("v4", v4);
                jsonObject.put("v5", v5);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonObject.toString();
        }
    }

    public static class Commmand {
        private String wendu;
        private String yanwu;
        private String zhengdong;
        private String pm25;
        private String jiaquan;
        private String renyuan;

        public void setWendu(boolean isCheck) {
            this.wendu = isCheck ? "1" : "0";
        }

        public void setYanwu(boolean isCheck) {
            this.yanwu = isCheck ? "1" : "0";

        }

        public void setZhengdong(boolean isCheck) {
            this.zhengdong = isCheck ? "1" : "0";
        }

        public void setPm25(boolean isCheck) {
            this.pm25 = isCheck ? "1" : "0";
        }

        public void setJiaquan(boolean isCheck) {
            this.jiaquan = isCheck ? "1" : "0";
        }

        public void setRenyuan(boolean isCheck) {
            this.renyuan = isCheck ? "1" : "0";
        }

        public Commmand(String s) {
            this.wendu = s.substring(0, 1);
            this.yanwu = s.substring(1, 2);
            this.zhengdong = s.substring(2, 3);
            this.pm25 = s.substring(3, 4);
            this.jiaquan = s.substring(4, 5);
            this.renyuan = s.substring(5, 6);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append(wendu)
                    .append(yanwu)
                    .append(zhengdong)
                    .append(pm25)
                    .append(jiaquan)
                    .append(renyuan)
                    .toString();
        }

        public boolean getWendu() {
            return wendu.equals("1") ? true : false;
        }

        public boolean getYanwu() {
            return yanwu.equals("1") ? true : false;
        }

        public boolean getZhengdong() {
            return zhengdong.equals("1") ? true : false;
        }

        public boolean getPm25() {
            return pm25.equals("1") ? true : false;
        }

        public boolean getJiaquan() {
            return jiaquan.equals("1") ? true : false;
        }

        public boolean getRenyuan() {
            return renyuan.equals("1") ? true : false;
        }
    }
}
