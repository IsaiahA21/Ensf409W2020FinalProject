package edu.ucalgary.ensf409;

import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.*;

/**
 * The class Application serves has our main class and was created as part of 
 * ENSF 409 W21 project.
 * 
 * The user is asked to enter their username and password to connect to the database
 * 
 * The program begins by asking the user to enter an order. 
 * The user input should be in form of: furnitureType furniture,quantity
 * For example, mesh chair, 1
 * For the example above, mesh is turned to an lowercase letter even if it's already all lowercase
 * Same thing is turned with chair. The leading and trailing zeros of 1 are trimed
 * 
 * If it doesn't match we write to file saying there was a problem with the user input
 * If it does match, check if the category/furniture matches anyone of the category we have in our inventory
 * If it doesn't we write to the console saying that the category isn't in our inventory.sql
 * However, if the category one of those in inventory, we go to the class corresponding to that inventory
 * If program is successful to find the cheapest way to complete the order, order is output to file
 * If not then message is output suggesting manufacturers to get the order
 *
 * Once all this is done program will end, simply run program again to complete a new order
 *
 * @author Ethan Conrad, Isaiah Asaolu, Tsenguun Ulambayar
 * @version 1.2
 * */
public class Application {
	static String writeOuput = "Furniture Order Form"
			+ "\r\n"
			+ "Faculty Name:\r\n"
			+ "Contact:\r\n"
			+ "Date:\r\n"
			+ "\n";
	static StringBuffer allUsedID= new StringBuffer(); 

