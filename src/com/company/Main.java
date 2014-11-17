package com.company;


import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.text.*;
import java.text.ParseException;
import java.util.*;

public class Main {

    public static ArrayList<Expense> expenses = null;

    public static SpreadsheetService service = null;


    public List<WorksheetEntry> getAllWorksheets() throws IOException, ServiceException{
        // Define the URL to request.  This should never change.  Получили объект файла Трат
        URL SPREADSHEET_FEED_URL = new URL(
                "https://spreadsheets.google.com/feeds/spreadsheets/0Aoa5WkgCFdrudEhzcnE0bU83QksteENZS3puSTZJRUE");
        SpreadsheetEntry spreadsheet = service.getEntry(SPREADSHEET_FEED_URL,SpreadsheetEntry.class);


        // Get the first worksheet of the first spreadsheet.  Получили все листы в файле Трат
        WorksheetFeed worksheetFeed = service.getFeed(
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        return worksheetFeed.getEntries();
    }

    public ArrayList<Expense> retrievAsList() throws IOException, ServiceException {
        ArrayList<Expense> expenses1 = new ArrayList<Expense>();

        List<WorksheetEntry> worksheets = getAllWorksheets();
        // пройдемся по всем месяцам
        for (WorksheetEntry worksheet:worksheets) {
            // Fetch the list feed of the worksheet. Получили список строк
            URL listFeedUrl = worksheet.getListFeedUrl();
            ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
            Date date = getDate(worksheet.getTitle().getPlainText());
            System.out.println(date);
            // Iterate through each row, printing its cell values.
            for (ListEntry row : listFeed.getEntries()) {
                if (needSkip(row.getCustomElements().getValue("название"))) continue;
                // Print the first column's cell value
                //  System.out.print(row.getTitle().getPlainText() + "\t");
                // Iterate over the remaining columns, and print each cell value
                String name = "";
                double sum = 0;
                String category = "";
                for (String tag : row.getCustomElements().getTags()) {
                    // System.out.print(row.getCustomElements().getValue(tag) + "\t" +"tags:"+tag+"\t");
                    switch (tag) {
                        case "название":
                            name = row.getCustomElements().getValue(tag);
                            break;
                        case "приход":
                            if (row.getCustomElements().getValue(tag) != null)
                                sum = Double.parseDouble(row.getCustomElements().getValue(tag));
                            category="приход";
                            break;
                        case "расход":
                            if (sum == 0 && row.getCustomElements().getValue(tag) != null)
                                sum = (-1) * Double.parseDouble(row.getCustomElements().getValue(tag));
                            break;
                        case "категория":
                            category = row.getCustomElements().getValue(tag);
                            break;
                        default:
                            break;
                    }
                }

             //   System.out.println(name+"\t"+sum+"\t"+category);
                // если это не пустая строка в данных
                if (name != null && !name.equals("")) expenses1.add(new Expense(name, sum, category, date));

            }
        }
        return expenses1;

    }

    private boolean needSkip(String name) {
        if (name == null)  return false;
        if (name.trim().equals("наличные")) return true;
        if (name.trim().equals("карта")) return true;
        if (name.trim().equals("на карте")) return true;
        if (name.trim().equals("на спец. счете")) return true;
        if (name.trim().equals("баланс на карте")) return true;
        if (name.trim().equals("наличка")) return true;
        return false;
    }


    public  void setPassword() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("security.txt"));
            String USERNAME = br.readLine();
            String PASSWORD = br.readLine();
            service.setUserCredentials(USERNAME, PASSWORD);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Date getDate(String dateText) {
        StringTokenizer st = new StringTokenizer(dateText);
        int month = getMonth(st.nextToken());
        int year=Integer.parseInt(st.nextToken());

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();

    }

    private int getMonth(String s) {
        if (s.toLowerCase().contains("январь") ) return 0;
        if (s.toLowerCase().contains("февраль") ) return 1;
        if (s.toLowerCase().contains("март") ) return 2;
        if (s.toLowerCase().contains("апрель") ) return 3;
        if (s.toLowerCase().contains("май") ) return 4;
        if (s.toLowerCase().contains("июнь") ) return 5;
        if (s.toLowerCase().contains("июль") ) return 6;
        if (s.toLowerCase().contains("август") ) return 7;
        if (s.toLowerCase().contains("сентябрь") ) return 8;
        if (s.toLowerCase().contains("октябрь") ) return 9;
        if (s.toLowerCase().contains("ноябрь") ) return 10;
        if (s.toLowerCase().contains("декабрь") ) return 11;

        return 0;
    }

    public void toJson() throws FileNotFoundException {
        Gson gson = new Gson();
        String json = gson.toJson(expenses);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("json.txt")));
        pw.println(json);
        pw.close();

    }



    public void init(){
        if (expenses ==null) expenses = new ArrayList<Expense>();
        if (service == null) service =   new SpreadsheetService("MySpreadsheetIntegration-v1");
    }
    public URL getLastListFeedUrl() throws IOException, ServiceException {
        return getAllWorksheets().get(0).getListFeedUrl();
    }

    public void addExpense(String name, double amount, String category )throws IOException, ServiceException {

        // Create a local representation of the new row.
        ListEntry row = new ListEntry();
        row.getCustomElements().setValueLocal("название", name);
        String  debit = (amount >= 0 ? Double.toString(amount):"");
        String credit = (amount < 0 ? Double.toString(-1*amount):"");
        row.getCustomElements().setValueLocal("приход", debit);
        row.getCustomElements().setValueLocal("расход", credit);
        row.getCustomElements().setValueLocal("категория", category);

        // Send the new row to the API for insertion.
        row = service.insert(getLastListFeedUrl(), row);
    }

    public void run() throws ServiceException, IOException, URISyntaxException, ParseException {
        init();
        setPassword();
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


        addExpense("Тест прибыль", 150,"Тестовая категория");
        addExpense("Тест убыль", -150,"Тестовая категория");
    }



    public static void main(String[] args)   throws AuthenticationException, MalformedURLException, IOException, ServiceException, URISyntaxException, ParseException {
        new Main().run();

    }
}
