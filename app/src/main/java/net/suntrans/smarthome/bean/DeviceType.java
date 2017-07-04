package net.suntrans.smarthome.bean;

/**
 * Created by Looney on 2017/5/23.
 */

public class DeviceType {
    private String name;
    private boolean ischecked;

    public boolean ischecked() {
        return ischecked;
    }

    public void setChecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
