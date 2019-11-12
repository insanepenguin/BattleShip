import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//SERVER

//SERVER RECIEVES ARRAY OF SHIPS

//SERVER BUILDS THE GRID
//play game method, once both players are built sleep (short period of time)
//game starts (do-while, while(win-condition: false) -> make it a thread?
//take turn methods? (2 ints that are pushed into the method -> shots coordinates)
//player1 take turn? player2 take turn? 
//on win-condition we break out, send health pools back into the client.
//Currently NO TURNS

//WHEREVER THERE IS A SHIP IT WILL PLACE A TRUE, EVERYWHERE ELSE WILL BE FALSE

//server waits until it gets both array, builds them in the order they come in

//wait for turns, after they hit the ready (Message back from server confirmation that the game is starting/grid was valid)

public class BattleshipServer
{//open class
   Boolean win_Condition = false;
   int player1HP = 17;
   int player2HP = 17;
   BattleshipClient player2;
   Boolean[][] player1Field = new Boolean[10][10];
   Boolean[][] player2Field = new Boolean[10][10];
   Boolean yourTurn = true;
   Boolean hit = false;
   String winner;
   
   ServerSocket ss;
   Socket cs;
   ObjectInputStream ois;                  //declare globally!
   ObjectOutputStream oos;
   Ship[] shipsReadIn;
   
