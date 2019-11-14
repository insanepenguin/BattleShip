import java.io.Serializable;
//ALL SHIPS ARE INSTANCE OF THIS CLASS!
//take all attributes used to create the ship add it to a global array once their all in.

//USE GETTERS AND SETTERS FOR EVERYTHING

//once client recieves that change the GUI to how we actually play the game.

public class Ship implements Serializable
{//open class

   //here are the attributes of our ship class
   private String name; //the name of the ship
   private int arrayLength; //the length of the ship, in coordinate squares
   private int startX; //the horizontal startpoint of the ship
   private int startY; //the vertical startpoint of the ship
   private boolean orientation; //whether or not the ship is vertical or horizontal
   
   public Ship(String _name, int _arrayLength, int _x, int _y, boolean _orientation) {
   
      //Array, two ints, orientation
      
      //I'M NOT SURE HOW THIS IS GOING TO BE IMPLEMENTED, BUT I'M NEED TO SEE IT BEFORE I CONTINUE SUDO CODE ON THE OTHER CLASSES
      //I believe 4 Outer Classes will be more than enough to handle the game (and as many inner/anonymous classes as we need)
   
   
      //name, length, two ints, orientation
      name = _name;
      arrayLength = _arrayLength;
      startX = _x;
      startY = _y;
      orientation = _orientation;

   }
   
   //getter methods for each of the attributes. 
   //I've elected not to include setters, as we'll
   //pass it everything it needs on instantiation
   //and none of the attributes are going to change
   public String getName() 
   {//open getName method
      return name;
   }//close getName method
   
   public int getArrayLength() 
   {//open getArrayLength method
      return arrayLength;
   }//close getArrayLength method
   
   public int getStartX()
   {//open getStartX method
      return startX;
   }//close getStartX method
   
   public int getStartY() 
   {//open getStartY method
      return startY;
   }//close getStartY method
   
   public boolean getOrientation() 
   {//open getOrientation method
      return orientation;
   }//close getOrientation method
   
   public String toString() 
   {//open toString method
      return String.format("%s, %d, %d, %d, %b", getName(), getArrayLength(), getStartX(), getStartY(), getOrientation());
   }//close toString method
   
   //method for acquiring getting the coordinates the
   //ship occupies. The server can call this for the 
   //ship objects the client sends it
   public String[] getCoordinates() 
   {//open getCoordinates method
      String[] coords = new String[arrayLength];
      if(orientation) 
      {//open if
         for(int i = 0; i < arrayLength; i++) 
         {//open for loop
            coords[i] = startX + ", " + (startY + i);
         }//close foor loop
      }//close if
      else 
      {//open else
         for(int i = 0; i < arrayLength; i++) 
         {//open for loop
            coords[i] = (startX + i) + ", " + startY;
         }//close for loop
      }//close else
      return coords;
   }//close getCoordinates method
}//close class
