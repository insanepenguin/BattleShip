import java.io.Serializable;
//ALL SHIPS ARE INSTANCE OF THIS CLASS!
//take all attributes used to create the ship add it to a global array once their all in.

//USE GETTERS AND SETTERS FOR EVERYTHING

//once client recieves that change the GUI to how we actually play the game.

public class Ships extends Grid implements Serializable
{//open class

   //here are the attributes of our ship class
   private String name; //the name of the ship
   private int arrayLength; //the length of the ship, in coordinate squares
   private int startX; //the horizontal startpoint of the ship
   private int startY; //the vertical startpoint of the ship
   private boolean orientation; //whether or not the ship is vertical or horizontal
   
   public Ships(String _name, int _arrayLength, int _x, int _y, boolean _orientation)
   {//open constructor
   
      //Array, two ints, orientation
      
      //I'M NOT SURE HOW THIS IS GOING TO BE IMPLEMENTED, BUT I'M NEED TO SEE IT BEFORE I CONTINUE PSUEDO CODE ON THE OTHER CLASSES
      //I believe 4 Outter Classes will be more than enough to handle the game (and as many inner/anonymous classes as we need)
   
   
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
   public String getName() {
      return name;
   }
   public int getArrayLength() {
      return arrayLength;
   }
   public int getStartX() {
      return startX;
   }
   public int getStartY() {
      return startY;
   }
   public boolean getOrientation() {
      return orientation;
   }
   
   //method for acquiring getting the coordinates the
   //ship occupies. The server can call this for the 
   //ship objects the client sends it
   public Grid.Coordinate[] getCoordinates() {
      Coordinate[] coords = new Coordinate[arrayLength];
      if(orientation) {
         for(int i = 0; i < arrayLength; i++) {
            coords[i] = getCoords(startX, startY + i);
         }
      }
      else {
         for(int i = 0; i < arrayLength; i++) {
            coords[i] = getCoords(startX + i, startY);
         }
      }
      return coords;
   }
}//close class
