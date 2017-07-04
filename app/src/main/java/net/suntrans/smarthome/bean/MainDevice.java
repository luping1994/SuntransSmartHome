package net.suntrans.smarthome.bean;

/**
 * Created by Looney on 2017/5/23.
 * 主页上 的设备Model,相当于通道的抽象 相对于 DeviceInfoResult.DeviceInfo则是 "我的"->"设备管理"->device为实体控制器设备 例如 第六感10通道等
 */

public class MainDevice {
    private String name;
    private boolean isChecked;

    public MainDevice(){
        this.isChecked = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
