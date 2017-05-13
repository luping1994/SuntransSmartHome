package net.suntrans.suntranssmarthome.homepage.device.sensus;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by Looney on 2017/4/24.
 */

public class SensusResult {
    public String status;
    public Result result;

    public static class Result {
        public Sensus row;
    }

    public static class Sensus extends BaseObservable {
        private String id;
        @Bindable
        private String created_at;

        private String dev_id;

        @Bindable
        private String pm1;

        @Bindable
        private String pm10;

        @Bindable
        private String pm25;

        @Bindable
        private String jiaquan;

        @Bindable
        private String yanwu;

        @Bindable
        private String wendu;

        @Bindable
        private String shidu;

        @Bindable
        private String renyuan;

        @Bindable
        public String x_zhou;

        @Bindable
        private String y_zhou;

        @Bindable
        private String z_zhou;

        @Bindable
        private String zhendong;

        @Bindable
        private String guangzhao;

        @Bindable
        private String daqiya;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getDev_id() {
            return dev_id;
        }

        public void setDev_id(String dev_id) {
            this.dev_id = dev_id;
        }

        public String getPm1() {
            return pm1;
        }

        public void setPm1(String pm1) {
            this.pm1 = pm1;
        }

        public String getPm10() {
            return pm10;
        }

        public void setPm10(String pm10) {
            this.pm10 = pm10;
        }

        public String getPm25() {
            return pm25;
        }

        public void setPm25(String pm25) {
            this.pm25 = pm25;
        }

        public String getJiaquan() {
            return jiaquan;
        }

        public void setJiaquan(String jiaquan) {
            this.jiaquan = jiaquan;
        }

        public String getYanwu() {
            return yanwu;
        }

        public void setYanwu(String yanwu) {
            this.yanwu = yanwu;
        }

        public String getWendu() {
            return wendu;
        }

        public void setWendu(String wendu) {
            this.wendu = wendu;
        }

        public String getShidu() {
            return shidu;
        }

        public void setShidu(String shidu) {
            this.shidu = shidu;
        }

        public String getRenyuan() {
            return renyuan;
        }

        public void setRenyuan(String renyuan) {
            this.renyuan = renyuan;
        }

        public String getX_zhou() {
            return x_zhou;
        }

        public void setX_zhou(String x_zhou) {
            this.x_zhou = x_zhou;
        }

        public String getY_zhou() {
            return y_zhou;
        }

        public void setY_zhou(String y_zhou) {
            this.y_zhou = y_zhou;
        }

        public String getZ_zhou() {
            return z_zhou;
        }

        public void setZ_zhou(String z_zhou) {
            this.z_zhou = z_zhou;
        }

        public String getZhendong() {
            return zhendong;
        }

        public void setZhendong(String zhendong) {
            this.zhendong = zhendong;
        }

        public String getGuangzhao() {
            return guangzhao;
        }

        public void setGuangzhao(String guangzhao) {
            this.guangzhao = guangzhao;
        }

        public String getDaqiya() {
            return daqiya;
        }

        public void setDaqiya(String daqiya) {
            this.daqiya = daqiya;
        }
    }
}
