package edu.ucalgary.ensf409;

import java.sql.*;
import java.io.*;
import java.util.*;
/**
 * HelpCreateLamp is used to help AssemblyLamp sort through all the parts from the database and 
 * find the cheapest combination of lamp.
 * @author Ethan Conrad, Isaiah Asaolu, Tsenguun Ulambayar
 * @version 1.3
 */
public class HelpCreateLamp {
	
	 private StringBuffer updateToDatabase = new StringBuffer();

	 private String type;
 	 private Object[][] allBase;
 	private Object[][] allBulb;
 	 private StringBuffer output = new StringBuffer();
	/**
 	  * Constructor for class HelpCreateLamp that assigns values to type, allBase, allBulb.
 	  * @param user specified type of furniture from AssemblyChair
 	  * @param allBase2 String[][] containing legs taken from database
 	  * @param allBulb2 String[][] containing arms taken from database
 	  */
		public HelpCreateLamp(String type, Object[][] allBase2, Object[][] allBulb2)
		{
			
		this.type = type;
		this.allBase = allBase2;
		this.allBulb = allBulb2;
	}
	/**
	 * returnIDs is a method that will return a string containing the IDs by converting output 
	 * to string. 
	 * @return Output containing IDs of parts used
	 */		
	public String returnIDs() {
		return output.toString();
			
	}
	/**
	 * getDatabaseUpdateString method will return a string containing all ID and which 
	 * component of the ID was used to create the furniture. eg rails from: C0123
	 * @return String containing IDs
	 */	
	public String getDatabaseUpdateString() {
		return updateToDatabase.toString();
	}
	/**
	 * constructLamp method sorts through all the lamps to find cheapest combination.
	 * @param retrieveIDOfMax2 String that contains the ID of lamp with max number of parts .
	 * @param totalPrice2 Integer that contains total price of the lamp.
	 * @param allID 2D string array that contains all IDs of lamp.
	 * @param getBase String[] that contain base IDs.
	 * @param getBulb String[] that contains bulb IDs.
	 * @return Price to construct the lamp
	 * @throws Exception
	 */	
	public	int constructLamp(String retrieveIDOfMax2, int totalPrice2, String[][] allID, String[] getBase,  String[] getBulb) throws Exception{
		int retPrice =0;
		boolean baseMax = false; // booleans used to check if respective part is found
		boolean bulbMax = false;
		int missing=0;
		int totalPrice =0;
		totalPrice = totalPrice2;
		
		String retrieveIDOfMax = retrieveIDOfMax2;
		
	// find Base
	for (int b=0; b< allBase.length ; b++) 
	{
//		System.out.println("Seeing which elements the ID yes's has and the ones it doesn't have");

		if (allBase[b][0].equals(retrieveIDOfMax)) {
			baseMax = true;
//			output.append("Legs from: " + retrieveIDOfMax + "\n");
			updateToDatabase.append("Base from: " + retrieveIDOfMax + "\n");

			break;
		} else if (allBase[b][0] != retrieveIDOfMax && b+1 == allBase.length)  {
			baseMax = false;
		}
	}
	
	// find Bulb
	for (int b=0; b< allBulb.length; b++) {
		if (allBulb[b][0].equals(retrieveIDOfMax)) {
			bulbMax = true;
			updateToDatabase.append("Bulb from: " + retrieveIDOfMax + "\n");
			break;
		} else if (allBulb[b][0] != retrieveIDOfMax && b+1 == allBulb.length)  {
			bulbMax = false;
		}
	}
	
	// so then if we find how many it's missing.
	// If it's missing just 1 we find it and put it in
	// However if it's missing more than one we have check if the ID with the second most yes has both elements 

	
	if (baseMax == false) {
		missing++;
	}
	if (bulbMax == false) {
		missing++;
	}
	
	// if it's not missing all two, then write the ID to the String
	if (missing< 2) {
		output.append("\nID: " + retrieveIDOfMax);
	}
//	System.out.println(output.toString() + " CushionMax is  " + CushionMax + " baseMax is " + baseMax + " bulbMax is " + bulbMax + " drawersMax is " + drawersMax);
	
	
	int addToTotalPrice_option1 =0;
	int addToTotalPrice_option2 =0;
 

	System.out.println("\n Missing equals " + missing +"\n");
	
	// if it's missing 1 find the cheapest way to assemble, same for 0
	if (missing == 0 || missing == 1) {
		StringBuffer resZero = new StringBuffer();
		StringBuffer WhatToUpdate_Zero = new StringBuffer();
		totalPrice += fillInPart(baseMax,bulbMax, getBase, getBulb, resZero,WhatToUpdate_Zero);
		output.append(resZero);
		updateToDatabase.append(WhatToUpdate_Zero);
		return totalPrice;
	} 	 
	// if it's missing more than 1, we have to check if the second most occurrence of yes has the elements we are missing
	// Also the allID[][] include the ID and the occurences of Y, so on we are going to get the next large ID. then see if it has 1 or all the parts we need. 
	else if (missing > 1) {
		boolean[] findParts = {baseMax, bulbMax};
		
    	System.out.println("The current ID is " + retrieveIDOfMax);
		
		// the first method works by first sorting the array by the number of occurennce and then seeing if it has the part
		// the method will be success if the largest or next largest occurence has all of the missing objects
		System.out.println("[");
		System.out.println("option 1: ");
		StringBuffer WhatToUpdate = new StringBuffer();
		StringBuffer res = new StringBuffer();
		addToTotalPrice_option1 = singleLargest(allID,  retrieveIDOfMax,findParts,res,WhatToUpdate);
		System.out.println(res.toString());
		System.out.println("Option 1 price is(Not the total price): " +addToTotalPrice_option1);
		
		
		// we then check with the method we used when missing was 1
		System.out.println();
		System.out.println("option 2: ");
		StringBuffer res2 = new StringBuffer();
		StringBuffer WhatToUpdateOption_2 = new StringBuffer();
		addToTotalPrice_option2 = fillInPart(baseMax,bulbMax, getBase, getBulb,res2,WhatToUpdateOption_2);
		System.out.println(res2.toString());
		System.out.println("Option 2 price is(Not the total price): " +addToTotalPrice_option2);

		System.out.println();

		if (addToTotalPrice_option1 < addToTotalPrice_option2) {
			System.out.println("using option 1, combined with total price is gives us: " + (totalPrice +addToTotalPrice_option1));
			retPrice= totalPrice +addToTotalPrice_option1;
			output.append(res);
			updateToDatabase.append(WhatToUpdate);
			
		}
		else if (addToTotalPrice_option1 == addToTotalPrice_option2) {
			System.out.println("Both options are the same price so I going to pick option1 .So using option 1, combined with total price is gives us: " + (totalPrice +addToTotalPrice_option1));
			retPrice= totalPrice +addToTotalPrice_option1;
			output.append(res);
			updateToDatabase.append(WhatToUpdateOption_2);
		}
		else {
			System.out.println("using option 2, combined with total price is gives us: " + (totalPrice +addToTotalPrice_option2));
			retPrice= totalPrice +addToTotalPrice_option2;
			output.append(res2);
			updateToDatabase.append(WhatToUpdateOption_2);
		}
		System.out.println("\n]");

		return retPrice;
		
	}
	// if this returns something went wrong
	return -5000;

	}
	
