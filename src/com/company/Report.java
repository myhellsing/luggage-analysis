package com.company;

import sun.reflect.generics.tree.Tree;

import java.util.*;

/**
 * Created by Integra on 23.07.2014.
 */
public class Report {
    public void printExpenses(List<Expense> expenseList) {
        for (Expense expense : expenseList) {
            System.out.print(expense.toString());
            System.out.println();
        }
    }
    public boolean checkTime(Date time1, Date time2){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(time1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(time2);
        return cal1.get(Calendar.YEAR)== cal2.get(Calendar.YEAR);
    }


    public void getAllCategories(List<Expense> expenseList) {
        getAllCategories(expenseList,null);
    }
    public void getAllCategories(List<Expense> expenseList, Date time) {
        System.out.println(time);
        HashMap<String, Double> hm = new HashMap<String, Double>();

        for (Expense expense : expenseList) {
            if (time != null &&  !checkTime(time,expense.date)) continue;
            double amount = expense.sum;
            if (hm.containsKey(expense.category)) {
                amount += hm.get(expense.category);
                hm.remove(expense.category);
            }
            hm.put(expense.category, amount);
        }

        CategoryTotal categoryTotals[] = new CategoryTotal[hm.size()];
        int i=0;
        for (String s : hm.keySet()) {
            categoryTotals[i]=(new CategoryTotal(hm.get(s),s));
            i++;
        }
        Arrays.sort(categoryTotals);

        for (CategoryTotal categoryTotal: categoryTotals){
            System.out.println(categoryTotal);
        }

    }

    public void getAllNames(List<Expense> expenseList, Date time) {
        System.out.println(time);
        HashMap<String, Double> hm = new HashMap<String, Double>();

        for (Expense expense : expenseList) {
            if (time != null &&  !checkTime(time,expense.date)) continue;
            double amount = expense.sum;
            if (hm.containsKey(expense.name)) {
                amount += hm.get(expense.name);
                hm.remove(expense.name);
            }
            hm.put(expense.name, amount);
        }

        CategoryTotal categoryTotals[] = new CategoryTotal[hm.size()];
        int i=0;
        for (String s : hm.keySet()) {
            categoryTotals[i]=(new CategoryTotal(hm.get(s),s));
            i++;
        }
        Arrays.sort(categoryTotals);

        for (CategoryTotal categoryTotal: categoryTotals){
            System.out.println(categoryTotal);
        }

    }
}
