package com.example.ml_test_android.models;

public class Success<T> {
    private T value;

    public Success(T value) {
        this.setValue(value);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }


}

