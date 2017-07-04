package net.suntrans.smarthome.bean;

import java.util.List;

/**
 * Created by Looney on 2017/6/16.
 */

public class SceneChannelResult {
    public String status;
    public List<SceneChannel> result;

    public static class SceneChannel extends Checked{
        public String id;
        public String dev_id;
        public String number;
        public String name;
        public String img_id;
        public String group_id;
        public String status;
        public String open;
        public String close;
        public String scene_id;
        public String channel_id;
        public String img;

    }
}
