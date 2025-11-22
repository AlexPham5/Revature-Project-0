package com.revature.project0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//Will execute the SQL commands after ManagerApp verifies the inputs
public class DAO {
    private String dbPath = "C:\\Users\\alex1\\Revature_work\\Project_0\\RevatureDatabase.db";
    Logger logger = LoggerFactory.getLogger(ManagerApp.class);

    public DAO(){
        //need logger?
    }

    //returns -1 if unsuccessful, otherwise returns the manager's id
    public int getCredentials(String username, String password){
        logger.info("Attempting to find manager user credential's in db");
        
        return -1;
    }
}
