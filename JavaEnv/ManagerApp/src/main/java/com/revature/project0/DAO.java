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

    //returns -1 if the id doesn't exists, otherwise return 1
    public int checkIDExists(int id, String tablename){
        return -1;
    }

    //returns -1 if unsuccessful, otherwise returns the manager's id
    public int getCredentials(String username, String password){
        logger.info("Attempting to find manager user credential's in db");
        try(Connection conn = DriverManager.getConnection(dbPath)){
            if(conn != null){
                logger.info("SQL connection successful");
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
}