   private JFrame jfServerFrame = new JFrame();
   private JTextArea jtaDiagnostics = new JTextArea(20,35);
   private JScrollPane jspScroller = new JScrollPane(jtaDiagnostics, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
   private JScrollBar ScrollBar = jspScroller.getVerticalScrollBar();
   private JPanel jpCenter = new JPanel(); 

   //may need to take in Player1 and Player2??
   BattleshipServer(/* Maybe take an Array of Coordinates for the ships? or an Array of Ships if ships implemented properly*/)
   {//open constructor
          
      //This should build a new version of the Grid from Grid, for each player!
      //This will deal with hit-handling, saving grids, HP, running the game
      //method for running game (Do-While)
   
      //SET THE BOOLEAN FIELDS TO FALSE FOR BOTH PLAY FIELDS!
      for(int y = 0; y < 10; y++)
      {//open 1st for
         for(int z = 0; z < 10; z++)
         {//close 2nd for
            player1Field[y][z] = false;
            player2Field[y][z] = false;
         }//close 2nd for
      }//close 1st for           

      jfServerFrame.setLayout(new BorderLayout(5,10));
      jtaDiagnostics.setEnabled(false);
      jpCenter.add(jspScroller);
      jfServerFrame.add(jpCenter, BorderLayout.CENTER);
      
      jfServerFrame.setTitle("Battleship Server by JavaPack Survivors");
      jfServerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jfServerFrame.setLocationRelativeTo(null);
      jfServerFrame.pack();
      jfServerFrame.setVisible(true);
      Thread[] playersReady = new Thread[2];
      try
      {//open try
         jtaDiagnostics.setText("Server launched @" + InetAddress.getLocalHost().getHostAddress());
         ss = new ServerSocket(16789);
         for(int i = 0; i < 2; i++)
         {//open for loop
            cs = ss.accept();
            Reciever thread = new Reciever(cs, i);
            playersReady[i] = thread;
            thread.start();
            console("Player " + (i + 1) + " Connected @" + cs.getInetAddress());       //update text area saying someone connected
         }//close for loop
      }//close try
      catch(IOException ioe)
      {//open catch
         ioe.printStackTrace();
      }//close catch
      console("Both players connected"); 
      
      //waiting for the players to send their ships 
      for(int i = 0; i < 2; i++) {
         try {
            playersReady[i].join();
         }
         catch(InterruptedException ie) {
            ie.printStackTrace();
         }
      }
      console("Ships recieved, starting game");
      
      /*AFTER THIS POINT IS THE GAME LOGIC, IN ORDER
      THIS IS A VERY IMPORTANT BLOCK OF CODE.
      THIS IS WHERE THE LOGIC OF THE GAME IS ACTUALLY HAPPENING
      PAY ATTENTION HERE!
      THIS IS ALL THE NEW STUFF (plus hitDetection Method)*/
      try{//open try  
         while(!win_Condition){//open while loop
            oos.writeBoolean(yourTurn);
            
            int xCoord = ois.readInt();
            int yCoord = ois.readInt();
            hitDetection(xCoord, yCoord);
            oos.writeBoolean(win_Condition);
         }//close while loop
         oos.writeUTF(winner);
      }//close try
      catch(IOException ioe){//open catch
      ioe.printStackTrace();
   }//close catch
      
   }//close constructor
   
   //a method so we don't have to be like "jtaDiagnostics.setText(jtaDiagnostics.getText() + '\n' + w/e) every time lol
   public void console(String msg) 
   {//open console updating method
      jtaDiagnostics.setText(jtaDiagnostics.getText() + '\n' + msg);
   }//close console updating method
   
   public class Reciever extends Thread {
      Socket sock;
      int player;
      
      public Reciever(Socket cs, int i) {
         cs = sock;
         player = i;
      }
      
      public void run() {
         try
         {//open try
            ois = new ObjectInputStream(cs.getInputStream());                       //instantiate locally!
            Object incoming = ois.readObject();
            if(incoming instanceof Ship[])                     //compare if what came in is an instance of an array of the Ship class
            {//open if                                         if it is, make our Ship array equal to the incoming object casted as a ship array
               shipsReadIn = (Ship[])incoming;                 //and print it out to the text area using the toString method in a for loop
               for(int j = 0; j < shipsReadIn.length; j++) {   //FALSE for Orientation = HORIZONTAL, TRUE for Orientation = Vertical
                  //this is setting the boolean values of the grids to true for the ships placement!
                  for(int i = 0; i < shipsReadIn[j].getCoordinates().length; i++) {
                     String[] assignable = shipsReadIn[j].getCoordinates()[i].split(", ");
                     synchronized(win_Condition) {
                        if(player == 0) {
                           player1Field[Integer.parseInt(assignable[0])][Integer.parseInt(assignable[1])] = true;
                        }
                        else {
                           player2Field[Integer.parseInt(assignable[0])][Integer.parseInt(assignable[1])] = true;
                        }
                     }
                  }//close second for loop
               }//close first for loop
               
               //TEST SHIT HERE
               System.out.println("PLAYER 1 GRID");
               for(int y = 0; y < 10; y++) {
                  for(int x = 0; x < 10; x++) {
                     System.out.print(String.format("%6b",player1Field[x][y]));
                  }
                  System.out.print("\n");
               }
               System.out.println("\nPLAYER 2 GRID");
               for(int y = 0; y < 10; y++) {
                  for(int x = 0; x < 10; x++) {
                     System.out.print(String.format("%6b",player2Field[x][y]));
                  }
                  System.out.print("\n");
               }
               System.out.println("\n\n\n");
            }//close if
            else
            {//open else                                         if it isn't, print an error message to the text area
               console("ERROR! OBJECT READ IN WASN'T A SHIP!");
            }//close else
         }//close try
         catch(IOException ioe)
         {//open catch
            ioe.printStackTrace();
         }//close catch
         catch(ClassNotFoundException cnfe)
         {//open 2nd catch
            cnfe.printStackTrace();
         }//close 2nd catch
      }
   }
   
   //This is the code that will handle if something is hit
   public void hitDetection(int x, int y){//open hitDetection method
      if(yourTurn){//open 1st if
         console("Shot taken at " + x + " " + y);
         if(player2Field[x][y]){//open 2nd if
            hit = true;
            player2HP--;
            console("Hit!");
            if(player2HP == 0){//open 3rd if
               win_Condition = true;
               winner = "Player 1 has won!";
            }//close 3rd if
         }//close 2nd if
         console("Miss!");
         yourTurn = false;
      }//close 1st if
      else{
         console("Shot taken at " + x + " " + y);
         if(player1Field[x][y]){//open 1st if
            hit = true;
            player1HP--;
            console("Hit!");
            if(player1HP == 0){//open 2nd if
               win_Condition = true;
               winner = "Player 2 has won!";
            }//close 2nd if
         }//close 1st if
         console("Miss!");
         yourTurn = true;
      }//close else
   }//close hitDetection method
   
   //If we don't thread it we can just use Player1.getHP() / Player2.getHP(), whichever player's current turn is going
   // if we thread it, we should be able to do Player.getHP() regardless.
   //Method? or just Check at start of each turn?
   //if Player.getHP() == 0
   //    win_Condition = true;
   //else
   //    
   public static void main(String[] args)
   {//open main
      new BattleshipServer();
   }//close main
}//close class
