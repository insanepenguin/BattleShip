import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//SERVER

public class BattleshipServer
{//open class
   Boolean win_Condition = false;
   int player1Hp = 17;
   int player2Hp = 17;
   Boolean[][] player1Field = new Boolean[10][10];
   Boolean[][] player2Field = new Boolean[10][10];
   Boolean yourTurn = true;
   Boolean hit = false;
   String sync = new String("");
   String currentMsg = ""; // this string is for the chat messages sent out to the clients
   
   ServerSocket ss;
   Socket cs;
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
      Socket[] sockets = new Socket[2];
      try
      {//open try
         jtaDiagnostics.setText("Server launched @" + InetAddress.getLocalHost().getHostAddress());
         ss = new ServerSocket(16789);
         for(int i = 0; i < 2; i++)
         {//open for loop
            cs = ss.accept();
            sockets[i] = cs;
            ShipReceiver thread = new ShipReceiver(cs, i);
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
     
      int goesFirst = (int)((Math.random() * 2) + 1);
     
      //waiting for the players to send their ships
      try {
         playersReady[0].join();
         playersReady[1].join();
         new Connection(sockets[0], 1, goesFirst).start();
         new Connection(sockets[1], 2, goesFirst).start();
      }
      catch(InterruptedException ie) {
         ie.printStackTrace();
      }
     
      console("Ships recieved, starting game");

   }//close constructor
   
   //a method so we don't have to be like "jtaDiagnostics.setText(jtaDiagnostics.getText() + '\n' + w/e) every time lol
   public void console(String msg)
   {//open console updating method
      jtaDiagnostics.setText(jtaDiagnostics.getText() + '\n' + msg);
   }//close console updating method
   
   public class ShipReceiver extends Thread {
      Socket sock;
      int player;
     
      public ShipReceiver(Socket cs, int i) {
         cs = sock;
         player = i;
      }
     
      public Socket getSock() {
         return sock;
      }
     
      public void run() {
         try
         {//open try
            ObjectInputStream shipReader = new ObjectInputStream(cs.getInputStream());                       //instantiate locally!
            Object incoming = shipReader.readObject();
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

            }//close if
            else
            {//open else                                         if it isn't, print an error message to the text area
               console("ERROR! OBJECT READ IN WASN'T A SHIP!");
            }//close else
            // shipReader.close();
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
   
   //connection inner class, this handles everything chat related
   public class Connection extends Thread {
   
     Socket sock;
     int playerNum;
     int first;
     
      public Connection(Socket _cs, int _playerNum, int _first) {
         playerNum = _playerNum;
         sock = _cs;
         first = _first;
         setName("[Connection " + playerNum + "]");
      }
     
      public void run() {
         //starts the reciever, then handles sending of chat data
         new Reciever().start();
         try {
            PrintWriter chatWriter = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
            chatWriter.println(playerNum);
            chatWriter.flush();
            chatWriter.println(first);
            chatWriter.flush();
            // int playerNum = integer.parseInt(incoming.substring(incoming.indexOf(":")));
            console(getName() + "initialized");
            while(sock.isConnected()) {
               synchronized(sync) {
                  sync.wait(); //when the reciever recieves something, it updates the global string currentmsg then notifies
               }
               chatWriter.println(currentMsg); //once notified, the sender sends out msg to the clients
               chatWriter.flush();
               console("message send out");
            }
         }
         catch(InterruptedException ie) {
            System.err.println(getName() + "Interrupted Exception");
            ie.printStackTrace();
         }
         catch(IOException ioe) {
            System.err.println(getName() + "IO Error");
            ioe.printStackTrace();
         }
      }
     
      public boolean hitDetection(int x, int y){//open hitDetection method
         if(playerNum == 1) {
            if(player2Field[x][y]){//open 2nd if
               console("Hit!");
               player2Field[x][y] = false;
               player2Hp--;
               return true;
            }
            else {
               console("Miss!");
               return false;
            }
         }
         else {
            if(player1Field[x][y]){//open 2nd if
               console("Hit!");
               player1Field[x][y] = false;
               player1Hp--;
               return true;
            }
            else {
               console("Miss!");
               return false;
            }
         }
      }
     
      //inner class controlling the thread that recieves chat data
      public class Reciever extends Thread {
         String winner;
         
         public Reciever() {
            setName("[Reciever " + playerNum + "]");
         }
         
         public void run() {
            try {
               BufferedReader chatReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
               console(getName() + "initialized");

                  while(sock.isConnected()) {
                     String incoming = chatReader.readLine();
                     console(incoming);
                     if(incoming.indexOf("[MSG]:") != -1) {
                        synchronized(sync) {
                           currentMsg = "[Player " + playerNum + "]: " + incoming.substring(7);
                           sync.notifyAll();
                        }
                     }
                     else {
                     String[] Coords = incoming.split(",");
                     int xCoord = Integer.parseInt(Coords[0]);
                     int yCoord = Integer.parseInt(Coords[1]);                    
                     boolean hitMiss = hitDetection(xCoord, yCoord);
                     
                     synchronized(sync) {
                        currentMsg = playerNum + "," + hitMiss + "," + xCoord + "," + yCoord;
                        sync.notifyAll();
                     }
                     if(player1Hp == 0 || player2Hp == 0) {
                        if(player1Hp ==0) {
                           winner = "[WIN]Player 2 wins!";
                        }
                        else {
                           winner = "[WIN]Player 1 wins!";
                        }
                        synchronized(sync) {
                           currentMsg = winner;
                           sync.notifyAll();
                        }
                     }
                  }
               }
            }
            catch(IOException ioe) {
               System.err.println(getName() + " IO Error");
               ioe.printStackTrace();
            }
         }
      }
   }
   
   public static void main(String[] args)
   {//open main
      new BattleshipServer();
   }//close main
}//close class
