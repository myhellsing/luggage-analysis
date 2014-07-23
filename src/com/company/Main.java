package com.company;

import com.google.gdata.client.authn.oauth.*;
import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.*;
import com.google.gdata.data.batch.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.text.*;
import java.text.ParseException;
import java.util.*;

public class Main {

    public static ArrayList<Expense> expenses = null;

    public void fillExpense() throws AuthenticationException, MalformedURLException, IOException, ServiceException, URISyntaxException, java.text.ParseException {
        if (expenses ==null) expenses = new ArrayList<Expense>();

        BufferedReader br = new BufferedReader(new FileReader("security.txt"));

        // write your code here
        String USERNAME = br.readLine();
        String PASSWORD = br.readLine();

        SpreadsheetService service =
                new SpreadsheetService("MySpreadsheetIntegration-v1");
        service.setUserCredentials(USERNAME, PASSWORD);

        // TODO: Authorize the service object for a specific user (see other sections)

        // Define the URL to request.  This should never change.
        URL SPREADSHEET_FEED_URL = new URL(
                "https://spreadsheets.google.com/feeds/worksheets/tHsrq4mO7BK-xCYKznI6IEA/private/basic");



        // Make a request to the API and get all spreadsheets.
        WorksheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, WorksheetFeed.class);
        List<WorksheetEntry> worksheetEntries = feed.getEntries();

        // Iterate through all of the spreadsheets returned
        for (WorksheetEntry worksheetEntry : worksheetEntries) {
            // Print the title of this spreadsheet to the screen
            System.out.println(worksheetEntry.getTitle().getPlainText() );
            System.out.println();


            // Fetch the list feed of the worksheet.
            URL cellFeedUrl = new URI(worksheetEntry.getCellFeedUrl().toString()+ "?min-row=6&min-col=1&max-col=4").toURL();
            CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

            // Iterate through each row, printing its cell values.
            List<CellEntry> cells  = cellFeed.getEntries();

            Date date = getDate(worksheetEntry.getTitle().getPlainText());
            int cnt = cells.size()/3;
            int inc = 2;
            for (int i =0;i<cnt;i=i+inc)
            {
                String name  = cells.get(i).getPlainTextContent();
                Double sum = Double.parseDouble(cells.get(i+1).getPlainTextContent()); //приход или расход
                String category="";
                if (cells.get(i+1).getTitle().getPlainText().contains("C")){
                    sum=sum*(-1);
                    if (date.after(new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH).parse("01-09-2013"))){
                        category = cells.get(i+2).getPlainTextContent();
                        inc = 3;
                    }
                    else inc=2;

                }
                else inc = 2;
                expenses.add(new Expense(name,sum,category,date));



            }
         //   break;



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
    public void run() throws ServiceException, IOException, URISyntaxException, ParseException {
        fillExpense();
        new Report().printExpenses(expenses);
    }

    public static void main(String[] args)   throws AuthenticationException, MalformedURLException, IOException, ServiceException, URISyntaxException, ParseException {
        new Main().run();



    }
}
