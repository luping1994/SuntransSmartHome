package net.suntrans.suntranssmarthome.homepage.device.switchs;

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
        public String id;
        public String name;
        public String number;
        public String status;
        public String updated_at;

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
