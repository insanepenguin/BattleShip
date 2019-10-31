//ALL SHIPS ARE INSTANCE OF THIS CLASS!
//take all attributes used to create the ship add it to a global array once their all in.

//SERVER RECIEVES ARRAY OF SHIPS

//SERVER BUILDS THE GRID
//play game method, once both players are built sleep (short period of time)
//game starts (do-while, while(win-condition: false) -> make it a thread?
//take turn methods? (2 ints that are pushed into the method -> shots coordinates)
//player1 take turn? player2 take turn? 
//on win-condition we break out, send health pools back into the client.
//Currently NO TURNS

//WHEREVER THERE IS A SHIP IT WILL PLACE A TRUE, EVERYWHERE ELSE WILL BE FALSE

//USE GETTERS AND SETTERS FOR EVERYTHING

//PLAYER CLASS which has a grid that is passed in (10/10 2d array with a string that is their name)
//String names (player1 and player2)
//int for their hp (summation of the length of the arrays)

//server waits until it gets both array, builds them in the order they come in

//wait for turns, after they hit the ready (Message back from server confirmation that the game is starting/grid was valid)

//once client recieves that change the GUI to how we actually play the game.

//

public class Ships
{//open class
   Ships(String Name, int Arraylength, int X, int Y, boolean Orientation)
   {//open constructor
   
      //Array, two ints, orientation
   
   }//close constructor
}//close class