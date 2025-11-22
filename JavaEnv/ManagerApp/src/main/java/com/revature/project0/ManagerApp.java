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
            while(true){
                //after users get past login, offer command options
                System.out.println("Login successful, now in input loop");
                break;
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
                        System.out.println("Credentials not found for manager");
                        this.managerID = id;
                        break;
                    }
                }else if(userInput == 2){
                    System.exit(0);
                }else{
                    System.out.println("Invalid number option for login, please enter 1 or 2");
                }
            }catch(NullPointerException|NumberFormatException e){
                System.out.print("Invalid input for login option, please enter 1 or 2");
                logger.warn("Invalid input for login option");
            }
        }
    }

    //      As a manager, I want to view a list of all pending expenses so that
    //      I can review them efficiently.
    public void viewExpensesPending(){

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
