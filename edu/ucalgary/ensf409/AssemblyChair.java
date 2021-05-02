package edu.ucalgary.ensf409;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.util.*;
/**
 * Class AssemblyChair was created as part of ENSF 409 W21 project. It contains a public method called createChair()
 * that uses other private methods to navigate the specified inventory database and find the cheapest 
 * combination of chair items whose parts can be used to create one or more functional chairs. 
 * @author Ethan Conrad, Isaiah Asaolu, Tsenguun Ulambayar
 * @version 1.2
 */
public class AssemblyChair {
	private  String type; //contains the type of chair
	private int quantity;//how many chairs are requested
	private  int max=0; 
	 
	private String[][] allLegs;
	private String[][] allArms;
	private String[][] allSeat;
	private String[][] allCushion;
	private String allManufacturer =null; //the manufacturers that will be given if order cannot be fulfilled
	  
	private String[] returnResult;
	private	Statement stmt=null; //stmt will be used to interact with the database
	private ConnectToDatabase accessDatabase =null; //accessDatabase will connect to the database 
	private  StringBuffer output = new StringBuffer();
	private boolean emptyOrNah =false; 
	
	/**
	 * This method will return true if the database is empty or could create a chair
	 * It does this using the emptyOrNah boolean*/
	public boolean emptyDatabase() {
		return emptyOrNah;
	}
	
