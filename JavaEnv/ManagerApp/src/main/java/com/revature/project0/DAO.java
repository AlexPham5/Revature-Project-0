package com.revature.project0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

//Will execute the SQL commands after ManagerApp verifies the inputs
public class DAO {
    final private static String dbPath = "jdbc:sqlite:C:\\Users\\alex1\\Revature_work\\Project_0\\RevatureDatabase.db";
    Logger logger = LoggerFactory.getLogger(ManagerApp.class);

    public DAO(){
    }

    //returns -1 if the id doesn't exists in expenses, otherwise return 1
    public int checkID(int id, String tablename){
        try(Connection conn = DriverManager.getConnection(dbPath)) {
            if (conn != null) {
                String check = String.format("SELECT * FROM %s WHERE id=?", tablename);
                PreparedStatement ps = conn.prepareStatement(check);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (tablename.equals("expenses") && rs.next()) {
                    //Show expense to be edited
                    System.out.println("Expense Chosen: ");
                    printExpense(rs);
                    return 1;
                }
                else if(tablename.equals("users") && rs.next()){
                    System.out.println("User ID Chosen: ");
                    printUser(rs);
                    return 1;
                }
                else
                    return -1;
            }
        }catch(SQLException e){
            logger.error("SQLException in checkID");
            logger.error(e.getMessage());
            System.out.println("SQL Error when verifying ID\n");
            return -1;
        }
        return -1;
    }

    //returns -1 if unsuccessful, otherwise returns the manager's id
    public int getCredentials(String username, String password){
        logger.info("Attempting to find manager user credential's in db");
        try(Connection conn = DriverManager.getConnection(dbPath)){
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
    public int displayExpenses(){
        logger.info("Attempting to display expenses");
        try(Connection conn = DriverManager.getConnection(dbPath)){
            if(conn != null) {
                logger.info("SQL connection successful for displayExpenses");
                //Get all the expense_id from approvals where status is pending
                String getEIDs = "SELECT expense_id FROM approvals WHERE status='pending'";
                Statement s1 = conn.createStatement();
                ResultSet eIDs = s1.executeQuery(getEIDs);
                if (!eIDs.isBeforeFirst()) {
                    //no expenses found
                    System.out.println("NO EXPENSES CURRENTLY PENDING");
                    return 1;
                }
                //Format for table printing
                String formatH = "| %-3s | %-7s | %-10s | %-25s | %-10s | %-8s |";
                String header = String.format(formatH, "ID", "User_ID", "Amount", "Description", "Date", "Status");
                System.out.println(header);
                String formatR = "| %-3d | %-7d | $%-9.2f | %-25s | %-10s | %-8s |";
                while (eIDs.next()) {
                    //for every expense_id, get the corresponding expense
                    String displayExpense = "SELECT * FROM expenses WHERE id=?";
                    PreparedStatement p1 = conn.prepareStatement(displayExpense);
                    p1.setInt(1, eIDs.getInt("expense_id"));
                    ResultSet expense = p1.executeQuery();
                    //Display the expense's values and status in a table format
                    int id = expense.getInt("id");
                    int user_id = expense.getInt("user_id");
                    float amount = expense.getFloat("amount");
                    String desc = expense.getString("description");
                    String date = expense.getString("date");
                    String row = String.format(formatR, id, user_id, amount, desc, date, "PENDING");
                    System.out.println(row);
                }
                logger.info("Successfully displayed all pending expenses");
                return 1;
            }
        }catch(SQLException e){
            logger.error("SQLException in displayExpenses");
            logger.error(e.getMessage());
            return -1;
        }
        return -1;
    }

    //set an expense as approved or denied, make comments, record date and manager id
    //return 1 on success, -1 on failure
    public int editApproval(int eID, String status, int manID, String comment){
        try(Connection conn = DriverManager.getConnection(dbPath)){
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
                    logger.info("Expense successfully reviewed");
                    return -1;
                }
                else {
                    logger.error("Expense update unsuccessful");
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
    public int generateReport(int user, String dateS, String dateE, String keyword, String reportName){
        try(Connection conn = DriverManager.getConnection(dbPath)) {
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
                    logger.info("No records found for criteria specified, no report generated");
                    return 2;
                }
                //Printing and saving the report
                System.out.println("Report Name: "+reportName);
                String formatH = "| %-3s | %-7s | %-9s | %-25s | %-12s | %-8s | %-11s | %-25s | %-12s |";
                String header = String.format(formatH, "ID", "User_ID", "Amount", "Description", "Expense Date", "Status", "Reviewer_ID", "Comment", "Review Date");
                System.out.println(header);
                String formatR = "| %-3d | %-7d | %-9.2f | %-25s | %-12s | %-8s | %-11s | %-25s | %-12s |";
                //save to file
                try {
                    FileWriter fw = new FileWriter(reportName + ".txt");
                    fw.write("Report Name: "+reportName+"\n");
                    fw.write(header+"\n");
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
                        //revID, comment, and revDate can be null
                        String row = String.format(formatR, expID, userID, amount, desc, expDate, status, (revID==0 ? "N/A":Integer.toString(revID)),(comment==null ? "N/A":comment), (revDate==null ? "N/A":revDate));
                        System.out.println(row);
                        fw.write(row+"\n");
                    }
                    fw.close();
                    logger.info("Report successfully written to external file");
                }catch(IOException e){
                    logger.error("IOException in generateReport on FileWriter instantiation");
                    return -1;
                }
                return 1;
            }
        }catch(SQLException e){
            logger.error("SQLException in generateReport");
            logger.error(e.getMessage());
            return -1;
        }
        return -1;
    }

    public void printExpense(ResultSet rs) throws SQLException{
        String formatH = "| %-3s | %-7s | %-10s | %-25s | %-10s |";
        String header = String.format(formatH, "ID", "User_ID", "Amount", "Description", "Date");
        System.out.println(header);
        String formatR = "| %-3d | %-7d | $%-9.2f | %-25s | %-10s |";
        int eid = rs.getInt("id");
        int user_id = rs.getInt("user_id");
        float amount = rs.getFloat("amount");
        String desc = rs.getString("description");
        String date = rs.getString("date");
        String row = String.format(formatR, eid, user_id, amount, desc, date);
        System.out.println(row);
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
        try(Connection conn = DriverManager.getConnection(dbPath)){
            if(conn != null) {
                String getUsers = "SELECT * FROM users WHERE role='Employee'";
                Statement s1 = conn.createStatement();
                ResultSet rs = s1.executeQuery(getUsers);
                if(!rs.isBeforeFirst()){
                    System.out.println("No users availabe");
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
