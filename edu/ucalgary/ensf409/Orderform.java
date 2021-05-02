package edu.ucalgary.ensf409;
import java.io.*;

/**
 * Class Orderform was created as part of ENSF 409 W21 group project.
 * This class is used to generate an orderform and remove the 
 * selected items from the inventory once the form is created.
 */
public class Orderform {
    public String category;
    public String type;
    public int quantity;
    public String [] itemIDs;
    public String totalPrice;
    
    
    /**
     * constructor for Orderform that accepts the properties of an order
     * @param cat category of requested furniture
     * @param t type of requested furniture
     * @param q quantity of requested furniture
     * @param ids IDs of required furniture items
     * @param price total price of required furniture items
     */
    public Orderform(String cat, String t, int q, String[] ids, String price){
        category = cat;
        type = t;
        quantity = q;
        itemIDs = ids;
        totalPrice = price;
    }
    /**
     * Getter for category
     * @return category
     */
    public String getCategory(){
        return category;
    }
    /**
     * Getter for type
     * @return type
     */
    public String getType(){
        return type;
    }
    /**
     * Getter for quantity of requested furniture items.
     * @return quantity
     */
    public int getQuantity(){
        return quantity;
    }
    /**
     * Gets the ID specified by the inputted index from the string array of item IDs
     * @param index of the desired ID from string array itemIDs
     * @return a single string ID from string array itemIDs
     */
    public String getItemID(int index){
        return itemIDs[index];
    }
    /**
     * Getter for totalPrice
     * @return totalPrice
     */
    public String getPrice(){
        return totalPrice;
    }
    /**
     * method for class Orderform that will generate the form using instance variable
     * values. Returns void.
     */
    public void generateForm(){
        //creates a new file called orderform
        //writes into the orderform.txt file. System.getProperty("line.separator")
        //works the same as \n to start new line.
        try {
            FileWriter file = new FileWriter("orderform.txt");
            BufferedWriter bf = new BufferedWriter(file);
            bf.write("Furniture Order Form"+ System.getProperty("line.separator"));
            bf.write(System.getProperty("line.separator")+"Faculty Name:");
            bf.write(System.getProperty("line.separator")+"Contact:");
            bf.write(System.getProperty("line.separator")+"Date:");
            bf.write(System.getProperty("line.separator"));
            bf.write(System.getProperty("line.separator")+"Original Request: ");
            bf.write(getType()+" "+getCategory()+", "+getQuantity());
            bf.write(System.getProperty("line.separator"));
            bf.write(System.getProperty("line.separator")+"Items Ordered");
            for(int i=0; i<quantity; i++){
                bf.write(System.getProperty("line.separator")+"ID: "+getItemID(i));
            }
            bf.write(System.getProperty("line.separator"));
            bf.write(System.getProperty("line.separator")+"Total Price: $"+getPrice());
            bf.close();
            System.out.println("Successfully created an orderform.");
            }catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            }
    }
}
