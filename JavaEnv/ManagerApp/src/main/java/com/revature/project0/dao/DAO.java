package com.revature.project0.dao;
import ch.qos.logback.core.util.FileSize;
import com.revature.project0.model.Approval;
import com.revature.project0.model.Expense;
import com.revature.project0.services.ManagerApp;
import com.revature.project0.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import java.util.Properties;

//Will execute the SQL commands after ManagerApp verifies the inputs
public class DAO {
    private static String dbPath;
    Logger logger = LoggerFactory.getLogger(ManagerApp.class);

    public DAO(){
        //dbPath = "jdbc:sqlite:C:\\Users\\alex1\\Revature_work\\Project_0\\RevatureDatabase.db";
        // Get the dbPath from the properties file db.properties
        Properties lp = new Properties();
        try(FileInputStream fis = new FileInputStream("src\\main\\resources\\db.properties")){
            lp.load(fis);
            dbPath = lp.getProperty("dbPathMySQL");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //returns -1 if the id doesn't exists in expenses, otherwise return 1
    // if mode = 1, check all pending expenses
    // if mode = 2, check all approved/denied expenses
    public Expense checkExpenseID(int id, boolean pending){
        try(Connection conn = Util.connect(dbPath)) {
            if (conn != null) {
                String check = "SELECT * FROM expenses WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(check);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.isBeforeFirst()) {
                    //check if the expense is pending by checking approval
                    rs.next();
                    int expense_id = rs.getInt("id");
                    StringBuilder query = new StringBuilder("SELECT status FROM approvals WHERE expense_id=?");
                    if(pending)
                        query.append(" AND status='pending'");
                    else
                        query.append(" AND NOT status='pending'");
                    PreparedStatement ps2 = conn.prepareStatement(query.toString());
                    ps2.setInt(1, expense_id);
                    ResultSet rs2 = ps2.executeQuery();
                    if(rs2.isBeforeFirst()){
                        rs2.next();
                        String status = rs2.getString("status");
                        //wanted pending expenses, got a pending expense
                        if((status.equals("pending") && pending) || (!status.equals("pending") && !pending)){
                            //Show expense to be edited
                            Expense e = new Expense(rs.getInt("id"), rs.getInt("user_id"), rs.getFloat("amount"), rs.getString("description"), rs.getString("date"));
                            return e;
                        }else{
                            return null;
                        }
                    }else{
                        logger.error("Approval corresponding to expense_id not found");
                        return null;
                    }
                }
                else
                    return null;
            }
        }catch(SQLException e){
            logger.error("SQLException in checkID");
            logger.error(e.getMessage());
            System.out.println("SQL Error when verifying ID\n");
            return null;
        }
        return null;
    }

    //return -1 if it doesnt exist, otherwise return 1
    public int checkUserID(int id) {
        try (Connection conn = Util.connect(dbPath)) {
            if(conn != null){
                String check = String.format("SELECT id, username, role FROM users WHERE id=? AND role='Employee'");
                PreparedStatement ps = conn.prepareStatement(check);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if(!rs.isBeforeFirst()){
                    logger.warn("User id not found");
                    return -1;
                }
                else{
                    rs.next();
                    System.out.println("User ID Chosen: ");
                    printUser(rs);
                    return 1;
                }
            }
        }catch(SQLException e){
            logger.error("SQLException in checkUserID");
            logger.error(e.getMessage());
            System.out.println("SQL Error when verifying user ID\n");
            return -1;
        }
        return -1;
    }

    //returns -1 if unsuccessful, otherwise returns the manager's id
    public int getCredentials(String username, String password){
        logger.info("Attempting to find manager user credential's in db");
        try(Connection conn = Util.connect(dbPath)){
            if(conn != null){
                logger.info("SQL connection successful for getCredentials");
                String getCredentials = "SELECT * FROM users WHERE username=? AND password=? AND role='Manager'";
                PreparedStatement ps = conn.prepareStatement(getCredentials);
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getInt("id");
                }else{
                    logger.warn("Manager credentials specified not found");
                    return -1;
                }
            }
        }catch(SQLException e){
            logger.error("SQLException in getCredentials");
            logger.error(e.getMessage());
            System.out.println("SQL Error when connecting\n");
            return -1;
        }
        return -1;
    }

    //show all pending expenses in the expense table
    public List<Expense> getExpenses(boolean pending){
        logger.info("Attempting to display expenses");
        try(Connection conn = Util.connect(dbPath)){
            if(conn != null) {
                logger.info("SQL connection successful for displayExpenses");
                //Get all the expense_id from approvals where status is pending
                //String getEIDs = "SELECT expense_id FROM approvals WHERE status='pending'";
                String getEIDs = "SELECT expense_id FROM approvals";
                StringBuilder query = new StringBuilder(getEIDs);
                if(pending)
                    query.append(" WHERE status = 'pending'");
                else
                    query.append(" WHERE NOT status = 'pending'");
                Statement s1 = conn.createStatement();
                ResultSet eIDs = s1.executeQuery(query.toString());
                if (!eIDs.isBeforeFirst()) {
                    //no expenses found
                    System.out.println("NO EXPENSES FOUND");
                    return null;
                }
                List<Expense> expenseList = new ArrayList<Expense>();
                while (eIDs.next()) {
                    //for every expense_id, get the corresponding expense
                    String displayExpense = "SELECT * FROM expenses WHERE id=?;";
                    PreparedStatement p1 = conn.prepareStatement(displayExpense);
                    System.out.println("Expense id: "+eIDs.getInt("expense_id"));
                    p1.setInt(1, eIDs.getInt("expense_id"));
                    ResultSet expense = p1.executeQuery();
                    expense.next();

                    //Make expense obj and add to list
                    int id = expense.getInt("id");
                    int user_id = expense.getInt("user_id");
                    float amount = expense.getFloat("amount");
                    String desc = expense.getString("description");
                    String date = expense.getString("date");
                    Expense e = new Expense(id, user_id, amount, desc, date);
                    expenseList.add(e);
                }
                logger.info("Successfully displayed all pending expenses");
                return expenseList;
            }
        }catch(SQLException e){
            logger.error("SQLException in displayExpenses");
            logger.error(e.getMessage());
            return null;
        }
        return null;
    }

