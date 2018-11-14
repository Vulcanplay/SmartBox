package icar.a5i4s.com.smartbox.module;

/**
 * Created by light on 2016/11/9.
 */

public class Vin {
    private String id;
    private String boxCellCode;
    private String vin;
    private String ssId;
    private String idIn;
    private String idOut;
    private String vinIn;
    private String vinOut;
    private String boxCode;

    public Vin(String id, String vin) {
        this.id = id;
        this.vin = vin;
    }

    public Vin(String idIn, String idOut, String vinIn, String vinOut, String boxCode, String boxCellCode, String ssId) {
        this.idIn = idIn;
        this.idOut = idOut;
        this.vinIn = vinIn;
        this.vinOut = vinOut;
        this.boxCode = boxCode;
        this.boxCellCode = boxCellCode;
        this.ssId = ssId;
    }

    public Vin(String id, String boxCellCode, String vin) {
        this.id = id;
        this.boxCellCode = boxCellCode;
        this.vin = vin;
    }

    public String getIdIn() {
        return idIn;
    }

    public void setIdIn(String idIn) {
        this.idIn = idIn;
    }

    public String getIdOut() {
        return idOut;
    }

    public void setIdOut(String idOut) {
        this.idOut = idOut;
    }

    public String getVinIn() {
        return vinIn;
    }

    public void setVinIn(String vinIn) {
        this.vinIn = vinIn;
    }

    public String getVinOut() {
        return vinOut;
    }

    public void setVinOut(String vinOut) {
        this.vinOut = vinOut;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getSsId() {
        return ssId;
    }

    public void setSsId(String ssId) {
        this.ssId = ssId;
    }

    public String getBoxCellCode() {
        return boxCellCode;
    }

    public void setBoxCellCode(String boxCellCode) {
        this.boxCellCode = boxCellCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }
}
