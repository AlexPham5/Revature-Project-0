package com.revature.project0.services;
import com.revature.project0.dao.DAO;
import com.revature.project0.model.Approval;
import com.revature.project0.model.Expense;
import com.revature.project0.util.Util;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.revature.project0.util.Util.*;

/*
Manager App (Java)

    As a manager, I want to log in securely so that I can access and manage employee expense reports.
    As a manager, I want to view a list of all pending expenses so that I can review them efficiently.
    As a manager, I want to approve or deny submitted expenses so that I can manage reimbursements appropriately.
    As a manager, I want to add comments to expense decisions so that employees understand the reasoning behind approvals or denials.
    As a manager, I want to generate reports by employee, category, or date so that I can analyze spending trends and make informed decisions.

 */

public class ManagerApp {
    //inside of package com/revature/project0
    Scanner sc = new Scanner(System.in);
    Logger logger = LoggerFactory.getLogger(ManagerApp.class);
    DAO d1 = new DAO();
    int managerID;

    public ManagerApp(){
        logger.info("LOG START");
    }

    public void promptInput(){
        while(true){
            this.login();
            //System.out.println("Your managerID is: "+this.managerID);
            boolean loop = true;
            while(loop){
                //after users get past login, offer command options
                System.out.println("\n=====Command Options=====\n"+
                        "1 - View all pending expenses\n"+
                        "2 - Review Expenses (Approve or Deny)\n"+
                        "3 - Generate Report\n"+
                        "4 - LOGOUT");
                try{
                    System.out.print("Enter an option: ");
                    int userInput = Integer.parseInt(sc.nextLine());
                    switch(userInput){
                        case 1:
                            //System.out.println("Viewing expenses");
                            this.viewExpenses();
                            break;
                        case 2:
                            //System.out.println("Reviewing expenses");
                            this.reviewExpenses();
                            break;
                        case 3:
                            //System.out.println("Generating Reports");
                            this.generateReport();
                            break;
                        case 4:
                            System.out.println("Logging Out");
                            loop = false;
                            logger.info("LOG END");
                            break;
                        default:
                            System.out.println("Invalid number input for command options");
                            break;
                    }
                }catch(NullPointerException|NumberFormatException e){
                    System.out.println("Invalid input for command options");
                    logger.error("Invalid input for command options");
                }
            }
        }
    }

