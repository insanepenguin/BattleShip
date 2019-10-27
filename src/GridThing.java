import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class GridThing extends JFrame implements ActionListener {

   private String state = "Place your ships by selecting them and clicking the grid!";
   
   private Coordinate[][] coordinates = new Coordinate[10][10];
   private Coordinate[] carrier = new Coordinate[5];
   private Coordinate[] cruiser = new Coordinate[4];
   private Coordinate[] destroyer = new Coordinate[3];
   private Coordinate[] patrolBoat = new Coordinate[2];
   
   
   private JLabel jlState = new JLabel(state, SwingConstants.CENTER);
   private JPanel jpShips = new JPanel(new GridLayout(0,1));
   private JPanel jpFireControl = new JPanel(new GridLayout(0,1));
   private JButton jbCarrier = new JButton("Carrier");
   private JButton jbCruiser = new JButton("Cruiser");
   private JButton jbDestroyer = new JButton("Destroyer");
   private JButton jbPatrolBoat = new JButton("Patrol Boat");
   private JButton jbRotate = new JButton("Rotate");
   private JButton jbReady = new JButton("READY");
   
   boolean rotate = true;
      
   public GridThing() {
      JPanel jpGrid = new JPanel(new GridLayout(0,10));
      for(int y = 0; y < 10; y++) {
         for(int x = 0; x < 10; x++) {
            coordinates[x][y] = new Coordinate(x, y);
            jpGrid.add(coordinates[x][y]);
         }
      }
      add(jpGrid, BorderLayout.CENTER);
      
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
      jpFireControl.add(new JLabel("Fire controls idk"));
      jpShips.setPreferredSize(new Dimension(200, 500));
      jpFireControl.setPreferredSize(new Dimension(200, 500));
      jpFireControl.setVisible(false);
      JPanel jpWest = new JPanel();
      jpWest.add(jpFireControl);
      jpWest.add(jpShips);
      add(jpWest, BorderLayout.WEST);
      add(jlState, BorderLayout.NORTH);
      
      setTitle("Grid Thingy by Ben");
      pack();
      setLocationRelativeTo(null);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
   }

   public class Coordinate extends JPanel {
      int x;
      int y;
      boolean occupied = false;
      boolean shot = false;
      
      public Coordinate(int _x, int _y) {
         x = _x;
         y = _y;
         setLayout(new BorderLayout());
         setBackground(Color.GRAY);
         setPreferredSize(new Dimension(50, 50));
         setBorder(new LineBorder(Color.BLACK, 1));
         addMouseListener(new MouseAdapter() {  
            public void mouseEntered(MouseEvent me) {
               try {
                  if(rotate) {
                     switch(state) {
                        case "Placing carrier": coordinates[x][y + 4].setBackground(Color.GREEN);
                        case "Placing cruiser": coordinates[x][y + 3].setBackground(Color.GREEN);
                        case "Placing destroyer": coordinates[x][y + 2].setBackground(Color.GREEN);
                        case "Placing patrol boat": coordinates[x][y + 1].setBackground(Color.GREEN);
                           setBackground(Color.GREEN);
                        break;
                        default:
                        break;
                     }
                  }
                  else {
                     switch(state) {
                        case "Placing carrier": coordinates[x + 4][y].setBackground(Color.GREEN);
                        case "Placing cruiser": coordinates[x + 3][y].setBackground(Color.GREEN);
                        case "Placing destroyer": coordinates[x + 2][y].setBackground(Color.GREEN);
                        case "Placing patrol boat": coordinates[x + 1][y].setBackground(Color.GREEN);
                           setBackground(Color.GREEN);
                        break;
                        default:
                        break;
                     }
                  }
               }
               catch(ArrayIndexOutOfBoundsException aioobe) {
                  setBackground(Color.RED);
               }
            }
            
            public void mouseExited(MouseEvent me) {
               try {
                  if(rotate) {
                     switch(state) {
                        case "Placing carrier": 
                           if(!coordinates[x][y + 4].occupied) coordinates[x][y + 4].setBackground(Color.GRAY);
                           else coordinates[x][y + 4].setBackground(Color.BLACK);
                        case "Placing cruiser": 
                           if(!coordinates[x][y + 3].occupied) coordinates[x][y + 3].setBackground(Color.GRAY);
                           else coordinates[x][y + 3].setBackground(Color.BLACK);
                        case "Placing destroyer": 
                           if(!coordinates[x][y + 2].occupied) coordinates[x][y + 2].setBackground(Color.GRAY);
                           else coordinates[x][y + 2].setBackground(Color.BLACK);
                        case "Placing patrol boat": 
                           if(!coordinates[x][y + 1].occupied) coordinates[x][y + 1].setBackground(Color.GRAY);
                           else coordinates[x][y + 1].setBackground(Color.BLACK);
                           if(!occupied) setBackground(Color.GRAY);
                           else setBackground(Color.BLACK);
                        break;
                        default:
                        break;
                     }
                  }
                  else {
                     switch(state) {
                        case "Placing carrier": 
                           if(!coordinates[x + 4][y].occupied) coordinates[x + 4][y].setBackground(Color.GRAY);
                           else coordinates[x + 4][y].setBackground(Color.BLACK);
                        case "Placing cruiser": 
                           if(!coordinates[x + 3][y].occupied) coordinates[x + 3][y].setBackground(Color.GRAY);
                           else coordinates[x + 3][y].setBackground(Color.BLACK);
                        case "Placing destroyer": 
                           if(!coordinates[x + 2][y].occupied) coordinates[x + 2][y].setBackground(Color.GRAY);
                           else coordinates[x + 2][y].setBackground(Color.BLACK);
                        case "Placing patrol boat": 
                           if(!coordinates[x + 1][y].occupied) coordinates[x + 1][y].setBackground(Color.GRAY);
                           else coordinates[x + 1][y].setBackground(Color.black);
                           if(!occupied) setBackground(Color.GRAY);
                           else setBackground(Color.BLACK);

                        break;
                        default:
                        break;
                     }
                  }
               }
               catch(ArrayIndexOutOfBoundsException aioobe) {
                  if(!occupied) setBackground(Color.GRAY);
                  else setBackground(Color.BLACK);
               }
            }
            
            public void mousePressed(MouseEvent me) {
               try {
                  if(rotate) {
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
                  if(state.equals("Placing carrier")) {
                     clear(carrier);
                     place(carrier);
                  }
                  else if(state.equals("Placing cruiser")) {
                     clear(cruiser);
                     place(cruiser);
                  }
                  else if(state.equals("Placing destroyer")) {
                     clear(destroyer);
                     place(destroyer);
                  }
                  else if(state.equals("Placing patrol boat")) {
                     clear(patrolBoat);
                     place(patrolBoat);
                  }
               }
               catch(ArrayIndexOutOfBoundsException aioobe) {
                  JOptionPane.showMessageDialog(null, "Error! Cannot place ship there :(");
               }
               readyCheck();
            }
         });
      }
      
      public void place(Coordinate[] coord) {
         for(int i = 0; i < coord.length; i++) {
            if(rotate) {
               coord[i] = coordinates[x][y + (coord.length - 1 - i)];
            }
            else {
               coord[i] = coordinates[x + (coord.length - 1 - i)][y];
            }
            coord[i].setBackground(Color.BLACK);
            coord[i].occupied = true;
         }
      }
      
      public String toString() {
         return "Coordinate (" + x + ", " + y + ") - Occupied: " + occupied + ", Shot: " + shot;
      }
   }
   
   public void clear(Coordinate[] coord) {
      if(coord[0] != null) {
         for(int i = 0; i < coord.length; i++) {
            coord[i].setBackground(Color.GRAY);
            coord[i].occupied = false;
         }
         coord = new Coordinate[coord.length];
      }
   }
   
   public void readyCheck() {
      int occupiedNum = 0;
      for(int y = 0; y < 10; y++) {
         for(int x = 0; x < 10; x++) {
            if(coordinates[x][y].occupied) {
               occupiedNum++;
            }
         }
      }
      if(occupiedNum == 14) {
         jbReady.setEnabled(true);
      }
      else {
         jbReady.setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent ae) {
      Object pressedButton = ae.getSource();
      if(pressedButton == jbCarrier) {
         clear(carrier);
         readyCheck();
         state = "Placing carrier";
      }
      else if(pressedButton == jbCruiser) {
         clear(cruiser);
         readyCheck();
         state = "Placing cruiser";
      }
      else if(pressedButton == jbDestroyer) {
         clear(destroyer);
         readyCheck();
         state = "Placing destroyer";
      }
      else if(pressedButton == jbPatrolBoat) {
         clear(patrolBoat);
         readyCheck();
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