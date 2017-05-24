package net.suntrans.suntranssmarthome.model;

import java.util.List;

/**
 * Created by Looney on 2017/5/23.
 */

public class PatternItem {
    private boolean isCheck = false;
    private String name;
    private List<MainDevice> devices;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MainDevice> getDevices() {
        return devices;
    }



    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }


    public void setDevices(List<MainDevice> devices) {
        this.devices = devices;
    }
}
