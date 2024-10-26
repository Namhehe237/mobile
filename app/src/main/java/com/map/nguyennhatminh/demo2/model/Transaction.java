package com.map.nguyennhatminh.demo2.model;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable, Comparable<Transaction> {
    private int id;
    private String name;
    private int amount;
    private Date day;
    private String note;
    private CatInOut catInOut;

    public Transaction(int id, String name, int amount, Date day, String note, CatInOut catInOut) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.day = day;
        this.note = note;
        this.catInOut = catInOut;
    }

    public Transaction(String name, int amount, Date day, String note, CatInOut catInOut) {
        this.name = name;
        this.amount = amount;
        this.day = day;
        this.note = note;
        this.catInOut = catInOut;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public CatInOut getCatInOut() {
        return catInOut;
    }

    public void setCatInOut(CatInOut catInOut) {
        this.catInOut = catInOut;
    }

    @Override
    public int compareTo(Transaction other) {
        return this.day.compareTo(other.getDay());
    }
}
