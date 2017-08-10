package net.suntrans.smarthome.bean;

import java.util.List;

/**
 * Created by Looney on 2017/7/23.
 */

public class AmeterEntity {

    /**
     * status : 1
     * desc : 电表列表
     * result : [{"id":"15","updated_at":"2017-08-10 17:53:08","user_id":"1","title":"公司总能耗","dev_id":"64","vtype":"3","sno":"222160041660","multiply":"80","area_id":"1","status":"1","client_id":"7f000001659e00000001"},{"id":"1","updated_at":"2017-08-10 19:55:31","user_id":"1","title":"办公一区","dev_id":"68","vtype":"1","sno":"171170040084","multiply":"1","area_id":"1","status":"1","client_id":"7f000001659a00000008"},{"id":"19","updated_at":"2017-08-10 17:53:08","user_id":"1","title":"会议室","dev_id":"64","vtype":"1","sno":"171170040030","multiply":"1","area_id":"1","status":"1","client_id":"7f000001659e00000001"},{"id":"18","updated_at":"2017-08-10 17:53:08","user_id":"1","title":"展厅右墙","dev_id":"63","vtype":"1","sno":"171170040024","multiply":"1","area_id":"1","status":"1","client_id":"7f000001659b00000002"},{"id":"17","updated_at":"2017-08-10 17:53:08","user_id":"1","title":"展厅展板","dev_id":"62","vtype":"1","sno":"171170040025","multiply":"1","area_id":"1","status":"1","client_id":"7f000001659e00000002"},{"id":"14","updated_at":"2017-08-10 19:41:03","user_id":"1","title":"环境实验室","dev_id":"77","vtype":"1","sno":"171170039908","multiply":"1","area_id":"1","status":"1","client_id":"7f00000165a000000006"},{"id":"13","updated_at":"2017-08-10 17:54:04","user_id":"1","title":"电气实验室","dev_id":"75","vtype":"1","sno":"171170039903","multiply":"1","area_id":"3","status":"1","client_id":"7f000001659d00000002"},{"id":"12","updated_at":"2017-08-10 17:54:07","user_id":"1","title":"管理二区","dev_id":"79","vtype":"1","sno":"171170040089","multiply":"1","area_id":"2","status":"1","client_id":"7f00000165a100000003"},{"id":"11","updated_at":"2017-08-10 17:54:04","user_id":"1","title":"员工二区","dev_id":"78","vtype":"1","sno":"171170040087","multiply":"1","area_id":"2","status":"1","client_id":"7f00000165a100000002"},{"id":"10","updated_at":"2017-08-10 19:57:44","user_id":"1","title":"生产一区","dev_id":"81","vtype":"1","sno":"171170040083","multiply":"1","area_id":"2","status":"1","client_id":"7f000001659e00000004"},{"id":"9","updated_at":"2017-08-10 17:54:04","user_id":"1","title":"办公三区","dev_id":"72","vtype":"1","sno":"171170039904","multiply":"1","area_id":"2","status":"1","client_id":"7f000001659b00000005"},{"id":"8","updated_at":"2017-08-10 17:54:04","user_id":"1","title":"办公二区","dev_id":"71","vtype":"1","sno":"171170039901","multiply":"1","area_id":"2","status":"1","client_id":"7f00000165a000000001"},{"id":"7","updated_at":"2017-08-10 18:30:06","user_id":"1","title":"生产二区","dev_id":"74","vtype":"1","sno":"171170039907","multiply":"1","area_id":"2","status":"1","client_id":"7f000001659f00000003"},{"id":"6","updated_at":"2017-08-10 18:37:10","user_id":"1","title":"生产一区","dev_id":"73","vtype":"1","sno":"171170039902","multiply":"1","area_id":"2","status":"1","client_id":"7f000001659a00000006"},{"id":"5","updated_at":"2017-08-10 17:54:02","user_id":"1","title":"管理一区","dev_id":"76","vtype":"1","sno":"171170040090","multiply":"1","area_id":"3","status":"1","client_id":"7f000001659b00000003"},{"id":"4","updated_at":"2017-08-10 19:15:42","user_id":"1","title":"会议室","dev_id":"67","vtype":"1","sno":"171170040088","multiply":"1","area_id":"1","status":"1","client_id":"7f00000165a000000005"},{"id":"3","updated_at":"2017-08-10 17:53:08","user_id":"1","title":"展厅电器","dev_id":"66","vtype":"1","sno":"171170040085","multiply":"1","area_id":"2","status":"1","client_id":"7f000001659a00000002"},{"id":"2","updated_at":"2017-08-10 17:53:08","user_id":"1","title":"机房","dev_id":"65","vtype":"1","sno":"171170040082","multiply":"1","area_id":"4","status":"1","client_id":"7f00000165a100000001"},{"id":"20","updated_at":"2017-08-10 19:47:06","user_id":"1","title":"产品库","dev_id":"95","vtype":"1","sno":"171170039906","multiply":"1","area_id":"3","status":"1","client_id":"7f000001659d00000004"}]
     */

    private int status;
    private String desc;
    private List<ResultBean> result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : 15
         * updated_at : 2017-08-10 17:53:08
         * user_id : 1
         * title : 公司总能耗
         * dev_id : 64
         * vtype : 3
         * sno : 222160041660
         * multiply : 80
         * area_id : 1
         * status : 1
         * client_id : 7f000001659e00000001
         */

        private String id;
        private String updated_at;
        private String user_id;
        private String title;
        private String dev_id;
        private String vtype;
        private String sno;
        private String multiply;
        private String area_id;
        private String status;
        private String client_id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDev_id() {
            return dev_id;
        }

        public void setDev_id(String dev_id) {
            this.dev_id = dev_id;
        }

        public String getVtype() {
            return vtype;
        }

        public void setVtype(String vtype) {
            this.vtype = vtype;
        }

        public String getSno() {
            return sno;
        }

        public void setSno(String sno) {
            this.sno = sno;
        }

        public String getMultiply() {
            return multiply;
        }

        public void setMultiply(String multiply) {
            this.multiply = multiply;
        }

        public String getArea_id() {
            return area_id;
        }

        public void setArea_id(String area_id) {
            this.area_id = area_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getClient_id() {
            return client_id;
        }

        public void setClient_id(String client_id) {
            this.client_id = client_id;
        }
    }
}
