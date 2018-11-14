package icar.a5i4s.com.smartbox.module;

import icar.a5i4s.com.smartbox.helper.MD5;

/**
 * Created by light on 2016/9/3.
 */
public class Order {
    private int id;
    private String orderId;
    private String appName;
    private String transId;
    private String resultCode;
    private String resultMsg;
    private String transData;
    private String sign;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Order(int id, String orderId, String appName, String transId, String resultCode, String resultMsg, String transData) {
        this.id = id;
        this.orderId = orderId;
        this.appName = appName;
        this.transId = transId;
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.transData = transData;
        this.sign = MD5.getMD5(orderId + appName + transId + resultCode + resultMsg + transData);
    }

    public Order(String orderId, String appName, String transId, String resultCode, String resultMsg, String transData) {
        this.orderId = orderId;
        this.appName = appName;
        this.transId = transId;
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.transData = transData;
        this.sign = MD5.getMD5(orderId + appName + transId + resultCode + resultMsg + transData);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getTransData() {
        return transData;
    }

    public void setTransData(String transData) {
        this.transData = transData;
    }
}