	/**
	 * fillInPart method sorts through parts to fill in what is missing
	 * @param baseMax boolean that indicates whether the lamp has base
	 * @param bulbMax boolean that indicates whether the lamp has bulb
	 * @param getBase String array that contains IDs of lamp with base
	 * @param getBulb String array that contains IDs of lamp with bulb
	 * @param res2 StringBuffer 
	 * @param whatToUpdate_Single StringBuffer
	 * @return price
	 */
	private int fillInPart(boolean baseMax, boolean bulbMax, String[] getBase, String[] getBulb, StringBuffer res2, StringBuffer whatToUpdate_Single) {
		String getMissingBase = null;
		int price =0;
		
		// check what is missing
		try {
		if (baseMax == false) {
			System.out.println("getting Base");
			String[] tempMax= getBase;
			getMissingBase =tempMax[0]  ;
			price += Integer.parseInt(tempMax[1]);
			res2.append("\nID: "+tempMax[0]);
			baseMax = true;
			whatToUpdate_Single.append("Base from: " + tempMax[0] + "\n");
			System.out.println(tempMax[0] + "  " + tempMax[1]);
		} 
		
		 if (bulbMax == false) {
			System.out.println("getting Bulb");
			String[] tempBulb= getBulb;
			
			if (tempBulb[0].equals(getMissingBase)) {
				// we already have the ID
			} else {
			res2.append("\nID: "+tempBulb[0]);
			}
			
			price += Integer.parseInt(tempBulb[1]);
			bulbMax =true;
			updateToDatabase.append("Bulb from: " + tempBulb[0] + "\n");
			System.out.println(tempBulb[0] + "  " + tempBulb[1]);
		}
		 
		 //check if any of the id are the same
	}
	// if we couldn't find any element because there;s nothing in any of the arrays
	catch (NullPointerException x) {
		System.out.println("Null error in the method fillInParts");
		x.printStackTrace();
		return -500;
	}
	catch (Exception e) {
		System.out.println("error in the method fillInParts");
		e.printStackTrace();
		return -500;
	}
		return price;
	}

