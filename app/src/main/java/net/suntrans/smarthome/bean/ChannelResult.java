package net.suntrans.smarthome.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

/**
 * Created by Looney on 2017/4/24.
 */

public class ChannelResult {
    public String status;
    public Result result;
    public String msg;

    public static class Result {
        public List<Channel> rows;
    }

    public static class Channel extends BaseObservable {
        private String id;
        private String name;
        private String number;
        private String img;
        private String addr;

        private String status;
        private String updated_at;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getImg_id() {
            return img_id;
        }

        public void setImg_id(String img_id) {
            this.img_id = img_id;
        }

        private String img_id;
        private boolean ischeck = false;

        public boolean ischeck() {
            return ischeck;
        }

        public void setIscheck(boolean ischeck) {
            this.ischeck = ischeck;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
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
