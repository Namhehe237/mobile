package com.map.nguyennhatminh.demo2.model;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private int icon;
    private String note;
    private Category category;

    public Category(int id) {
        this.id = id;
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(int id, String name, int icon, String note, Category category) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.note = note;
        this.category = category;
    }



    public Category(String name, int icon, String note, Category category) {
        this.name = name;
        this.icon = icon;
        this.note = note;
        this.category = category;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
