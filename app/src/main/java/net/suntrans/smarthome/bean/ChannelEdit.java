package net.suntrans.smarthome.bean;

/**
 * Created by Looney on 2017/6/16.
 */

public class ChannelEdit {
    public String status;
    public Result result;
    public static class Result{
        public Row row;
    }

    public static class Row{
        public String id;
        public String name;
        public String img_id;
        public String img;
    }
}