	/**
	* program runs from main to get user input and output a cheapest combination of items 
	* to order.
	* @param args ignored command line argument
	* @throws Exception
	*/
	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in); //reads input from console
		ConnectToDatabase connecting;
		String password = null;
		String username = null;
		 
		System.out.println("To access the inventory database, please enter your username and password.");
		System.out.println("Enter your username: ");
		username = input.nextLine();//set userName to input
		System.out.println("Enter your password: ");
		password = input.nextLine();//set password to input

		connecting = new ConnectToDatabase(username,password);//create new database connection
		
		FileWriter FW = null; //This FileWriter will be used to write into the orderform text file

		String inputRegex = "(.*) (.*),(.*)";

		File order = new File("GroupOrderFile.txt"); //this will be the orderform file
		
		
		try {
			FW = new FileWriter(order); //connecting the file with the file writer
		} catch (IOException e) {
			e.printStackTrace();
		}

		String type =null;
		String category =null;
		int quantity = 0;
		

		System.out.print("Enter order (type category, quantity): ");
		String UserInfo = input.nextLine();  // Read user input
		Pattern inputPattern = Pattern.compile(inputRegex);
		Matcher match = inputPattern.matcher(UserInfo);

		//give error message if all three inputs are not found, else assigns inputs to local variables
		if (!match.find()){
		    System.out.println("couldn't find all 3 parameters from the user input");
		    try {
				//write into orderform file    
				FW.write("couldn't find all 3 parameters from the user input");
			    FW.close();
		    } catch (IOException e) {
				e.printStackTrace();
			}
		    System.exit(1);
		}else {
		    type = match.group(1).toLowerCase(); //set type to first input in lowercase
		    category = match.group(2).toLowerCase();//set category to second input in lowercase
		    quantity = Integer.parseInt(match.group(3).trim()); //set quanity to last input as integer
		}
        //Output user input
		System.out.println("User input is: " + type+ " "+category+", "+ Integer.toString(quantity));
		//write user input into orderform
		writeOuput += "Original Request: "+ type+ " "+category+", "+ Integer.toString(quantity)+"\n" ;
		
		if (quantity < 1) {
			writeOuput += "\n \"Error: The user quantity is invalid. Please enter a quantity greater than 0.";
			throw new Exception ("Error: The user quantity is invalid. Please enter a quantity greater than 0.");
		} 
		else {
		//will create furniture items until the requested quantity is fulfilled
			
			createFurniture
			(connecting,category,type,quantity);	    

		connecting.initializeConnection();
		Statement stmt = connecting.myConnect.createStatement();
		wipeDatabase(allUsedID,stmt);
		}
   	    try {		    
		    FW.write(writeOuput); //write into the orderfile everything in writeOuput
   		    connecting.myConnect.close(); //closing the connection
			FW.close(); //closing the FileWriter
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * createFurniture is the method that will perform the calls to the Assembly classes. 
	 * It will recieve and output that consist of both the ID,and price of the furniture
	 * It also calls the method emptyDatabase, which is in every assembly class and 
	 * what this method does is check if the databse has been used and has no manufacturer
	 * The variable writOutput contents will be written into the output file
	 * @param connecting ConnectToDatabase object for connecting to inventory
	 * @param category String that indicates the furniture category
	 * @param type String that indicates type
	 * @param quantity Integer that indicates the desired quantity of requested furniture item
	 */
	private static void createFurniture
	(ConnectToDatabase connecting, String category, String type, int quantity) {
		int run =0;
		String res ="";
		
	    while (run < quantity) {
			connecting.initializeConnection();
		    run++;
			try {
	    	switch(category) {
				//if category is chair it will create chair and write into the orderform
				//file accordingly
	    		case "chair": 
	    			System.out.println("Assemblying a chair");
	    			AssemblyChair myChair = new AssemblyChair(connecting,type, quantity);

					res = myChair.createChair(allUsedID);

	    			if (myChair.emptyDatabase() == true) {
	    				writeOuput += "\n\nCould only create " + (run-1) +  " " + type + " " + category + "(s).";
		    			writeOuput += res;
		    			return;
	    			} else {
	    			writeOuput +=  "\n\nItem(s) needed to create "+type + " " + category + " " + run + " are: ";
	    			writeOuput += res;
	    			}
	    			break;
	    		case "desk": //similarly, if category is desk
	    			System.out.println("Assemblying a desk");
	    			AssemblyDesk myDesk = new AssemblyDesk(connecting,type, quantity);
	    			res = myDesk.createDesk(allUsedID);
	    			if (myDesk.emptyDatabase() == true) {
	    				writeOuput += "\n\nCould only create " + (run-1) +  " " + type + " " + category + "(s).";
		    			writeOuput += res;
		    			return;
	    			} else {
	    			writeOuput +=  "\n\nItem(s) needed to create "+type + " " + category + " " + run + " are: ";
	    			writeOuput += res;
	    			}
	    			break;
				case "lamp": //similarly, if category is lamp
	    			System.out.println("Assemblying a lamp");
	    			AssemblyLamp myLamp = new AssemblyLamp(connecting,type, quantity);
	    			res = myLamp.createLamp(allUsedID);
	    			if (myLamp.emptyDatabase() == true) {
	    				writeOuput += "\n\nCould only create " + (run-1) +  " " + type + " " + category + "(s).";
		    			writeOuput += res;
		    			return;
	    			} else {
	    			writeOuput +=  "\n\nItem(s) needed to create "+type + " " + category + " " + run + " are: ";
	    			writeOuput += res;
	    			}
	    			break;
				case "filing": //similarly, if category is filing
	    			System.out.println("Assemblying filing");
	    			AssemblyFiling myFiling = new AssemblyFiling(connecting,type, quantity);
	    			res =myFiling.createFiling(allUsedID);
	    			if (myFiling.emptyDatabase() == true) {
	    				writeOuput += "\n\nCould only create " + (run-1) +  " " + type + " " + category + "(s).";
		    			writeOuput += res;
		    			return;
	    			} else {
	    			writeOuput +=  "\n\nItem(s) needed to create "+type + " " + category + " " + run + " are: ";
	    			writeOuput += res;
	    			}
	    			break;
	    		default: // default case to catch errors
	    			System.out.println(category + " isn't on inventory.sql");
	    			writeOuput += category + " isn't on inventory.sql";
	    	}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }		
	}
	/**
	 * wipeDatabase will receives a StringBuffer containning a list of all the ID used to create the furniuture
	 * Aswell has a connection to the database. This method will then delete the Row based on the ID.
	 * @param allUsedID StringBuffer with all used IDs
	 * @param stmt Statement
	 */
	public static void wipeDatabase(StringBuffer allUsedID, Statement stmt) 
	{
		String extractComponent_ID = "([a-zA-Z]+).*[ ]([a-zA-Z0-9]+)";
		Pattern myPattern = Pattern.compile(extractComponent_ID);
		String tempString ="";
		try {
		 for (int i=0; i< allUsedID.toString().length(); i++) {
			if (allUsedID.toString().charAt(i) != '\n') { 
				tempString +=allUsedID.toString().charAt(i);
			} 
			else 
			{				
				Matcher theMatch = myPattern.matcher(tempString);
				if (theMatch.find()) {
					stmt.executeUpdate("DELETE FROM chair WHERE ID = "+ '"' +theMatch.group(2)+ '"');
				}
				tempString ="";
				//The database has now been updated
			}
		 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println();
	}
	
}
