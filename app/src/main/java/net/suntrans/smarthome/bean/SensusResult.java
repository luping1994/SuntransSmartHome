package net.suntrans.smarthome.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import static net.suntrans.smarthome.R.id.smoke;

/**
 * Created by Looney on 2017/4/24.
 */
public class SensusResult {
    public String status;
    public Result result;

    public static class Result {
        public Sensus row;
    }

    public static class Sensus extends BaseObservable implements Parcelable {
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

        public String pm25Eva;
        public String pm1Eva;
        public String pm10Eva;
        public String wenduEva;
        public String shiduEva;
        public String guanzhaoEva;
        public String zEva;
        public String yanwuEva;
        public String jiaquanEva;
        public String daqiYaEva;
        public String xEva;
        public String yEva;

        public int wenduPro;
        public int shiduPro;
        public int daqiyaPro;
        public int guanzhaoPro;
        public int yanwuPro;
        public int jiaquanPro;


        public int pm1Pro;
        public int pm25Pro;
        public int pm10Pro;

        public void setEva() {
            if (pm25 != null) {

                float pm1F = 0;
                try {
                    pm1F = Float.valueOf(pm25);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (pm1F <= 35) {
                    this.pm25Eva = "优";
                    pm25Pro = (int) (pm1F / 35 / 6 * 100);
                } else if (pm1F <= 75) {
                    this.pm25Eva = "良";
                    pm25Pro = (int) ((pm1F - 35) / 240 * 100 + 100 / 6);
                } else if (pm1F <= 115) {
                    this.pm25Eva = "轻度污染";
                    pm25Pro = (int) ((pm1F - 75) / 240 * 100 + 200 / 6);
                } else if (pm1F <= 150) {
                    this.pm25Eva = "中度污染";
                    pm25Pro = (int) ((pm1F - 115) / 35 / 6 * 100 + 300 / 6);
                } else if (pm1F <= 250) {
                    this.pm25Eva = "重度污染";
                    pm25Pro = (int) ((pm1F - 150) / 6 + 400 / 6);

                } else {
                    this.pm25Eva = "严重污染";
                    pm1Pro = 90;

                }
            }

            if (pm1 != null) {
                float pm1F = 0.0f;
                try {
                    pm1F = Float.valueOf(pm1);
                } catch (NumberFormatException e) {

                }
                if (pm1F <= 35) {
                    this.pm1Eva = "优";
                    pm1Pro = (int) (pm1F / 35 / 6 * 100);

                } else if (pm1F <= 75) {
                    pm1Pro = (int) ((pm1F - 35) / 240 * 100 + 100 / 6);

                    this.pm1Eva = "良";
                } else if (pm1F <= 115) {
                    pm1Pro = (int) ((pm1F - 75) / 240 * 100 + 200 / 6);

                    this.pm1Eva = "轻度污染";
                } else if (pm1F <= 150) {
                    pm1Pro = (int) ((pm1F - 115) / 35 / 6 * 100 + 300 / 6);

                    this.pm1Eva = "中度污染";
                } else if (pm1F <= 250) {
                    pm1Pro = (int) ((pm1F - 150) / 6 + 400 / 6);

                    this.pm1Eva = "重度污染";
                } else {
                    pm1Pro = 90;

                    this.pm1Eva = "严重污染";
                }
            }

            if (pm10 != null) {
                float pm10F = 0;
                try {
                    pm10F = Float.valueOf(pm10);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (pm10F <= 50) {
                    this.pm10Eva = "优";
                    pm10Pro = (int) (pm10F / 50 * 100 / 6);

                } else if (pm10F <= 150) {
                    this.pm10Eva = "良";
                    pm10Pro = (int) ((pm10F - 50) / 6 + 100 / 6);

                } else if (pm10F <= 250) {
                    this.pm10Eva = "轻度污染";
                    pm10Pro = (int) ((pm10F - 150) / 6 + 200 / 6);

                } else if (pm10F <= 350) {
                    this.pm10Eva = "中度污染";
                    pm10Pro = (int) ((pm10F - 250) / 6 + 300 / 6);

                } else if (pm10F <= 420) {
                    this.pm10Eva = "重度污染";
                    pm10Pro = (int) ((pm10F - 350) / 420 + 400 / 6);

                } else {
                    this.pm10Eva = "严重污染";
                    pm10Pro = 90;

                }
            }

            if (yanwu != null) {
                float aFloat = 0;
                try {
                    aFloat = Float.valueOf(yanwu);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (aFloat < 750) {
                    yanwuEva = "清洁";
                    yanwuPro = (int) (aFloat / 750 * 100 / 6);
                } else {
                    yanwuEva = "污染";
                    yanwuPro = (int) (100 / 6 + (aFloat - 750) * 500 / 9250 / 6);

                }
            }
            if (jiaquan != null) {
                float jiaquanF = 0;
                try {
                    jiaquanF = Float.valueOf(jiaquan);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (jiaquanF < 0.1) {
                    jiaquanPro = (int) (jiaquanF / 0.1 * 100 / 6);

                    this.jiaquanEva = "清洁";
                } else {
                    this.jiaquanEva = "超标";
                    jiaquanPro = (int) (100 / 6 + (jiaquanF - 0.1) * 500 / 6);
                    if (jiaquanPro >= 80)
                        jiaquanPro = 80;
                }
            }

            if (wendu != null) {
                float wenduF = 0;
                try {
                    wenduF = Float.valueOf(wendu);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (wenduF <= 10) {
                    wenduEva = "极寒";
                    wenduPro = 50 / 6;

                } else if (wenduF <= 15) {
                    wenduEva = "寒冷";
                    wenduPro = (int) ((wenduF - 10) / 5 * 100 / 6 + 100 / 6);

                } else if (wenduF <= 20) {
                    wenduPro = (int) ((wenduF - 15) / 5 * 100 / 6 + 200 / 6);
                    wenduEva = "凉爽";
                } else if (wenduF <= 28) {
                    wenduEva = "舒适";
                    wenduPro = (int) ((wenduF - 20) / 8 * 100 / 6 + 300 / 6);

                } else if (wenduF <= 34) {
                    wenduEva = "闷热";
                    wenduPro = (int) ((wenduF - 28) / 6 * 100 / 6 + 400 / 6);

                } else {
                    wenduEva = "极热";
                    wenduPro = 550 / 6;

                }
            }

            if (shidu != null) {
                float shiduF = Float.valueOf(shidu);
                if (shiduF <= 40) {
                    shiduEva = "干燥";
                    shiduPro = (int) (shiduF / 40.0 * 100 / 3.0);
                } else if (shiduF <= 70) {
                    shiduEva = "舒适";
                    shiduPro = (int) ((shiduF - 40) / 30.0 * 100 / 3.0 + 100 / 3.0);

                } else {
                    shiduEva = "潮湿";
                    shiduPro = (int) ((shiduF - 70) / 30.0 * 100 / 3.0 + 200 / 3.0);

                }
            }
            if (guangzhao != null) {
                float guangxianqdF = 0;
                try {
                    guangxianqdF = Float.valueOf(guangzhao);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (guangxianqdF == 0) {
                    guanzhaoEva = "极弱";
                    guanzhaoPro = 10;
                } else if (guangxianqdF == 1) {
                    guanzhaoEva = "适中";
                    guanzhaoPro = 30;
                } else if (guangxianqdF == 2) {
                    guanzhaoEva = "强";
                    guanzhaoPro = 50;

                } else if (guangxianqdF == 3) {
                    guanzhaoEva = "很强";
                    guanzhaoPro = 70;
                } else {
                    guanzhaoPro = 90;
                    guanzhaoEva = "极强";
                }
            }

            if (z_zhou != null) {
                float qingxie = 0;
                try {
                    qingxie = Float.valueOf(z_zhou);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (qingxie > 10) {
                    zEva = "倾斜";
                } else {
                    zEva = "正常";
                }
            }
            if (x_zhou != null) {
                float qingxie = 0;
                try {
                    qingxie = Float.valueOf(x_zhou);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (qingxie > 10) {
                    xEva = "倾斜";
                } else {
                    xEva = "正常";
                }
            }
            if (y_zhou != null) {
                float qingxie = 0;
                try {
                    qingxie = Float.valueOf(y_zhou);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (qingxie > 10) {
                    yEva = "倾斜";
                } else {
                    yEva = "正常";
                }
            }

            if (daqiya != null) {
                float daqiyaF = Float.valueOf(daqiya);
                if (daqiyaF >= 110) {
                    daqiYaEva = "气压高";
                    daqiyaPro = 80;
                } else if (daqiyaF <= 90) {
                    daqiYaEva = "气压低";
                    daqiyaPro = 20;
                } else {
                    daqiYaEva = "正常";
                    daqiyaPro = 50;
                }
            }


        }


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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.created_at);
            dest.writeString(this.dev_id);
            dest.writeString(this.pm1);
            dest.writeString(this.pm10);
            dest.writeString(this.pm25);
            dest.writeString(this.jiaquan);
            dest.writeString(this.yanwu);
            dest.writeString(this.wendu);
            dest.writeString(this.shidu);
            dest.writeString(this.renyuan);
            dest.writeString(this.x_zhou);
            dest.writeString(this.y_zhou);
            dest.writeString(this.z_zhou);
            dest.writeString(this.zhendong);
            dest.writeString(this.guangzhao);
            dest.writeString(this.daqiya);
        }

        public Sensus() {
        }

        protected Sensus(Parcel in) {
            this.id = in.readString();
            this.created_at = in.readString();
            this.dev_id = in.readString();
            this.pm1 = in.readString();
            this.pm10 = in.readString();
            this.pm25 = in.readString();
            this.jiaquan = in.readString();
            this.yanwu = in.readString();
            this.wendu = in.readString();
            this.shidu = in.readString();
            this.renyuan = in.readString();
            this.x_zhou = in.readString();
            this.y_zhou = in.readString();
            this.z_zhou = in.readString();
            this.zhendong = in.readString();
            this.guangzhao = in.readString();
            this.daqiya = in.readString();
        }

        public static final Parcelable.Creator<Sensus> CREATOR = new Parcelable.Creator<Sensus>() {
            @Override
            public Sensus createFromParcel(Parcel source) {
                return new Sensus(source);
            }

            @Override
            public Sensus[] newArray(int size) {
                return new Sensus[size];
            }
        };
    }
}
