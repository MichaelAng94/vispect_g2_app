package com.vispect.android.vispect_g2_app.bean;

/**
 * 更新文件
 *
 * Created by xu on 2017/1/23.
 */
public class VispectUpdateFile {
    private String versionname;
    private String path;
    private String filename;
    private int versioncode;
    private int length;
    private String md5;

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "VispectUpdateFile{" +
                "versionname='" + versionname + '\'' +
                ", path='" + path + '\'' +
                ", filename='" + filename + '\'' +
                ", versioncode=" + versioncode +
                ", length=" + length +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
