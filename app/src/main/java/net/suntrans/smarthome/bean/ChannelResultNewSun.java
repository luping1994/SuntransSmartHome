package net.suntrans.smarthome.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

/**
 * Created by Looney on 2017/4/24.
 */

public class ChannelResultNewSun {
    public String status;
    public String desc;
    public List<Channel>  result;


    public static class Channel extends BaseObservable {
        private String id;
        private String name;
        private String addr;
        private String vtype;
        private String img_url;
        private String device_type;
        private String xenon_addr;
        private String number;
        private String status;
        private String updated_at;

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public String getXenon_addr() {
            return xenon_addr;
        }

        public void setXenon_addr(String xenon_addr) {
            this.xenon_addr = xenon_addr;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getVtype() {
            return vtype;
        }

        public void setVtype(String vtype) {
            this.vtype = vtype;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        @Override
        public String toString() {
            return "Channel{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", number='" + number + '\'' +
                    ", status='" + status + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    '}';
        }

        public String getId() {
            return id;
        }

        @Bindable
        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }

        @Bindable
        public String getStatus() {
            return status;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
