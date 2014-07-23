package com.company;

import java.util.List;

/**
 * Created by Integra on 23.07.2014.
 */
public class Report {
    public void printExpenses(List<Expense> expenseList){
        for (Expense expense:expenseList){
            System.out.print(expense.toString());
            System.out.println();
        }
    }
}
