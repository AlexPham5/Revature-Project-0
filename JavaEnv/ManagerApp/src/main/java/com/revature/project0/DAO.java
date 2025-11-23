package com.revature.project0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

//Will execute the SQL commands after ManagerApp verifies the inputs
public class DAO {
    private static String dbPath = "jdbc:sqlite:C:\\Users\\alex1\\Revature_work\\Project_0\\RevatureDatabase.db";
    Logger logger = LoggerFactory.getLogger(ManagerApp.class);

    public DAO(){
    }

    //returns -1 if the id doesn't exists in expenses, otherwise return 1
    public int checkID(int id){
        try(Connection conn = DriverManager.getConnection(dbPath)) {
            if (conn != null) {
                String check = "";
                return 1;
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
            logger.info("SQL connection successful for displayExpenses");
            //Get all the expense_id from approvals where status is pending
            String getEIDs = "SELECT expense_id FROM approvals WHERE status='pending'";
            Statement s1 = conn.createStatement();
            ResultSet eIDs = s1.executeQuery(getEIDs);
            if(!eIDs.next()){
                //no expenses found
                System.out.println("NO EXPENSES CURRENTLY PENDING");
                return 1;
            }
            //Format for table printing
            String formatH = "| %-3s | %-7s | %-10s | %-25s | %-10s | %-8s |";
            String header = String.format(formatH, "ID", "User_ID", "Amount", "Description", "Date", "Status");
            System.out.println(header);
            String formatR = "| %-3d | %-7d | $%-9.2f | %-25s | %-10s | %-8s |";
            while(eIDs.next()){
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
        }catch(SQLException e){
            logger.error("SQLException in getCredentials");
            logger.error(e.getMessage());
            return -1;
        }
    }

    //set an expense as approved or denied, make comments, record date and manager id
    public int reviewExpense(){
        return -1;
    }
}
