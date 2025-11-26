package com.revature.project0.util;

// For extra functions to debug the project but are not part
// of the project's functionality

import com.revature.project0.model.Expense;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Util {
    public static void printExpenseHeader(){
        String formatH = "| %-3s | %-7s | %-10s | %-25s | %-10s |";
        String header = String.format(formatH, "ID", "User_ID", "Amount", "Description", "Date");
        System.out.println(header);
    }

    public static void printExpense(Expense exp){
        String formatR = "| %-3d | %-7d | $%-9.2f | %-25s | %-10s |";
        int eid = exp.getId();
        int user_id = exp.getUser_id();
        float amount = exp.getAmount();
        String desc = exp.getDescription();
        String date = exp.getDate();
        String row = String.format(formatR, eid, user_id, amount, desc, date);
        System.out.println(row);
    }

    public static void printExpenseHeaderPending(){
        String formatH = "| %-3s | %-7s | %-10s | %-25s | %-10s | %-8s |";
        String header = String.format(formatH, "ID", "User_ID", "Amount", "Description", "Date", "Status");
        System.out.println(header);
    }

    public static void printExpensePending(Expense exp){
        String formatR = "| %-3d | %-7d | $%-9.2f | %-25s | %-10s | %-8s |";
        int eid = exp.getId();
        int user_id = exp.getUser_id();
        float amount = exp.getAmount();
        String desc = exp.getDescription();
        String date = exp.getDate();
        String row = String.format(formatR, eid, user_id, amount, desc, date, "PENDING");
        System.out.println(row);
    }
}
