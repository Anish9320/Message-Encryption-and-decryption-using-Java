//This program is client for Bidirectional Message Transfer

//import java.net.*;  //For Socket Programming
import java.io.*;   //For Streaming
//import java.util.*;  //For Scanner class

import java.sql.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.crypto.Cipher;   //Encryption and decryption operations.
import javax.crypto.SecretKey;   //Represents secret cryptographic keys.
//import javax.crypto.SecretKeyFactory;   //Generates secret keys.
//import javax.crypto.spec.DESKeySpec;  //Specifies keys for the DES algorithm.
import java.util.Base64;  //Encodes and decodes binary data using Base64.
import java.net.Socket;  //Creates client-server communication sockets.

//import java.nio.charset.StandardCharsets;   //Defines character encodings.
//import javax.crypto.*;                         //Cryptographic operations package.
import javax.crypto.spec.SecretKeySpec;   //Specifies secret keys based on a byte array.
//import java.security.InvalidKeyException;   //Indicates invalid cryptographic keys.
//import java.security.NoSuchAlgorithmException;  //Indicates unavailable cryptographic algorithms.


class Encryption_C
{

    JFrame frame;
    JLabel enterTextLabel, enterEncryptionKeyLabel, server_encryptedTextLabel,client_encryptedTextLabel,enterDecryptionKeyLabel, decryptedTextLabel;
    JTextField plainTextField, encryptionKeyField, server_encryptedTextField,client_encryptedTextField, decryptionKeyField, decryptedTextField;
    JButton encryptButton, decryptButton,clearButton;
    //Socket socket;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;
    OutputStream obs;
    DataOutputStream dout;
    InputStream ibs; 
    DataInputStream din;
    public int decryptionAttempts = 0;
    public boolean msgDestroyed = false;
   Encryption_C() throws Exception
   {
     

        frame = new JFrame("Encryption Client");
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

        frame.add(client_encryptedTextLabel);
        frame.add(client_encryptedTextField);
        frame.add(server_encryptedTextLabel);
        frame.add(server_encryptedTextField);


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

       Socket s = new Socket("localhost",2222); //Socket Object is created

      //connecting socket with DataOutputStream
      OutputStream obs = s.getOutputStream(); 
           //getOutputStream() returns OutputStream object

      DataOutputStream dout = new DataOutputStream(obs);
       

      //connecting socket with DataInputStream
      InputStream ibs = s.getInputStream();
         //getInputStream() returns InputStream object

      DataInputStream din = new DataInputStream(ibs); 


      //Scanner sc = new Scanner(System.in);
      String tx_str="", rx_str="";

      while( !rx_str.equals("stop") )   //continue loop till rx_str receives "stop" from Server
      {
        //client is transmitting
         //tx_str = sc.nextLine();  //it reads string message from command prompt  -> for testing
         tx_str = client_encryptedTextField.getText();
         dout.writeUTF(tx_str);   //sending message via stream using unicode(UTF)

        //client is receiving
         rx_str = din.readUTF();  //it reads message from input stream in Unicode format (UTF)

         if( !rx_str.equals(""))
              server_encryptedTextField.setText(rx_str);


         //if( !rx_str.equals(""))   -> for testing
                //System.out.println("Server Recieved message -> "+rx_str);

         dout.flush();           //flush() is used to remove contents from stream
      
       }
      
      System.out.println("The communication ended");
      //s.close();       //socket connection is closed
   }


     class EncryptButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
         {
            try {

                String encryptionKey = encryptionKeyField.getText();
            if (encryptionKey.length() != 8) {
                JOptionPane.showMessageDialog(frame, "Encryption key must be 8 digits long.", "Invalid Key", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method if the key length is invalid
            }
                String plainText = plainTextField.getText();
                //String encryptionKey = encryptionKeyField.getText();

                
                 Cipher cipher = Cipher.getInstance("DES"); //Get an instance of the DES encryption algorithm
                 SecretKey secretKey = new SecretKeySpec(encryptionKey.getBytes(), "DES");   // Create a secret key from the provided encryption key bytes
                 cipher.init(Cipher.ENCRYPT_MODE, secretKey);  // Initialize the cipher in encryption mode with the secret key
                 byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());  // Encrypt the plaintext bytes using the initialized cipher
                 String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);  // Convert the encrypted bytes to a Base64-encoded string
                                                                /*Base64 encoding is a method used to encode binary data (such as encrypted bytes) 
                                                                into ASCII characters, making it suitable for transmission over text-based protocols 
                                                                 like email or HTTP. This step is often performed to ensure that 
                                                                encrypted data can be safely transmitted and stored without the risk of data corruption or loss. */

                // Display encrypted text
                client_encryptedTextField.setText("");               //remove previous string
                client_encryptedTextField.setText(encryptedText);

               
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

            String encryptedText = server_encryptedTextField.getText();

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
            Cipher cipher = Cipher.getInstance("DES"); // Initialize a Cipher object for decryption using the DES algorithm
            SecretKey secretKey = new SecretKeySpec(decryptionKey.getBytes(), "DES");  // Create a SecretKey object using the decryption key provided as a byte array 
            cipher.init(Cipher.DECRYPT_MODE, secretKey);  // Initialize the cipher in decryption mode with the provided key
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText)); //Decode the encrypted text from Base64 format to obtain the encrypted bytes
            String decryptedText = new String(decryptedBytes); // Convert the decrypted bytes to a string using the String constructor

           
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
            new Encryption_C();
     }

}