    //      As a manager, I want to log in securely so that I can access and
    //      manage employee expense reports.
    public void login(){
        while(true){
            try {
                System.out.println("\n===Welcome Manager===\n" + "1 - Enter Credentials\n" + "2 - EXIT");
                System.out.print("Please enter an option: ");
                int userInput = Integer.parseInt(sc.nextLine());
                if(userInput == 1){
                    //Get username and password
                    System.out.print("Enter Username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String password = sc.nextLine();
                    //Check revature db for valid username, password, and role
                    int id = d1.getCredentials(username, password);
                    if(id == -1){
                        System.out.println("Credentials not found for manager");
                    }
                    else {
                        System.out.println("SUCCESS: Credentials found for manager");
                        this.managerID = id;
                        break;
                    }
                }else if(userInput == 2)
                    System.exit(0);
                else
                    System.out.println("Invalid number option for login, please enter 1 or 2");
            }catch(NullPointerException|NumberFormatException e){
                System.out.println("Invalid input for login option, please enter 1 or 2");
                logger.warn("Invalid input for login option");
            }
        }
    }

    //      As a manager, I want to view a list of all pending expenses so that
    //      I can review them efficiently.
    public void viewExpenses(){
        logger.info("viewExpenses called");
        List<Expense> expenses = new ArrayList<Expense>();
        expenses = d1.getExpenses();
        if(expenses == null){
            System.out.println("Error while viewing expenses");
        }
        else{
            //Format for table printing
            String bordertop = "=".repeat(82);
            System.out.println(bordertop);
            Util.printExpenseHeaderPending();
            for(Expense e : expenses)
                Util.printExpensePending(e);
            String border = "=".repeat(18)+"[All current pending expenses displayed above]"+"=".repeat(18);
            System.out.println(border);
        }
    }

    //      As a manager, I want to approve or deny submitted expenses so that I
    //      can manage reimbursements appropriately.
    //      As a manager, I want to add comments to expense decisions so that
    //      employees understand the reasoning behind approvals or denials.
    public void reviewExpenses(){
        logger.info("ReviewExpenses called");
        //check if id selected in valid
        this.viewExpenses();
        try {
            System.out.print("Enter ID number of the expense to be reviewed: ");
            int userInput = Integer.parseInt(sc.nextLine());
            boolean looped = false;
            while(d1.checkID(userInput, "expenses") == 1){
                looped = true;
                try {
                    //ask for input values
                    System.out.println("Approve or Deny expense?\n" +
                            "1 - Approve\n" +
                            "2 - Deny\n" +
                            "3 - Cancel");
                    System.out.print("Enter a number: ");
                    int approvalChoice = Integer.parseInt(sc.nextLine());
                    if(approvalChoice == 1 || approvalChoice == 2){
                        System.out.print("Enter a short description why the expense was approved/denied (at least 10 characters): ");
                        String desc = sc.nextLine();
                        if(desc.length() < 10)
                            throw new NullPointerException("Description too short");
                        int valid = d1.editApproval(userInput, approvalChoice==1 ? "approved":"denied", this.managerID, desc);
                        if(valid == 1){
                            System.out.println("Expense successfully reviewed");
                            break;
                        }else{
                            System.out.println("Error while attempting to review expense");
                            break;
                        }
                    }else if(approvalChoice == 3){
                        System.out.println("Canceling review");
                        logger.info("Canceled expense review");
                        break;
                    }else{
                        System.out.println("Invalid number input for review choices");
                        logger.warn("Invalid number input for review choices");
                    }
                }catch(NullPointerException | NumberFormatException e){
                    System.out.println("Invalid input");
                    System.out.println(e.getMessage());
                    logger.error("Invalid input for review choices/desc size");
                }
            }
            if(!looped){
                System.out.println("The expense ID specified was not found");
                logger.warn("Expense ID not found in reviewExpense");
            }
        } catch (NullPointerException | NumberFormatException e) {
            System.out.println("Invalid input for review ID");
            logger.warn("Invalid input for reviewExpense ID");
        }
    }

    //      As a manager, I want to generate reports by employee, category, or date
    //      so that I can analyze spending trends and make informed decisions.
    //SQL states with LIKE? (ex. for %food% or %supplies%)

    public void generateReport(){
        logger.info("generateReport called");
        boolean userFilter = false;
        boolean dateFilter = false;
        boolean keywordFilter = false;
        while(true){
            try{
                System.out.println("\nGENERATE REPORT INTERFACE");
                System.out.println("Toggle any desired filters to generate report on, choose none to see all possible records: \n"+
                        "1 - Specify User ("+(userFilter ? "ON" : "OFF")+")\n"+
                        "2 - Specify Date Range ("+(dateFilter ? "ON" : "OFF")+")\n"+
                        "3 - Specify Category ("+(keywordFilter ? "ON" : "OFF")+")\n"+
                        "4 - CONTINUE\n"+
                        "5 - CANCEL");
                System.out.print("Enter an option: ");
                int userInput = Integer.parseInt(sc.nextLine());
                if(userInput == 1){
                    userFilter = !userFilter;
                }else if(userInput == 2){
                    dateFilter = !dateFilter;
                }else if(userInput == 3){
                    keywordFilter = !keywordFilter;
                }else if(userInput == 4){
                    //prompt and verify inputs
                    int userID = -1;
                    String dateS = "-1";
                    String dateE = "-1";
                    String keyword = "-1";

                    if (userFilter) {
                        //check if userID exists in user table
                        if(d1.displayUsers() == -1){
                            System.out.println("SQL Error in display users");
                            break;
                        }
                        System.out.print("Enter user id: ");
                        userID = Integer.parseInt(sc.nextLine());
                        if(d1.checkID(userID, "users") == -1){
                            throw new Exception("User id not found");
                        }
                    }
                    if (dateFilter) {
                        //prompt and verify dates are valid (exists and right format)
                        DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dFormat.setLenient(false);
                        LocalDate now = LocalDate.now();

                        System.out.print("Enter a start date range in format YYYY-MM-DD (cannot be in the future): ");
                        dateS = sc.nextLine();
                        dFormat.parse(dateS);
                        if(dateS.compareTo(now.toString()) > 0)
                            throw new Exception("Start date must be before or equal to current date");
                        System.out.print("Enter an end date range in format YYYY-MM-DD (cannot be before start date): ");
                        dateE = sc.nextLine();
                        dFormat.parse(dateE);
                        if(dateE.compareTo(dateS) < 0)
                            throw new Exception("End date must be after or equal to the start date");
                    }
                    if (keywordFilter) {
                        //prompt a keyword (any string)
                        System.out.print("Enter a keyword to search with (Ex. food, supplies, repair, etc.): ");
                        keyword = sc.nextLine();
                    }
                    System.out.print("Enter a name for this report: ");
                    String reportName = sc.nextLine();
                    //call to DAO to do operations
                    Pair<List<Expense>, List<Approval>> reports;
                    reports = d1.getReport(userID, dateS, dateE, keyword);

                    if(reports != null){
                        //Print out reports
                        printReportHeader(reportName);
                        for(int i=0; i <reports.getKey().size();i++){
                            printReport(reports.getKey().get(i), reports.getValue().get(i));
                        }

                        //Save reports to a file
                        try {
                            saveReport(reportName, reports.getKey(), reports.getValue());
                        }catch(IOException e){
                            System.out.println("Error when saving report");
                            logger.error("Report generation failed, file not saved");
                            break;
                        }

                        System.out.println("REPORT GENERATED");
                        logger.info("Report generation success");
                        break;
                    }
                    else{
                        System.out.println("REPORT NOT GENERATED");
                        logger.error("Report generation failed");
                        break;
                    }
                }else if(userInput == 5){
                    System.out.println("Cancelling report");
                    break;
                }else{
                    System.out.println("Invalid number input for toggles");
                }

            }catch(NullPointerException | NumberFormatException e){
                System.out.println("Invalid input for filters");
                logger.error("Invalid input for filters");
            }catch(DateTimeParseException e){
                System.out.println("Invalid date: "+e.getMessage());
                logger.error("Invalid input for date");
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }


    }
}
