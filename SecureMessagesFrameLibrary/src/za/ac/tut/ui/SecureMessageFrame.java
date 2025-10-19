
package za.ac.tut.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.border.BevelBorder;

public class SecureMessageFrame extends JFrame{

    private JPanel plainPanel ;
    private JPanel encryptedPanel;
    private JPanel centerPanel;
    
    private JTextArea plainTextArea;
    private JTextArea encryptedTextArea;
    
    private JScrollPane plainScroll;
    private JScrollPane encryptedScroll;

    private JLabel titleLabel;
    
    private JMenuBar menuBar;
    
    private JMenu fileMenu ;
    
    private JMenuItem openItem;
    private JMenuItem encryptItem;
    private JMenuItem saveItem;
    private JMenuItem clearItem;
    private JMenuItem exitItem;
    
    public SecureMessageFrame() {
        
        setTitle("Secure Messages");

                // === TEXT AREAS ===
        plainTextArea = new JTextArea(15, 30);
        encryptedTextArea = new JTextArea(15, 30);
        encryptedTextArea.setEditable(false);

        // === ADD SCROLL PANES ===
         plainScroll = new JScrollPane(plainTextArea , JScrollPane.VERTICAL_SCROLLBAR_ALWAYS , JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
         encryptedScroll = new JScrollPane(encryptedTextArea , JScrollPane.VERTICAL_SCROLLBAR_ALWAYS , JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

         /*
        // === LABEL (BLUE TITLE) ===
        titleLabel = new JLabel("Message Encryptor", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLUE);
        */
        
        
        titleLabel = new JLabel("Message Encryptor" , SwingConstants.CENTER);
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setFont(new Font("SERIF", Font.BOLD + Font.ITALIC, 30));
        titleLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
        

        // === PANELS FOR TEXT AREAS ===
        plainPanel = new JPanel(new BorderLayout());
        plainPanel.setBorder(BorderFactory.createTitledBorder("Plain Message"));
        plainPanel.add(plainScroll, BorderLayout.CENTER); // ✅ using scroll pane

        encryptedPanel = new JPanel(new BorderLayout());
        encryptedPanel.setBorder(BorderFactory.createTitledBorder("Encrypted Message"));
        encryptedPanel.add(encryptedScroll, BorderLayout.CENTER); // ✅ using scroll pane

        // === MENU BAR ===
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");

        openItem = new JMenuItem("Open File");
        encryptItem = new JMenuItem("Encrypt Message");
        saveItem = new JMenuItem("Save Encrypted Message");
        clearItem = new JMenuItem("Clear");
        exitItem = new JMenuItem("Exit");

        fileMenu.add(openItem);
        fileMenu.add(encryptItem);
        fileMenu.add(saveItem);
        fileMenu.add(clearItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // === MAIN PANEL (FLOW LAYOUT) ===
        centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        centerPanel.add(plainPanel);
        centerPanel.add(encryptedPanel);

        // === ADD COMPONENTS TO FRAME ===
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // === EVENT HANDLERS ===
        openItem.addActionListener(e -> openFile());
        encryptItem.addActionListener(e -> encryptMessage());
        saveItem.addActionListener(e -> saveEncryptedMessage());
        clearItem.addActionListener(e -> clearTextAreas());
        exitItem.addActionListener(e -> exitApp());
        
        // === FRAME SETTINGS ===
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
         
        setVisible(true);
    }  
    
        //3.1
    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                plainTextArea.setText("");
                String line;
                while ((line = reader.readLine()) != null) {
                    plainTextArea.append(line + "\n");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
            }
        }
    }
    
    //3.2 , 3.3 encrypt massage
    private void encryptMessage() {
        String plain = plainTextArea.getText().toLowerCase();
        StringBuilder encrypted = new StringBuilder();

        for (char c : plain.toCharArray()) {
            if (Character.isLetter(c)) {
                char shifted = (char) (((c - 'a' + 3) % 26) + 'a');
                encrypted.append(shifted);
            } else {
                encrypted.append(c);
            }
        }

        encryptedTextArea.setText(encrypted.toString());//3.3
    }

    //3.4 , 3.5 SAVE ENCRYPTED MESSAGE ===
    private void saveEncryptedMessage() {
        
        //-->3.5
        String message = encryptedTextArea.getText();
        
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No encrypted message to save!");
            return;
        }
        //<--3.5
        // Database connection details
        String dbURL = "jdbc:derby://localhost:1527/SecureMessagesDB";
        String user = "app";
        String password = "app";

        try (Connection conn = DriverManager.getConnection(dbURL, user, password)) {
            String sql = "INSERT INTO ENCRYPTEDMESSAGES (ID, MESSAGE , TIMESTAMP) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            int id = (int) (Math.random() * 1000000);
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            ps.setInt(1, id);
            ps.setString(2, message);
            ps.setString(3, timestamp);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Encrypted message saved successfully!");
            JOptionPane.showMessageDialog(this, "Encrypted message saved successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving to database: " + ex.getMessage());
        }
    }
    
    //3.6 === CLEAR ===
    private void clearTextAreas() {
        plainTextArea.setText("");
        encryptedTextArea.setText("");
    }
    
    //3.7 === EXIT ===
    private void exitApp() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?",
                "Exit", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
