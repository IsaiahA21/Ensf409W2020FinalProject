package edu.ucalgary.ensf409;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.util.*;
/**
 * Class AssemblyDesk was created as part of ENSF 409 W21 project. It contains a public method called createDesk()
 * that uses other private methods to navigate the specified inventory database and find the cheapest 
 * combination of desk items whose parts can be used to create one or more functional desks. 
 * @author Ethan Conrad, Isaiah Asaolu, Tsenguun Ulambayar
 * @version 1.2
 */
public class AssemblyDesk {
	private  String type;
	private int quantity;
	private  int max=0; 
	 
	private String[][] allLeg;
	private String[][] allTop;
	private  String[][] allDrawer;
	private String allManufacturer =null;
	  
	private String[] returnResult;
	private	Statement stmt=null;
	private ConnectToDatabase accessDatabase =null;
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
			accessDatabase.myConnect.close();
			stmt.close();
			//myConnect.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructor for AssemblyDesk class. It accepts three parameters to set the values 
	 * for accessDatabase, type, quantity, and returnResult.
	 * @param connecting The ConnectToDatabase object that will be assigned to accessDatabase.
	 * @param type String that will be assigned to type.
	 * @param quantity Integer that will be assigned to quantity. It will also be used to 
	 * assign returnResult to a new string of length quantity.
	 */
	public AssemblyDesk(ConnectToDatabase connecting, String type, int quantity) 
	{
		this.accessDatabase = connecting;
		this.type = type;
		this.quantity = quantity;
		
		returnResult = new String[this.quantity];
        setStmt();


	}
	
	/** 
	 * So over in the sqlWriteMethod, we will be updating the columns in the databases that have being selected.
	 * the input writeUpdate will include a long String input that will broken up into lines. Each line is in the form 
	 * <Component from: theID>. the line will be spilt using regex and we'll extract the component and ID and then use it to update the database
	 * */
	
