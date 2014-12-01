package com.company;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by myhellsing on 01.12.14.
 */
public class driverToGoogleData {

    // service for connection to Google Drive
    public static SpreadsheetService service = null;
    // url for data
    URL SPREADSHEET_FEED_URL = null;

    public driverToGoogleData(String feed_url) {
        init();
        try {
            SPREADSHEET_FEED_URL = new URL(feed_url);
        } catch (MalformedURLException e) {
            System.out.println("SPREADSHEET_FEED_URL may be is not correct "+ e.getMessage() );
            e.printStackTrace();
        }
    }

    public void init(){

        if (service == null) service =   new SpreadsheetService("MySpreadsheetIntegration-v1");
        setPassword();
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


//TODO refactor it
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

    public List<WorksheetEntry> getAllWorksheets() {
        //   Получили объект файла Трат
        WorksheetFeed worksheetFeed = null;
        try {
            SpreadsheetEntry spreadsheet = service.getEntry(SPREADSHEET_FEED_URL,SpreadsheetEntry.class);
            // Get the first worksheet of the first spreadsheet.  Получили все листы в файле Трат
            worksheetFeed = service.getFeed(
                    spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return worksheetFeed.getEntries();
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



    public ArrayList<Expense> getExpenses() throws IOException, ServiceException {

        ArrayList<Expense> expenses = new ArrayList<Expense>();
        // список таблиц по месяцам
        List<WorksheetEntry> worksheets = getAllWorksheets();
        // пройдемся по всем месяцам
        for (WorksheetEntry worksheet:worksheets) {
            // Получили список строк
            ListFeed listFeed = service.getFeed(worksheet.getListFeedUrl(), ListFeed.class);
            Date date = getDate(worksheet.getTitle().getPlainText());
            System.out.println(date);
            // Iterate through each row, printing its cell values.
            for (ListEntry row : listFeed.getEntries()) {
                // если это строки с фиксацией текущего состояния на начало месяца - то пропускаем их
                if (needSkip(row.getCustomElements().getValue("название"))) continue;
                // парсим данные - определяем, что относится к доходу, а что к тратам
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

                // если это не пустая строка в данных
                if (name != null && !name.equals("")) expenses.add(new Expense(name, sum, category, date));

            }
        }

        return expenses;
    }

    /**
     * Отдает урл самого свежего листа
     * @return
     * @throws IOException
     * @throws ServiceException
     */

    public URL getLastListFeedUrl() throws IOException, ServiceException {
        return getAllWorksheets().get(0).getListFeedUrl();
    }


    /**
     *  Добавляет строчку в самый свежий лист
     * @param name
     * @param amount
     * @param category
     * @throws IOException
     * @throws ServiceException
     */
    public void writeExpense(String name, double amount, String category)throws IOException, ServiceException {

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
}
