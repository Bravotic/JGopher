package com.bravotic.JGopher;

import com.bravotic.libjgopher.GopherConnectionEvent;
import com.bravotic.libjgopher.GopherHoverEvent;
import com.bravotic.libjgopher.GopherURL;
import com.bravotic.libjgopher.GopherUrlEvent;
import com.bravotic.libjgopher.JGopherView;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class Client extends JPanel implements ActionListener{
    private JTextField urlBar;
    private JGopherView gviewer;
    
    private boolean inConnectionEvent;
    
    private ArrayList<GopherURL> history;
    private int placeInHistory;
    
    public Client() throws BadLocationException, IOException{
    
        super(new BorderLayout());
        inConnectionEvent = false;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            
        }
        
        JToolBar navBar = new JToolBar("NavBar");
        JToolBar statusBar = new JToolBar("StatusBar");
        
        urlBar = new JTextField(30);
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setMaximumSize(new Dimension(125, 125));
        JLabel status = new JLabel();
        
        
        JButton back = new JButton(new ImageIcon(getClass().getClassLoader().getResource("icons/left.png")));
        JButton forward = new JButton(new ImageIcon(getClass().getClassLoader().getResource("icons/right.png")));
        JButton go = new JButton("GO!",new ImageIcon(getClass().getClassLoader().getResource("icons/index.png")));
        
        back.addActionListener(this);
        back.setActionCommand("BACK");
        
        forward.addActionListener(this);
        forward.setActionCommand("FORWARD");
        
        go.addActionListener(this);
        go.setActionCommand("GO");
        
        navBar.add(back);
        navBar.add(forward);
        navBar.add(urlBar);
        navBar.add(go);
        statusBar.add(progressBar);
        statusBar.add(status);

        //urlBar.setText("gopher://floodgap.com/");
        
        urlBar.addActionListener(new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                String raw = urlBar.getText();
                gviewer.openUrl(raw);
            }
        });
        
        gviewer = new JGopherView(this);
        
        
        gviewer.openUrl("gopher://floodgap.com/");
        
        gviewer.addEvent(new GopherUrlEvent(){
           public void run(){
               urlBar.setText(gviewer.getURL());
           } 
        });
        
        gviewer.addEvent(new GopherConnectionEvent(){
            public void connecting(){
                inConnectionEvent = true;
                status.setText("    Connecting to " + gviewer.getURL()+"...");
                progressBar.setValue(33);
            }
            public void rendering(){
                status.setText("    Rendering " + gviewer.getURL() +"...");
                progressBar.setValue(66);
            }
            public void finished(){
                inConnectionEvent = false;
                status.setText("    Done.");
                progressBar.setValue(0);
            }
        });
        
        gviewer.addEvent(new GopherHoverEvent(){
            public void hoverOn(){
                if(!inConnectionEvent){
                    status.setText("    " + gviewer.getHoveredURL());
                }
            }
            public void hoverOff(){
                if(!inConnectionEvent){
                    status.setText("    Done.");
                }
            }
        });
        gviewer.setCaretPosition(0);
        
        
        
        JScrollPane mainView = new JScrollPane(gviewer);
        add(navBar, BorderLayout.PAGE_START);
        add(mainView, BorderLayout.CENTER);
        add(statusBar, BorderLayout.PAGE_END);
    }
    
    // Simply our method to update the URL bar at the top of the screen
    public void updateUrlBar(){
        urlBar.setText(gviewer.getURL());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        /*if(cmd.equals("GO")){
            String raw = urlBar.getText();
            GopherURL test = parseToGopherURL(raw);
            try{
            gviewer.setText("");
            gviewer.renderGopher(test.getURL(), test.getDir(), Integer.toString(test.getPort()));
            urlBar.setText("gopher://" + test.getURL() + "/" + test.getType() + test.getDir());
            gviewer.setCaretPosition(0);
            }
            catch (BadLocationException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        
        if(cmd.equals("GO")){
            String raw = urlBar.getText();
            gviewer.openUrl(raw);
        }
        else if(cmd.equals("BACK")){
            gviewer.goBack();
        }
        else if(cmd.equals("FORWARD")){
            gviewer.goForward();
        }
    }
    
    protected static void createAndShowGUI() throws BadLocationException, IOException{
        JFrame frame = new JFrame("JGopher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Client());
        
        frame.setPreferredSize(new Dimension(640, 480));

        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            } catch (BadLocationException | IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
}
