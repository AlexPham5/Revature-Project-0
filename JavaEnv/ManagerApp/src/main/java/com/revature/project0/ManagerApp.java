package com.revature.project0;
import java.sql.*;

/*
Manager App (Java)

    As a manager, I want to log in securely so that I can access and manage employee expense reports.
    As a manager, I want to view a list of all pending expenses so that I can review them efficiently.
    As a manager, I want to approve or deny submitted expenses so that I can manage reimbursements appropriately.
    As a manager, I want to add comments to expense decisions so that employees understand the reasoning behind approvals or denials.
    As a manager, I want to generate reports by employee, category, or date so that I can analyze spending trends and make informed decisions.

 */

public class ManagerApp {
    //in com/revature/project0
    //private String dbPath = "..\\..\\..\\..\\..\\..\\..\\..\\RevatureDatabase.db";
    private String dbPath = "C:\\Users\\alex1\\Revature work\\Project 0\\RevatureDatabase.db";

    public ManagerApp(){

    }

    public void promptInput(){

        while(true){
            login();
            while(true){
                //after users get past login, offer command options
                System.out.println();
            }
        }
    }

    //      As a manager, I want to log in securely so that I can access and
    //      manage employee expense reports.
    public void login(){
        //check revaturedb
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
    public void generateReport(){

    }
}