	/**
	 * closeThings is a private helper method used to close the statement and the connection to database 
	 * in a try block. Returns void.
	 */
	private void closeThings() 
	{
			try {
				accessDatabase.myConnect.close();//closes the accessDatabase connection
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Constructor for AssemblyChair class. It accepts three parameters to set the values 
	 * for accessDatabase, type, quantity, and returnResult.
	 * @param connecting The ConnectToDatabase object that will be assigned to accessDatabase.
	 * @param type String that will be assigned to type.
	 * @param quantity Integer that will be assigned to quantity. It will also be used to 
	 * assign returnResult to a new string of length quantity.
	 */
	public AssemblyChair(ConnectToDatabase connecting, String type, int quantity) 
	{
		this.accessDatabase = connecting;
		this.type = type;
		this.quantity = quantity;
		returnResult = new String[this.quantity];
		setStmt(); //calls setStmt() which sets the stmt value
	}
	
	/**
	 * sqlWrite is a private helper method that updates the columns in the database using the
	 * input string.
	 * @param writeUpdate String input that consists of multiple lines. Each line is in
	 * the form <Component from: theID>. The lines will be split using regex.
	 * It will change the database to 'N' indicating that we can't use does components again
	 */
	private void sqlWrite(String writeUpdate) 
	{
		String extractComponent_ID = "([a-zA-Z]+).*[ ]([a-zA-Z0-9]+)";
		Pattern myPattern = Pattern.compile(extractComponent_ID);
		String tempString ="";
		try {
		 for (int i=0; i< writeUpdate.length(); i++) {
			if (writeUpdate.charAt(i) != '\n') { 
				tempString +=writeUpdate.charAt(i);
			} 
			else 
			{				
				Matcher theMatch = myPattern.matcher(tempString);
				if (theMatch.find()) {
					stmt.executeUpdate("UPDATE chair set " + theMatch.group(1) +" = " + '"' + 'N' +'"' +" WHERE ID = "+ '"' +theMatch.group(2)+ '"');
//					stmt.executeUpdate("UPDATE chair set Legs  = " + '"' + 'N' +'"' +", Arms  = " + '"' + 'N' +'"' + ", Seat  = " + '"' + 'N' +'"' +", Cushion  = " + '"' + 'N' +'"' +" WHERE ID = "+ '"' +theMatch.group(2)+ '"');
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
	/**
	 * setStmt() is a private helper method for createChair(). It sets Statement object, stmt,
	 * by calling createStatement() for the current ConnectToDatabase object, accessDatabase, in
	 * a try block. Returns void.
	 */
	private void setStmt() 
	{
		try {
			 stmt = accessDatabase.myConnect.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	/**
	 * createChair is the method that calls other private methods in AssemblyChair to
	 * find the cheapest combination of chair items in the inventory.
	 * @param allUsedID 
	 * @return String that will be written into the orderform text file.
	 * @throws Exception
	 */
	public String createChair(StringBuffer allUsedID) throws Exception {
		// first I going to create 2D array of each column(leg arms, etc). 
		//Wherever there's a is a Y it gets added to the 2d String
		allLegs = leg2D();
		allArms = arm2D();
		allSeat = seat2D();
		allCushion = cushion2D();
		allManufacturer = manufacturer();
				
		// check I am checking if an ID shows up in each 2D array
		//  allID is a 2d array. The first row will consist of the IDs and the second row will contain the occurrence
		String[][] allID = getAllID();
		
		if (allManufacturer == null && (allLegs == null || allArms== null || allSeat== null || allCushion == null || allID == null)) {
			System.out.println("No manufactuers");
			emptyOrNah = true;
			return "\nWe couldn't create a furniture of type " + this.type +", and there were no manufacuter name(s) found on the database.";
		}
		// if any one of the method is empty we are going to return and recommend a store instead
		if (allLegs == null || allArms== null || allSeat== null || allCushion == null || allID == null) {
			emptyOrNah = true;
			System.out.println(allManufacturer);
			return allManufacturer;
		} 
		
		//looking for the most y
		for (int col =0; col< allID[0].length; col++) { 		
		//checking inside Leg first
			for (int a=0; a<allLegs.length; a++) {
				if (allID[0][col].equals(allLegs[a][0])) {
					int tempInt=0;
					if (allID[1][col] != null) {
						tempInt = Integer.parseInt(allID[1][col]) + 1;
					} else {
						tempInt=1;
					}
					allID[1][col] = String.valueOf(tempInt);
				} 
				else {
					int makeZero=0;
					if (allID[1][col] != null) {
						makeZero = Integer.parseInt(allID[1][col]) + 0;
					} else {
						makeZero=0;
					}
					allID[1][col] = String.valueOf(makeZero);

			}
			  }
			  
			  // next, checking inside of Arms 
			  for (int a=0; a<allArms.length; a++) {
				if (allID[0][col].equals(allArms[a][0])) {					
					int tempInt=0;
					if (allID[1][col] != null) {
						tempInt = Integer.parseInt(allID[1][col]) + 1;
					} else {
						tempInt=1;
					}
					allID[1][col] = String.valueOf(tempInt);
				} else {
					int makeZero=0;
					if (allID[1][col] != null) {
						makeZero = Integer.parseInt(allID[1][col]) + 0;
					} else {
						makeZero=0;
					}
					allID[1][col] = String.valueOf(makeZero);

				}
			  }
			  
			  // next, checking inside seats
			  for (int a=0; a<allSeat.length; a++) 
			  {
				if (allID[0][col].equals(allSeat[a][0])) {
					int tempInt=0;
					if (allID[1][col] != null) {
						tempInt = Integer.parseInt(allID[1][col]) + 1;
					} else {
						tempInt=1;
					}
					allID[1][col] = String.valueOf(tempInt);
				} else {
					int makeZero=0;
					if (allID[1][col] != null) {
						makeZero = Integer.parseInt(allID[1][col]) + 0;
					} else {
						makeZero=0;
					}
					allID[1][col] = String.valueOf(makeZero);

				}
			  }
			  
			  // next checking inside cushion
			  for (int a=0; a<allCushion.length; a++) {
				  
				if (allID[0][col].equals(allCushion[a][0])) {					
					int tempInt=0;
					if (allID[1][col] != null) {
						tempInt = Integer.parseInt(allID[1][col]) + 1;
					} else {
						tempInt=1;
					}
					allID[1][col] = String.valueOf(tempInt);
				} 
				else {
					int makeZero=0;
					if (allID[1][col] != null) {
						makeZero = Integer.parseInt(allID[1][col]) + 0;
					} else {
						makeZero=0;
					} 
					allID[1][col] = String.valueOf(makeZero);
				}
			  }
			  
			}
			//Looking for which ID shows has the most yes's
				
		// Now, after we are done checking to we see which ID has the most yes's
		
		// Now, we look for which ID has the most yes
		// IF there are multiple IDs with the same amount of yes we have to compare then and see which one produces the cheapest chair
		StringBuffer stringMax = new StringBuffer();
		int[] large  = largest(allID,stringMax);	  
		max = Integer.parseInt(stringMax.toString()); 
		int howManyMax =0;
		if (max ==0) {
			System.out.println("There is no possible chair furniture of type " + type + " that can be created because every ID has no available parts");
			return null;
		}
		else if (max == 1) {
			//All ID have one Y each There's possibly 1 combination possible
			howManyMax++;
		} 
		else {
			for (int i =0; i < large.length; i++) {
				if(large[i] >0 ) {
			   howManyMax++;
				}
			}
		}
		 
		// now we use the amount of many to look for the lowest chair 

	if (howManyMax == 1) 
	{
		HelpCreateChair createHelp = new HelpCreateChair(type,allLegs, allArms, allSeat, allCushion);
		int totalPrice =0;
		String retrieveIDOfMax = findID(max,allID);
		
		try {
		ResultSet resultPrice = stmt.executeQuery("SELECT Price FROM chair WHERE ID = "+ '"' +retrieveIDOfMax+ '"');
		
			if (resultPrice.next())
				totalPrice =  Integer.parseInt(resultPrice.getString("Price").toString());
		
			resultPrice.close();
			
			if (totalPrice < 0) {
				System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
				return "\nInvalid price "+ getManufacturer();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		int price = createHelp.constructChair(retrieveIDOfMax, totalPrice,allID, getLeg(), getArm(), getSeat(),getCushion());
		
		if (price < 0) {
			System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
			return "Invalid price "+ getManufacturer();
		}  
		
		String outPutID = createHelp.returnIDs();
		String writeUpdate = createHelp.getDatabaseUpdateString();
	
		allUsedID.append(writeUpdate);
	    sqlWrite(writeUpdate);
	      
		System.out.println(outPutID + "\nThe total price is: $" + price);
		return (outPutID + "\n\nThe total price is: $" + price);
	} 
	
		
	/*We have finished the case when there's only one max
	 * Now we move on to what if multiple rows that have the same length
	 */
	// if there are multiple rows with the same max length we form both and compare the price, then choose the lowest 1, if it's still tied we choose the first
	
	else if (howManyMax >1)
	{
		int[] comparePrice = new int[howManyMax];
		String[] outputIDs = new String[howManyMax];
		String[] writeUpdate = new String[howManyMax];
		String stringFinalOutput ="";

		// Initialize them 
		for (int x =0; x <howManyMax; x++) {
			
	        String [][] sortedAllID = sort2DArray(allID);
	        String iDMax = allID[0][x];
	        
	        int price = 0;
	        
	        try {
	        ResultSet resultPrice = stmt.executeQuery("SELECT Price FROM chair WHERE ID = "+ '"' +iDMax+ '"');

	        if (resultPrice.next())

	        	price =  Integer.parseInt(resultPrice.getString("Price").toString());
	        
	        resultPrice.close();	
			if (price < 0) {
				System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
				return "\nInvalid price "+ getManufacturer();
			}  
			
	        } catch (SQLException e) {
	        	e.printStackTrace();
	        } catch (NumberFormatException e) {
	        	e.printStackTrace();
	        }
			HelpCreateChair createHelp = new HelpCreateChair(type,allLegs, allArms, allSeat, allCushion);
			comparePrice[x] = createHelp.constructChair(iDMax, price,sortedAllID, getLeg(), getArm(), getSeat(),getCushion());
			
			if (comparePrice[x] <0) {
				System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
				return allManufacturer;
			}
			
			outputIDs[x] = createHelp.returnIDs();
			writeUpdate[x] = createHelp.getDatabaseUpdateString();
			
			//System.out.println("The " + x  + "in the compare Array of ID with max ID is " + iDMax + " and it's price is " + (comparePrice[x]));

		}
		
        int i;
        
        // Initialize maximum element
        int maximum = comparePrice[0];
        stringFinalOutput = outputIDs[0];
        String chooseToUpdate = writeUpdate[0];
        
        // Traverse array elements from second and
        // compare every element with current max  
        for (i = 1; i < comparePrice.length; i++) {
            if (comparePrice[i] < maximum) {
            	maximum = comparePrice[i];
                stringFinalOutput = outputIDs[i];
                chooseToUpdate = writeUpdate[i];
            }
        }
        
        allUsedID.append(chooseToUpdate);
      sqlWrite(chooseToUpdate);
        
	      System.out.println();
	      
			System.out.println(stringFinalOutput + "\nThe total price is: $" + maximum);
			closeThings();
		      
			return (stringFinalOutput + "\nThe total price is: $" + maximum);

	} 
	else {
		System.out.println("why are we here?");
	}
	closeThings();
		
		return "error";
	}

    /**
	 * sort2DArray is a private helper method that sorts a 2D array in
	 * ascending order.
	 * @param allID 2D string array that will be rearranged in ascending order.
	 * @return The rearranged 2D array.
	 */
	private  String[][] sort2DArray(String[][] allID)
	{
		String[][] ret2D;
        int temp = 0;    
        String tempID = "";
	        //Sort the array in ascending order using two for loops    
	        for (int i = 0; i <allID[1].length; i++) {     
	          for (int j = i+1; j <allID[1].length; j++) {     
	              if((Integer.parseInt(allID[1][i])) < (Integer.parseInt(allID[1][j])) ) {      //swap elements if not in order
	                 
	            	 temp = (Integer.parseInt(allID[1][i]));   
	                 tempID = allID[0][i];
	                 
	                allID[1][i] = allID[1][j]; 
	                allID[0][i] = allID[0][j];
	                
	                allID[1][j] = String.valueOf(temp); 
	                allID[0][j] = tempID;
	                
	               }     
	            }     
	        }    
		 ret2D= allID;
		return ret2D;
	}
    /**
	 * findID is a private helper method that searches a 2D string array of IDs
	 * to find a chair that has the max number of available parts.
	 * @param maxValue Integer that indicates the current maximum number of available
	 * parts in occurence.
	 * @param allID A 2D string array that holds the IDs of the chairs in inventory and
	 * the number of available parts each chair has.
	 * @return String that holds ID of the chair that holds the current maximum number 
	 * of available parts.
	 */
	private String findID(int maxValue, String[][] allID) 
	{
		//search the 2dArray of Object to find the maxvalue;
		for (int i =0; i < allID[1].length; i++) {
			if (Integer.parseInt(allID[1][i]) == maxValue ) {
				return allID[0][i];
			}
		}
		return null;
	}

	/**
	 * largest is a private helper method that finds the maximum occurence of parts that
	 * a chair can have and makes an integer array of the indexes of chairs that have the
	 * current maximum number of parts. 
	 * @param allID 2D string array that holds the chair IDs and their respective number of
	 * parts.
	 * @param stringMax StringBuffer where the local maximum is appended to.
	 * @return Integer array that returns the indexes of the chairs that have the most
	 * available parts.
	 */
    private int[] largest(String[][] allID, StringBuffer stringMax)
    {
    	int[] indexOFMostY = new int[allID[0].length]; 
        int i;
        // Initialize maximum element
        int localMax = Integer.parseInt(allID[1][0]);
       
        // Traverse array elements from second and
        // compare every element with current max  
        for (i = 1; i < allID[1].length; i++){
            if (Integer.parseInt(allID[1][i]) > localMax)
            	localMax = Integer.parseInt(allID[1][i]);
        }
        //System.out.println("Local most occurence is " + localMax);
        
       //finding if there are more than one max location. eg if max is 3, and 2 ID have the a occurrence of 3
       for (int k=0; k<allID[1].length; k++) {
           if (Integer.parseInt(allID[1][k]) == localMax) {
        	 //  System.out.println("allID[1]["+k+"]" + " is one of the max");
        	   indexOFMostY[k] = 1;
           }
       }
       stringMax.append(localMax);
        return indexOFMostY;
    }
	/**
	 * getAllID is a private helper method that creates and returns a 2D string array that
	 * holds the IDs of all chairs of the specified type and their respective prices.
	 * @return 2D string array that holds the IDs of all chairs of the specified type 
	 * and their respective prices.
	 */
	private String[][] getAllID() 
	{
		 ResultSet readID;
		 int countCol =0;
		 String[][] retIDs = null;

		 try {
			readID = stmt.executeQuery("SELECT ID FROM chair WHERE TYPE = " + '"' +this.type+ '"'  +  "ORDER BY Price ASC");
			while (readID.next()) {
				try {
				String ID = readID.getString("ID");
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				countCol++;
			}
			if (countCol ==0) {
				System.out.println("The allID() is empty. There aren't any IDs available");
				readID.close();
				return null;
			}
			
			readID = stmt.executeQuery("SELECT ID FROM chair WHERE TYPE = " + '"' +this.type+ '"'  +  "ORDER BY Price ASC");
			
			retIDs = new String[2][countCol];
			int Col=0;
			while (readID.next()) {
				retIDs[0][Col] = readID.getString("ID");
			    if (retIDs[0][Col].length() > 25 ||retIDs[0][Col].length() <1) {
			    	return null;
				}
				Col++;
	        }
			
			readID.close();
			return retIDs;		 
			
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		return null;
	}

    /**
	 * cushion2D is a private helper method that searches the inventory for chair IDs that are of
	 * the specified chair type and have useable cushions. Creates a 2D string array that holds the
	 * IDs of such chairs and their respective information.
	 * @return A 2D string array that holds the chair IDs of specified type that have useable 
	 * cushions and their respective information.
	 */
	private String[][] cushion2D() 
	{
		 ResultSet readCushion;
		 ResultSet readCushion2;

		try {
			readCushion = stmt.executeQuery("SELECT ID,Legs,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Cushion = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           int numRows, numCols;

			ResultSet temp2 = readCushion;
			int count=0;
			
			while (temp2.next()) {
				try {
				String ID = temp2.getString("ID");
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			count++;
			}
			
			if (count ==0) {
//				System.out.println("The cushion2D() is empty. There aren't any Cushion available");
				readCushion.close();
				return null;
			}
			
			numRows = count;
			numCols = readCushion.getMetaData().getColumnCount();
			String[][] CushionData = new String[numRows][numCols];
       	
       	readCushion2 = stmt.executeQuery("SELECT ID,Legs,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Cushion = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           int row = 0;

   
			while (readCushion2.next()) {
	            for (int i = 0; i < numCols; i++) {
	            	CushionData[row][i] = (readCushion2.getObject(i+1)).toString();
	            }
	            if (checkIfValid(CushionData,row) == false) {
	            	return null;
	            }
	            row++;
	        }
			readCushion.close();
			readCushion2.close();
	        return CushionData;
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return null;
	}
	/**
	 * seat2D is a private helper method that searches the inventory for chair IDs that are of
	 * the specified chair type and have useable seats. Creates a 2D string array that holds the
	 * IDs of such chairs and their respective information.
	 * @return A 2D string array that holds the chair IDs of specified type that have useable 
	 * seats and their respective information.
	 */
	private String[][] seat2D() 
	{
		 ResultSet readSeats;
		 ResultSet readSeats2;

		try {
			readSeats = stmt.executeQuery("SELECT ID,Legs,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Seat = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           //retrieve number of columns and rows
           int numRows, numCols;

			ResultSet temp2 = readSeats;
			int count=0;
			
			while (temp2.next()) {
				try {
				String ID = temp2.getString("ID");
//				System.out.println(ID);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			count++;
			}
			
			if (count ==0) {
		//		System.out.println("There leg2D() is empty. There aren't any Arms available");
				readSeats.close();
				return null;
			}
			
			numRows = count;
			numCols = readSeats.getMetaData().getColumnCount();
//			System.out.println("In seats there are " + count + " Y's " + numCols); //for debugging purposes
			String[][] SeatsData = new String[numRows][numCols];
       	
       	readSeats2 = stmt.executeQuery("SELECT ID,Legs,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Seat = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           int row = 0;
   
			while (readSeats2.next()) {
	            for (int i = 0; i < numCols; i++) {
	            	SeatsData[row][i] = (readSeats2.getObject(i+1)).toString();
	            }
	            if (checkIfValid(SeatsData,row) == false) {
	            	return null;
	            }
	            row++;
	        }
			readSeats.close();
			readSeats2.close();
			return SeatsData;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	/**
	 * arm2D is a private helper method that searches the inventory for chair IDs that are of
	 * the specified chair type and have useable arms. Creates a 2D string array that holds the
	 * IDs of such chairs and their respective information.
	 * @return A 2D string array that holds the chair IDs of specified type that have useable 
	 * arms and their respective information.
	 */
	private String[][] arm2D() 
	{
		 ResultSet readArms;
		 ResultSet readArms2;

		try {
			readArms = stmt.executeQuery("SELECT ID,Legs,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Arms = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           //retrieve number of columns and rows
           int numRows, numCols;

			ResultSet temp2 = readArms;
			int count=0;
			
			while (temp2.next()) {
				try {
				String ID = temp2.getString("ID");
//				System.out.println(ID);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			count++;
			}
			if (count ==0) {
	//			System.out.println("The arm2D() is empty. There aren't any Arms available");
				readArms.close();
				return null;
			}
			numRows = count;
			numCols = readArms.getMetaData().getColumnCount();
			String[][] ArmsData = new String[numRows][numCols];
       	
       	readArms2 = stmt.executeQuery("SELECT ID,Legs,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Arms = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           int row = 0;
   
			while (readArms2.next()) {
	            for (int i = 0; i < numCols; i++) {
	            	ArmsData[row][i] = (readArms2.getObject(i+1)).toString();
	            }
	            if (checkIfValid(ArmsData,row) == false) {
	            	return null;
	            }
	            row++;
	        }
			readArms.close();
	        return ArmsData;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

		return null;
	}
    /**
	 * leg2D is a private helper method that searches the inventory for chair IDs that are of
	 * the specified chair type and have useable legs. Creates a 2D string array that holds the
	 * IDs of such chairs and their respective information.
	 * @return A 2D string array that holds the chair IDs of specified type that have useable 
	 * legs and their respective information.
	 */
	private String[][] leg2D() 
	{
		 ResultSet readLegs;
		 ResultSet readLegs2;

		try {
			 readLegs = stmt.executeQuery("SELECT ID,Legs,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Legs = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
            //retrieve number of columns and rows
            int numRows, numCols;

			ResultSet temp2 = readLegs;
			int count=0;
			
			// counting the number of times ID shows up
			while (temp2.next()) {
				try {
				String ID = temp2.getString("ID");
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			count++;
			}
			
			if (count ==0) {
				readLegs.close();
				return null;
			}
			
			numRows = count;
			numCols = readLegs.getMetaData().getColumnCount();
			
			String[][] g_trainingdata = new String[numRows][numCols];
        	
        	readLegs2 = stmt.executeQuery("SELECT ID,Legs,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Legs = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");    
        	int row = 0;
			while (readLegs2.next()) {
	            for (int i = 0; i < numCols; i++) {
	            	g_trainingdata[row][i] = (readLegs2.getObject(i+1)).toString();
	            }
	            if (checkIfValid(g_trainingdata,row) == false) {
	            	return null;
	            }
	            row++;
	        }
			
			readLegs2.close();
	        return g_trainingdata;
		} 
		catch (SQLException e) {
			e.printStackTrace();

		}
		return null;
	}
	
	
	/**
	 * getLeg is a private helper method that gets the searches the inventory for 
	 * chairs of specified type with available legs, that have the lowest price and
	 * returns the ID of that chair and its price.
	 * @return String array containing the ID of chair with legs and its price.
	 */
	private String[] getLeg() 
	{
		ResultSet localResult;
		String[] retArr = new String[2];
		try {
			 localResult = stmt.executeQuery("SELECT ID,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Legs = " + '"' + "Y"  +'"'+" ORDER BY Price ASC LIMIT 1");
			
			if (localResult.next()) {

			retArr[0] = localResult.getString("ID");
			retArr[1] = localResult.getString("Price");
			
			if (checkIfValid(retArr) == false) {
				return null;
			}
			
			} else {
				retArr[1] = null;
				retArr[0] = null; 
			}
			
			localResult.close();
			return retArr;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * getArm is a private helper method that gets the searches the inventory for 
	 * chairs of specified type with available arms, that have the lowest price and
	 * returns the ID of that chair and its price.
	 * @return String array containing the ID of chair with arms and its price.
	 */
	private String[] getArm() 
	{
		String[] retArm = new String[2];
		try {
			ResultSet localResultArm = stmt.executeQuery("SELECT ID,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Arms = " + '"' + "Y"  +'"'+" ORDER BY Price ASC LIMIT 1");
			
			if (localResultArm.next()) {
				retArm[0] = localResultArm.getString("ID");
				retArm[1] = localResultArm.getString("Price");
				
				if (checkIfValid(retArm) == false) {
					return null;
				}

			} else {
				retArm[1] = null;
				retArm[0] = null;
			}
			
			localResultArm.close();
			return retArm;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;

		} 
//		return retArm;	
	}
    /**
	 * getSeat is a private helper method that gets the searches the inventory for 
	 * chairs of specified type with available seat, that have the lowest price and
	 * returns the ID of that chair and its price.
	 * @return String array containing the ID of chair with seat and its price.
	 */
	private String[] getSeat() 
	{
		String[] retSeat = new String[2];
		try {
			ResultSet localResultSeat = stmt.executeQuery("SELECT ID,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Seat = " + '"' + "Y"  +'"'+" ORDER BY Price ASC LIMIT 1");
			
			if (localResultSeat.next()) {

				retSeat[0] = localResultSeat.getString("ID");
				retSeat[1] = localResultSeat.getString("Price");

				if (checkIfValid(retSeat) == false) {
					return null;
				}
				
			} else {
				retSeat[1] = null;
				retSeat[0] = null;
			}
			
			localResultSeat.close();
			return retSeat;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		//return retSeat;	
	}
	/**
	 * getCushion is a private helper method that gets the searches the inventory for 
	 * chairs of specified type with available cushion, that have the lowest price and
	 * returns the ID of that chair and its price.
	 * @return String array containing the ID of chair with cushion and its price.
	 */
	private String[] getCushion() 
	{
		String[] retCushion = new String[2];
		try {
			ResultSet localResultCushion = stmt.executeQuery("SELECT ID,Price FROM chair WHERE TYPE = "+ '"' +this.type+ '"' +" AND Cushion = " + '"' + "Y"  +'"'+" ORDER BY Price ASC LIMIT 1");
			
			if (localResultCushion.next()) {
				retCushion[0] = localResultCushion.getString("ID");
				retCushion[1] = localResultCushion.getString("Price");
				
				if (checkIfValid(retCushion) == false) {
					return null;
				}
				
			} 
			else {
				retCushion[1] = null;
				retCushion[0] = null;
			}
			localResultCushion.close();
			return retCushion;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}		
	}
    /**
	 * manufacturer is a private helper method that searches the database for the names of the 
	 * manufacturers that supply a certain type of chair. 
	 * @return String of manufacturers that supply the specified chair type.
	 */
	private String manufacturer() 
	{
		String[] manu_for_specifiedType = null;
		int count=0;
		StringBuffer allManu = new StringBuffer();
		allManu.append("\nCould not fulfill order.\nSuggested manufacturers are: ");
		try {
			ResultSet manufacturer = stmt.executeQuery("SELECT ManuID FROM chair WHERE Type = "+ '"' +this.type+ '"' + " ORDER BY ManuID");
			while(manufacturer.next()) {
				count++;
			}
			if (count <= 0) {
				return null;
			}
			manu_for_specifiedType = new String[count];
			manufacturer = stmt.executeQuery("SELECT ManuID FROM chair WHERE Type = "+ '"' +this.type+ '"' + " ORDER BY ManuID");
        	int row = 0;
        	
			while (manufacturer.next()) {
	            	manu_for_specifiedType[row] = manufacturer.getString("ManuID");
	            	if (manu_for_specifiedType[row].length()>3 || manu_for_specifiedType[row].length() < 1) {
	            		return null;
	            	}
	            row++;
	        }
			
			// now we check if the array contains any duplicates
			// if there is we set it to null then we recalculate the array
			
			for (int i = 0; i < manu_for_specifiedType.length; i++) {
	            for (int j = i+1; j < manu_for_specifiedType.length; j++) {
					System.out.println(manu_for_specifiedType[i] + ", "+manu_for_specifiedType[j]);
	                if (manu_for_specifiedType[i].equals(manu_for_specifiedType[j]) && (i != j) && manu_for_specifiedType[i] != " " && manu_for_specifiedType[j] != " ") {
	                	manu_for_specifiedType[j] = " ";
	                }
	                }
	            System.out.println();
			}
			int newCount =0;
			for (int i=0; i < count; i++) {
				if (manu_for_specifiedType[i] != " ") {
					newCount++;
				}
			}
			// now we copy the into the new array
			String[] newManufactureID = new String[newCount];
			int i=0;
			for (int k=0; k < manu_for_specifiedType.length; k++) {
				if (manu_for_specifiedType[k] != " ") {
					newManufactureID[i] = manu_for_specifiedType[k];
					i++;
				}
			} 
			
			// next we get the the manufacturer names based off the ID
			
			for (int j=0; j<newManufactureID.length; j++ ) {
		        ResultSet Result = stmt.executeQuery("SELECT Name FROM manufacturer WHERE ManuID = "+ '"' +newManufactureID[j]+ '"');
		        Result.next();
		        String tem = Result.getString("Name");
//		        System.out.println("the manufacture name is "+tem + "and the manuID is: " + newManufactureID[j]);
		        if (tem.length() > 25 || tem.length() <1) {
		        	return null;
		        }
		        if (j+1 == newManufactureID.length) {
			        allManu.append(tem +".");
		        } else {
		        allManu.append(tem +", ");
		        }
			}
	        
			manufacturer.close();
			return allManu.toString();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch(NullPointerException x) {
			
		}
		return null;
	}
	/**
	 * getManufacturer is private helper method that is called when an order cannot be fulfilled.
	 * It returns allManufacturer. 
	 * @return String that holds the names of the manufacturers that supply chairs.
	 */
	private String getManufacturer() 
	{
		if (allManufacturer == null) {
			return "\nWe couldn't create a furniture of type " + this.type +", and there were no manufacuter name(s) found on the database.";
		}
		return allManufacturer;
	}
	
	/***
	 * checkIfValid is a private helper method that checks if the ID and price are valid
	 * @param input String array that holds the ID and price of a chair in inventory 
	 * @return Boolean indicating whether both the ID and price are valid or not.
	 */
	private boolean checkIfValid(String[] input) 
	{
        if (input[0].length() > 25 || input[0].length() <1) {
			System.out.println("Invalid ID. \nThe  ID " + input[0] + " is invalid.");
			return false;
		}
		if (Integer.parseInt(input[1]) <0) {
			System.out.println("Invalid integer. \nThe ID " + input[0] + " has an invalid price of " + input[1]);
			return false;
		}
		return true;

		
	}
 
	/**
	 * checkIfValid is a private helper method that checks if the IDs and prices of a row 
	 * in a 2D string array are valid.
	 * @param input 2D string array whose elements will be checked for validity. 
	 * @param row Integer that indicates the row of 2D string array that needs to be checked.
	 * @return Boolean indicating whether all IDs and Prices of a row of the 2D string array are
	 * valid.
	 */
	private boolean checkIfValid(String[][] input,int row) 
	{
        if (input[row][0].length() > 25 || input[row][0].length() <1) {
			System.out.println("Invalid ID. \nThe  ID " + input[0][0] + " is invalid.");
			return false;
		}
        if (input[row][1].length() > 25 || input[row][1].length() <1) {
			System.out.println("Invalid component in the ID " + input[0][0]);
			return false;
		}
		
		if (Integer.parseInt(input[row][2]) <0) {
			System.out.println("Invalid integer. \nThe ID " + input[0][0] + " has an invalid price of " + input[0][2]);
			return false;
		}
		return true;

		
	}

}
