import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
* This class is here for me to experiment on how best to implement the gui and any user interactivity
* I want to keep it separate from Client.java for now because it doesn't have any server <-> client connection code
* Also I'm not gonna write javadoc comments, this opener is more of a meme than anything
*
* @author Ben Shapiro
* @version 2019-10-27
*/

public class GridThing extends JFrame implements ActionListener {

   //which ship the player is placing down, if they aren't in the placing phase it's null
   private Coordinate[] selected;
   
   //the grid has a collection of coordinates, they're referred to by their position in this array
   private Coordinate[][] coordinates = new Coordinate[10][10];
   
   //the ships are just arrays of coordinates. I know we wanted a ship class but this just feels easier
   private Coordinate[] carrier = new Coordinate[5];
   private Coordinate[] cruiser = new Coordinate[4];
   private Coordinate[] destroyer = new Coordinate[3];
   private Coordinate[] submarine = new Coordinate[3];
   private Coordinate[] patrolBoat = new Coordinate[2];
   
   //these are the gui components that need to be declared globally
   private JPanel jpShips = new JPanel(new GridLayout(0,1));
   private JPanel jpFireControl = new JPanel(new GridLayout(0,1));
   private JButton jbCarrier = new JButton("Carrier");
   private JButton jbCruiser = new JButton("Cruiser");
   private JButton jbDestroyer = new JButton("Destroyer");
   private JButton jbSubmarine = new JButton("Submarine");
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
      jbSubmarine.addActionListener(this);
      jbPatrolBoat.addActionListener(this);
      jbRotate.addActionListener(this);
      jbReady.addActionListener(this);
      jbReady.setEnabled(false);
      jpShips.add(jbCarrier);
      jpShips.add(jbCruiser);
      jpShips.add(jbDestroyer);
      jpShips.add(jbSubmarine);
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
               try {
                  shade(Color.LIGHT_GRAY, Color.CYAN);
               }
               catch(ArrayIndexOutOfBoundsException aioobe) {}
            }
            //this is basically the same as the first method, but in reverse
            //it puts the colors back to their normal (non hovering) state. 
            public void mouseExited(MouseEvent me) {
               try {
                  shade(Color.GRAY, Color.BLUE);
               }
               catch(ArrayIndexOutOfBoundsException aioobe) {}
            }
            
            //this is when you click to place the ship down on the grid
            //if it can't place, it throws an exception which is handled
            public void mousePressed(MouseEvent me) {
               try {
                  check();
                  //if it managed to get this far without throwing an exception, time to place the ship!
                  //it checks what ship we're placing, then deletes any old one and places a new one
                  //this is so that you can't have a fleet of like 5 aircraft carriers or smth
                  //we can add more ships at any time just lmk, I only did 4 cus I think one is the same size?
                  if(selected == carrier) {
                     clear(carrier);
                     place(carrier);
                  }
                  else if(selected == cruiser) {
                     clear(cruiser);
                     place(cruiser);
                  }
                  else if(selected == destroyer) {
                     clear(destroyer);
                     place(destroyer);
                  }
                  else if(selected == submarine) {
                     clear(submarine);
                     place(submarine);
                  }
                  else if(selected == patrolBoat) {
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
            coord[i].setBackground(Color.GRAY);
            //set that coordinate as occupied by a ship
            coord[i].occupied = true;
         }
      }
      
      //method that colors the coordinates when you're hovering on them
      public void shade(Color occ, Color vac) {
         if(selected != null) {
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
         return "Coordinate (" + x + ", " + y + ") - Occupied: " + occupied + ", Shot: " + shot;
      }
   }
   
   //getter for coordinates, not sure if we'll need but w/e
   public Coordinate getCoords(int x, int y) {
      return coordinates[x][y];
   }
   
   //button stuff, comments are hard
   public void actionPerformed(ActionEvent ae) {
      Object pressedButton = ae.getSource();
      if(pressedButton == jbCarrier) selected = carrier;
      else if(pressedButton == jbCruiser) selected = cruiser;
      else if(pressedButton == jbDestroyer) selected = destroyer;
      else if(pressedButton == jbSubmarine) selected = submarine;
      else if(pressedButton == jbPatrolBoat) selected = patrolBoat;
      else if(pressedButton == jbRotate) {
         if (rotate == true) rotate = false;
         else rotate = true;
      }
      else {
         selected = null;
         jpFireControl.setVisible(true);
         jpShips.setVisible(false);
      }
   }
   
   public static void main(String[] args) {
      new GridThing();
   }
}
