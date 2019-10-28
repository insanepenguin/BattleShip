import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
* This class is here for me to experiment on how best to implement the gui and any user interactivity
* I want to keep it separate from Client.java for now because it doesn't have any server <-> client connection code
* Comments are hard, I'll write more tomorrow, if this still looks confusing as hell by then just ask me about it
* Also I'm not gonna write javadoc comments, this opener is more of a meme than anything
*
* @author Ben Shapiro
* @version 2019-10-27
*/

public class GridThing extends JFrame implements ActionListener {

   //the state of the game, what the player is doing, it's displayed up top
   private String state = "Place your ships by selecting them and clicking the grid!";
   
   //the grid has a collection of coordinates, they're referred to by their position in this array
   private Coordinate[][] coordinates = new Coordinate[10][10];
   
   //the ships are just arrays of coordinates. I know we wanted a ship class but this just feels easier
   private Coordinate[] carrier = new Coordinate[5];
   private Coordinate[] cruiser = new Coordinate[4];
   private Coordinate[] destroyer = new Coordinate[3];
   private Coordinate[] patrolBoat = new Coordinate[2];
   
   //these are the gui components that need to be declared globally
   private JLabel jlState = new JLabel(state, SwingConstants.CENTER);
   private JPanel jpShips = new JPanel(new GridLayout(0,1));
   private JPanel jpFireControl = new JPanel(new GridLayout(0,1));
   private JButton jbCarrier = new JButton("Carrier");
   private JButton jbCruiser = new JButton("Cruiser");
   private JButton jbDestroyer = new JButton("Destroyer");
   private JButton jbPatrolBoat = new JButton("Patrol Boat");
   private JButton jbRotate = new JButton("Rotate");
   private JButton jbReady = new JButton("READY");
   
   //what direction you want to place the ship in
   boolean rotate = false;
      
   //setting up the grid!
   public GridThing() {
   
      //each coordinate is a panel on the grid, the coords can be occupied by a ship, or shot
      JPanel jpGrid = new JPanel(new GridLayout(0,10));
      for(int y = 0; y < 10; y++) {
         for(int x = 0; x < 10; x++) {
            coordinates[x][y] = new Coordinate(x, y);
            jpGrid.add(coordinates[x][y]);
         }
      }
      add(jpGrid, BorderLayout.CENTER);
      
      //this is just taking care of GUI assembly, button setup component add etc.
      jbCarrier.addActionListener(this);
      jbCruiser.addActionListener(this);
      jbDestroyer.addActionListener(this);
      jbPatrolBoat.addActionListener(this);
      jbRotate.addActionListener(this);
      jbReady.addActionListener(this);
      jbReady.setEnabled(false);
      jpShips.add(jbCarrier);
      jpShips.add(jbCruiser);
      jpShips.add(jbDestroyer);
      jpShips.add(jbPatrolBoat);
      jpShips.add(jbRotate);
      jpShips.add(jbReady);
      jpFireControl.add(new JLabel("Fire controls or chat? idk")); //this is just a placeholder lol
      jpShips.setPreferredSize(new Dimension(200, 500));
      jpFireControl.setPreferredSize(new Dimension(200, 500));
      jpFireControl.setVisible(false);
      JPanel jpWest = new JPanel();
      jpWest.add(jpFireControl);
      jpWest.add(jpShips);
      add(jpWest, BorderLayout.WEST);
      add(jlState, BorderLayout.NORTH);
      
      //final setup for the GUI
      setTitle("Grid Thingy by Ben");
      pack();
      setLocationRelativeTo(null);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
   }
   
   //here is the coordinate inner class! these guys do the heavy lifting
   //they have location, if they're occupied by a ship, and if they're shot
   public class Coordinate extends JPanel {
      int x;
      int y;
      
      //at the start the coordinates aren't occupied or shot
      boolean occupied = false;
      boolean shot = false;
      