	private void sqlWrite(String writeUpdate) 
	{
		String extractComponent_ID = "([a-zA-Z]+).*[ ]([a-zA-Z0-9]+)";
		Pattern myPattern = Pattern.compile(extractComponent_ID);
		System.out.println("Inside sqlWrite() now going to update the database");
		String tempString ="";
		try {
			
		 for (int i=0; i< writeUpdate.length(); i++) {
			if (writeUpdate.charAt(i) != '\n') { 
				tempString +=writeUpdate.charAt(i);
			} 
			else 
			{				
				System.out.println("The tempString is: " + tempString);
				Matcher theMatch = myPattern.matcher(tempString);
				if (theMatch.find()) {
					stmt.executeUpdate("UPDATE desk set " + theMatch.group(1) +" = " + '"' + 'N' +'"' +" WHERE ID = "+ '"' +theMatch.group(2)+ '"');
				}
				tempString ="";
				System.out.println("The database has now been updated");
			}
		 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println();
	}
	
	private void setStmt() 
	{
		try {
			 stmt = accessDatabase.myConnect.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	public String createDesk(StringBuffer allUsedID) throws Exception {
		System.out.println("inside AssemblyDesk class");

		// first I going to create 2D array of each column. Wherever there's a is a Y it gets added to the 2d String
		allLeg = leg2D();
		allTop = top2D();
		allDrawer = drawer2D();
		allManufacturer = manufacturer();
				
		// check I am checking if an ID shows up in each 2D array
		//  allID is a 2d array. The first row will consist of the IDs and the second row will contain the occurrence
		String[][] allID = getallID();
		
		if (allManufacturer == null && (allLeg == null || allTop== null || allDrawer== null || allID == null)) {
			System.out.println("No manufactuers");
			emptyOrNah = true;
			return "\nWe couldn't create a furniture of type " + this.type +", and there were no manufacuter name(s) found on the database.";
		}
		// if any one of the method is empty we are going to return and recommend a store instead
		if (allLeg == null || allTop== null || allDrawer== null || allID == null) {
			//System.out.println("it seems like one of the 2d array is empty");
			emptyOrNah = true;
			System.out.println(allManufacturer);
			return allManufacturer;
		} 
		
		//looking for the most y
		for (int col =0; col< allID[0].length; col++) { 		
		//checking inside Leg first
			for (int a=0; a<allLeg.length; a++) {
				if (allID[0][col].equals(allLeg[a][0])) {
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
			  
			  // next, checking inside of Top 
			  for (int a=0; a<allTop.length; a++) {
				if (allID[0][col].equals(allTop[a][0])) {					
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
			  
			  // next, checking inside Drawers
			  for (int a=0; a<allDrawer.length; a++) 
			  {
				if (allID[0][col].equals(allDrawer[a][0])) {					
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
			  
			  
		}
			  
			  
			  
	
		System.out.println("Looking for which ID shows has the most yes's");
				
		// Now, after we are done checking to we see which ID has the most yes's
		
			// Now, we look for which ID has the most yes
			// IF there are multiple IDs with the same amount of yes we have to compare then and see which one produces the cheapest desk
		StringBuffer stringMax = new StringBuffer();
		int[] large  = largest(allID,stringMax);	  
		max = Integer.parseInt(stringMax.toString())/*Integer.parseInt(allID[1][0].toString())*/; 
		System.out.println("the # of elements  is " + max );
		int howManyMax =0;
		if (max ==0) {
			System.out.println("There is no possible desk furniture of type " + type + " that can be created because every ID has no avaiable parts");
			System.out.println("We write that to file");
			return null;
		}
		else if (max == 1) {
			System.out.println("All ID have one Y each There's possibly 1 combination possible");
			   howManyMax++;
		} 
		else {
			for (int i =0; i < large.length; i++) {
				if(large[i] >0 ) {
			   System.out.println("The max ID is " + allID[0][i] + " with " + allID[1][i] + " occurences");
			   howManyMax++;
				}
			}
		}
		 
		// now we use the amount of many to look for the lowest desk 

	if (howManyMax == 1) 
	{
		HelpCreateDesk createHelp = new HelpCreateDesk(type,allLeg, allTop, allDrawer);
		int totalPrice =0;
		String retrieveIDOfMax = findID(max,allID);		
		try {
		ResultSet ResultPrice = stmt.executeQuery("SELECT Price FROM desk WHERE ID = "+ '"' +retrieveIDOfMax+ '"');
			if (ResultPrice.next())
				totalPrice =  Integer.parseInt(ResultPrice.getString("Price").toString());
			ResultPrice.close();
			
			if (totalPrice < 0) {
				System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
				return "Invalid price "+ getManufactuer();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		int price = createHelp.constructDesk(retrieveIDOfMax, totalPrice,allID, getLeg(), getTop(), getDrawer());
		
		if (price < 0) {
			System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
			return "\nInvalid price "+ getManufactuer();
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
		System.out.println("\n\nThere are " + howManyMax + " IDs that have max occurence");
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
	        ResultSet ResultPrice = stmt.executeQuery("SELECT Price FROM desk WHERE ID = "+ '"' +iDMax+ '"');

	        if (ResultPrice.next())

	        	price =  Integer.parseInt(ResultPrice.getString("Price").toString());
	        
	        ResultPrice.close();	
			if (price < 0) {
				System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
				return "Invalid price "+ getManufactuer();
			}  
			
	        } catch (SQLException e) {
	        	e.printStackTrace();
	        } catch (NumberFormatException e) {
	        	e.printStackTrace();
	        }
			HelpCreateDesk createHelp = new HelpCreateDesk(type,allLeg, allTop, allDrawer);
			comparePrice[x] = createHelp.constructDesk(iDMax, price,sortedAllID, getLeg(), getTop(), getDrawer());
			
			if (comparePrice[x] <0) {
				System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
				return allManufacturer;
			}
			
			outputIDs[x] = createHelp.returnIDs();
			writeUpdate[x] = createHelp.getDatabaseUpdateString();
			
			System.out.println("The " + x  + "in the compare Array of ID with max ID is " + iDMax + " and it's price is " + (comparePrice[x]));

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
        System.out.println("Local most occurence is " + localMax);
        
       //finding if there are more than one max location. eg if max is 3, and 2 ID have the a occurrence of 3
       for (int k=0; k<allID[1].length; k++) {
           if (Integer.parseInt(allID[1][k]) == localMax) {
        	   System.out.println("allID[1]["+k+"]" + " is one of the max");
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
	private String[][] getallID() 
	{
		 ResultSet readID;
		 int countCol =0;
		 String[][] retIDs = null;

		 try {
			readID = stmt.executeQuery("SELECT ID FROM desk WHERE TYPE = " + '"' +this.type+ '"'  +  "ORDER BY Price ASC");
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
			
//			System.out.println("in get ID, there are " + countCol +" Rows");
			readID = stmt.executeQuery("SELECT ID FROM desk WHERE TYPE = " + '"' +this.type+ '"'  +  "ORDER BY Price ASC");
			
			retIDs = new String[2][countCol];
			int Col=0;
			while (readID.next()) {
				retIDs[0][Col] = readID.getString("ID");
			    if (retIDs[0][Col].length() > 25 ||retIDs[0][Col].length() <1) {
			    	System.out.println("The Invalid " + retIDs[0][Col] + " is invalid.");
			    	return null;
				}
				Col++;
	        }
			
			readID.close();
			return retIDs;		 
			
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		return null;
	}
	
    /**
	 * drawer2D is a private helper method that searches the inventory for desk IDs that are of
	 * the specified chair type and have useable drawer. Creates a 2D string array that holds the
	 * IDs of such chairs and their respective information.
	 * @return A 2D string array that holds the desk IDs of specified type that have useable 
	 * drawer and their respective information.
	 */
	private String[][] drawer2D() 
	{
		 ResultSet readDrawers;
		 ResultSet readDrawers2;

		try {
			readDrawers = stmt.executeQuery("SELECT ID,Drawer,Price FROM desk WHERE TYPE = "+ '"' +this.type+ '"' +" AND Drawer = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           //retrieve number of columns and rows
           int numRows, numCols;

			ResultSet temp2 = readDrawers;
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
				System.out.println("There drawer2D() is empty. There aren't any drawers available");
				readDrawers.close();
				return null;
			}
			
			numRows = count;
			numCols = readDrawers.getMetaData().getColumnCount();
			String[][] DrawersData = new String[numRows][numCols];
       	
       	readDrawers2 = stmt.executeQuery("SELECT ID,Drawer,Price FROM desk WHERE TYPE = "+ '"' +this.type+ '"' +" AND Drawer = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           int row = 0;
   
			while (readDrawers2.next()) {
	            for (int i = 0; i < numCols; i++) {
	            	DrawersData[row][i] = (readDrawers2.getObject(i+1)).toString();
	            }
	            if (checkIfValid(DrawersData,row) == false) {
	            	return null;
	            }
	            row++;
	        }
			readDrawers.close();
			readDrawers2.close();
			return DrawersData;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	/**
	 * top2D is a private helper method that searches the inventory for chair IDs that are of
	 * the specified chair type and have useable top. Creates a 2D string array that holds the
	 * IDs of such chairs and their respective information.
	 * @return A 2D string array that holds the desk IDs of specified type that have useable 
	 * top and their respective information.
	 */
	private String[][] top2D() 
	{
		 ResultSet readTop;
		 ResultSet readTop2;

		try {
			readTop = stmt.executeQuery("SELECT ID,Legs,Price FROM desk WHERE TYPE = "+ '"' +this.type+ '"' +" AND Top = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           //retrieve number of columns and rows
           int numRows, numCols;

			ResultSet temp2 = readTop;
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
				System.out.println("The top2D() is empty. There aren't any Tops available");
				readTop.close();
				return null;
			}
			numRows = count;
			numCols = readTop.getMetaData().getColumnCount();
			String[][] topData = new String[numRows][numCols];
       	
       	readTop2 = stmt.executeQuery("SELECT ID,Legs,Price FROM desk WHERE TYPE = "+ '"' +this.type+ '"' +" AND Top = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           int row = 0;
   
			while (readTop2.next()) {
	            for (int i = 0; i < numCols; i++) {
	            	topData[row][i] = (readTop2.getObject(i+1)).toString();
	            }
	            if (checkIfValid(topData,row) == false) {
	            	return null;
	            }
	            row++;
	        }
			readTop.close();
	        return topData;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
    /**
	 * leg2D is a private helper method that searches the inventory for chair IDs that are of
	 * the specified desk type and have useable legs. Creates a 2D string array that holds the
	 * IDs of such chairs and their respective information.
	 * @return A 2D string array that holds the chair IDs of specified type that have useable 
	 * legs and their respective information.
	 */
	private String[][] leg2D() 
	{
		 ResultSet readLegs;
		 ResultSet readLegs2;

		try {
			 readLegs = stmt.executeQuery("SELECT ID,Legs,Price FROM desk WHERE TYPE = "+ '"' +this.type+ '"' +" AND Legs = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
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
				System.out.println("There leg2D() is empty. There aren't any legs available");
				readLegs.close();
				return null;
			}
			
			numRows = count;
			numCols = readLegs.getMetaData().getColumnCount();
			
			String[][] g_trainingdata = new String[numRows][numCols];
        	
        	readLegs2 = stmt.executeQuery("SELECT ID,Legs,Price FROM desk WHERE TYPE = "+ '"' +this.type+ '"' +" AND Legs = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");    
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
	 * desks of specified type with available legs, that have the lowest price and
	 * returns the ID of that chair and its price.
	 * @return String array containing the ID of chair with legs and its price.
	 */
	private String[] getLeg() 
	{
		ResultSet localResult;
		String[] retArr = new String[2];
		try {
			 localResult = stmt.executeQuery("SELECT ID,Price FROM desk WHERE TYPE = "+ '"' +this.type+ '"' +" AND Legs = " + '"' + "Y"  +'"'+" ORDER BY Price ASC LIMIT 1");
			
			if (localResult.next()) {

			retArr[0] = localResult.getString("ID");
			retArr[1] = localResult.getString("Price");
			
			if (checkIfValid(retArr) == false) {
				return null;
			}
			
			} else {
				System.out.println("localResult is " + localResult);
				System.out.println("Couldn't get the ID and price of anywhere Legs is Y");
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
	 * @return String array containing the ID of desk with arms and its price.
	 */
	private String[] getTop() 
	{
		String[] retTop = new String[2];
		try {
			ResultSet localResultTop = stmt.executeQuery("SELECT ID,Price FROM desk WHERE TYPE = "+ '"' +this.type+ '"' +" AND Top = " + '"' + "Y"  +'"'+" ORDER BY Price ASC LIMIT 1");
			
			if (localResultTop.next()) {
				retTop[0] = localResultTop.getString("ID");
				retTop[1] = localResultTop.getString("Price");
				
				if (checkIfValid(retTop) == false) {
					return null;
				}

			} else {
				retTop[1] = null;
				retTop[0] = null;
			}
			
			localResultTop.close();
			return retTop;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;

		} 
	}
    /**
	 * getDrawer is a private helper method that gets the searches the inventory for 
	 * desk of specified type with available seat, that have the lowest price and
	 * returns the ID of that chair and its price.
	 * @return String array containing the ID of desk with seat and its price.
	 */
	private String[] getDrawer() 
	{
		String[] retDrawer = new String[2];
		try {
			ResultSet localResultDrawer = stmt.executeQuery("SELECT ID,Price FROM desk WHERE TYPE = "+ '"' +this.type+ '"' +" AND Drawer = " + '"' + "Y"  +'"'+" ORDER BY Price ASC LIMIT 1");
			
			if (localResultDrawer.next()) {

				retDrawer[0] = localResultDrawer.getString("ID");
				retDrawer[1] = localResultDrawer.getString("Price");

				if (checkIfValid(retDrawer) == false) {
					return null;
				}
				
			} else {
				System.out.println("localResult is " + localResultDrawer);
				System.out.println("Couldn't assemeble desk get drawer is empty");
				retDrawer[1] = null;
				retDrawer[0] = null;
			}
			
			localResultDrawer.close();
			return retDrawer;
		} catch (SQLException e) {
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
		StringBuffer allManu = new StringBuffer();
		allManu.append("\nCould not fulfill order.\nSuggested manufacturers are: ");
		try {
			ResultSet manufacturer = stmt.executeQuery("SELECT ManuID FROM desk WHERE Type = "+ '"' +this.type+ '"' + " ORDER BY ManuID");
			int count=0;
			while(manufacturer.next()) {
				count++;
			}
			manu_for_specifiedType = new String[count];
			manufacturer = stmt.executeQuery("SELECT ManuID FROM desk WHERE Type = "+ '"' +this.type+ '"' + " ORDER BY ManuID");
        	int row = 0;
        	
			while (manufacturer.next()) {
	            	manu_for_specifiedType[row] = manufacturer.getString("ManuID");
	            	if (manu_for_specifiedType[row].length()>3 || manu_for_specifiedType[row].length() < 1) {
	            		return null;
	            	}
	            row++;
	        }
			
			for (int i = 0; i < manu_for_specifiedType.length; i++) {
	            for (int j = i+1; j < manu_for_specifiedType.length; j++) {
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
					//System.out.println("The manuID is "+newManufactureID[i]);
					i++;
				}
			} 
			
			// next we get the the manufacturer names based off the ID
			
			for (int j=0; j<newManufactureID.length; j++ ) {
		        ResultSet Result = stmt.executeQuery("SELECT Name FROM manufacturer WHERE ManuID = "+ '"' +newManufactureID[j]+ '"');
		        Result.next();
		        String tem = Result.getString("Name");
		        if (tem.length() > 25 || tem.length() <1) {
		        	return null;
		        }
		        if (j+1 == newManufactureID.length) {
			        allManu.append(tem +".");
		        } else {
		        allManu.append(tem +", ");
		        }
			}
	      //  System.out.println("the manufacture name is "+allManu.toString());
	        
			manufacturer.close();
			return allManu.toString();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	private String getManufactuer() 
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
