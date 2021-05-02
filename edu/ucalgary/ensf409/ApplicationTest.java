package edu.ucalgary.ensf409;

import org.junit.Test;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.Scanner;
/**
 * Because we are testing different scenarios, each test is designed to be ran individually.
 * What this  means is that is a test is not running you have to comment it out
 * @author
 * @version
 * */
public class ApplicationTest {
	public ApplicationTest(){}
	
	
	/**
	 * A boundary case we have, is if the ID is empty/invalid. we check this case, by saying is the ID length < 1,
	 *  if so we expect the program to exit with a message on the console saying that that the IDis invalid 
	 *  and either displaying manufactures  or not in the case that no manufactures were found
	 *  To run this boundary case, you will need to modify your database to include a row that is -> ' ' <-
	 *  The test can be ran using any of the Assembly Classes. It runs on the assumption that the user enters a category that is on the database as well as a 
	 *  Type that is currently in the category, and a valid quantity mean >0 
	 *  We except a StringBuffer returns of nothing. This is because StringBuffer returns all the ID that was used in creating all furniture
	 * */
	@Test
	public void testInvalidID() 
	{
		System.out.println("testInvalidID: ");
		 ConnectToDatabase connecting;
		 String password = "ensf409";
		 String userName = "isaiah";
		connecting = new ConnectToDatabase(userName,password);
		StringBuffer parameter = new StringBuffer();
		// this expected String is only for type mesh
		String expected = "\nSuggested manufacturers are: Chairs R Us, Fine Office Supplies.";
		StringBuffer expectedParameter =new StringBuffer();
		String type="mesh";
		int quantity = 1; 
		connecting.initializeConnection();
		AssemblyChair testing = new AssemblyChair(connecting,type, quantity);
		try {
			String test_result =testing.createChair(parameter);
			System.out.println("The result after running the test output was " + test_result + " And the StringBuffer input return: " + parameter.toString());
			
			assertEquals( expected, test_result);
			assertEquals(expectedParameter.toString(),parameter.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Next we check what occurs when the user inputs a type that is not one we have already
	 * The way our program works is that based off the furniture we generated a 2d array of all the components needed to build that furniture
	 * and we use the type to select the right components, manufacturer and ID. 
	 * So if the type isn't one we have on the we can't possibly return a product or a recommend a manufacturer
	 * We except a StringBuffer returns of nothing. This is because StringBuffer returns all the ID that was used in creating all furniture
	 * */
	
	@Test
	public void testUserInput() 
	{
		System.out.println("testUserInput: ");
		 ConnectToDatabase connecting;
		 String password = "ensf409";
		 String userName = "isaiah";
		connecting = new ConnectToDatabase(userName,password);
		StringBuffer parameter = new StringBuffer();
		String expected = "\nWe couldn't create a furniture of type high, and there were no manufacuter name(s) found on the database.";
		StringBuffer expectedParameter =new StringBuffer();
		String type="high";
		int quantity = 1; 
		connecting.initializeConnection();
		AssemblyChair testing = new AssemblyChair(connecting,type, quantity);
		try {
			String test_result =testing.createChair(parameter);
			System.out.println("The result after running the test output was " + test_result + " And the StringBuffer input return: " + parameter.toString());
			
			assertEquals( expected, test_result);
			assertEquals(expectedParameter.toString(),parameter.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
/**
 * The next test if for when we can't connect to the inventory database
 * Because we don't want to mess with the program, we will be entering an incorrect dburl
 * This test focuses on the catch exception That is meant to catch sqlException
 * The test is ran on the ConncectToDatabase Class
 * This class doesn't return anything, it just prints the statement  "The dburl " + DBURL + " is invalid for this program. Please try again"
 * */
	@Test
	public void testInvalidDatabase() 
	{
		System.out.println("testInvalidDatabase: ");
		// this is meant to simulate if the user doesn't have inventory.sql on their computer or the name was changed
	     String DBURL = "jdbc:mysql://localhost/pets";
		 ConnectToDatabase connecting;
		 String password = "ensf409";
		 String userName = "isaiah";
		connecting = new ConnectToDatabase(DBURL,userName,password);
	}

}
