package com.company;

/**
 * Created by Integra on 01.09.2014.
 */
public class CategoryTotal implements Comparable {
    Double total;
    String name;

    public CategoryTotal(Double total, String name) {
        this.total = total;
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        CategoryTotal categoryTotal = (CategoryTotal)o;
        return (int)(categoryTotal.total - total)*(-1);
    }

    @Override
    public String toString() {
        return name + "\t" + total;
    }
}
