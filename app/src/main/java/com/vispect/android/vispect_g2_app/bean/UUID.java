package com.vispect.android.vispect_g2_app.bean;

/**
 * 设备的UUID
 *
 * Created by xu on 2017/12/20.
 */
public class UUID {
    int id;
    String createTime;
    int obdid;
    int buzzerid;
    int gprsid;
    int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getBuzzerid() {
        return buzzerid;
    }

    public void setBuzzerid(int buzzerid) {
        this.buzzerid = buzzerid;
    }

    public int getObdid() {
        return obdid;
    }

    public void setObdid(int obdid) {
        this.obdid = obdid;
    }

    public int getGprsid() {
        return gprsid;
    }

    public void setGprsid(int gprsid) {
        this.gprsid = gprsid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
