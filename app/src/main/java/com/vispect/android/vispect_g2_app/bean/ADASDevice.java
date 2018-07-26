package com.vispect.android.vispect_g2_app.bean;

import java.util.Date;

/**
 * 设备实体类
 * Created by xu on 2017/9/20.
 */
public class ADASDevice {
    private int deviceId;
    private Date createTime;
    private int obdid;
    private int buzzerid;
    private int gprsid;
    private int status;    //0:未被使用 1：已被使用 2：已被预订

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        deviceId = deviceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getObdid() {
        return obdid;
    }

    public void setObdid(int obdid) {
        this.obdid = obdid;
    }

    public int getBuzzerid() {
        return buzzerid;
    }

    public void setBuzzerid(int buzzerid) {
        this.buzzerid = buzzerid;
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

    @Override
    public String toString() {
        return "ADASDevice{" +
                "DeviceId=" + deviceId +
                ", createTime=" + createTime +
                ", obdid=" + obdid +
                ", buzzerid=" + buzzerid +
                ", gprsid=" + gprsid +
                ", status=" + status +
                '}';
    }
}
