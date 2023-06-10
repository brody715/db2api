package com.brody715.db2api.model;

public class OperationConfig {
    String name;
    String table;
    Boolean add;
    Boolean delete;
    Boolean update;
    Boolean get;
    String detail;

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    public Boolean getAdd() {
        return add;
    }

    public Boolean getDelete() {
        return delete;
    }

    public Boolean getUpdate() {
        return update;
    }

    public Boolean getGet() {
        return get;
    }

    public String getDetail() {
        return detail;
    }
    
    public void setName(final String name) {
        this.name = name;
    }

    public void setTable(final String table) {
        this.table = table;
    }

    public void setAdd(final Boolean add) {
        this.add = add;
    }

    public void setDelete(final Boolean delete) {
        this.delete = delete;
    }

    public void setUpdate(final Boolean update) {
        this.update = update;
    }

    public void setGet(final Boolean get) {
        this.get = get;
    }

    public void setDetail(final String detail) {
        this.detail = detail;
    }
}
