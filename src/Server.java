import java.util.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.text.*;

public class Server implements ActionListener
{//open ServerMockup class

    //Object for sync purposes
    private Object onMe = new Object();

    //String to be updated with the newest line in chat
    private String message = "I get updated... a LOT...";

    //GUI components that need to be declared globally
    private JFrame jfMain;
    private JLabel jlClock;
    private JLabel jlUptime;
    private JTextArea jtaConsole = new JTextArea();
    private JPanel jpUsers = new JPanel();
    private JButton jbQuit;

    //clock and uptime related items
    private ActionListener clockThread;
    private javax.swing.Timer clockTimer;
    private int upSec = 0;
    private int upMin = 0;
    private int upHour = 0;

    public Server()
    {//open ChatServer constructor

        jfMain = new JFrame();
        JPanel jpNorth = new JPanel(new GridLayout(0, 2));
        jlClock = new JLabel("Server starting...", SwingConstants.CENTER);
        jlUptime = new JLabel("Uptime:  00  :  00  :  00", SwingConstants.CENTER);
        jpNorth.add(jlClock);
        jpNorth.add(jlUptime);
        jpNorth.add(new JLabel("[Diagnostics Console]", SwingConstants.CENTER));
        jpNorth.add(new JLabel("[Present users (by connection# and address)]", SwingConstants.CENTER));
        jfMain.add(jpNorth, BorderLayout.NORTH);
        jtaConsole.setEnabled(false);
        jtaConsole.setForeground(Color.RED);
        JScrollPane jspScroller = new JScrollPane(jtaConsole, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jspScroller.setPreferredSize(new Dimension(250, 1000));
        JPanel jpCenter = new JPanel(new GridLayout(0, 2));
        jpCenter.add(jspScroller);
        jpUsers.setBorder(new LineBorder(Color.BLACK, 1));
        jpCenter.add(jpUsers);
        jfMain.add(jpCenter, BorderLayout.CENTER);
        jbQuit = new JButton("Close Server");
        jbQuit.addActionListener(this);
        jfMain.add(jbQuit, BorderLayout.SOUTH);

        jfMain.setTitle("Serverside GUI");
        jfMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfMain.setLocationRelativeTo(null);
        jfMain.setSize(800, 500);
        jfMain.setVisible(true);


        clockTimer = new javax.swing.Timer(1000, this);
        clockTimer.start();

        jtaConsole.setText(" Gui launched!");

        try
        {//open Try
            //these two lines show how to get the ip address of this client
            addition("Server starting with host: " + InetAddress.getLocalHost());
            addition("Server getByName: " + InetAddress.getByName("localhost"));
        }//close Try
        catch(UnknownHostException uhe)
        {//open catch
            uhe.printStackTrace();
        }//close catch
        ServerSocket ss;
        Socket cs;
        try
        {//open try
            ss = new ServerSocket(16789);
            int counter = 0;
            while(true)
            {//open while
                cs = ss.accept();
                counter++;
                new Connection(cs, counter).start();
            }//close while
        }//close try
        catch(IOException ioe)
        {//open catch
            System.err.println("IO error in main class constructor");
            ioe.printStackTrace();
        }//close catch
    }//close ChatServer constructor

    public void actionPerformed(ActionEvent ae)
    { //open action performed
        if(ae.getSource() == jbQuit)
        { //open if
            jfMain.dispose();
            System.exit(0);
        } //close if
        else
        { //open else
            jlClock.setText("Date & time: " + fmtDate());
            jlUptime.setText("Uptime:  " + uptimeCalc());
        } //close else
    } //close action performed


    //Method responsible for formatting the clock
    //I included the day and
    private String fmtDate()
    {//open fmtDate
        final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd/MM - hh:mm:ss aa");
        String theDate = DATE_FMT.format(new Date());
        return theDate;
    }//close fmtDate

    private String uptimeCalc()
    {//open timeCalc
        upSec++;
        if(upSec == 60) {
            upSec = 0;
            upMin++;
        }
        if(upMin == 60) {
            upMin = 0;
            upHour++;
        }

        String uptime = String.format("%02d  :  %02d  :  %02d", upHour, upMin, upSec);
        return uptime;
    }//close timeCalc

    public void addition(String str)
    {//open addition
        synchronized(jtaConsole) {
            jtaConsole.requestFocus();
            jtaConsole.setText(jtaConsole.getText() + "\n " + str);
        }
    }//close addition

    // this thread is instantiated once for every connection and is in charge of that connection,
    // handling sends and recieves to and from the client
    public class Connection extends Thread
    {//open Connection innerClass (extends threads)

        // these are the subthreads, there are 2 subthreads on the serverside thread of every connection;
        // 1 to send out chats, and 1 to recieve them
        Sender send;
        Reciever recieve;
        boolean running = true;

        //the GUI component that shows user ID is declared here
        JLabel jlUserId;

        // I was thinking we could ID clients by their InetAddresses?
        public Connection(Socket sock, int count)
        {//open Connection constructor
            send = new Sender(sock);
            recieve = new Reciever(sock);
            setName("[Connection " + (sock.getInetAddress() + "").substring(1) + "] ");

            //initializing gui component that shows user ID
            jlUserId = new JLabel("     User "+ count + ": " + getName());
            //adding the user to the list of active users, ID'd by address
            jpUsers.add(jlUserId);
        }//close Connection constructor

        public void run()
        {//open Run method (for Connection class)
            // this thread just runs the subthreads for it's connection
            addition(getName() + "Connector Thread opening");
            send.start();
            recieve.start();
            addition(getName() + "Connected, Connector closing");
        }//close run method (for connection class)

        public class Sender extends Thread
        {//open Sender class (extends thread)
            Socket cs;
            public Sender(Socket sock)
            {//open Sender constructor
                setName((sock.getInetAddress() + "").substring(1));
                cs = sock;
            }//close Sender constructor

            public void run()
            {//open Run method (for Sender class)
                addition("[Sender " + getName() + "] Data sender opening");
                try
                {//open try
                    PrintWriter pout = new PrintWriter(new OutputStreamWriter(cs.getOutputStream()));
                    addition("[Sender " + getName() + "] Data sender open");
                    while(running)
                    {//open while
                        synchronized(onMe)
                        {//open synchronization block
                            onMe.wait();
                        }//close synchronization block
                        pout.println(message);
                        pout.flush();
                    }//close while
                    pout.close();
                }//close try
                catch (InterruptedException ie)
                {//open 1st catch
                    addition("Sender " + getName() + ": An InterruptedException was caught");
                    ie.printStackTrace();
                }//close 1st catch
                catch (IOException ioe)
                {//open 2nd catch
                    addition("Sender " + getName() + ": An IO error was caught");
                    ioe.printStackTrace();
                }//close 2nd catch
                jlUserId.setVisible(false);
                addition("[Sender " + getName() + "] Data sender closed");
            }//close Run method (for Sender class)
        }//close Sender inner class

        public class Reciever extends Thread
        {//open Reciever inner class
            Socket cs;
            String chattorName;
            String[] curses ={"shit", "fuck", "damn", "bitch", "ass", "cunt"};
            boolean allGood = true;

            public Reciever(Socket sock)
            {//open Reciever constructor
                chattorName = (sock.getInetAddress() + "").substring(1);
                setName(chattorName);
                cs = sock;
            }//close Reciever constructor

            public void run()
            {//open Run method (for Reciever class)
                addition("[Reciever " + getName() + "] Data reciever opening");
                try
                {//open Try
                    ObjectInputStream ois = new ObjectInputStream(cs.getInputStream());
                    addition("[Reciever " + getName() + "] Data reciever open");
                    while(running)
                    {//open While
                        String holder = ois.readUTF();
                        if(holder.equals("/disconnect"))
                        {//open if
                            throw new SocketException();
                        }//close if
                        else if (holder.length() > 8 && holder.substring(0,9).equals("/setname "))
                        {//open else if
                            for(int i=0; i<6; i++)
                            {//open for loop
                                if ((holder.substring(9).toLowerCase()).contains(curses[i]))
                                {//open if
                                    allGood = false;
                                    synchronized(onMe)
                                    {//open synchronized
                                        message = "[" + chattorName + " tried to set an inappropriate name]";
                                        onMe.notifyAll();
                                    }//close synchronized
                                    break;
                                }//close if
                            }//close for loop
                            if(allGood)
                            {//open if
                                synchronized(onMe)
                                {//open Synchronization block
                                    message = "[" + chattorName + " has set their name to " + holder.substring(9) + "]";
                                    onMe.notifyAll();
                                }//close Synchronization block
                                chattorName = holder.substring(9);
                            }
                        }//close else if
                        else
                        {//open else
                            synchronized(onMe)
                            {//open Synchronization block
                                message = "[" + chattorName + "]: " + holder;
                                onMe.notifyAll();
                            }//close Synchronization block
                        }//close else
                    }//close while loop
                    ois.close();
                }//close try
                catch(SocketException se)
                {//open 1st catch
                    disconnect();
                }//close 1st catch
                catch (IOException ioe)
                {//open 2nd catch
                    addition("Reciever " + getName() + ": An IO error was caught");
                    ioe.printStackTrace();
                }//close 2nd catch
                addition("[Reciever " + getName() + "] Data reciever closed");
            }//close run method (for Reciever class)

            public void disconnect()
            {//open disconnect method
                synchronized(onMe)
                {//open Synchronization block
                    message = "[" + chattorName + " has left the chat]";
                    running = false;
                    onMe.notifyAll();
                }//close Synchronization block
            }//close disconnect method
        }//close Reciever inner class
    }//close Connection inner class

    public static void main(String[] args)
    {//open Main
        new Server();
    }//close Main
}//close ServerMockUp class
