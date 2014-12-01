package com.company;


import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.*;

public class Main {

    public static ArrayList<Expense> expenses = null;



    public void toJson() throws FileNotFoundException {
        Gson gson = new Gson();
        String json = gson.toJson(expenses);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("json.txt")));
        pw.println(json);
        pw.close();

    }



    public void run() throws ServiceException, IOException, URISyntaxException, ParseException {
        driverToGoogleData rd = new driverToGoogleData("https://spreadsheets.google.com/feeds/spreadsheets/0Aoa5WkgCFdrudEhzcnE0bU83QksteENZS3puSTZJRUE");
        expenses = rd.getExpenses();

    //   new Report().getAllCategories(expenses);
     //   Calendar calendar= Calendar.getInstance();
     /*   calendar.set(Calendar.YEAR, 2013);
        new Report().getAllCategories(expenses,calendar.getTime());
        calendar.set(Calendar.YEAR, 2014);
        new Report().getAllCategories(expenses,calendar.getTime());*/
     //   calendar.set(Calendar.YEAR, 2014);
      //  new Report().getAllNames(expenses,calendar.getTime());
        //new Report().printExpenses(expenses);
     //   toJson();

   //     expenses = retrievAsList();
      //  new Report().getAllCategories(expenses);


        rd.writeExpense("Тест прибыль", 150, "Тестовая категория");
        rd.writeExpense("Тест убыль", -150, "Тестовая категория");
    }



    public static void main(String[] args)   throws AuthenticationException, MalformedURLException, IOException, ServiceException, URISyntaxException, ParseException {
        new Main().run();

    }
}
