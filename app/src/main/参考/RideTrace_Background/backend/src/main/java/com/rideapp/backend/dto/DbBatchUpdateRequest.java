package com.rideapp.backend.dto;

import java.util.List;

public class DbBatchUpdateRequest {
    private String sql;
    private List<List<Object>> paramsList;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<List<Object>> getParamsList() {
        return paramsList;
    }

    public void setParamsList(List<List<Object>> paramsList) {
        this.paramsList = paramsList;
    }
}

