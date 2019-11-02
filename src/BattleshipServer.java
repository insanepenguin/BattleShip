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
   Boolean Win_Condition = false;
   BattleshipClient player1;
   BattleshipClient player2;
   Boolean[][] PlayField = new Boolean[10][10];
   
   ServerSocket ss;
   Socket cs;
   ObjectInputStream ois;                    //declare globally!
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
      jfServerFrame.setLayout(new BorderLayout(5,10));
      jtaDiagnostics.setEnabled(false);
      jpCenter.add(jspScroller);
      jfServerFrame.add(jpCenter, BorderLayout.CENTER);
      
      jfServerFrame.setTitle("Battleship Server");
      jfServerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jfServerFrame.setLocationRelativeTo(null);
      jfServerFrame.pack();
      jfServerFrame.setVisible(true);
      
      try
      {//open try
         jtaDiagnostics.setText("Server has launched!");
         ss = new ServerSocket(16789);
         while(true)
         {//open while
            cs = ss.accept();
            jtaDiagnostics.setText(jtaDiagnostics.getText() + "\nSomeone Connected!");       //update text area saying someone connected
            try
            {//open try
               ois = new ObjectInputStream(cs.getInputStream());                             //instantiate locally!
               String test = new String();
               Object incoming = ois.readObject();
               if(incoming instanceof Ship[])                     //compare if what came in is an instance of an array of the Ship class
               {//open if                                           if it is, make our Ship array equal to the incoming object casted as a ship array
               shipsReadIn = (Ship[])incoming;                    //and print it out to the text area using the toString method in a for loop
                  for(int i = 0; i < shipsReadIn.length; i++)     //FALSE for Orientation = HORIZONTAL, TRUE for Orientation = Vertical
                  {//open for loop
                     test =  shipsReadIn[i].toString();
                     jtaDiagnostics.setText(jtaDiagnostics.getText() + "\n" + test);
                  }//close for loop
               }//close if
               else
               {//open else                             if it isn't, print an error message to the text area
                  jtaDiagnostics.setText(jtaDiagnostics.getText() + "\n" + "ERROR! OBJECT READ IN WASN'T A SHIP!");
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
         }//close while
      }//close try
      catch(IOException ioe)
      {//open catch
         ioe.printStackTrace();
      }//close catch
   }//close constructor
   
   //If we don't thread it we can just use Player1.getHP() / Player2.getHP(), whichever player's current turn is going
   // if we thread it, we should be able to do Player.getHP() regardless.
   //Method? or just Check at start of each turn?
   //if Player.getHP() == 0
   //    Win_Condition = true;
   //else
   //    
   public static void main(String[] args)
   {//open main
      new BattleshipServer();
   }//close main
}//close class
