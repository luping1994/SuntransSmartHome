package net.suntrans.smarthome.bean;

import java.util.List;

/**
 * Created by Looney on 2017/4/24.
 * 旧版本的我的 -房间-结果
 */

public class HomeRoomResult {
    public String status;
    public Result result;

    public static class Result {
        public List<Room> rows;
    }

    public static class Room {
        public String id;
        public String user_id;
        public String name;
        public String img;
        public String timer;
        public String sort;
        public String status;
        public int res_id;
        public boolean isChecked;

        public Room() {
            this.isChecked = false;
        }
    }
}
