package net.suntrans.smarthome.bean;

import java.util.List;

/**
 * Created by Looney on 2017/6/17.
 */

public class RAC {

    public SB house;
    public static class SB {
        public String status;
        public List<PatternItem> result;

    }

}
