package com.android.print.demo.bean;

public class Language {

    private int coding;
    private String name;

    public Language(int coding, String name) {
        this.coding = coding;
        this.name = name;
    }

    public int getCoding() {
        return coding;
    }

    public void setCoding(int coding) {
        this.coding = coding;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Language{" +
                "coding=" + coding +
                ", name='" + name + '\'' +
                '}';
    }
}
