package com.revature.project0;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//Will execute the SQL commands after ManagerApp verifies the inputs
public class DAO {
    private String dbPath = "C:\\Users\\alex1\\Revature_work\\Project_0\\RevatureDatabase.db";

    public DAO(){
        //open db connection here? or just use with whenever opening connections?
    }

    //returns -1 if unsuccessful, otherwise returns the manager's id
    public int getCredentials(String username, String password){
        
        return -1;
    }
}
