package net.suntrans.smarthome.bean;

import java.util.List;

/**
 * Created by Looney on 2017/6/17.
 */

public class PatternItem2 extends Checked{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MainDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<MainDevice> devices) {
        this.devices = devices;
    }

    private List<MainDevice> devices;
}
