package com.vispect.android.vispect_g2_app.bean;

/**
 * 安装视频
 *
 * Created by xu on 2017/8/23.
 */
public class InstallVideo {
    private int type;
    private int step;
    private String creationtime;
    private int status;
    private String url;
    private String note;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getCreationtime() {
        return creationtime;
    }

    public void setCreationtime(String creationtime) {
        this.creationtime = creationtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "InstallVideo{" +
                "type=" + type +
                ", step=" + step +
                ", creationtime='" + creationtime + '\'' +
                ", status=" + status +
                ", url='" + url + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
