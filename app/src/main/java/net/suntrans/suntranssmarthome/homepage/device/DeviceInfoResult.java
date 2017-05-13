package net.suntrans.suntranssmarthome.homepage.device;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

/**
 * Created by Looney on 2017/4/21.
 */

public class DeviceInfoResult  extends BaseObservable {
    public String status;
    public Result result;

    public static class Result{
        public List<DeviceInfo> rows;
    }

    public static class DeviceInfo{
        public String id;
        public String name;
        public String title;
        public String vtype;
        public String is_online;
        public String img;

        @Override
        public String toString() {
            return "DeviceInfo{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", title='" + title + '\'' +
                    ", vtype='" + vtype + '\'' +
                    ", is_online='" + is_online + '\'' +
                    ", img='" + img + '\'' +
                    '}';
        }
    }

    @Bindable
    public boolean isEmpty(){
        return result.rows.size()==0?true:false;
    }
}
