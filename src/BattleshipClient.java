import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.net.*;
import java.io.*;

/**
* This class is the Client of the Battleship game, and handles the client's gui, and part of the game
* (sending and receiving if you hit or not and placing the ships at the start)
*
* @author JavaPack Survivors
* @version 2019-11-14
*/

public class BattleshipClient extends JFrame implements ActionListener {

   //synchronization object
   String syncOn = new String("Sync on me");

   //All server-client relations stuff
   Socket s;
   BufferedReader bin;
   PrintWriter pout;
   String ipAddress;
   int playerNum = 0;
   boolean win = false;
   boolean yourTurn = false;

   //which ship the player is placing down, if they aren't in the placing phase it's null
   private Coordinate[] selected;
   private Ship[] ships = new Ship[5];
   private Ship toShoot;
   
   //the grid has a collection of coordinates, they're referred to by their position in this array
   private Coordinate[][] coordinates = new Coordinate[10][10];
   private Coordinate[][] enemyCoords = new Coordinate[10][10];
   
   //the ships are just arrays of coordinates. I know we wanted a ship class but this just feels easier
   private Coordinate[] carrier = new Coordinate[5];
   private Coordinate[] cruiser = new Coordinate[4];
   private Coordinate[] destroyer = new Coordinate[3];
   private Coordinate[] submarine = new Coordinate[3];
   private Coordinate[] patrolBoat = new Coordinate[2];
   private Coordinate[] target = new Coordinate[1];
   
   //these are the gui components that need to be declared globally
   private JLabel jlPlayer = new JLabel("Click each ship and place them here!", SwingConstants.CENTER);
   private JLabel jlEnemy = new JLabel("Enemy's grid; ignore this for now.", SwingConstants.CENTER);
   private JPanel jpShips = new JPanel(new GridLayout(0, 1));
   private JPanel jpGameRunning = new JPanel(new BorderLayout());
   private JPanel jpGameRunningCenter = new JPanel();
   private JPanel jpGameRunningSouth = new JPanel();
   private JPanel jpEnemyGrid;
   private JButton jbCarrier = new JButton("Carrier");
   private JButton jbCruiser = new JButton("Cruiser");
   private JButton jbDestroyer = new JButton("Destroyer");
   private JButton jbSubmarine = new JButton("Submarine");
   private JButton jbPatrolBoat = new JButton("Patrol Boat");
   private JButton jbRotate = new JButton("Rotate");
   private JButton jbReady = new JButton("READY");
   private JButton jbTarget = new JButton("Target");
   private JButton jbFire = new JButton("Fire");
   
