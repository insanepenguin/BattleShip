import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MyClient - demo of client - server network connection
 */
public class Client extends Thread implements ActionListener
{//open the ChatClient class

    //create private instance variables for our GUI
    private JFrame jfChatFrame = new JFrame();
    private JTextArea jtaChatBox = new JTextArea(20,35);
    private JScrollPane jspScroller = new JScrollPane(jtaChatBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JScrollBar ScrollBar = jspScroller.getVerticalScrollBar();
    private JPanel jpSouth = new JPanel();
    private JPanel jpCenter = new JPanel();
    private JTextField jtfInput = new JTextField(15);
    private JButton jbSend = new JButton("Send");

    //create Jmenu/menu bar-- and items
    JMenu jmMenu1 = new JMenu("Commands");
    JMenuBar jmbChatMenu = new JMenuBar();

    JMenuItem jmiSetName = new JMenuItem("Set Name");
    JMenuItem jmiDisconnect = new JMenuItem("Disconnect");

    //create private instance variables for sending/connecting
    private String ipAddress;
    PrintWriter pout;
    Socket s;

    public Client()
    {//open the ChatClient constructor
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
                new Recieve(s).start();
            }//close try
            catch(IOException ioe)
            {//open 1st catch
                JOptionPane.showMessageDialog(null, "Couldn't find that server! (Or it doesn't exist?) Please try again.");
                continue;
            }//close 1st catch
            break;
        }//close while
        //Set layout to Border Layout
        jfChatFrame.setLayout(new BorderLayout(5,10));

        //Add the Scroller (with the text area to the north segment)
        jpCenter.add(jspScroller);

        //add the label and the search text field to the panel)
        jpSouth.add(new JLabel("Input: "));
        jpSouth.add(jtfInput);

        //add the Find and clear buttons to the south panel)
        jpSouth.add(jbSend);
        jbSend.addActionListener(this);


        //add menu items to menu
        jmiSetName.addActionListener(this);
        jmiDisconnect.addActionListener(this);
        jmMenu1.add(jmiSetName);
        jmMenu1.add(jmiDisconnect);

        //add menu to menu bar
        jmbChatMenu.add(jmMenu1);

        //add the gui components to the total JFrame
        jfChatFrame.add(jpCenter, BorderLayout.CENTER);
        jfChatFrame.add(jpSouth, BorderLayout.SOUTH);
        jfChatFrame.setJMenuBar(jmbChatMenu);

        //make Enter press the "jbSend" button
        jfChatFrame.getRootPane().setDefaultButton(jbSend);

        //make the text area un-editable
        jtaChatBox.setEnabled(false);
        jtaChatBox.setLineWrap(true);
        jtaChatBox.setWrapStyleWord(true);

        //make the Jframe a real visible object
        jfChatFrame.setTitle("Chat Box");
        jfChatFrame.pack();
        jfChatFrame.setLocationRelativeTo(null);
        jfChatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfChatFrame.setVisible(true);

        try
        {//open try
            //the initial "boot up" message
            jtaChatBox.setText("Welcome you joined the chat!\nBy default, your name will be your IP address");
            pout = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
        }//close try
        catch(IOException ioe)
        {//open Catch
            System.out.println("Something went wrong with client initialization");
            ioe.printStackTrace();
        }//close catch
    }//close the ChatClient constructor

    public void actionPerformed(ActionEvent ae)
    {//open ActionPerformed method

        //make the actual action listener
        switch(ae.getActionCommand())
        {//open switch
            case "Send":
                //open Case "SEND"
                String message = jtfInput.getText();
                if(message.trim().equals("")) {
                    //don't do nothin! we don't want people sending just blank space
                }
                else if(message.equals("/disconnect")) {
                    disconnect("[Disconnected from server]");
                }
                else {
                    pout.println(message);
                    pout.flush();
                }
                break;
            //CLOSE CASE "SEND"
            case "Set Name":
                //OPEN CASE "SET NAME"
                newName();
                break;
            //CLOSE CASE "SET NAME"
            case "Disconnect":
                //OPEN CASE "DISCONNECT"
                disconnect("[Disconnected from server]");
                break;
            //CLOSE CASE "DISCONNECT"
            case "Reconnect":
                //OPEN CASE "RECONNECT"
                reconnect();
                break;
            //CLOSE CASE "RECONNECT"
        }//close switch
        jtfInput.setText("");
    }//close actionPerformed method

    public void newName()
    { //open newName
        String name = "";
        try
        { //open try
            name = JOptionPane.showInputDialog("Please type a name to use in chat");
            while(name.trim().equals(""))
            { //open while
                name = JOptionPane.showInputDialog("No Name detected, try again");
            } //close while
            pout.println("/setname " + name);
            pout.flush();
        } //close try
        catch(NullPointerException npe)
        { //open catch
            jtaChatBox.setText(jtaChatBox.getText() + "\n[No name set]");
        } //close catch
    } //close newName

    public void disconnect(String statement)
    { //open disonnect
        try
        {//open try
            pout.println("/disconnect");
            pout.flush();
            s.close();
        }//close try
        catch(IOException ioe)
        {//open catch
            System.out.println("Error while disconnecting");
        }//close catch
        jmiDisconnect.setText("Reconnect");
        jtaChatBox.setText(statement + "\n[To connect again, choose Reconnect from the menu]");
    } //close disconnect

    public void reconnect()
    { //open reconnect
        jfChatFrame.dispose();
        new Client();
    } //close reconnect

    public class Recieve extends Thread
    {//open Recieve innerclass
        Socket sock;
        String message;

        public Recieve(Socket _sock)
        {//open Recieve constructor
            sock = _sock;

            try
            {//open try
                setName(InetAddress.getLocalHost() + "");
            }//close try
            catch(UnknownHostException uhe)
            {//open catch
                uhe.printStackTrace();
            }//close catch
        }//close Recieve constructor

        public void run()
        {//open Run method (for Recieve class?)
            try
            {//open try
                BufferedReader bin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                while(sock.isConnected())
                {//open while
                    message = bin.readLine();
                    jtaChatBox.setText(jtaChatBox.getText() + "\n" + message);
                    ScrollBar.setValue(ScrollBar.getMaximum()+1);
                }//close while
                bin.close();
            }//close try
            catch(SocketException se)
            {//open 1st catch
                disconnect("[Disconnected from server]");
            }//close 1st catch
            catch(IOException ioe)
            {//open 2nd catch
                System.out.println("[Unexpected IO error; disconnecting from server]");
                ioe.printStackTrace();
            }//close 2nd catch
        }//close run method for Recieve
    }//close Recieve innerclass
    public static void main(String[] args)
    {//open Main
        Client jfMain = new Client();
    }//close Main
}//close ChatClient class
