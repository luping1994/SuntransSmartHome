package net.suntrans.smarthome.bean;

/**
 * Created by Looney on 2017/6/17.
 */

public class SpeechBean {
    private String type ;
    private String msg;
    private int id=-1;
    public SpeechBean(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