	/**
	 * singleLargest is a private helper method that will find the nextMax Array, when we reach it we will check if 
	 * it has the parts we are missing. The boolean of the stuff we are missing is false, indcating that 
	 * current doesn't current have it
	 * @param allID 2D String array that contains IDs of all lamp
	 * @param ID_ofCurrentMax String that contains ID of lamp that has the current maximum parts
	 * @param allParts boolean array that indicates which parts the lamp has
	 * @param res StringBuffer
	 * @param whatToUpdate StringBuffer
	 * @return integer that indicates the cost of the parts needed
	 */
     private int singleLargest(String[][] allID, String ID_ofCurrentMax,boolean[] allParts, StringBuffer res, StringBuffer whatToUpdate)
    {
    	int CostOfParts = 0; 
        // first we are going to sort the array by the integer value of it's occurence
        String [][] sortedallID = sort2DArray(allID);
        System.out.println();

        // Initialize maximum element. which is going to be the second element in the array, cause the first is the max
         //max = Integer.parseInt(allID[1][1]);
         
         // so we check if the the next occurene yes include 1 or all of the missing yes.
         // we keep looping to we have found all missing
         // if we can't find it we write, that wouldn't find <inserNameOfPart> 
   	 System.out.print("changes is : ");
  	  for (int f=0; f< allParts.length; f++) {
		  System.out.print(allParts[f] + ", ");
	  }System.out.println();
	  
         for (int d=0; d<sortedallID[0].length; d++) {
        	 String currID = sortedallID[0][d];
    	  if (!currID.equals(ID_ofCurrentMax)) {
    		  
        	 // next we'll send that ID and the boolean array to another method to see if we can get the missing parts
    	  CostOfParts += lookThrough(currID, allParts, res,whatToUpdate);
        	 System.out.print("changes is : ");
           	  for (int f=0; f< allParts.length; f++) {
        		  System.out.print(allParts[f] + ", ");
        	  }System.out.println();
        	  
        	  if(allParts[0] == true && allParts[1] == true) {
        		System.out.println("all parts have been found we can assemble " + type);  
        		System.out.println();
        		break;
        	  }
        	  else if ((allParts[0] == false || allParts[1] == false) && d+1 == allID[0].length) {
          		System.out.println("we don't all have parts to assemble " + type);  
        	  }
    	  }
         }
        return CostOfParts;
    }
	
    /**
     * lookThrough is a private helper method that checks the ID to see if there 
	 * are missing elements in the 2d array.
     * If it does it returns true and we update the boolean array that calls it.
     * @param currID String of the current lamp ID.
     * @param allParts Boolean array that indicates which part is available or not.
     * @param res StringBuffer
     * @param whatToUpdate StringBuffer
     * @return
     */
	private int lookThrough(String currID, boolean[] allParts, StringBuffer res, StringBuffer whatToUpdate) 
	{
		// already used to meant to indicate that the cost has already been added
		System.out.println("ID passed to lookthrouhg is " + currID);
		boolean already_used = false;
		 int retCost = 0;
		 int row = 0;
		for (int e = 0; e < allParts.length; e++) {
			// if it equals false it means that part is missing
			// and we check if we can find it in the other ID
			// ALso because I have hardcorded the columns, I know that baseMax(0), bulbMax(1), drawersMax(2), CushionMax(3)
			
			// if ID equal one of the IDs that is store in allBulbs etc, then we know we can use that part and we need to include it to cost
			if(allParts[e] == false) {
				switch(e)  
				{
				case 0:{
					// looking for Base
					for (int i =0; i < allBase.length; i++) {
						if (allBase[i][0].equals(currID)) {
							System.out.println(currID + " has the missing Base");
						allParts[e] = true;
						row =i;
						whatToUpdate.append("Base from: " + currID + "\n");
						break;
						}
					}
					
					if (already_used == false && allParts[e] == true) {
						retCost= Integer.parseInt(allBase[row][2].toString());
						already_used =true;
						res.append("\nID: "+allBase[row][0]);
					}
					break;
				}

					case 1: {
						// looking for a Bulb
						for (int i =0; i < allBulb.length; i++) {
							if (allBulb[i][0].equals(currID)) {
							System.out.println(currID + " has the missing Bulb");
							allParts[e] = true;
							row =i;
							whatToUpdate.append("Bulbs from: " + currID + "\n");
							break;
							}
						}
						
						if (already_used == false && allParts[e] == true) {
							retCost= Integer.parseInt(allBulb[row][2].toString());
							System.out.println("the price of " + allBulb[row][0] + " is " + retCost);
							res.append("\nID: "+ allBulb[row][0]);
							already_used =true;
						}
						break;
					}
						default:
							//System.out.println("can't use anything from " + currID);
				}
				
				// if part became yes we need to add to the cost
//				AllCushion
				
			}
		}
		already_used =false;
//		System.out.println("returning");
		return retCost;
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
	        //print sorted array    
	        /*System.out.println("\nArray sorted in descending order: ");    
		       for (int i = 0; i <allID[1].length; i++) {     
		           System.out.print(allID[0][i] + ", " +allID[1][i] + " ");    
		        } */
		 ret2D= allID;
		return ret2D;
	}
}