   //Chat Gui stuff
   private JTextArea jtaChatBox = new JTextArea(25,17);
   private JScrollPane jspScroller = new JScrollPane(jtaChatBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
   private JScrollBar ScrollBar = jspScroller.getVerticalScrollBar();
   private JTextField jtfInput = new JTextField(8);
   private JButton jbSend = new JButton("Send");

   //what direction you want to place the ship in
   boolean rotate = false;
      
   //setting up the grid!
   public BattleshipClient() {
   
      //Start with JOptionPane.showInputDialog from our chat client that prompts for a server to connect to
      while(true)
      {//open while
         try 
         {//open try
            //connect with server
            ipAddress = JOptionPane.showInputDialog(null, "Please Input the Server's IP address");
            if(ipAddress == null)
            {//open if
               System.exit(0);
            }//close if
            else if(ipAddress.equals(""))
            {//open else if
               throw new IOException();
            }//close
            s = new Socket(ipAddress, 16789);
            //new Receive(s).start();     DONT CURRENTLY NEED RECIEVER
         }//close try
         catch(IOException ioe) 
         {//open 1st catch
            JOptionPane.showMessageDialog(null, "Couldn't find that server! (Or it doesn't exist?) Please try again.");
            continue;
         }//close 1st catch
         break;
      }//close while   
      
      //each coordinate is a panel on the grid, the coords can be occupied by a ship, or shot
      JPanel jpGrid = new JPanel(new GridLayout(0,10));
      for(int y = 0; y < 10; y++) {
         for(int x = 0; x < 10; x++) {
            coordinates[x][y] = new Coordinate(x, y);
            coordinates[x][y].active = true;
            jpGrid.add(coordinates[x][y]);
         }
      }
      
      
      jpEnemyGrid = new JPanel(new GridLayout(0,10));
      for(int y = 0; y < 10; y++) {
         for(int x = 0; x < 10; x++) {
            enemyCoords[x][y] = new Coordinate(x, y);
            jpEnemyGrid.add(enemyCoords[x][y]);
         }
      }
      
      //this is just taking care of GUI assembly, button setup component add etc.
      JPanel jpEast = new JPanel();
      jpEast.setPreferredSize(new Dimension(500, 550));
      JPanel jpWest = new JPanel();
      jpWest.setPreferredSize(new Dimension(500, 550));
      jpWest.add(jlPlayer);
      jpEast.add(jlEnemy);
      jpWest.add(jpGrid);
      jpEast.add(jpEnemyGrid);
      add(jpWest, BorderLayout.WEST);
      add(jpEast, BorderLayout.EAST);
      jbCarrier.addActionListener(this);
      jbCruiser.addActionListener(this);
      jbDestroyer.addActionListener(this);
      jbSubmarine.addActionListener(this);
      jbPatrolBoat.addActionListener(this);
      jbRotate.addActionListener(this);
      jbReady.addActionListener(this);
      jbTarget.addActionListener(this);
      jbFire.addActionListener(this);
      jbSend.addActionListener(this);
      jbReady.setEnabled(false);
      jpShips.add(jbCarrier);
      jpShips.add(jbCruiser);
      jpShips.add(jbDestroyer);
      jpShips.add(jbSubmarine);
      jpShips.add(jbPatrolBoat);
      jpShips.add(jbRotate);
      jpShips.add(jbReady);
      jpGameRunning.add(jspScroller, BorderLayout.NORTH); 
      jpGameRunningCenter.add(new JLabel("Chat Input:"));
      jpGameRunningCenter.add(jtfInput); 
      jpGameRunningCenter.add(jbSend);
      jpGameRunningSouth.add(jbTarget);
      jpGameRunningSouth.add(jbFire);
      jpGameRunning.add(jpGameRunningCenter, BorderLayout.CENTER);
      jpGameRunning.add(jpGameRunningSouth, BorderLayout.SOUTH);
      JPanel jpCenter = new JPanel();
      jpShips.setPreferredSize(new Dimension(250, 500));
      jpGameRunning.setVisible(false);
      jpCenter.add(jpGameRunning, BorderLayout.CENTER);
      jpCenter.add(jpShips, BorderLayout.CENTER);
      add(jpCenter, BorderLayout.CENTER);
      
      //make Enter press the "jbSend" button
      getRootPane().setDefaultButton(jbSend);
      
      //make the text area un-editable
      jtaChatBox.setEnabled(false);
      jtaChatBox.setLineWrap(true);
      jtaChatBox.setWrapStyleWord(true);
      jtaChatBox.setText("Waiting for both players to connect");
      
      //final setup for the GUI
      setTitle("Battleship Client by JavaPack Survivors!");
      pack();
      setLocationRelativeTo(null);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
     
      synchronized(syncOn) {
         try {
            syncOn.wait();
         }
         catch(InterruptedException ie) {
            System.err.println("something went wrong, failed to start game");
         }
      }
      jlPlayer.setText("Ships locked! Ready for battle.");
      jlEnemy.setText("Click this grid to fire!");
      setActive();
   }//close constructor
   
   //here is the coordinate inner class! these guys do the heavy lifting
   //they have location, if they're occupied by a ship, and if they're shot
   public class Coordinate extends JPanel {
      int x;
      int y;
      
      //at the start the coordinates aren't occupied or active
      boolean occupied = false;
      boolean active = false;
      boolean placedOrientation;
      
      //setting up each coordinate! 
      public Coordinate(int _x, int _y) {
         x = _x;
         y = _y;
         
         //this is setting up the visuals of the component, they're little black boxes with blue inside
         //for now everything is colors, if we decide to go way overboard we could be pictures and stuff
         setBackground(Color.BLUE);
         setPreferredSize(new Dimension(50, 50));
         setBorder(new LineBorder(Color.BLACK, 1));
         
         //heres the big boy, this is how ships are placed, and make sure they're correct
         //I'd really like to implement firing in a similar way, but with another grid
         addMouseListener(new MouseAdapter() {  
            
            //hovering over the grid to place your ships down!
            //it's a bit chunky, I'd like to implement a better way to handle rotating
            public void mouseEntered(MouseEvent me) {
               if(active) {
                  try {
                     shade(Color.LIGHT_GRAY, Color.CYAN);
                  }
                  catch(ArrayIndexOutOfBoundsException aioobe) {
                  }
               }
            }
            //this is basically the same as the first method, but in reverse
            //it puts the colors back to their normal (non hovering) state. 
            public void mouseExited(MouseEvent me) {
               if(active) {
                  try {
                        shade(Color.GRAY, Color.BLUE);
                  }
                  catch(ArrayIndexOutOfBoundsException aioobe) {
                  }
               }
            }
            //this is when you click to place the ship down on the grid
            //if it can't place, it throws an exception which is handled
            public void mousePressed(MouseEvent me) {
               if(active) {
                  try {
                  
                     //if it managed to get this far without throwing an exception, time to place the ship!
                     //it checks what ship we're placing, then deletes any old one and places a new one
                     //this is so that you can't have a fleet of like 5 aircraft carriers or smth
                     //we can add more ships at any time just lmk, I only did 4 cus I think one is the same size?
                  
                     if(selected == target){
                        clear(target);
                        place(target);
                     }
                     else if(selected == carrier) {
                        check();
                        clear(carrier);
                        place(carrier);
                     }
                     else if(selected == cruiser) {
                        check();
                        clear(cruiser);
                        place(cruiser);
                     }
                     else if(selected == destroyer) {
                        check();
                        clear(destroyer);
                        place(destroyer);
                     }
                     else if(selected == submarine) {
                        check();
                        clear(submarine);
                        place(submarine);
                     }
                     else if(selected == patrolBoat) {
                        check();
                        clear(patrolBoat);
                        place(patrolBoat);
                     }
                  }
                  catch(ArrayIndexOutOfBoundsException aioobe) {
                     //if you try to put the ship out of bounds, or on top of another ship... ERROR!
                     JOptionPane.showMessageDialog(null, "Error! Cannot place ship there :(");
                  }
                  //this is a check to see if it needs to enable or disable the ready state
                  // "ready" means you can start the battle, so all your ships are placed
                  if(carrier[0] != null && cruiser[0] != null && destroyer[0] != null && submarine[0] != null && patrolBoat[0] != null) jbReady.setEnabled(true);
               }
            }
         });
      }//close constructor for Coordinate
      
      //method to place a ship down
      //@param Coordinatep[] coord, the ship being placed
      public void place(Coordinate[] coord) {
      
            //do this for the length of the ship, so like carrier 5 cruiser 4 etc
            for(int i = 0; i < coord.length; i++) {
               //check horizontal/vertical placement
               if(coord.length ==1){
                  coord[i]=enemyCoords[x][y];
               }
               else if(rotate) {
                  //add the coordinate to the ship array
                  coord[i] = coordinates[x][y + i];
               }
               else {
                  coord[i] = coordinates[x + i][y];
               }
               //visually show that the coordinate is occupied by a ship or targetted to fire
               coord[i].setBackground(Color.GRAY);
                  
               //set that coordinate as occupied by a ship
               coord[i].occupied = true;
               coord[i].placedOrientation = rotate;      //LEFT TO RIGHT is FALSE, TOP TO BOTTOM is TRUE
            }
         //}//close else
      }//close place method
      
      //method that colors the coordinates when you're hovering on them
      public void shade(Color occ, Color vac) {
         if((selected != null)&&(selected != target)) {
            for(int i = 0; i < selected.length; i++) {
               if(rotate) {
                  if(coordinates[x][y + i].occupied) coordinates[x][y + i].setBackground(occ);
                  else coordinates[x][y + i].setBackground(vac);
               }
               else{
                  if(coordinates[x + i][y].occupied) coordinates[x + i][y].setBackground(occ);
                  else coordinates[x + i][y].setBackground(vac);
               }
            }
         }
         else if(selected == target){
            if((enemyCoords[x][y].getBackground() != Color.RED)||(enemyCoords[x][y].getBackground() != Color.WHITE))
            if(enemyCoords[x][y].occupied)
               enemyCoords[x][y].setBackground(occ);
            else
               enemyCoords[x][y].setBackground(vac);

         }//close else if
      }
      
      //checking to make sure the ship doesn't overlap with others (it's okay to overlap with itself)
      public void check() throws ArrayIndexOutOfBoundsException {
         if(selected != null) {
            for(int i = 0; i < selected.length; i++) {
               if(rotate) {
                  if(Arrays.asList(carrier).contains(coordinates[x][y + i]) && selected != carrier) throw new ArrayIndexOutOfBoundsException();
                  else if(Arrays.asList(cruiser).contains(coordinates[x][y + i]) && selected != cruiser) throw new ArrayIndexOutOfBoundsException();
                  else if(Arrays.asList(destroyer).contains(coordinates[x][y + i]) && selected != destroyer) throw new ArrayIndexOutOfBoundsException();
                  else if(Arrays.asList(submarine).contains(coordinates[x][y + i]) && selected != submarine) throw new ArrayIndexOutOfBoundsException();
                  else if(Arrays.asList(patrolBoat).contains(coordinates[x][y + i]) && selected != patrolBoat) throw new ArrayIndexOutOfBoundsException();
               }
               else{
                  if(Arrays.asList(carrier).contains(coordinates[x + i][y]) && selected != carrier) throw new ArrayIndexOutOfBoundsException();
                  else if(Arrays.asList(cruiser).contains(coordinates[x + i][y]) && selected != cruiser) throw new ArrayIndexOutOfBoundsException();
                  else if(Arrays.asList(destroyer).contains(coordinates[x + i][y]) && selected != destroyer) throw new ArrayIndexOutOfBoundsException();
                  else if(Arrays.asList(submarine).contains(coordinates[x + i][y]) && selected != submarine) throw new ArrayIndexOutOfBoundsException();
                  else if(Arrays.asList(patrolBoat).contains(coordinates[x + i][y]) && selected != patrolBoat) throw new ArrayIndexOutOfBoundsException();
               }
            }
         }
      }
      
      //clearing a ship from the board, for if you want to place it somewhere else
      //the placements of the ships aren't final until you hit the ready button
      public void clear(Coordinate[] coord) {
      if(coord[0] != null) {
         for(int i = 0; i < coord.length; i++) {
               coord[i].setBackground(Color.BLUE);
               coord[i].occupied = false;
               coord[i] = null;
            }
         }
      }
      
      //toString for each coordinate, just for our testing purposes, no use in game
      public String toString() {
         return "Coordinate (" + x + ", " + y + ") - Occupied: " + occupied;
      }
   }//close Coordinate class

    public class Receive extends Thread{//open Receive innerclass
      Socket sock;
      String message;
      
      public Receive(Socket _sock){//open Receive constructor
         sock = _sock;
                  
         try{//open try
            setName(InetAddress.getLocalHost() + "");
            pout = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
         }//close try
         catch(UnknownHostException uhe){//open 1st catch
            uhe.printStackTrace();
         }//close 1st catch
         catch(IOException ioe){//open 1st catch
            System.err.println("There was a problem instantiating the Chat connection");
         }//close 2nd catch catch
      }//close Receive constructor
         
      public void run() 
      {//open Run method (for Receive class?)
         try 
         {//open try
            BufferedReader bin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String incoming = bin.readLine();
            playerNum = Integer.parseInt(incoming.substring((incoming.indexOf(":")+1)));
            System.out.println(playerNum);
            while(sock.isConnected()) 
            {//open while
               message = bin.readLine();
               if(message.indexOf("[Player") != -1){
                  jtaChatBox.setText(jtaChatBox.getText() + "\n" + message);
                  ScrollBar.setValue((ScrollBar.getMaximum() + 5));
               }
               else{
                  String[] gamestats = message.split(",");
                  if(playerNum == Integer.parseInt(gamestats[0])){
                     if(Boolean.parseBoolean(gamestats[1])){
                        enemyCoords[Integer.parseInt(gamestats[2])][Integer.parseInt(gamestats[3])].setBackground(Color.RED);
                        pout.println("[MSG]: \"" + "[Player " + playerNum + "] You hit at coordinates: X: " + (Integer.parseInt(gamestats[2])+1) + ", Y: " + (Integer.parseInt(gamestats[3])+1));
                        pout.flush();
                     }
                     else{
                        enemyCoords[Integer.parseInt(gamestats[2])][Integer.parseInt(gamestats[3])].setBackground(Color.WHITE);
                        pout.println("[MSG]: \"" + "[Player " + playerNum + "] You missed at coordinates: X: " + (Integer.parseInt(gamestats[2])+1) + ", Y: " + (Integer.parseInt(gamestats[3])+1));
                        pout.flush();
                     }
                  }
                  else{
                     jbFire.setEnabled(true);
                     jbTarget.setEnabled(true);
                     setActive();
                  }                  
               }
            }//close while
            bin.close();
         }//close try
         catch(SocketException se){//open 1st catch
            System.exit(9);
         }//close 1st catch
         catch(IOException ioe) 
         {//open 2nd catch
            System.out.println("[Unexpected IO error; disconnecting from server]");
            ioe.printStackTrace();
         }//close 2nd catch
      }//close run method for Receive 
   }//close Receive innerclass
      
   //actionPerformed, for all the button events we made earlier
   public void actionPerformed(ActionEvent ae) {
      Object pressedButton = ae.getSource();
      if(pressedButton == jbCarrier) selected = carrier;
      else if(pressedButton == jbCruiser) selected = cruiser;
      else if(pressedButton == jbDestroyer) selected = destroyer;
      else if(pressedButton == jbSubmarine) selected = submarine;
      else if(pressedButton == jbPatrolBoat) selected = patrolBoat;
      else if(pressedButton == jbTarget) selected = target;
      else if(pressedButton == jbRotate) rotate();
      else if(pressedButton == jbSend) sendMessage();
      else if(pressedButton == jbReady) ready();
      else if(pressedButton == jbFire) fire();
   }
   
   // BEGINNING OF METHODS FOR THE ACTION PERFORMED SECTION OF CODE!
   //method to change the orientation of the ships being placed
   public void rotate(){
      if (rotate == true) rotate = false;
         else rotate = true;
   }//close rotate
   
   //method for the chat section of code, to send a message
   public void sendMessage(){
   String message = jtfInput.getText();
      if(message.trim().equals("")) {
      //Don't send anything, don't want blank messages sent
      }
      else{
         pout.println("[MSG]: \"" +  message  + "\"");
         pout.flush();
         jtfInput.setText("");
      }
      jtfInput.requestFocus();
   }//close sendMessage method
   
   //Method for when you hit the ready button, prepares the actual playing of the game
   public void ready(){
      new Receive(s).start();
      selected = null;
      jpGameRunning.setVisible(true);
      jpEnemyGrid.setVisible(true);
      jpShips.setVisible(false);
      //MAKE SHIPS OF EACH AND SEND TO SERVER
      ships[0] = new Ship("Carrier", 5, carrier[0].x, carrier[0].y, carrier[0].placedOrientation);
      ships[1] = new Ship("Cruiser", 4, cruiser[0].x, cruiser[0].y, cruiser[0].placedOrientation);
      ships[2] = new Ship("Destroyer", 3, destroyer[0].x, destroyer[0].y, destroyer[0].placedOrientation);
      ships[3] = new Ship("Submarine", 3, submarine[0].x, submarine[0].y, submarine[0].placedOrientation);
      ships[4] = new Ship("Patrol Boat", 2, patrolBoat[0].x, patrolBoat[0].y, patrolBoat[0].placedOrientation);
      
      //THIS IS AN ATTEMPT TO FIX AN ERROR FOR THE OIS/OOS STUFF, DOES NOT CURRENTLY WORK
      //THROWING AN EOF EXCEPTION!
      try {
         ObjectOutputStream oos_ships = new ObjectOutputStream(s.getOutputStream());
         oos_ships.writeObject(ships);
         oos_ships.flush();
      }//close try
      catch(IOException ioe) {
         ioe.printStackTrace();
      }//close catch
      if(!win) {
         synchronized(syncOn) {
            syncOn.notifyAll();
         }//close sync
      }//close if
      jtfInput.requestFocus();
   }//close ready method
   
   //method to create the set of coordinates that would be sent as a ship object
   public void fire(){
      toShoot = new Ship("Target", 1, target[0].x, target[0].y, target[0].placedOrientation);
      pout.println(toShoot.getStartX() + "," + toShoot.getStartY()); //send String instead of toShoot object/Ship
      pout.flush();
      jbTarget.setEnabled(false);
      jbFire.setEnabled(false);
      setInactive();
      jtfInput.requestFocus();
      //yourTurn = false;
   }//close fire method
   //ENDING OF THE METHODS FOR THE ACTION PERFORMED SECTION OF CODE
   
   //THIS IS CURRENTLY A REPEAT SECTION OF CODE!
   //THIS WAS A QUICK AND SLOPPY ATTEMPT AT GAME LOGIC
   //IN ORDER TO TEST SENDING/RECEIVING FROM THE SERVER
   //CURRENTLY NOT BEING IMPLEMENTED
   public void setActive(){//open setActive method
      for(int y = 0; y < coordinates.length; y++) {
         for(int x = 0; x < coordinates.length; x++) {
            coordinates[x][y].active = false;
            enemyCoords[x][y].active = true;
         }
      }
   }//close setActive method
    public void setInactive(){//open setInactive method
      for(int y = 0; y < coordinates.length; y++) {
         for(int x = 0; x < coordinates.length; x++) {
            enemyCoords[x][y].active = false;
         }
      }
   }//close setInactive method

   
   public static void main(String[] args) {
      new BattleshipClient();
   }
}//close main
