package com.map.nguyennhatminh.demo2.model;

import java.io.Serializable;

public class CatInOut implements Serializable {
    private int id;
    private InOut inOut;
    private Category category;

    public CatInOut(int id) {
        this.id = id;
    }

    public CatInOut(int id, InOut inOut, Category category) {
        this.id = id;
        this.inOut = inOut;
        this.category = category;
    }

    public CatInOut(InOut inOut, Category category) {
        this.inOut = inOut;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InOut getInOut() {
        return inOut;
    }

    public void setInOut(InOut inOut) {
        this.inOut = inOut;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
