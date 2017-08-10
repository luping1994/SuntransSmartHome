package net.suntrans.smarthome.bean;

/**
 * Created by Administrator on 2017/8/10.
 */

public class XenonData {

    /**
     * code : 200
     * device : 4700
     * action : rdata
     * result : {"status":0,"V_value":229.59,"I_value":0.02,"P_value":1.69,"E_value":8.95,"PR_value":0.36,"VO_value":25.49,"IO_value":0,"PO_value":0,"T_value":1.34,"L_value":5,"addr":"000002"}
     */

    private int code;
    private String device;
    private String action;
    private ResultBean result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * status : 0
         * V_value : 229.59
         * I_value : 0.02
         * P_value : 1.69
         * E_value : 8.95
         * PR_value : 0.36
         * VO_value : 25.49
         * IO_value : 0
         * PO_value : 0
         * T_value : 1.34
         * L_value : 5
         * addr : 000002
         */

        private int status;
        private double V_value;
        private double I_value;
        private double P_value;
        private double E_value;
        private double PR_value;
        private double VO_value;
        private int IO_value;
        private int PO_value;
        private double T_value;
        private int L_value;
        private String addr;

        @Override
        public String toString() {
            return "ResultBean{" +
                    "status=" + status +
                    ", V_value=" + V_value +
                    ", I_value=" + I_value +
                    ", P_value=" + P_value +
                    ", E_value=" + E_value +
                    ", PR_value=" + PR_value +
                    ", VO_value=" + VO_value +
                    ", IO_value=" + IO_value +
                    ", PO_value=" + PO_value +
                    ", T_value=" + T_value +
                    ", L_value=" + L_value +
                    ", addr='" + addr + '\'' +
                    '}';
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public double getV_value() {
            return V_value;
        }

        public void setV_value(double V_value) {
            this.V_value = V_value;
        }

        public double getI_value() {
            return I_value;
        }

        public void setI_value(double I_value) {
            this.I_value = I_value;
        }

        public double getP_value() {
            return P_value;
        }

        public void setP_value(double P_value) {
            this.P_value = P_value;
        }

        public double getE_value() {
            return E_value;
        }

        public void setE_value(double E_value) {
            this.E_value = E_value;
        }

        public double getPR_value() {
            return PR_value;
        }

        public void setPR_value(double PR_value) {
            this.PR_value = PR_value;
        }

        public double getVO_value() {
            return VO_value;
        }

        public void setVO_value(double VO_value) {
            this.VO_value = VO_value;
        }

        public int getIO_value() {
            return IO_value;
        }

        public void setIO_value(int IO_value) {
            this.IO_value = IO_value;
        }

        public int getPO_value() {
            return PO_value;
        }

        public void setPO_value(int PO_value) {
            this.PO_value = PO_value;
        }

        public double getT_value() {
            return T_value;
        }

        public void setT_value(double T_value) {
            this.T_value = T_value;
        }

        public int getL_value() {
            return L_value;
        }

        public void setL_value(int L_value) {
            this.L_value = L_value;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }
    }
}
