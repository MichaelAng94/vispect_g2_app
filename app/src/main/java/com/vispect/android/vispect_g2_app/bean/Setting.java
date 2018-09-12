package com.vispect.android.vispect_g2_app.bean;

import android.support.v4.app.Fragment;

public class Setting {
    private int description;
    private Fragment fragment;

    public Setting(int description, Fragment fragment) {
        this.description = description;
        this.fragment = fragment;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
