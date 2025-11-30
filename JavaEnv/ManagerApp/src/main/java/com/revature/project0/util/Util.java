package com.revature.project0.util;

// For extra functions to debug the project but are not part
// of the project's functionality

import com.revature.project0.model.Approval;
import com.revature.project0.model.Expense;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    public static void printExpenseHeaderStatus(){
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

    public static void printReportHeader(String reportName){
        System.out.println(reportName);
        String formatH = "| %-3s | %-7s | %-9s | %-25s | %-12s | %-8s | %-11s | %-25s | %-12s |";
        String header = String.format(formatH, "ID", "User_ID", "Amount", "Description", "Expense Date", "Status", "Reviewer_ID", "Comment", "Review Date");
        System.out.println(header);
    }

    public static void printReport(Expense e, Approval a){
        String formatR = "| %-3d | %-7d | $%-8.2f | %-25s | %-12s | %-8s | %-11s | %-25s | %-12s |";
        int expID = e.getId();
        int userID = e.getUser_id();
        float amount = e.getAmount();
        String desc = e.getDescription();
        String expDate = e.getDate();
        String status = a.getStatus();
        int revID = a.getReviewer();
        String comment = a.getComment();
        String revDate = a.getReview_date();

        String row = String.format(formatR, expID, userID, amount, desc, expDate, status, (revID==0 ? "N/A":Integer.toString(revID)),(comment==null ? "N/A":comment), (revDate==null ? "N/A":revDate));
        System.out.println(row);
    }

    public static void saveReport(String reportName, List<Expense> e, List<Approval> a) throws IOException{
        System.out.println("Report Name: "+reportName);
        String formatH = "| %-3s | %-7s | %-9s | %-25s | %-12s | %-8s | %-11s | %-25s | %-12s |";
        String header = String.format(formatH, "ID", "User_ID", "Amount", "Description", "Expense Date", "Status", "Reviewer_ID", "Comment", "Review Date");

        try(FileWriter fw = new FileWriter("target\\" + reportName + ".txt")) {
            fw.write("Report Name: " + reportName + "\n");
            fw.write(header + "\n");
            for(int i = 0; i < e.size(); i++){
                int expID = e.get(i).getId();
                int userID = e.get(i).getUser_id();
                float amount = e.get(i).getAmount();
                String desc = e.get(i).getDescription();
                String expDate = e.get(i).getDate();
                String status = a.get(i).getStatus();
                int revID = a.get(i).getReviewer();
                String comment = a.get(i).getComment();
                String revDate = a.get(i).getReview_date();
                //revID, comment, and revDate can be null
                String formatR = "| %-3d | %-7d | $%-8.2f | %-25s | %-12s | %-8s | %-11s | %-25s | %-12s |";
                String row = String.format(formatR, expID, userID, amount, desc, expDate, status, (revID==0 ? "N/A":Integer.toString(revID)),(comment==null ? "N/A":comment), (revDate==null ? "N/A":revDate));
                fw.write(row+"\n");
            }
        } catch (IOException err) {
            throw err;
        }
    }

    public static Connection connect(String path){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(path, "root", "");
        }catch(SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
}
