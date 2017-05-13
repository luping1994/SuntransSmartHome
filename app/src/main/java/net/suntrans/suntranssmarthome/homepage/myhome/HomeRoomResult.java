package net.suntrans.suntranssmarthome.homepage.myhome;

import android.databinding.BaseObservable;

import java.util.List;

/**
 * Created by Looney on 2017/4/24.
 */

public class HomeRoomResult {
    public String status;
    public Result result;

    public static class Result {
        public List<Room> rows;
    }

    public static class Room extends BaseObservable {
        public String id;
        public String user_id;
        public String name;
        public String img;
        public String timer;
        public String sort;
        public String status;

    }
}
