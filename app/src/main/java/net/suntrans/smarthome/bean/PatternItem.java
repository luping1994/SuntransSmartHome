package net.suntrans.smarthome.bean;

import java.util.List;

/**
 * Created by Looney on 2017/5/23.
 */

public class PatternItem {
    public boolean isCheck = false;
    public String name;
    public List<SceneChannelResult.SceneChannel> channel;
    public HomeRoomResult.Room house;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
