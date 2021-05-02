package edu.ucalgary.ensf409;

import java.sql.*;
import java.io.*;
import java.util.*;
/**
 * This class is used to help AssemblyDesk sort through all the parts from the database and 
 * find the cheapest combination of desks.
 * @author Ethan Conrad, Isaiah Asaolu, Tsenguun Ulambayar
 * @version 1.3
 */
public class HelpCreateDesk {
	
	 private StringBuffer updateToDatabase = new StringBuffer();

	 private String type;
 	 private Object[][] allLeg;
 	private Object[][] allTop;
 	private	 Object[][] allDrawer;

 	 private StringBuffer output = new StringBuffer();
 		/**
 	  * Constructor for class HelpCreateDesk that assigns values to type, allLeg, allTop, 
	   and allDrawer.
 	  * @param user specified type of furniture from AssemblyDesk
 	  * @param allLeg2 String[][] containing legs taken from database
 	  * @param allTop2 String[][] containing top taken from database
 	  * @param allDrawer2 String[][] containing drawer taken from database
 	  */
		public HelpCreateDesk(String type, Object[][] allLeg2, Object[][] allTop2, Object[][] allDrawer2)
		{
			
		this.type = type;
		this.allLeg = allLeg2;
		this.allTop = allTop2;
		this.allDrawer = allDrawer2;
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
	 * component of the ID was used to create the furniture. eg Leg from: C0123
	 * @return String containing IDs
	 */	
	public String getDatabaseUpdateString() {
		return updateToDatabase.toString();
	}
	/**
	 * constructDesk method sorts through all the desks to find cheapest combination.
	 * @param retrieveIDOfMax2 String that contains the ID of desk with max number of parts .
	 * @param totalPrice2 Integer that contains total price of the desk.
	 * @param allID 2D string array that contains all IDs of desk.
	 * @param getLeg String[] that leg IDs.
	 * @param getTop String[] that contains top IDs.
	 * @param getDrawer String[] that contains drawer IDs.
	 * @return Price to construct the desk
	 * @throws Exception
	 */	
	public	int constructDesk(String retrieveIDOfMax2, int totalPrice2, String[][] allID, String[] getLeg,  String[] getTop,String[] getDrawer) throws Exception{
		int retPrice =0;
		boolean legMax = false; // booleans used to check if respective part is found
		boolean topMax = false;
		boolean drawerMax = false; 
		
		int missing=0;
		int totalPrice =0;
		totalPrice = totalPrice2;
		
		String retrieveIDOfMax = retrieveIDOfMax2;
		
	// find Legs
	for (int b = 0; b < allLeg.length ; b++) 
	{

		if (allLeg[b][0].equals(retrieveIDOfMax)) {
			legMax = true;
			updateToDatabase.append("Legs from: " + retrieveIDOfMax + "\n");

			break;
		} else if (allLeg[b][0] != retrieveIDOfMax && b+1 == allLeg.length)  {
			legMax = false;
		}
	}
	
	// find Top
	for (int b=0; b< allTop.length; b++) {
		if (allTop[b][0].equals(retrieveIDOfMax)) {
			topMax = true;
			updateToDatabase.append("Top from: " + retrieveIDOfMax + "\n");
			break;
		} else if (allTop[b][0] != retrieveIDOfMax && b+1 == allTop.length)  {
			topMax = false;
		}
	}
	
	// find Drawer
	for (int b=0; b< allDrawer.length; b++) {
		if (allDrawer[b][0].equals(retrieveIDOfMax)) {
			drawerMax = true;
//			output.append("Drawer from: " + retrieveIDOfMax + "\n");
			updateToDatabase.append("Drawer from: " + retrieveIDOfMax + "\n");

			break;
		} else if (allDrawer[b][0] != retrieveIDOfMax && b+1 == allDrawer.length)  {
			drawerMax = false;
		}
	}
	
	// so then if we find how many it's missing.
	// If it's missing just 1 we find it and put it in
	// However if it's missing more than one we have check if the ID with the second most yes has both elements 

	
	if (legMax == false) {
		missing++;
	}
	if (topMax == false) {
		missing++;
	}

	if (drawerMax == false) {
		missing++;
	}
	
	// if it's not missing all three, then write the ID to the String
	if (missing < 3) {
		output.append("\nID: " + retrieveIDOfMax);
	}
//	System.out.println(output.toString() + " CushionMax is  " + CushionMax + " legMax is " + legMax + " topMax is " + topMax + " drawerMax is " + drawerMax);
	
	
	int addToTotalPrice_option1 = 0;
	int addToTotalPrice_option2 = 0;
 

//	System.out.println("\n Missing equals " + missing +"\n");
	
	// if it's missing 1 find the cheapest way to assemble, same for 0
	if (missing == 0 || missing == 1) {
		StringBuffer resZero = new StringBuffer();
		StringBuffer WhatToUpdate_Zero = new StringBuffer();
		totalPrice += fillInPart(legMax,topMax,drawerMax, getLeg, getTop,getDrawer, resZero,WhatToUpdate_Zero);
		output.append(resZero);
		updateToDatabase.append(WhatToUpdate_Zero);
		return totalPrice;
	}	 
	// if it's missing more than 1, we have to check if the second most occurrence of yes has the elements we are missing
	// Also the allID[][] include the ID and the occurences of Y, so on we are going to get the next large ID. then see if it has 1 or all the parts we need. 
	else if (missing > 1) {
		boolean[] findParts = {legMax, topMax, drawerMax};
		
 //   	System.out.println("The current ID is " + retrieveIDOfMax);
		
		// the first method works by first sorting the array by the number of occurennce and then seeing if it has the part
		// the method will be success if the largest or next largest occurence has all of the missing objects
//		System.out.println("[");
//		System.out.println("option 1: ");
		StringBuffer WhatToUpdate = new StringBuffer();
		StringBuffer res = new StringBuffer();
		addToTotalPrice_option1 = singleLargest(allID,  retrieveIDOfMax,findParts,res,WhatToUpdate);
		System.out.println(res.toString());
//		System.out.println("Option 1 price is(Not the total price): " +addToTotalPrice_option1);
		
		
		// we then check with the method we used when missing was 1
		System.out.println();
//		System.out.println("option 2: ");
		StringBuffer res2 = new StringBuffer();
		StringBuffer WhatToUpdateOption_2 = new StringBuffer();
		addToTotalPrice_option2 = fillInPart(legMax,topMax,drawerMax, getLeg, getTop,getDrawer,res2,WhatToUpdateOption_2);
		System.out.println(res2.toString());
//		System.out.println("Option 2 price is(Not the total price): " +addToTotalPrice_option2);

		System.out.println();

		if (addToTotalPrice_option1 < addToTotalPrice_option2) {
//			System.out.println("using option 1, combined with total price is gives us: " + (totalPrice +addToTotalPrice_option1));
			retPrice= totalPrice +addToTotalPrice_option1;
			output.append(res);
			updateToDatabase.append(WhatToUpdate);
			
		}
		else if (addToTotalPrice_option1 == addToTotalPrice_option2) {
//			System.out.println("Both options are the same price so I going to pick option1 .So using option 1, combined with total price is gives us: " + (totalPrice +addToTotalPrice_option1));
			retPrice= totalPrice +addToTotalPrice_option1;
			output.append(res);
			updateToDatabase.append(WhatToUpdateOption_2);
		}
		else {
//			System.out.println("using option 2, combined with total price is gives us: " + (totalPrice +addToTotalPrice_option2));
			retPrice= totalPrice +addToTotalPrice_option2;
			output.append(res2);
			updateToDatabase.append(WhatToUpdateOption_2);
		}
//		System.out.println("\n]");

		return retPrice;
		
	}
	// if this returns something went wrong
	return -5000;

	}
	
	/**
	 * fillInPart method sorts through parts to fill in what is missing
	 * @param legMax boolean that indicates whether the desk has legs
	 * @param topMax boolean that indicates whether the desk has top
	 * @param drawerMax boolean that indicates whether the desk has a drawer
	 * @param getLeg String array that contains IDs of desks with legs
	 * @param getTop String array that contains IDs of desks with top
	 * @param getDrawer String array that contains IDs of desks with a drawer
	 * @param res2 StringBuffer 
	 * @param whatToUpdate_Single StringBuffer
	 * @return price
	 */
	private int fillInPart(boolean legMax, boolean topMax, boolean drawerMax, String[] getLeg, String[] getTop, String[] getDrawer, StringBuffer res2, StringBuffer whatToUpdate_Single) {
		String getMissingLeg = null;
		String getMissingTop = null;
		int price = 0;
		
		//checks what is missing
		try {
		if (legMax == false) {
//			System.out.println("getting legs");
			String[] tempMax = getLeg;
			getMissingLeg = tempMax[0]  ;
			price += Integer.parseInt(tempMax[1]);
			res2.append("\nID: "+tempMax[0]);
			legMax = true;
			whatToUpdate_Single.append("Legs from: " + tempMax[0] + "\n");
	//		System.out.println(tempMax[0] + "  " + tempMax[1]);
		} 
		
		 if (topMax == false) {
//			System.out.println("getting Top");
			String[] tempTop= getTop;
			
			if (tempTop[0].equals(getMissingLeg)) {
				// we already have the ID
			} else {
			getMissingTop = tempTop[0];
			res2.append("\nID: "+tempTop[0]);
			}
			
			price += Integer.parseInt(tempTop[1]);
			topMax =true;
			updateToDatabase.append("Top from: " + tempTop[0] + "\n");
	//		System.out.println(tempTop[0] + "  " + tempTop[1]);
		}
		 
		 if (drawerMax == false) {
//			System.out.println("getting drawers");
			String[] tempDrawer= getDrawer;
			
			if (tempDrawer[0].equals(getMissingLeg) || tempDrawer[0].equals(getMissingTop)) {
				// we already have the ID
			} else {
				res2.append("\nID: "+tempDrawer[0]);
			}
			
			price += Integer.parseInt(tempDrawer[1]);
			drawerMax = true;
			whatToUpdate_Single.append("Drawer from: " + tempDrawer[0] + "\n");
//			System.out.println(tempDrawer[0] + "  " + tempDrawer[1]);
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
	 * @param allID 2D String array that contains IDs of all desks
	 * @param ID_ofCurrentMax String that contains ID of desk that has the current maximum parts
	 * @param allParts boolean arraay that indicates which parts the desk has
	 * @param res StringBuffer
	 * @param whatToUpdate StringBuffer
	 * @return integer that indicates the cost of the parts needed
	 */
    private int singleLargest(String[][] allID, String ID_ofCurrentMax,boolean[] allParts, StringBuffer res, StringBuffer whatToUpdate)
    {
    	int costOfParts = 0; 
        // first we are going to sort the array by the integer value of it's occurence
        String [][] sortedallID = sort2DArray(allID);
        System.out.println();

        // Initialize maximum element. which is going to be the second element in the array, cause the first is the max
         //max = Integer.parseInt(allID[1][1]);
         
         // so we check if the the next occurene yes include 1 or all of the missing yes.
         // we keep looping to we have found all missing
         // if we can't find it we write, that wouldn't find <inserNameOfPart> 
 //  	 System.out.print("changes is : ");
  	  for (int f=0; f< allParts.length; f++) {
//		  System.out.print(allParts[f] + ", ");
	  }System.out.println();
	  
         for (int d=0; d<sortedallID[0].length; d++) {
        	 String currID = sortedallID[0][d];
    	  if (!currID.equals(ID_ofCurrentMax)) {
    		  
        	 // next we'll send that ID and the boolean array to another method to see if we can get the missing parts
    	  costOfParts += lookThrough(currID, allParts, res,whatToUpdate);
   //     	 System.out.print("changes is : ");
           	  for (int f=0; f< allParts.length; f++) {
     //   		  System.out.print(allParts[f] + ", ");
        	  }System.out.println();
        	  
        	  if(allParts[0] == true && allParts[1] == true && allParts[2] == true) {
       // 		System.out.println("all parts have been found we can assemble " + type);  
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
     * @param currID String of the current desk ID.
     * @param allParts Boolean array that indicates which part is available or not.
     * @param res StringBuffer
     * @param whatToUpdate StringBuffer
     * @return
     */
	private int lookThrough(String currID, boolean[] allParts, StringBuffer res, StringBuffer whatToUpdate) 
	{
		// already used to meant to indicate that the cost has already been added
//		System.out.println("ID passed to lookthrouhg is " + currID);
		boolean already_used = false;
		 int retCost = 0;
		 int row= 0;
		for (int e = 0; e < allParts.length; e++) {
			// if it equals false it means that part is missing
			// and we check if we can find it in the other ID
			// ALso because I have hardcorded the columns, I know that legMax(0), topMax(1), drawerMax(2), CushionMax(3)
			
			// if ID equal one of the IDs that is store in allTops etc, then we know we can use that part and we need to include it to cost
			if(allParts[e] == false) {
				switch(e)  
				{
				case 0:{
					// looking for a leg
					for (int i =0; i < allLeg.length; i++) {
						if (allLeg[i][0].equals(currID)) {
//							System.out.println(currID + " has the missing Legs");
						allParts[e] = true;
						row =i;
						whatToUpdate.append("Legs from: " + currID + "\n");
						break;
						}
					}
					
					if (already_used == false && allParts[e] == true) {
						retCost= Integer.parseInt(allLeg[row][2].toString());
						already_used =true;
						res.append("\nID: "+allLeg[row][0]);
					}
					break;
				}

					case 1: {
						// looking for a Top
						for (int i =0; i < allTop.length; i++) {
							if (allTop[i][0].equals(currID)) {
				//			System.out.println(currID + " has the missing top");
							allParts[e] = true;
							row =i;
							whatToUpdate.append("tops from: " + currID + "\n");
							break;
							}
						}
						
						if (already_used == false && allParts[e] == true) {
							retCost= Integer.parseInt(allTop[row][2].toString());
			//				System.out.println("the price of " + allTop[row][0] + " is " + retCost);
							res.append("\nID: "+ allTop[row][0]);
							already_used =true;
						}
						break;
					}
					case 2: {
						// looking for a drawer
						for (int i =0; i < allDrawer.length; i++) {
							if (allDrawer[i][0].equals(currID)) {
			//				System.out.println(currID + " has the missing drawer");
							allParts[e] = true;
							row =i;
							whatToUpdate.append("drawer from: " + currID + "\n");
							break;
							}
						}
						if (already_used == false && allParts[e] == true) {
							retCost= Integer.parseInt(allDrawer[row][2].toString());
							already_used =true;
							res.append("\nID: "+allDrawer[row][0]);
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