      //setting up each coordinate! 
      public Coordinate(int _x, int _y) {
         x = _x;
         y = _y;
         
         //this is setting up the visuals of the component, they're little black boxes with gray inside
         //for now everything is colors, if we decide to go way overboard we could be pictures and stuff
         setBackground(Color.GRAY);
         setPreferredSize(new Dimension(50, 50));
         setBorder(new LineBorder(Color.BLACK, 1));
         
         //heres the big boy, this is how ships are placed, and make sure they're correct
         //I'd really like to implement firing in a similar way, but with another grid
         addMouseListener(new MouseAdapter() {  
            
            //hovering over the grid to place your ships down!
            //it's a bit chunky, I'd like to implement a better way to handle rotating
            public void mouseEntered(MouseEvent me) {
               try {
                  //check horizontal or vertical placement?
                  if(rotate) {
                     //check which type of ship we're placing? Each has a game state.
                     switch(state) {
                        //depending on the length of the ship, more or less coords are shaded. 
                        case "Placing carrier": 
                           //if the coordinate is already occupied by a ship, it shades RED, otherwise it's GREEN
                           if(coordinates[x][y + 4].occupied) coordinates[x][y + 4].setBackground(Color.RED);
                           else coordinates[x][y + 4].setBackground(Color.GREEN);
                        case "Placing cruiser":
                           if(coordinates[x][y + 3].occupied) coordinates[x][y + 3].setBackground(Color.RED);
                           else coordinates[x][y + 3].setBackground(Color.GREEN);
                        case "Placing destroyer":
                           if(coordinates[x][y + 2].occupied) coordinates[x][y + 2].setBackground(Color.RED);
                           else coordinates[x][y + 2].setBackground(Color.GREEN);
                        case "Placing patrol boat": 
                           if(coordinates[x][y + 1].occupied) coordinates[x][y + 1].setBackground(Color.RED);
                           else coordinates[x][y + 1].setBackground(Color.GREEN);
                           if(occupied) setBackground(Color.RED);
                           else setBackground(Color.GREEN);
                        break;
                        default:
                        break;
                     }
                  }
                  else {
                     switch(state) {
                        case "Placing carrier": 
                           if(coordinates[x + 4][y].occupied) coordinates[x + 4][y].setBackground(Color.RED);
                           else coordinates[x + 4][y].setBackground(Color.GREEN);
                        case "Placing cruiser":
                           if(coordinates[x + 3][y].occupied) coordinates[x + 3][y].setBackground(Color.RED);
                           else coordinates[x + 3][y].setBackground(Color.GREEN);
                        case "Placing destroyer":
                           if(coordinates[x + 2][y].occupied) coordinates[x + 2][y].setBackground(Color.RED);
                           else coordinates[x + 2][y].setBackground(Color.GREEN);
                        case "Placing patrol boat": 
                           if(coordinates[x + 1][y].occupied) coordinates[x + 1][y].setBackground(Color.RED);
                           else coordinates[x + 1][y].setBackground(Color.GREEN);
                           if(occupied) setBackground(Color.RED);
                           else setBackground(Color.GREEN);
                        break;
                        default:
                        break;
                     }
                  }
               }
               catch(ArrayIndexOutOfBoundsException aioobe) {
                  // if you hover your ship out of bounds, it will shade RED
                  setBackground(Color.RED);
               }
            }
            
            //this is basically the same as the first method, but in reverse
            //it puts the colors back to their normal (non hovering) state. 
            public void mouseExited(MouseEvent me) {
               try {
                  if(rotate) {
                     switch(state) {
                        case "Placing carrier": 
                           if(coordinates[x][y + 4].occupied) coordinates[x][y + 4].setBackground(Color.BLACK);
                           else coordinates[x][y + 4].setBackground(Color.GRAY);
                        case "Placing cruiser": 
                           if(coordinates[x][y + 3].occupied) coordinates[x][y + 3].setBackground(Color.BLACK);
                           else coordinates[x][y + 3].setBackground(Color.GRAY);
                        case "Placing destroyer": 
                           if(coordinates[x][y + 2].occupied) coordinates[x][y + 2].setBackground(Color.BLACK);
                           else coordinates[x][y + 2].setBackground(Color.GRAY);
                        case "Placing patrol boat": 
                           if(coordinates[x][y + 1].occupied) coordinates[x][y + 1].setBackground(Color.BLACK);
                           else coordinates[x][y + 1].setBackground(Color.GRAY);
                           if(occupied) setBackground(Color.BLACK);
                           else setBackground(Color.GRAY);
                        break;
                        default:
                        break;
                     }
                  }
                  else {
                     switch(state) {
                        case "Placing carrier": 
                           if(coordinates[x + 4][y].occupied) coordinates[x + 4][y].setBackground(Color.BLACK);
                           else coordinates[x + 4][y].setBackground(Color.GRAY);
                        case "Placing cruiser": 
                           if(coordinates[x + 3][y].occupied) coordinates[x + 3][y].setBackground(Color.BLACK);
                           else coordinates[x + 3][y].setBackground(Color.GRAY);
                        case "Placing destroyer": 
                           if(coordinates[x + 2][y].occupied) coordinates[x + 2][y].setBackground(Color.BLACK);
                           else coordinates[x + 2][y].setBackground(Color.GRAY);
                        case "Placing patrol boat": 
                           if(coordinates[x + 1][y].occupied) coordinates[x + 1][y].setBackground(Color.BLACK);
                           else coordinates[x + 1][y].setBackground(Color.GRAY);
                           if(occupied) setBackground(Color.BLACK);
                           else setBackground(Color.GRAY);
                        break;
                        default:
                        break;
                     }
                  }
               }
               catch(ArrayIndexOutOfBoundsException aioobe) {
                  if(occupied) setBackground(Color.BLACK);
                  else setBackground(Color.GRAY);
               }
            }
            
            //this is when you click to place the ship down on the grid
            //if it can't place, it throws an exception which is handled
            public void mousePressed(MouseEvent me) {
               try {
                  if(rotate) {
                     //checking to ensure that none of the coordinates you're placing are already taken by another ship
                     //if any of them are, it will throw an exception which is handled!
                     switch(state) {
                        case "Placing carrier": if(coordinates[x][y + 4].occupied) throw new ArrayIndexOutOfBoundsException();
                        case "Placing cruiser": if(coordinates[x][y + 3].occupied) throw new ArrayIndexOutOfBoundsException();
                        case "Placing destroyer": if(coordinates[x][y + 2].occupied) throw new ArrayIndexOutOfBoundsException();
                        case "Placing patrol boat": if(coordinates[x][y + 1].occupied || occupied) throw new ArrayIndexOutOfBoundsException();
                        break;
                        default:
                        break;
                     }
                  }
                  else {
                     switch(state) {
                        case "Placing carrier": if(coordinates[x + 4][y].occupied) throw new ArrayIndexOutOfBoundsException();
                        case "Placing cruiser": if(coordinates[x + 3][y].occupied) throw new ArrayIndexOutOfBoundsException();
                        case "Placing destroyer": if(coordinates[x + 2][y].occupied) throw new ArrayIndexOutOfBoundsException();
                        case "Placing patrol boat": if(coordinates[x + 1][y].occupied || occupied) throw new ArrayIndexOutOfBoundsException();
                        break;
                        default:
                        break;
                     }
                  }
                  //if it managed to get this far without throwing an exception, time to place the ship!
                  //it checks what ship we're placing, then deletes any old one and places a new one
                  //this is so that you can't have a fleet of like 5 aircraft carriers or smth
                  //we can add more ships at any time just lmk, I only did 4 cus I think one is the same size?
                  if(state.equals("Placing carrier")) {
                     clear(carrier);
                     place(carrier);
                     jbCarrier.setText("Remove carrier");
                  }
                  else if(state.equals("Placing cruiser")) {
                     clear(cruiser);
                     place(cruiser);
                     jbCruiser.setText("Remove cruiser");
                  }
                  else if(state.equals("Placing destroyer")) {
                     clear(destroyer);
                     place(destroyer);
                     jbDestroyer.setText("Remove destroyer");
                  }
                  else if(state.equals("Placing patrol boat")) {
                     clear(patrolBoat);
                     place(patrolBoat);
                     jbPatrolBoat.setText("Remove patrol boat");
                  }
               }
               catch(ArrayIndexOutOfBoundsException aioobe) {
                  //if you try to put the ship out of bounds, or on top of another ship... ERROR!
                  JOptionPane.showMessageDialog(null, "Error! Cannot place ship there :(");
               }
               //this is a check to see if it needs to enable or disable the ready state
               // "ready" means you can start the battle, so all your ships are placed
               readyCheck();
            }
         });
      }
      
