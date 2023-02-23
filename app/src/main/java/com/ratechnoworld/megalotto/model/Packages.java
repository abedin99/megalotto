package com.ratechnoworld.megalotto.model;

import com.google.gson.annotations.SerializedName;

public class Packages {

    @SerializedName("id")
    private String id;

    @SerializedName("pkg_name")
    private String pkg_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPkg_name() {
        return pkg_name;
    }

    public void setPkg_name(String pkg_name) {
        this.pkg_name = pkg_name;
    }
}
