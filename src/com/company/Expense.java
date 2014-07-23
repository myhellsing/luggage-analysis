package com.company;

import java.util.Date;

/**
 * Created by Integra on 23.07.2014.
 */
public class Expense {
    public String name;
    public Double sum;
    public String category;
    public Date date;

    public Expense() {
    }

    public Expense(String name, Double sum, String category, Date date) {
        this.name = name;
        this.sum = sum;
        this.category = category;
        this.date = date;
    }

    public String toString(){
      return this.name+"\t"+this.sum+"\t"+this.category;
    }
}
