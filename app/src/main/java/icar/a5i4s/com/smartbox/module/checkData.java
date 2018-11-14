package icar.a5i4s.com.smartbox.module;

/**
 * Created by light on 2016/10/25.
 */

public class checkData {
    private int checkStatus;
    private int certificateCheckResult;
    private String comment;
    private String currUser;
    private String checkId;
    private String certificateId;
    private String keyQuantity;
    private String keyCheckResult;


    public checkData(int certificateCheckResult, String comment, String currUser, String checkId, String certificateId, String keyQuantity, String keyCheckResult) {
        this.certificateCheckResult = certificateCheckResult;
        this.comment = comment;
        this.currUser = currUser;
        this.checkId = checkId;
        this.certificateId = certificateId;
        this.keyQuantity = keyQuantity;
        this.keyCheckResult = keyCheckResult;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getKeyQuantity() {
        return keyQuantity;
    }

    public void setKeyQuantity(String keyQuantity) {
        this.keyQuantity = keyQuantity;
    }

    public String getKeyCheckResult() {
        return keyCheckResult;
    }

    public void setKeyCheckResult(String keyCheckResult) {
        this.keyCheckResult = keyCheckResult;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public int getCertificateCheckResult() {
        return certificateCheckResult;
    }

    public void setCertificateCheckResult(int certificateCheckResult) {
        this.certificateCheckResult = certificateCheckResult;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCurrUser() {
        return currUser;
    }

    public void setCurrUser(String currUser) {
        this.currUser = currUser;
    }

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }
}
