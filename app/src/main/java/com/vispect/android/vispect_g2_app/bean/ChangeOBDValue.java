package com.vispect.android.vispect_g2_app.bean;

import java.util.List;

/**
 * 改变过的OBD数据，主要是保存ID和变化过的第几个位
 * Created by mo on 2018/3/23.
 */

public class ChangeOBDValue {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List getChangebit() {
        return changebit;
    }

    public void setChangebit(List changebit) {
        this.changebit = changebit;
    }

    private List changebit;

}
