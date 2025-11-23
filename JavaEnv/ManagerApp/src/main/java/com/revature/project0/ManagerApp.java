package com.revature.project0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Scanner;

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
                System.out.println("=====Command Options=====\n"+
                        "1 - View all pending expenses\n"+
                        "2 - Review Expenses (Approve or Deny)\n"+
                        "3 - Generate Report\n"+
                        "4 - LOGOUT");
                try{
                    System.out.print("Enter an option: ");
                    int userInput = Integer.parseInt(sc.nextLine());
                    switch(userInput){
                        case 1:
                            System.out.println("Viewing expenses");
                            break;
                        case 2:
                            System.out.println("Reviewing expenses");
                            break;
                        case 3:
                            System.out.println("Generating Reports");
                            break;
                        case 4:
                            System.out.println("Logging Out");
                            loop = false;
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
        //Show only pending expenses
    }

    //      As a manager, I want to approve or deny submitted expenses so that I
    //      can manage reimbursements appropriately.
    //      As a manager, I want to add comments to expense decisions so that
    //      employees understand the reasoning behind approvals or denials.
    public void reviewExpenses(){

    }

    //      As a manager, I want to generate reports by employee, category, or date
    //      so that I can analyze spending trends and make informed decisions.
    //SQL states with LIKE? (ex. for %food% or %supplies%)
    public void generateReport(){

    }
}