      //method to place a ship down
      //@param Coordinatep[] coord, the ship being placed
      public void place(Coordinate[] coord) {
         
         //do this for the length of the ship, so like carrier 5 cruiser 4 etc
         for(int i = 0; i < coord.length; i++) {
            
            //check horizontal/vertical placement
            if(rotate) {
               //add the coordinate to the ship array
               coord[i] = coordinates[x][y + i];
            }
            else {
               coord[i] = coordinates[x + i][y];
            }
            //visually show that the coordinate is occupied by a ship
            coord[i].setBackground(Color.BLACK);
            //set that coordinate as occupied by a ship
            coord[i].occupied = true;
         }
      }
      
      //toString for each coordinate, just for our testing purposes, no use in game
      public String toString() {
         return "Coordinate (" + x + ", " + y + ") - Occupied: " + occupied + ", Shot: " + shot;
      }
   }
   
   //clearing a ship from the board, for if you want to place it somewhere else
   //the placements of the ships aren't final until you hit the ready button
   public void clear(Coordinate[] coord) {
      if(coord[0] != null) {
         for(int i = 0; i < coord.length; i++) {
            coord[i].setBackground(Color.GRAY);
            coord[i].occupied = false;
            coord[i] = null;
         }
      }
   }
   
   //checking to see if your ships are placed. 
   //you can check this a number of ways, from occupied coords on the screen to the contents of the ship arrays
   public void readyCheck() {
      if(carrier[0] != null && cruiser[0] != null && destroyer[0] != null && patrolBoat[0] != null) jbReady.setEnabled(true);
      else jbReady.setEnabled(false);
   }
   
