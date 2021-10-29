package com.array.paul_backend_test.rest;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonResponse implements Serializable {
    private boolean success;
    private String message;
    private String error;
    private Map<String,Object> dataMap = new LinkedHashMap<>();

    public JsonResponse() {
        this.success=false;
    }

    public void addData(String key, Object value) {
        this.dataMap.put(key,value);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }
}
