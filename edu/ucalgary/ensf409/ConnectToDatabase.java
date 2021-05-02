package edu.ucalgary.ensf409;

import java.sql.*;

public class ConnectToDatabase {
	
    public  String DBURL = "jdbc:mysql://localhost/inventory"; //store the database url information
    public final String USERNAME; //store the user's account username
    public final String PASSWORD; //store the user's account password
    
    Connection myConnect;

    
    // constructor 
    public ConnectToDatabase(String username, String password) {
		this.USERNAME = username;
		this.PASSWORD = password;
    }
    
    public ConnectToDatabase(String dburl, String username, String password) {
    	boolean in = check(dburl);
    	if (in == false) {
    		System.out.println("The dburl " + dburl + " is invalid for this program. Please try again");
    		System.exit(1);
    	   }
		this.DBURL = dburl;
		this.USERNAME = username;
		this.PASSWORD = password;
    }

	private boolean check(String dburl2) {
		if (dburl2.equals(DBURL) == false ) {
			return false;
		}
		return true;
	}

	public void initializeConnection() {
		try { 
			 myConnect = DriverManager.getConnection(DBURL,USERNAME,PASSWORD);
		} catch (SQLException e) {
			System.out.println("error connecting to the database with the  provide URL " + DBURL +" with userName " + USERNAME + "with password " + PASSWORD);
			e.printStackTrace();
		}			
	}
}
