package edu.ucalgary.ensf409;

import java.sql.*;
import java.io.*;
import java.util.*;
/**
 * HelpCreateFiling is used to help AssemblyFiling sort through all the parts from the database and 
 * find the cheapest combination of filing.
 * @author Ethan Conrad, Isaiah Asaolu, Tsenguun Ulambayar
 * @version 1.3
 */
public class HelpCreateFiling {
	
	 private StringBuffer updateToDatabase = new StringBuffer();

	 private String type;
 	 private Object[][] allRails;
 	private Object[][] allCabinet;
 	private	 Object[][] allDrawers;
 	 private StringBuffer output = new StringBuffer();
 
		/**
 	  * Constructor for class HelpCreateFiling that assigns values to type, allRails, allCabinet,
	    and allDrawers
 	  * @param user specified type of furniture from AssemblyFiling
 	  * @param allRails2 String[][] containing rails taken from database
 	  * @param allCabinet2 String[][] containing cabinet taken from database
 	  * @param allDrawers2 String[][] containing drawers taken from database
 	  */
		public HelpCreateFiling( String type, Object[][] allRails2, Object[][] allCabinet2, Object[][] allDrawers2)
		{
			
		this.type = type;
		this.allRails = allRails2;
		this.allCabinet = allCabinet2;
		this.allDrawers = allDrawers2;
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
	 * constructFiling method sorts through all the filings to find cheapest combination.
	 * @param retrieveIDOfMax2 String that contains the ID of filing with max number of parts .
	 * @param totalPrice2 Integer that contains total price of the filing.
	 * @param allID 2D string array that contains all IDs of filing.
	 * @param getRails String[] that rails IDs.
	 * @param getCabinet String[] that contains cabinet IDs.
	 * @param getDrawers String[] that contains drawer IDs.
	 * @return Price to construct the filing
	 * @throws Exception
	 */	
	public	int constructFiling(String retrieveIDOfMax2, int totalPrice2, String[][] AllID, String[] getRails,  String[] getCabinet,String[] getDrawers) throws Exception{
		int retPrice =0;
		boolean railsMax = false; // booleans used to check if respective part is found
		boolean cabinetMax = false;
		boolean drawersMax = false; 
		
		int missing=0;
		int totalPrice =0;
		totalPrice = totalPrice2;
		
		String retrieveIDOfMax = retrieveIDOfMax2;
		
	// find railss
	for (int b=0; b< allRails.length ; b++) 
	{
//		System.out.println("Seeing which elements the ID yes's has and the ones it doesn't have");

		if (allRails[b][0].equals(retrieveIDOfMax)) {
			railsMax = true;
//			output.append("railss from: " + retrieveIDOfMax + "\n");
			updateToDatabase.append("Rails from: " + retrieveIDOfMax + "\n");

			break;
		} else if (allRails[b][0] != retrieveIDOfMax && b+1 == allRails.length)  {
			railsMax = false;
		}
	}
	
	// find Cabinet
	for (int b=0; b< allCabinet.length; b++) {
		if (allCabinet[b][0].equals(retrieveIDOfMax)) {
			cabinetMax = true;
			updateToDatabase.append("Cabinet from: " + retrieveIDOfMax + "\n");
			break;
		} else if (allCabinet[b][0] != retrieveIDOfMax && b+1 == allCabinet.length)  {
			cabinetMax = false;
		}
	}
	
	// find Drawers
	for (int b=0; b< allDrawers.length; b++) {
		if (allDrawers[b][0].equals(retrieveIDOfMax)) {
			drawersMax = true;
//			output.append("Drawers from: " + retrieveIDOfMax + "\n");
			updateToDatabase.append("Drawers from: " + retrieveIDOfMax + "\n");

			break;
		} else if (allDrawers[b][0] != retrieveIDOfMax && b+1 == allDrawers.length)  {
			drawersMax = false;
		}
	}
	
	// so then if we find how many it's missing.
	// If it's missing just 1 we find it and put it in
	// However if it's missing more than one we have check if the ID with the second most yes has both elements 

	
	if (railsMax == false) {
		missing++;
	}
	if (cabinetMax == false) {
		missing++;
	}

	if (drawersMax == false) {
		missing++;
	}
	
	// if it's not missing all three, then write the ID to the String
	if (missing< 3) {
		output.append("\nID: " + retrieveIDOfMax);
	}
//	System.out.println(output.toString() + " CushionMax is  " + CushionMax + " railsMax is " + railsMax + " cabinetMax is " + cabinetMax + " drawersMax is " + drawersMax);
	
	
	int addToTotalPrice_option1 =0;
	int addToTotalPrice_option2 =0;
 

	System.out.println("\n Missing equals " + missing +"\n");
	
	// if it's missing 1 find the cheapest way to assemble
	if (missing == 0 || missing == 1) {
		StringBuffer resZero = new StringBuffer();
		StringBuffer WhatToUpdate_Zero = new StringBuffer();
		totalPrice += fillInPart(railsMax,cabinetMax,drawersMax, getRails, getCabinet,getDrawers, resZero,WhatToUpdate_Zero);
		output.append(resZero);
		updateToDatabase.append(WhatToUpdate_Zero);
		return totalPrice;
	}	 
	// if it's missing more than 1, we have to check if the second most occurrence of yes has the elements we are missing
	// Also the AllID[][] include the ID and the occurences of Y, so on we are going to get the next large ID. then see if it has 1 or all the parts we need. 
	else if (missing > 1) {
		boolean[] findParts = {railsMax, cabinetMax, drawersMax};
		
    	System.out.println("The current ID is " + retrieveIDOfMax);
		
		// the first method works by first sorting the array by the number of occurennce and then seeing if it has the part
		// the method will be success if the largest or next largest occurence has all of the missing objects
		System.out.println("[");
		System.out.println("option 1: ");
		StringBuffer WhatToUpdate = new StringBuffer();
		StringBuffer res = new StringBuffer();
		addToTotalPrice_option1 = singleLargest(AllID,  retrieveIDOfMax,findParts,res,WhatToUpdate);
		System.out.println(res.toString());
		System.out.println("Option 1 price is(Not the total price): " +addToTotalPrice_option1);
		
		
		// we then check with the method we used when missing was 1
		System.out.println();
		System.out.println("option 2: ");
		StringBuffer res2 = new StringBuffer();
		StringBuffer WhatToUpdateOption_2 = new StringBuffer();
		addToTotalPrice_option2 = fillInPart(railsMax,cabinetMax,drawersMax, getRails, getCabinet,getDrawers,res2,WhatToUpdateOption_2);
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
	 * @param railsMax boolean that indicates whether the filing has rails
	 * @param cabinetMax boolean that indicates whether the filing has cabinet
	 * @param drawersMax boolean that indicates whether the filing has drawers
	 * @param getRails String array that contains IDs of filing with rails
	 * @param getCabinet String array that contains IDs of filing with cabinet
	 * @param getDrawers String array that contains IDs of filing with a drawer
	 * @param res2 StringBuffer 
	 * @param whatToUpdate_Single StringBuffer
	 * @return price
	 */
	private int fillInPart(boolean railsMax, boolean cabinetMax, boolean drawersMax, String[] getRails, String[] getCabinet, String[] getDrawers, StringBuffer res2, StringBuffer whatToUpdate_Single) {
		String getMissingRails = null;
		String getMissingCabinet = null;
		int price = 0;
		
		// checks what is missing
		try {
		if (railsMax == false) {
			System.out.println("getting rails");
			String[] tempMax= getRails;
			getMissingRails =tempMax[0]  ;
			price += Integer.parseInt(tempMax[1]);
			res2.append("\nID: "+tempMax[0]);
			railsMax = true;
			whatToUpdate_Single.append("rails from: " + tempMax[0] + "\n");
			System.out.println(tempMax[0] + "  " + tempMax[1]);
		} 
		
		 if (cabinetMax == false) {
			System.out.println("getting Cabinet");
			String[] tempCabinet= getCabinet;
			
			if (tempCabinet[0].equals(getMissingRails)) {
				// we already have the ID
			} else {
			getMissingCabinet = tempCabinet[0];
			res2.append("\nID: "+tempCabinet[0]);
			}
			
			price += Integer.parseInt(tempCabinet[1]);
			cabinetMax =true;
			updateToDatabase.append("Cabinet from: " + tempCabinet[0] + "\n");
			System.out.println(tempCabinet[0] + "  " + tempCabinet[1]);
		}
		 
		 if (drawersMax == false) {
			System.out.println("getting Drawerss");
			String[] tempDrawers= getDrawers;
			
			if (tempDrawers[0].equals(getMissingRails) || tempDrawers[0].equals(getMissingCabinet)) {
				// we already have the ID
			} else {
				res2.append("\nID: "+tempDrawers[0]);
			}
			
			price += Integer.parseInt(tempDrawers[1]);
			drawersMax = true;
			whatToUpdate_Single.append("Drawers from: " + tempDrawers[0] + "\n");
			System.out.println(tempDrawers[0] + "  " + tempDrawers[1]);
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
	 * @param allID 2D String array that contains IDs of all filing
	 * @param ID_ofCurrentMax String that contains ID of filing that has the current maximum parts
	 * @param allParts boolean arraay that indicates which parts the filing has
	 * @param res StringBuffer
	 * @param whatToUpdate StringBuffer
	 * @return integer that indicates the cost of the parts needed
	 */
     private int singleLargest(String[][] allID, String ID_ofCurrentMax,boolean[] allParts, StringBuffer res, StringBuffer whatToUpdate)
    {
    	int costOfParts = 0; 
        // first we are going to sort the array by the integer value of it's occurence
        String [][] sortedAllID = sort2DArray(allID);
        System.out.println();

        // Initialize maximum element. which is going to be the second element in the array, cause the first is the max
         //max = Integer.parseInt(allID[1][1]);
         
         // so we check if the the next occurene yes include 1 or all of the missing yes.
         // we keep looping to we have found all missing
         // if we can't find it we write, that wouldn't find <inserNameOfPart> 
   	 System.out.print("changes is : ");
  	  for (int f = 0; f < allParts.length; f++) {
		  System.out.print(allParts[f] + ", ");
	  }System.out.println();
	  
         for (int d=0; d<sortedAllID[0].length; d++) {
        	 String currID = sortedAllID[0][d];
    	  if (!currID.equals(ID_ofCurrentMax)) {
    		  
        	 // next we'll send that ID and the boolean array to another method to see if we can get the missing parts
    	  costOfParts += lookThrough(currID, allParts, res,whatToUpdate);
        	 System.out.print("changes is : ");
           	  for (int f=0; f< allParts.length; f++) {
        		  System.out.print(allParts[f] + ", ");
        	  }System.out.println();
        	  
        	  if(allParts[0] == true && allParts[1] == true && allParts[2] == true) {
        		System.out.println("all parts have been found we can assemble " + type);  
        		System.out.println();
        		break;
        	  }
        	  else if ((allParts[0] == false || allParts[1] == false || allParts[2] == false) && d+1 == allID[0].length) {
          		System.out.println("we don't all have parts to assemble " + type);  
        	  }
    	  }
         }
        return costOfParts;
    }
	
    /**
     * lookThrough is a private helper method that checks the ID to see if there 
	 * are missing elements in the 2d array.
     * If it does it returns true and we update the boolean array that calls it.
     * @param currID String of the current filing ID.
     * @param allParts Boolean array that indicates which part is available or not.
     * @param res StringBuffer
     * @param whatToUpdate StringBuffer
     * @return
     */
	private int lookThrough(String currID, boolean[] allParts, StringBuffer res, StringBuffer whatToUpdate) 
	{
		// already used to meant to indicate that the cost has already been added
		System.out.println("ID passed to lookthrouhg is " + currID);
		boolean already_used =false;
		 int retCost =0;
		 int row=0;
		for (int e=0; e<allParts.length; e++) {
			// if it equals false it means that part is missing
			// and we check if we can find it in the other ID
			// ALso because I have hardcorded the columns, I know that railsMax(0), cabinetMax(1), drawersMax(2), CushionMax(3)
			
			// if ID equal one of the IDs that is store in allCabinets etc, then we know we can use that part and we need to include it to cost
			if(allParts[e] == false) {
				switch(e)  
				{
				case 0:{
					// looking for rails
					for (int i =0; i < allRails.length; i++) {
						if (allRails[i][0].equals(currID)) {
							System.out.println(currID + " has the missing rails");
						allParts[e] = true;
						row =i;
						whatToUpdate.append("rails from: " + currID + "\n");
						break;
						}
					}
					
					if (already_used == false && allParts[e] == true) {
						retCost= Integer.parseInt(allRails[row][2].toString());
						already_used =true;
						res.append("\nID: "+allRails[row][0]);
					}
					break;
				}

					case 1: {
						// looking for a Cabinet
						for (int i =0; i < allCabinet.length; i++) {
							if (allCabinet[i][0].equals(currID)) {
							System.out.println(currID + " has the missing Cabinet");
							allParts[e] = true;
							row =i;
							whatToUpdate.append("Cabinets from: " + currID + "\n");
							break;
							}
						}
						
						if (already_used == false && allParts[e] == true) {
							retCost= Integer.parseInt(allCabinet[row][2].toString());
							System.out.println("the price of " + allCabinet[row][0] + " is " + retCost);
							res.append("\nID: "+ allCabinet[row][0]);
							already_used =true;
						}
						break;
					}
					case 2: {
						// looking for a Drawers
						for (int i =0; i < allDrawers.length; i++) {
							if (allDrawers[i][0].equals(currID)) {
							System.out.println(currID + " has the missing Drawers");
							allParts[e] = true;
							row =i;
							whatToUpdate.append("Drawers from: " + currID + "\n");
							break;
							}
						}
						if (already_used == false && allParts[e] == true) {
							retCost= Integer.parseInt(allDrawers[row][2].toString());
							already_used =true;
							res.append("\nID: "+allDrawers[row][0]);
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