   //getter for coordinates, not sure if we'll need but w/e
   public Coordinate getCoords(int x, int y) {
      return coordinates[x][y];
   }
   
   //button stuff, comments are hard
   public void actionPerformed(ActionEvent ae) {
      Object pressedButton = ae.getSource();
      if(pressedButton == jbCarrier) {
         clear(carrier);
         readyCheck();
         jbCarrier.setText("Carrier");
         state = "Placing carrier";
      }
      else if(pressedButton == jbCruiser) {
         clear(cruiser);
         readyCheck();
         jbCruiser.setText("Cruiser");
         state = "Placing cruiser";
      }
      else if(pressedButton == jbDestroyer) {
         clear(destroyer);
         readyCheck();
         jbDestroyer.setText("Destroyer");
         state = "Placing destroyer";
      }
      else if(pressedButton == jbPatrolBoat) {
         clear(patrolBoat);
         readyCheck();
         jbPatrolBoat.setText("Patrol boat");
         state = "Placing patrol boat";
      }
      else if(pressedButton == jbRotate) {
         if (rotate == true) rotate = false;
         else rotate = true;
      }
      else {
         state = "Ready for battle! Waiting for other player...";
         jpFireControl.setVisible(true);
         jpShips.setVisible(false);
      }
      jlState.setText(state);
   }
   
   public static void main(String[] args) {
      new GridThing();
   }
}