    //set an expense as approved or denied, make comments, record date and manager id
    //return 1 on success, -1 on failure
    public int editApproval(int eID, String status, int manID, String comment){
        try(Connection conn = Util.connect(dbPath)){
            if(conn != null) {
                logger.info("SQL connection successful for editApproval");
                String review = "UPDATE approvals SET status=?, reviewer=?, comment=?, review_date=? WHERE expense_id=?";
                PreparedStatement p1 = conn.prepareStatement(review);
                p1.setString(1, status);
                p1.setInt(2, manID);
                p1.setString(3, comment);
                //date will be date.now();
                LocalDate now = LocalDate.now();
                p1.setString(4, now.toString());
                p1.setInt(5, eID);
                if(p1.executeUpdate() < 1) {
                    logger.info("Expense review failed");
                    return -1;
                }
                else {
                    logger.error("Expense successfully reviewed");
                    return 1;
                }
            }
        }catch(SQLException e){
            logger.error("SQLException in editApproval");
            logger.error(e.getMessage());
            return -1;
        }
        return -1;
    }

    //Show all linked expenses and approvals based on:
    //User id
    //Date using BETWEEN
    //'Category' by using LIKE for description
    //Display on console and write to a text file
    //return 1 on success, return 2 on no reports found, -1 on failure
    public Pair<List<Expense>, List<Approval>> getReport(int user, String dateS, String dateE, String keyword){
        try(Connection conn = Util.connect(dbPath)) {
            if(conn != null){
                logger.info("Successful DB connect in generateReport");
                StringBuilder query = new StringBuilder("SELECT * FROM expenses INNER JOIN approvals ON(expenses.id=approvals.expense_id)");
                if(user != -1 || (!dateS.equals("-1") && !dateE.equals("-1") || !keyword.equals("-1"))){
                    //for multiple parameters, ensure right AND syntax
                    boolean mult = false;
                    query.append(" WHERE");
                    if (user != -1) {
                        mult = true;
                        query.append(String.format(" user_id=%d", user));
                    }
                    if (!dateS.equals("-1") && !dateE.equals("-1")) {
                        if (mult)
                            query.append(" AND");
                        mult = true;
                        query.append(String.format(" date BETWEEN '%s' AND '%s'", dateS, dateE));
                    }
                    if (!keyword.equals("-1")) {
                        if (mult)
                            query.append(" AND");
                        query.append(String.format(" description LIKE '%%%s%%'", keyword));
                    }
                }
                //System.out.println("Query executed: "+query);
                Statement s1 = conn.createStatement();
                ResultSet rs = s1.executeQuery(query.toString());
                if(!rs.isBeforeFirst()){
                    System.out.println("NO RECORDS FOUND FOR CRITERIA SPECIFIED");
                    logger.info("No records found for criteria specified, no report generated");
                    return null;
                }
                //pair of expense list and approval list
                List<Expense> eList = new ArrayList<Expense>();
                List<Approval> aList = new ArrayList<Approval>();
                while(rs.next()){
                    //print to console and save in a txt file named reportName.txt
                    int expID = rs.getInt("id");
                    int userID = rs.getInt("user_id");
                    float amount = rs.getFloat("amount");
                    String desc = rs.getString("description");
                    String expDate = rs.getString("date");
                    String status = rs.getString("status");
                    int revID = rs.getInt("reviewer");
                    String comment = rs.getString("comment");
                    String revDate = rs.getString("review_date");
                    Expense e = new Expense(expID, userID, amount, desc, expDate);
                    Approval a = new Approval(status, revID, comment, revDate);
                    eList.add(e);
                    aList.add(a);
                }
                Pair<List<Expense>, List<Approval>> reports = new Pair<>(eList, aList);
                return reports;
            }
        }catch(SQLException e){
            logger.error("SQLException in generateReport");
            logger.error(e.getMessage());
            return null;
        }
        return null;
    }

    public void printUser(ResultSet rs) throws SQLException{
        String formatR = "| %-3d | %-20s | %-8s |";
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String role = rs.getString("role");
        String row = String.format(formatR, id, username, role);
        System.out.println(row);
    }

    public int displayUsers(){
        //show all usernames and ids (NOT PASSWORDS)
        try(Connection conn = Util.connect(dbPath)){
            if(conn != null) {
                String getUsers = "SELECT * FROM users WHERE role='Employee'";
                Statement s1 = conn.createStatement();
                ResultSet rs = s1.executeQuery(getUsers);
                if(!rs.isBeforeFirst()){
                    System.out.println("No users available");
                    return -1;
                }
                String formatH = "| %-3s | %-20s | %-8s |";
                String header = String.format(formatH, "ID", "Username", "Role");
                System.out.println(header);
                while(rs.next()){
                    printUser(rs);
                }
                return 1;
            }
        }catch(SQLException e){
            logger.error("SQL error in display users");
            return -1;
        }
        return -1;
    }
}
