package icar.a5i4s.com.smartbox.module;

/**
 * Created by light on 2016/10/24.
 */

public class Grid {
    private String checkId;
    private String boxId;
    private String checkStatus;
    private String boxCellCode;
    private String vin;
    private String certificateId;
    private String certificateCheckResult;
    private String comment;
    private String keyQuantity;
    private String keyCheckResult;

    public Grid(String checkId,String boxId,String checkStatus,String boxCellCode,String vin,String certificateId,String certificateCheckResult, String comment, String keyQuantity, String keyCheckResult){
        this.checkId = checkId;
        this.boxId = boxId;
        this.checkStatus = checkStatus;
        this.boxCellCode = boxCellCode;
        this.vin = vin;
        this.certificateId = certificateId;
        this.certificateCheckResult = certificateCheckResult;
        this.comment = comment;
        this.keyQuantity = keyQuantity;
        this.keyCheckResult = keyCheckResult;
    }


    public String getKeyCheckResult() {
        return keyCheckResult;
    }

    public void setKeyCheckResult(String keyCheckResult) {
        this.keyCheckResult = keyCheckResult;
    }

    public String getKeyQuantity() {
        return keyQuantity;
    }

    public void setKeyQuantity(String keyQuantity) {
        this.keyQuantity = keyQuantity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getBoxCellCode() {
        return boxCellCode;
    }

    public void setBoxCellCode(String boxCellCode) {
        this.boxCellCode = boxCellCode;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getCertificateCheckResult() {
        return certificateCheckResult;
    }

    public void setCertificateCheckResult(String certificateCheckResult) {
        this.certificateCheckResult = certificateCheckResult;
    }
}
