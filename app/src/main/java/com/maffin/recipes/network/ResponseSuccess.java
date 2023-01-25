package com.maffin.recipes.network;

import java.util.List;

public class ResponseSuccess<T> {
    private List<T> data;
    private boolean success;

    public List<T> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
