package com.chanin.lincc.exdisplay.model;

import java.util.ArrayList;

public class ExClass {

    public String name;
    public int count;
    public int unCount;
    public ArrayList<ExGroup> groups;


    public ExClass(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public ExClass(String name, int count, ArrayList<ExGroup> groups) {
        this.name = name;
        this.count = count;
        this.groups = groups;
    }

    public ExClass(String name, int count, int unCount, ArrayList<ExGroup> groups) {
        this.name = name;
        this.count = count;
        this.unCount = unCount;
        this.groups = groups;
    }

    public int getUnCount() {
        return unCount;
    }

    public void setUnCount(int unCount) {
        this.unCount = unCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<ExGroup> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<ExGroup> groups) {
        this.groups = groups;
    }


    @Override
    public String toString() {
        return "ExClass{" +
                "name='" + name + '\'' +
                ", count=" + count +
                '}';
    }
}
