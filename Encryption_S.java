//This program is Server for Bidirectional Message Transfer

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;  //For Socket Programming
import java.io.*;   //For Streaming
import java.util.*;  //For Scanner class

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.DESKeySpec;

import java.sql.*;

//import java.nio.charset.StandardCharsets;
//import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;

class Encryption_S
{

         JFrame frame;
    	 JLabel enterTextLabel, enterEncryptionKeyLabel, server_encryptedTextLabel,client_encryptedTextLabel, enterDecryptionKeyLabel, decryptedTextLabel;
    	 JTextField plainTextField, encryptionKeyField, server_encryptedTextField,client_encryptedTextField ,decryptionKeyField, decryptedTextField;
    	 JButton encryptButton, decryptButton,clearButton;
    	 ServerSocket serverSocket;
    	 //Socket clientSocket;
         Socket s;
    	 ObjectOutputStream outputStream;
    	 ObjectInputStream inputStream;
         OutputStream obs;
         DataOutputStream dout;
         InputStream ibs; 
         DataInputStream din;
         public int decryptionAttempts = 0;
         public boolean msgDestroyed = false;


   //public static void main(String args[])throws Exception
   Encryption_S() throws Exception
   {
      
        frame = new JFrame("Encryption Server");
        frame.setLayout(new GridLayout(8, 3));

        enterTextLabel = new JLabel("Enter Text here:");
        plainTextField = new JTextField();
        enterEncryptionKeyLabel = new JLabel("Enter 8 digit Encryption Key:");
        encryptionKeyField = new JTextField();

        server_encryptedTextLabel = new JLabel("Server Encrypted Text:");
        server_encryptedTextField = new JTextField();
        client_encryptedTextLabel = new JLabel("Client Encrypted Text:");
        client_encryptedTextField = new JTextField();

        enterDecryptionKeyLabel = new JLabel("Enter 8 digit Decryption Key:");
        decryptionKeyField = new JTextField();
        decryptedTextLabel = new JLabel("Decrypted Text:");
        decryptedTextField = new JTextField();

        encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(new EncryptButtonListener());
        

        decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(new DecryptButtonListener());
       
        clearButton = new JButton("Clear All");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                plainTextField.setText("");
                encryptionKeyField.setText("");
                server_encryptedTextField.setText("");
                client_encryptedTextField.setText("");
                decryptionKeyField.setText("");
                decryptedTextField.setText("");
                decryptionAttempts = 0;
                msgDestroyed = false;
            }
        });
        frame.add(enterTextLabel);
        frame.add(plainTextField);
        frame.add(enterEncryptionKeyLabel);
        frame.add(encryptionKeyField);

        frame.add(server_encryptedTextLabel);
        frame.add(server_encryptedTextField);
        frame.add(client_encryptedTextLabel);
        frame.add(client_encryptedTextField);

        frame.add(enterDecryptionKeyLabel);
        frame.add(decryptionKeyField);
        frame.add(decryptedTextLabel);
        frame.add(decryptedTextField);
        frame.add(encryptButton);
        frame.add(decryptButton);
        frame.add(clearButton);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setVisible(true);
     
      ServerSocket ss = new ServerSocket(2222); //ServerSocket Object is created with port number 2222
      s = ss.accept();       //accept() will listen client request and generates Socket object

      //connecting socket with DataOutputStream
      OutputStream obs = s.getOutputStream(); 
           //getOutputStream() returns OutputStream object

      DataOutputStream dout = new DataOutputStream(obs);
       

      //connecting socket with DataInputStream
      InputStream ibs = s.getInputStream();
         //getInputStream() returns InputStream object

      DataInputStream din = new DataInputStream(ibs); 


      Scanner sc = new Scanner(System.in);
      String tx_str="", rx_str="";

      while( !rx_str.equals("stop") )   //continue loop till rx_str receives "stop" from client
      {
         //Server is receiving
          rx_str = din.readUTF();  //it reads message from input stream in Unicode format (UTF)
          client_encryptedTextField.setText(rx_str);
     
         //if( !rx_str.equals(""))
             //System.out.println("client recieved message -> "+rx_str);         


       //Server is transmitting
         //tx_str = sc.nextLine();  //it reads string message from command prompt
         tx_str = server_encryptedTextField.getText();
         dout.writeUTF(tx_str);   //sending message via stream using unicode(UTF)
        

         dout.flush();           //flush() is used to remove contents from stream

      }
   
      System.out.println("The communication ended");
      s.close();                                        //socket connection is closed

  }


 class EncryptButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                
                String encryptionKey = encryptionKeyField.getText();
                if (encryptionKey.length() != 8) {
                    JOptionPane.showMessageDialog(frame, "Encryption key must be 8 digits long.", "Invalid Key", JOptionPane.ERROR_MESSAGE);
                    return; // Exit the method if the key length is invalid
                }

                String plainText = plainTextField.getText();
                //String encryptionKey = encryptionKeyField.getText();

                // Encrypt the plain text using DES algorithm
                 Cipher cipher = Cipher.getInstance("DES");
                 SecretKey secretKey = new SecretKeySpec(encryptionKey.getBytes(), "DES");
                 cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                 byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
                 String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
                
                // Display encrypted text
                server_encryptedTextField.setText("");        //remove previous string     
                server_encryptedTextField.setText(encryptedText);

                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
//DATABASE
class Database{
    public static Connection getConnection(){
        try{
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/finalyear","root","");
            return connection;
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Connection could be Established"+e.getMessage());
            return null;
        }
    }
}
class DecryptButtonListener implements ActionListener {

    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String decryptionKey = decryptionKeyField.getText();
            if (decryptionKey.length() != 8) {
                JOptionPane.showMessageDialog(frame, "Decryption key must be 8 digits long.", "Invalid Key", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method if the key length is invalid
            }

            Connection conn = Database.getConnection();
            PreparedStatement ps;
            
            if(conn != null){
                System.out.println("Connection created");
            }

            String encryptedText = client_encryptedTextField.getText();

            // Check if the message has been destroyed
            if (msgDestroyed) {
                JOptionPane.showMessageDialog(frame, "Message destroyed. Cannot proceed with decryption.", "Message Destroyed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the encryption key used by the client
            String clientEncryptionKey = encryptionKeyField.getText();
            //else {
                // Reset decryption attempts counter
              //  decryptionAttempts = 0;
            //}

            // Decrypt the encrypted text using DES algorithm
            Cipher cipher = Cipher.getInstance("DES");
            SecretKey secretKey = new SecretKeySpec(decryptionKey.getBytes(), "DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            String decryptedText = new String(decryptedBytes);

           
            // Display decrypted text
            decryptedTextField.setText("");          //remove previous string              
            decryptedTextField.setText(decryptedText);

            //Insert decrypted data into database
            ps = conn.prepareStatement("INSERT INTO client("+"Server_encrypted,decryptionKey,decryptedText) VALUES(?,?,?)");  
            ps.setString(1, encryptedText);
            ps.setString(2, decryptionKey);
            ps.setString(3, decryptedText);

            ps.execute();
            System.out.println("DONE RESULTS");
        
        } catch (Exception ex) {
            decryptionAttempts++;
                if (decryptionAttempts >= 5) {
                    msgDestroyed = true;
                    JOptionPane.showMessageDialog(frame, "Message destroyed.", "Message Destroyed", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Wrong decryption key entered. Attempts left: " + (5 - decryptionAttempts), "Decryption Error", JOptionPane.ERROR_MESSAGE);
                }
        }
    }

    }


  public static void main(String args[])throws Exception
   {
         new Encryption_S();
   }


   
}