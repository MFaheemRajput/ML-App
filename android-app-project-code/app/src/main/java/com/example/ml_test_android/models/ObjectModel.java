package com.example.ml_test_android.models;

import java.io.Serializable;

public class ObjectModel implements Serializable {
    String name;
    Float prob;
    Float x;
    Float y;
    Float height;

    public ObjectModel(String name, Float prob, Float x, Float y, Float height, Float width) {
        this.name = name;
        this.prob = prob;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getProb() {
        return prob;
    }

    public void setProb(Float prob) {
        this.prob = prob;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    Float width;
}
