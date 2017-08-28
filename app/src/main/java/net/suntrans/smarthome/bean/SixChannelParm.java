package net.suntrans.smarthome.bean;

import java.util.List;

/**
 * Created by Looney on 2017/8/28.
 */

public class SixChannelParm {

    /**
     * status : 1
     * result : {"row":[{"id":"10","dev_id":"70","V":"217.6","I":"5.36","P":"1129.74","PR":"0.982"}]}
     */

    public int status;
    public ResultBean result;

    public static class ResultBean {
        public List<RowBean> row;

        public static class RowBean {
            /**
             * id : 10
             * dev_id : 70
             * V : 217.6
             * I : 5.36
             * P : 1129.74
             * PR : 0.982
             */

            public String id;
            public String dev_id;
            public String V;
            public String I;
            public String P;
            public String PR;
        }
    }
}
