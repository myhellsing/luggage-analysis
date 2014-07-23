package com.company;

import com.google.gdata.client.authn.oauth.*;
import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.*;
import com.google.gdata.data.batch.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class Main {

    public static void main(String[] args)   throws AuthenticationException, MalformedURLException, IOException, ServiceException {
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

            URL cellFeedUrl = worksheetEntry.getCellFeedUrl();
            CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

            // Iterate through each cell, printing its value.
            for (CellEntry cell : cellFeed.getEntries()) {
                // Print the cell's address in A1 notation
                System.out.print(cell.getTitle().getPlainText() + "\t");
                // Print the cell's address in R1C1 notation
                System.out.print(cell.getId().substring(cell.getId().lastIndexOf('/') + 1) + "\t");

                // Print the cell's displayed value (useful if the cell has a formula)
                System.out.println(cell.getPlainTextContent() + "\t");
            }
            break;

        }
    }
}
