package com.baidu.aip.fl.bean;

public class User {
    private String id;

    private String name;

    private String groupp;

    public User(String id, String name, String groupp) {
        this.id = id;
        this.name = name;
        this.groupp = groupp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getGroupp() {
        return groupp;
    }

    public void setGroupp(String groupp) {
        this.groupp = groupp == null ? null : groupp.trim();
    }

    @Override
    public String toString() {
        return id + "-" + name + "-" + groupp;
    }
}