package edu.ucalgary.ensf409;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.util.*;
/**
 * Class AssemblyLamp was created as part of ENSF 409 W21 project. It contains a public method called createLamp()
 * that uses other private methods to navigate the specified inventory database and find the cheapest 
 * combination of lamp items whose parts can be used to create one or more functional lamps. 
 * @author Ethan Conrad, Isaiah Asaolu, Tsenguun Ulambayar
 * @version 1.2
 */ 
public class AssemblyLamp {
	private  String type;
	private int quantity;
	private  int max=0; 
	 
	private String[][] allBase;
	private String[][] allBulb;
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
	public AssemblyLamp(ConnectToDatabase connecting, String type, int quantity) 
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
					stmt.executeUpdate("DELETE FROM lamp WHERE ID = "+ '"' +theMatch.group(2)+ '"');
				}
				tempString ="";
				System.out.println("The database has now been updated");
			}
		 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println();
	}
	
	private void setStmt() 
	{
		try {
			 stmt = accessDatabase.myConnect.createStatement();
//			 accessDatabase.myConnect.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public String createLamp(StringBuffer allUsedID) throws Exception {
		System.out.println("inside AssemblyLamp class");

		// first I going to create 2D array of each column. Wherever there's a is a Y it gets added to the 2d String
		allBase = base2D();
		allBulb = bulb2D();
		allManufacturer = manufacturer();
				
		// check I am checking if an ID shows up in each 2D array
		//  allID is a 2d array. The first row will consist of the IDs and the second row will contain the occurrence
		String[][] allID = getallID();
		
		if (allManufacturer == null && (allBase == null || allBulb== null || allID == null)) {
			System.out.println("No manufactuers");
			return "\nWe couldn't create a furniture of type " + this.type +", and there were no manufacuter name(s) found on the database.";
		}
		// if any one of the method is empty we are going to return and recommend a store instead
		if (allBase == null || allBulb== null || allID == null) {
			//System.out.println("it seems like one of the 2d array is empty");
			System.out.println(allManufacturer);
			return allManufacturer;
		} 
		
		//looking for the most y
		for (int col =0; col< allID[0].length; col++) { 		
		//checking inside Base first
			for (int a=0; a<allBase.length; a++) {
				if (allID[0][col].equals(allBase[a][0])) {
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
			  
			  // next, checking inside of Bulb 
			  for (int a=0; a<allBulb.length; a++) {
				if (allID[0][col].equals(allBulb[a][0])) {
					
//					System.out.println(allID[1][col]);
					int tempInt=0;
					if (allID[1][col] != null) {
						tempInt = Integer.parseInt(allID[1][col]) + 1;
					} else {
						tempInt=1;
					}
					allID[1][col] = String.valueOf(tempInt);
					//System.out.print("  ---> "+ allID[1][col] + "\n\n");
				} else {
					int makeZero=0;
					if (allID[1][col] != null) {
						makeZero = Integer.parseInt(allID[1][col]) + 0;
					} else {
						makeZero=0;
					}
					allID[1][col] = String.valueOf(makeZero);
					//System.out.println("No ID " + allID[0][col] + " in allID wasn't found in allBulb[" + a +"][0] which is " + allBulb[a][0]);

				}
			  }
			  
			  
		}
			  
			  
			  
	
		System.out.println("Looking for which ID shows has the most yes's");
				
		// Now, after we are done checking to we see which ID has the most yes's
		
			// Now, we look for which ID has the most yes
			// IF there are multiple IDs with the same amount of yes we have to compare then and see which one produces the cheapest lamp
		StringBuffer stringMax = new StringBuffer();
		int[] large  = largest(allID,stringMax);	  
		max = Integer.parseInt(stringMax.toString())/*Integer.parseInt(allID[1][0].toString())*/; 
		System.out.println("the # of elements  is " + max );
		int howManyMax =0;
		if (max ==0) {
			System.out.println("There is no possible lamp furniture of type " + type + " that can be created because every ID has no avaiable parts");
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
		 
		// now we use the amount of many to look for the lowest lamp 

	if (howManyMax == 1) 
	{
		HelpCreateLamp createHelp = new HelpCreateLamp(type,allBase, allBulb);

		int totalPrice =0;
		System.out.println();
		System.out.println("There's only one ID that has max occurence");
		String retrieveIDOfMax = findID(max,allID);
		System.out.println("The ID of most yes is " + retrieveIDOfMax);
		
		try {
		ResultSet resultPrice = stmt.executeQuery("SELECT Price FROM lamp WHERE ID = "+ '"' +retrieveIDOfMax+ '"');
		
			if (resultPrice.next())
				totalPrice =  Integer.parseInt(resultPrice.getString("Price").toString());
		
			resultPrice.close();
			
			if (totalPrice < 0) {
				System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
				return "Invalid price "+ getManufactuer();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		int price = createHelp.constructLamp(retrieveIDOfMax, totalPrice,allID, getBase(), getBulb());
		
		if (price < 0) {
			System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
			return "Invalid price "+ getManufactuer();
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
	        ResultSet resultPrice = stmt.executeQuery("SELECT Price FROM lamp WHERE ID = "+ '"' +iDMax+ '"');

	        if (resultPrice.next())

	        	price =  Integer.parseInt(resultPrice.getString("Price").toString());
	        
	        resultPrice.close();	
			if (price < 0) {
				System.out.println("Invalid price couldn't create a complete " + this.type + this.quantity);
				return "Invalid price "+ getManufactuer();
			}  
			
	        } catch (SQLException e) {
	        	e.printStackTrace();
	        } catch (NumberFormatException e) {
	        	e.printStackTrace();
	        }
			HelpCreateLamp createHelp = new HelpCreateLamp(type,allBase, allBulb);
			comparePrice[x] = createHelp.constructLamp(iDMax, price,sortedAllID, getBase(), getBulb());
			
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
	 * to find a lamp that has the max number of available parts.
	 * @param maxValue Integer that indicates the current maximum number of available
	 * parts in occurence.
	 * @param allID A 2D string array that holds the IDs of the chairs in inventory and
	 * the number of available parts each lamp has.
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
	 * a lamp can have and makes an integer array of the indexes of lamp that have the
	 * current maximum number of parts. 
	 * @param allID 2D string array that holds the chair IDs and their respective number of
	 * parts.
	 * @param stringMax StringBuffer where the local maximum is appended to.
	 * @return Integer array that returns the indexes of the lamp that have the most
	 * available parts.
	 */
	// Method to find maximum in arr[]
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
 	 * holds the IDs of all lamp of the specified type and their respective prices.
 	 * @return 2D string array that holds the IDs of all lamp of the specified type 
 	 * and their respective prices.
 	 */
	private String[][] getallID() 
	{
		 ResultSet readID;
		 int countCol =0;
		 String[][] retIDs = null;

		 try {
			readID = stmt.executeQuery("SELECT ID FROM lamp WHERE TYPE = " + '"' +this.type+ '"'  +  "ORDER BY Price ASC");
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
			readID = stmt.executeQuery("SELECT ID FROM lamp WHERE TYPE = " + '"' +this.type+ '"'  +  "ORDER BY Price ASC");
			
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
				e1.printStackTrace();
			}
		return null;
	}
	
	private String[][] bulb2D() 
	{
		 ResultSet readBulb;
		 ResultSet readBulb2;

		try {
			readBulb = stmt.executeQuery("SELECT ID,Base,Price FROM lamp WHERE TYPE = "+ '"' +this.type+ '"' +" AND Bulb = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           //retrieve number of columns and rows
           int numRows, numCols;

			ResultSet temp2 = readBulb;
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
				System.out.println("The bulb2D() is empty. There aren't any Bulbs available");
				readBulb.close();
				return null;
			}
			numRows = count;
			numCols = readBulb.getMetaData().getColumnCount();
			String[][] bulbData = new String[numRows][numCols];
       	
       	readBulb2 = stmt.executeQuery("SELECT ID,Base,Price FROM lamp WHERE TYPE = "+ '"' +this.type+ '"' +" AND Bulb = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
           int row = 0;
   
			while (readBulb2.next()) {
	            for (int i = 0; i < numCols; i++) {
	            	bulbData[row][i] = (readBulb2.getObject(i+1)).toString();
	            }
	            if (checkIfValid(bulbData,row) == false) {
	            	return null;
	            }
	            row++;
	        }
			readBulb.close();
	        return bulbData;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

		return null;
	}

    /**
	 * base2D is a private helper method that searches the inventory for lamp IDs that are of
	 * the specified lamp type and have useable lamp. Creates a 2D string array that holds the
	 * IDs of such lamp and their respective information.
	 * @return A 2D string array that holds the lamp IDs of specified type that have useable 
	 * base and their respective information.
	 */
	private String[][] base2D() 
	{
		 ResultSet readBase;
		 ResultSet readBase2;

		try {
			 readBase = stmt.executeQuery("SELECT ID,Base,Price FROM lamp WHERE TYPE = "+ '"' +this.type+ '"' +" AND Base = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");
            //retrieve number of columns and rows
            int numRows, numCols;

			ResultSet temp2 = readBase;
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
				System.out.println("There base2D() is empty. There aren't any Base available");
				readBase.close();
				return null;
			}
			
			numRows = count;
			numCols = readBase.getMetaData().getColumnCount();
			
			String[][] g_trainingdata = new String[numRows][numCols];
        	
        	readBase2 = stmt.executeQuery("SELECT ID,Base,Price FROM lamp WHERE TYPE = "+ '"' +this.type+ '"' +" AND Base = " + '"' + "Y"  +'"'+" ORDER BY Price ASC");    
        	int row = 0;
			while (readBase2.next()) {
	            for (int i = 0; i < numCols; i++) {
	            	g_trainingdata[row][i] = (readBase2.getObject(i+1)).toString();
	            }
	            if (checkIfValid(g_trainingdata,row) == false) {
	            	return null;
	            }
	            row++;
	        }
			
			readBase2.close();
	        return g_trainingdata;
		} 
		catch (SQLException e) {
			e.printStackTrace();

		}
		return null;
	}
	
	/**
	 * getBase is a private helper method that gets the searches the inventory for 
	 * lamp of specified type with available Bases, that have the lowest price and
	 * returns the ID of that chair and its price.
	 * @return String array containing the ID of chair with legs and its price.
	 */
	private String[] getBase() 
	{
		ResultSet localResult;
		String[] retArr = new String[2];
		try {
			 localResult = stmt.executeQuery("SELECT ID,Price FROM lamp WHERE TYPE = "+ '"' +this.type+ '"' +" AND Base = " + '"' + "Y"  +'"'+" ORDER BY Price ASC LIMIT 1");
			
			if (localResult.next()) {

			retArr[0] = localResult.getString("ID");
			retArr[1] = localResult.getString("Price");
			
			if (checkIfValid(retArr) == false) {
				return null;
			}
			
			} else {
				System.out.println("localResult is " + localResult);
				System.out.println("Couldn't get the ID and price of anywhere Base is Y");
				retArr[1] = null;
				retArr[0] = null; 
			}
			
			localResult.close();
			return retArr;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		//return retArr;
	}
	
	/**
	 * getBulb is a private helper method that gets the searches the inventory for 
	 * lamp of specified type with available Bulbs, that have the lowest price and
	 * returns the ID of that lamp and its price.
	 * @return String array containing the ID of lamp with Bulb and its price.
	 */
	private String[] getBulb() 
	{
		String[] retBulb = new String[2];
		try {
			ResultSet localResultBulb = stmt.executeQuery("SELECT ID,Price FROM lamp WHERE TYPE = "+ '"' +this.type+ '"' +" AND Bulb = " + '"' + "Y"  +'"'+" ORDER BY Price ASC LIMIT 1");
			
			if (localResultBulb.next()) {
				retBulb[0] = localResultBulb.getString("ID");
				retBulb[1] = localResultBulb.getString("Price");
				
				if (checkIfValid(retBulb) == false) {
					return null;
				}

			} else {
				retBulb[1] = null;
				retBulb[0] = null;
			}
			
			localResultBulb.close();
			return retBulb;
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
		allManu.append("\nCouldn't fulfill order.\nSuggested manufacturers are: ");
		try {
			ResultSet manufacturer = stmt.executeQuery("SELECT ManuID FROM lamp WHERE Type = "+ '"' +this.type+ '"' + " ORDER BY ManuID");
			int count=0;
			while(manufacturer.next()) {
				count++;
			}
			manu_for_specifiedType = new String[count];
			manufacturer = stmt.executeQuery("SELECT ManuID FROM lamp WHERE Type = "+ '"' +this.type+ '"' + " ORDER BY ManuID");
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
					//System.out.println(manu_for_specifiedType[i] + ", "+manu_for_specifiedType[j]);
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
