package edu.ucalgary.ensf409;

import java.sql.*;
import java.io.*;
import java.util.*;
/**
 * This class is used to help AssemblyChair sort through all the parts from the database and 
 * find the cheapest combination of chairs.
 * @author Ethan Conrad, Isaiah Asaolu, Tsenguun Ulambayar
 * @version 1.3
 */
public class HelpCreateChair {
	
	private StringBuffer updateToDatabase = new StringBuffer();

	private String type;
 	private Object[][] allLeg;
 	private Object[][] allArm;
 	private	 Object[][] allSeat;
 	private Object[][] allCushion;

 	private StringBuffer output = new StringBuffer();
 	
	/**
 	  * Constructor for class HelpCreateChair that assigns values to type, allLeg, allArm,
	   allSeat, and allCushion.
 	  * @param user specified type of furniture from AssemblyChair
 	  * @param allLeg2 String[][] containing legs taken from database
 	  * @param allArm2 String[][] containing arms taken from database
 	  * @param allSeat2 String[][] containing seats taken from database
 	  * @param allCushion2 String[][] containing cushions taken from database
 	  */
	public HelpCreateChair(String type, Object[][] allLeg2, Object[][] allArm2, Object[][] allSeat2, Object[][] allCushion2 )
	{

		this.type = type;
		this.allLeg = allLeg2;
		this.allArm = allArm2;
		this.allSeat = allSeat2;
		this.allCushion = allCushion2;
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
	 * constructChair method sorts through all the chairs to find cheapest combination.
	 * @param retrieveIDOfMax2 String that contains the ID of chair with max number of parts .
	 * @param totalPrice2 Integer that contains total price of the chair.
	 * @param allID 2D string array that contains all IDs of chairs.
	 * @param getLeg String[] that leg IDs.
	 * @param getArm String[] that contains arm IDs.
	 * @param getSeat String[] that contains seat IDs.
	 * @param getCushion String[] that contains cushion IDs.
	 * @return Price to construct the chair
	 * @throws Exception
	 */	
	public	int constructChair(String retrieveIDOfMax2, int totalPrice2, String[][] allID, String[] getLeg,  String[] getArm,String[] getSeat, String[] getCushion) throws Exception{
		int retPrice =0;
		boolean legMax = false; // booleans used to check if respective part is found
		boolean armMax = false; 
		boolean seatMax = false; 
		boolean cushionMax = false; 
		
		int missing = 0; // number of parts missing
		int totalPrice = 0;
		totalPrice = totalPrice2;
		
		String retrieveIDOfMax = retrieveIDOfMax2;
		
	// find Legs
	for (int b=0; b< allLeg.length ; b++) 
	{

		if (allLeg[b][0].equals(retrieveIDOfMax)) {
			legMax = true; // legs
			updateToDatabase.append("Legs from: " + retrieveIDOfMax + "\n");

			break;
		} else if (allLeg[b][0] != retrieveIDOfMax && b+1 == allLeg.length)  {
			legMax = false;
		}
	}
	
	// find Arms
	for (int b=0; b< allArm.length; b++) {
		if (allArm[b][0].equals(retrieveIDOfMax)) {
			armMax = true;
			updateToDatabase.append("Arms from: " + retrieveIDOfMax + "\n");
			break;
		} else if (allArm[b][0] != retrieveIDOfMax && b+1 == allArm.length)  {
			armMax = false;
		}
	}
	
	// find Seats
	for (int b=0; b< allSeat.length; b++) {
		if (allSeat[b][0].equals(retrieveIDOfMax)) {
			seatMax = true;
			updateToDatabase.append("Seat from: " + retrieveIDOfMax + "\n");

			break;
		} else if (allSeat[b][0] != retrieveIDOfMax && b+1 == allSeat.length)  {
			seatMax = false;
		}
	}
	
	// find allCushion
	for (int b=0; b< allCushion.length; b++) {
		if (allCushion[b][0].equals(retrieveIDOfMax)) {
			updateToDatabase.append("Cushion from: " + retrieveIDOfMax + "\n");
			cushionMax = true;
			break;
		} else if (allCushion[b][0] != retrieveIDOfMax && b+1 == allCushion.length)  {
			cushionMax = false;
			break;
		}
	}
	
	// so then if we find how many it's missing.
	// If it's missing just 1 we find it and put it in
	// However if it's missing more than one we have check if the ID with the second most yes has both elements 

	
	if (legMax == false) {
		missing++;
	}
	if (armMax == false) {
		missing++;
	}

	if (seatMax == false) {
		missing++;
	}
	if(cushionMax == false) {
		missing++;
	}
	
	// if it's not missing all four, then write the ID to the String
	if (missing< 4) {
		output.append("\nID: " + retrieveIDOfMax);
	}

	
	int addToTotalPrice_option1 =0;
	int addToTotalPrice_option2 =0;
	
	// if it's missing 1 find the cheapest way to assemble, same for 0
	if (missing == 0 || missing == 1) {
		StringBuffer resZero = new StringBuffer();
		StringBuffer WhatToUpdate_Zero = new StringBuffer();
		totalPrice += fillInPart(legMax,armMax,seatMax,cushionMax, getLeg, getArm,getSeat, getCushion, resZero,WhatToUpdate_Zero);
		output.append(resZero);
		updateToDatabase.append(WhatToUpdate_Zero);
		return totalPrice;
	}	 
	// if it's missing more than 1, we have to check if the second most occurrence of yes has the elements we are missing
	// Also the allID[][] include the ID and the occurences of Y, so on we are going to get the next large ID. then see if it has 1 or all the parts we need. 
	else if (missing > 1) {
		boolean[] findParts = {legMax, armMax, seatMax,cushionMax};
		
 //   	System.out.println("The current ID is " + retrieveIDOfMax);
		
		// the first method works by first sorting the array by the number of occurennce and then seeing if it has the part
		// the method will be success if the largest or next largest occurence has all of the missing objects
//		System.out.println("[");
//		System.out.println("option 1: ");
		StringBuffer WhatToUpdate = new StringBuffer();
		StringBuffer res = new StringBuffer();
		addToTotalPrice_option1 = singleLargest(allID,  retrieveIDOfMax,findParts,res,WhatToUpdate);
		System.out.println(res.toString());
		
		// we then check with the method we used when missing was 1
		System.out.println();
//		System.out.println("option 2: ");
		StringBuffer res2 = new StringBuffer();
		StringBuffer WhatToUpdateOption_2 = new StringBuffer();
		addToTotalPrice_option2 = fillInPart(legMax,armMax,seatMax,cushionMax, getLeg, getArm,getSeat, getCushion,res2,WhatToUpdateOption_2);
		System.out.println(res2.toString());
		System.out.println();

		if (addToTotalPrice_option1 < addToTotalPrice_option2) {
			retPrice= totalPrice +addToTotalPrice_option1;
			output.append(res);
			updateToDatabase.append(WhatToUpdate);
			
		}
		else if (addToTotalPrice_option1 == addToTotalPrice_option2) {
			retPrice= totalPrice +addToTotalPrice_option1;
			output.append(res);
			updateToDatabase.append(WhatToUpdateOption_2);
		}
		else {
			retPrice= totalPrice +addToTotalPrice_option2;
			output.append(res2);
			updateToDatabase.append(WhatToUpdateOption_2);
		}
//		System.out.println("\n]");

		return retPrice;
		
	}
	
	return -5000;

	}
	
	/**
	 * fillInPart method sorts through parts to fill in what is missing
	 * @param legMax boolean that indicates whether the chair has legs
	 * @param armMax boolean that indicates whether the chair has arms
	 * @param seatMax boolean that indicates whether the chair has a seat
	 * @param cushionMax boolean that indicates whether the chair has a cushion
	 * @param getLeg String array that contains IDs of chairs with legs
	 * @param getArm String array that contains IDs of chairs with arms
	 * @param getSeat String array that contains IDs of chairs with a seat
	 * @param getCushion String array that contains IDs of chairs with a cushion
	 * @param res2 StringBuffer 
	 * @param whatToUpdate_Single StringBuffer
	 * @return price
	 */
	private int fillInPart(boolean legMax, boolean armMax, boolean seatMax, boolean cushionMax, String[] getLeg, String[] getArm, String[] getSeat, String[] getCushion, StringBuffer res2, StringBuffer whatToUpdate_Single) {
		String getMissingLeg = null;
		String getMissingArm = null;
		String getMissingSeat = null;
		int price = 0;
		
	try {
		if (legMax == false) {
//			System.out.println("getting legs");
			String[] tempMax= getLeg;
			getMissingLeg =tempMax[0]  ;
			price += Integer.parseInt(tempMax[1]);
			res2.append("\nID: "+tempMax[0]);
			legMax = true;
			whatToUpdate_Single.append("Legs from: " + tempMax[0] + "\n");
			System.out.println(tempMax[0] + "  " + tempMax[1]);
		} 
		
		 if (armMax == false) {
			//getting Arms
			String[] tempArm= getArm;
			
			if (tempArm[0].equals(getMissingLeg)) {
				// we already have the ID
			} else {
			getMissingArm = tempArm[0];
			res2.append("\nID: "+tempArm[0]);
			}
			
			price += Integer.parseInt(tempArm[1]);
			armMax =true;
			updateToDatabase.append("Arms from: " + tempArm[0] + "\n");
//			System.out.println(tempArm[0] + "  " + tempArm[1]);
		}
		 
		 if (seatMax == false) {
			//getting seats
			String[] tempSeat= getSeat;
			
			if (tempSeat[0].equals(getMissingLeg) || tempSeat[0].equals(getMissingArm)) {
				// we already have the ID
			} else {
				getMissingSeat= tempSeat[0];
			res2.append("\nID: "+tempSeat[0]);
			}
			
			price += Integer.parseInt(tempSeat[1]);
			seatMax = true;
			whatToUpdate_Single.append("Seat from: " + tempSeat[0] + "\n");
//			System.out.println(tempSeat[0] + "  " + tempSeat[1]);
		}
		 
		 if (cushionMax == false) {
			//getting cushion
			String[] tempCushion = getCushion;
			if (tempCushion[0].equals(getMissingLeg) || tempCushion[0].equals(getMissingArm) || tempCushion[0].equals(getMissingSeat)) {
				// we already have the ID
			} else {
				res2.append("\nID: "+tempCushion[0]);
			}
			price += Integer.parseInt(tempCushion[1]);
			cushionMax = true;
			whatToUpdate_Single.append("Cushion from: " + tempCushion[0] + "\n");
//			System.out.println(tempCushion[0] + "  " + tempCushion[1]);
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
	 * singeLargest is a private helper method that will find the nextMax Array, when we reach it we will check if 
	 * it has the parts we are missing. The boolean of the stuff we are missing is false, indcating that 
	 * current doesn't current have it
	 * @param allID 2D String array that contains IDs of all chairs
	 * @param ID_ofCurrentMax String that contains ID of chair that has the current maximum parts
	 * @param allParts boolean arraay that indicates which parts the chair has
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
 //  	 System.out.print("changes is : ");
  	  for (int f=0; f< allParts.length; f++) {
		  System.out.print(allParts[f] + ", ");
	  }System.out.println();
	  
         for (int d=0; d<sortedAllID[0].length; d++) {
        	 String currID = sortedAllID[0][d];
    	  if (!currID.equals(ID_ofCurrentMax)) {
    		  
        	 // next we'll send that ID and the boolean array to another method to see if we can get the missing parts
    	  costOfParts += lookThrough(currID, allParts, res,whatToUpdate);
   //     	 System.out.print("changes is : ");
           	  for (int f=0; f< allParts.length; f++) {
     //   		  System.out.print(allParts[f] + ", ");
        	  }System.out.println();
        	  
        	  if(allParts[0] == true && allParts[1] == true && allParts[2] == true && allParts[3] == true) {
        		System.out.println("all parts have been found we can assemble " + type);  
        		System.out.println();
        		break;
        	  }
        	  else if ((allParts[0] == false || allParts[1] == false || allParts[2] == false || allParts[3] == false) && d+1 == allID[0].length) {
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
     * @param currID String of the current chair ID.
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
		 int row = 0;
		for (int e = 0; e < allParts.length; e++) {
			// if it equals false it means that part is missing
			// and we check if we can find it in the other ID
			// ALso because I have hardcorded the columns, I know that legMax(0), armMax(1), seatMax(2), cushionMax(3)
			
			// if ID equal one of the IDs that is store in allArms etc, then we know we can use that part and we need to include it to cost
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
						// looking for a Arms
						for (int i =0; i < allArm.length; i++) {
							if (allArm[i][0].equals(currID)) {
	//						System.out.println(currID + " has the missing arm");
							allParts[e] = true;
							row =i;
							whatToUpdate.append("Arms from: " + currID + "\n");
							break;
							}
						}
						
						if (already_used == false && allParts[e] == true) {
							retCost= Integer.parseInt(allArm[row][2].toString());
							System.out.println("the price of " + allArm[row][0] + " is " + retCost);
							res.append("\nID: "+ allArm[row][0]);
							already_used =true;
						}
						break;
					}
					case 2: {
						// looking for a Seat
						for (int i =0; i < allSeat.length; i++) {
							if (allSeat[i][0].equals(currID)) {
			//				System.out.println(currID + " has the missing Seat");
							allParts[e] = true;
							row =i;
							whatToUpdate.append("Seat from: " + currID + "\n");
							break;
							}
						}
						if (already_used == false && allParts[e] == true) {
							retCost= Integer.parseInt(allSeat[row][2].toString());
							already_used =true;
							res.append("\nID: "+allSeat[row][0]);
						}
						break;
					}
					
					case 3: {
						// looking for a Cushion
						for (int i =0; i < allCushion.length; i++) {
			//				System.out.println(currID + " has the missing Cushion");
							if (allCushion[i][0].equals(currID)) {
							allParts[e] = true;
							row =i;
							whatToUpdate.append("Cushion from: " + currID + "\n");
							break;
							}
						}
						if (already_used == false && allParts[e] == true) {
							retCost= Integer.parseInt(allCushion[row][2].toString());
							already_used =true;						
							res.append("\nID:"+ allCushion[row][0]);

						}
						break;
					}
						
						default:
							
				}
				
				// if part became yes we need to add to the cost
//				allCushion
				
			}
		}
		already_used =false;
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
		 ret2D = allID;
		return ret2D;
	}
	
}
