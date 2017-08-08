package net.suntrans.smarthome.bean;

import java.util.List;

/**
 * Created by Looney on 2017/6/10.
 */

public class AllDeviceResult {
    public Channel channel;
    public class Channel {
        public String status;
        public String desc;
        public String count;
        public List<ChannelInfo> result;
    }

    public class ChannelInfo {
        public String name;
        public String dev_id;
        public String channel_id;
        public String houseid;
        public String housename;
        public String img;
        public String status;
        public String img_id;
        public String addr;
        public String vtype;
        public String number;
        public String device_type;
    }
}
