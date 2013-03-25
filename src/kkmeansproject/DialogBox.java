/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kkmeansproject;

/**
 *
 * @author Denda
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
 
/**
 * TextInputDemo.java uses these additional files:
 *   SpringUtilities.java
 *   ...
 */
public abstract class DialogBox extends JPanel
                                          implements ActionListener{
    JTextField URL1, URL2;
    boolean addressSet = false;
    Font regularFont;
    JTextArea addressDisplay;
    final static int GAP = 10;
 
    public DialogBox() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
 
        JPanel leftHalf = new JPanel() {
            //Don't allow us to stretch vertically.
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE,
                                     pref.height);
            }
        };
        leftHalf.setLayout(new BoxLayout(leftHalf,
                                         BoxLayout.PAGE_AXIS));
        leftHalf.add(createEntryFields());
        leftHalf.add(createButtons());
 
        add(leftHalf);
        add(createAddressDisplay());
    }
 
    protected JComponent createButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
 
        JButton button = new JButton("Start Analysis");
        button.addActionListener(this);
        panel.add(button);
 
        button = new JButton("Clear URLs Field");
        button.addActionListener(this);
        button.setActionCommand("clear");
        panel.add(button);
 
        //Match the SpringLayout's gap, subtracting 5 to make
        //up for the default gap FlowLayout provides.
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0,
                                                GAP-5, GAP-5));
        return panel;
    }
 
    /**
     * Called when the user clicks the button or presses
     * Enter in a text field.
     */
    public void actionPerformed(ActionEvent e) {
        if ("clear".equals(e.getActionCommand())) {
            addressSet = false;
            URL1.setText("");
            URL2.setText("");
        } else {
            addressSet = true;
            
        }
        updateDisplays();
    }
 
    protected void updateDisplays() {
        addressDisplay.setText(formatAddress());
        URL1.setText("http://nadeausoftware.com/node/73");
        URL2.setText("http://www.fit.cvut.cz/en"); 
        addressDisplay.setFont(regularFont);


    }
 
    protected JComponent createAddressDisplay() {
        JPanel panel = new JPanel(new BorderLayout());
        addressDisplay = new JTextArea();
addressDisplay.setEditable(false);

JScrollPane scrollPane = new JScrollPane(addressDisplay);
scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);


        regularFont = addressDisplay.getFont().deriveFont(Font.PLAIN,
                                                            12.0f);
        updateDisplays();

        panel.setBorder(BorderFactory.createEmptyBorder(
                                GAP/2, //top
                                0,     //left
                                GAP/2, //bottom
                                0));   //right
        panel.add(new JSeparator(JSeparator.VERTICAL),
                  BorderLayout.LINE_START);
      
        panel.add(scrollPane,
                  BorderLayout.CENTER);
        
        panel.setPreferredSize(new Dimension(900, 600));
 
        return panel;
    }
 
    protected String formatAddress() {
        if (!addressSet) return "No URL sets \n";
        String FirstURL = URL1.getText();
        String SecondURL = URL2.getText();
        String empty = "";
 
        if ((FirstURL == null) || empty.equals(FirstURL)) {
            FirstURL = "<em>(First url not specified)</em>\n";
        }
        if ((SecondURL == null) || empty.equals(SecondURL)) {
            SecondURL = "<em>(Second url not specified)</em>\n";
        }
      
 
        StringBuffer sb = new StringBuffer();
        sb.append(FirstURL);
        sb.append("\n");
        sb.append(SecondURL);
        sb.append("\n");
        
        KkmeansProject content = new KkmeansProject();
        String content1 = content.StartClustering(FirstURL, SecondURL);
        sb.append(content1);
        return sb.toString();
    }
 
    //A convenience method for creating a MaskFormatter.
    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

 
    protected JComponent createEntryFields() {
        JPanel panel = new JPanel(new SpringLayout());
 
        String[] labelStrings = {
            "URL1: ",
            "URL2: ",
        };
        JLabel[] labels = new JLabel[labelStrings.length];
        JComponent[] fields = new JComponent[labelStrings.length];
        int fieldNum = 0;
 
        //Create the text field and set it up.
        URL1  = new JTextField();
        URL1.setColumns(20);
        fields[fieldNum++] = URL1;
 
        URL2 = new JTextField();
        URL2.setColumns(20);
        fields[fieldNum++] = URL2;
 
      
        for (int i = 0; i < labelStrings.length; i++) {
            labels[i] = new JLabel(labelStrings[i],
                                   JLabel.TRAILING);
            labels[i].setLabelFor(fields[i]);
            panel.add(labels[i]);
            panel.add(fields[i]);
 
            //Add listeners to each field.
            JTextField tf = null;
   
                tf = (JTextField)fields[i];
 
            tf.addActionListener(this);
        }
        SpringUtilities.makeCompactGrid(panel,
                                        labelStrings.length, 2,
                                        GAP, GAP, //init x,y
                                        GAP, GAP/2);//xpad, ypad
        return panel;
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Kmeans Clustering project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add contents to the window.
        frame.add(new DialogBox() {

        });
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }
}