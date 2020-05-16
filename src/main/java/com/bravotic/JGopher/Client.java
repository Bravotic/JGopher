package com.bravotic.JGopher;

import com.bravotic.libjgopher.GopherURL;
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

public class Client extends JPanel implements ActionListener{
    private JTextField urlBar = new JTextField(30);
    private JGopherView gviewer;
    
    //private ArrayList<GopherURL> history;
    //private int placeInHistory;
    
    public Client() throws BadLocationException, IOException{
    
        super(new BorderLayout());
        JToolBar navBar = new JToolBar("NavBar");
        
        // History related functions are coming later, I have been on and off
        // testing them for a while so take these as maybe a preview for the 
        // future.
        
        //history = new ArrayList<GopherURL>();
        //placeInHistory = 0;
        
        JButton back = new JButton(new ImageIcon(getClass().getClassLoader().getResource("icons/left.png")));
        JButton forward = new JButton(new ImageIcon(getClass().getClassLoader().getResource("icons/right.png")));
        JButton go = new JButton("GO!",new ImageIcon(getClass().getClassLoader().getResource("icons/index.png")));
        
        back.addActionListener(this);
        back.setActionCommand("BACK");
        
        back.addActionListener(this);
        back.setActionCommand("FORWARD");
        
        go.addActionListener(this);
        go.setActionCommand("GO");
        
        navBar.add(back);
        navBar.add(forward);
        navBar.add(urlBar);
        navBar.add(go);
        
        urlBar.setText("gopher://floodgap.com/");
        
        
        gviewer = new JGopherView(this);
        
        // Eventually renderGopher will be replaced with a method able to decode
        // gopher URLs, probably something like gotoUrl or loadUrl.  As well an
        // option to set homepage will eventaully be added to the client, but
        // for now floodgap is the homepage.
        gviewer.renderGopher("floodgap.com", "/", "70");
        gviewer.addUrlUpdateMethod(this, "updateUrlBar");
        gviewer.setCaretPosition(0);
        
        JScrollPane mainView = new JScrollPane(gviewer);
        add(navBar, BorderLayout.PAGE_START);
        add(mainView, BorderLayout.CENTER);
    }
    
    // Simply our method to update the URL bar at the top of the screen
    public void updateUrlBar(){
        urlBar.setText(gviewer.getURL());
    }
    
    // Yes, I know this is horrible, I just wanted something quick to test, soon
    // this will be completely rewritten, rest assured.
    public GopherURL parseToGopherURL(String raw){
        String url = new String();
        char type = 0x0;
        String dir = new String();
        boolean urlStage = false;
        
        for(int i = 0; i < raw.length(); i++){
            if(i > 1 && raw.charAt(i) == '/' && raw.charAt(i - 1) == '/'){
                i++;
                while(raw.charAt(i) != '/'){
                    url += raw.charAt(i);
                    i++;
                }
                if(i <= raw.length() - 2){
                i++;
                type = raw.charAt(i);
                }
                else{
                    type = '1';
                }
                urlStage = true;
                
            }
            else if(urlStage){
                dir += raw.charAt(i);
            }
        }
        return new GopherURL(url, dir, 70, type);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        if(cmd.equals("GO")){
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
        }
        // History related functions are coming later, I have been on and off
        // testing them for a while so take these as maybe a preview for the 
        // future.
        
        /*
        placeInHistory--;
        history.remove(history.size() - 1);
        try{
            renderGopher(history.get(placeInHistory).getURL(), history.get(placeInHistory).getDir(),            Integer.toString(history.get(placeInHistory).getPort()));
        }
        catch (BadLocationException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    */
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
