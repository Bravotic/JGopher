package com.bravotic.libjgopher;

import javax.swing.JTextPane;
import static java.awt.Color.blue;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.xml.namespace.QName;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.JLabel;
import java.lang.reflect.Method;
import java.awt.Cursor;
import java.lang.reflect.InvocationTargetException;

public class JGopherView extends JTextPane{
    
    private Document doc;
    
    // Images that we will render along side the selectors when we render the 
    // page.
    private Image file;
    private Image folder;
    private Image binhex;
    private Image binary;
    private Image blank;
    
    // Attributes we will place on every selector, allows us to easily pull these
    // from a click and place them directly into the render gopher method.
    private QName gopherDir = new QName("dir");
    private QName gopherServer = new QName("server");
    private QName gopherPort = new QName("port");
    private QName gopherType = new QName("type");
    
    // ArrayLists for both Methods and their respective Objects to call. Is this
    // an abuse of java?  Totally. Could I do it another way?  Of course.  Would
    // it be as fun?  Definately not.
    private final ArrayList<Method> methods;
    private final ArrayList<Object> classobjs;
    
    private Cursor pointer;
    private Cursor normal;
    private Cursor load;
    
    // Eventually users will be able to specify this
    private int fontsize = 14;
    
    private String url;
    
    public void runUpdateUrlMethods(){
        for(int i = 0; i < methods.size(); i++){
            try{
                methods.get(i).invoke(classobjs.get(i));
            }   
            catch( IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
                Logger.getLogger(JGopherView.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    public void setUrlUpdate(String in_url){
        url = in_url;
    }
    
    public JGopherView(){
        setContentType("text/html");
        try{
            loadImages();
        }
        catch (IOException e){
            Logger.getLogger(JGopherView.class.getName()).log(Level.SEVERE, null, e);
        }
        
        methods = new ArrayList<>();
        classobjs = new ArrayList<>();
        
        pointer = new Cursor(Cursor.HAND_CURSOR);
        normal = new Cursor(Cursor.DEFAULT_CURSOR);
        load = new Cursor(Cursor.WAIT_CURSOR);
        
        addMouseListener(new MouseAdapter() {
             @Override
             public void mouseClicked(MouseEvent e){
                JTextPane view = (JTextPane) e.getSource();
                Point pt = new Point(e.getX(), e.getY());
        
                int clickpos = view.viewToModel(pt);
        
                Document doc = view.getDocument();
                DefaultStyledDocument parsedoc = (DefaultStyledDocument) doc;
        
                Element tag = parsedoc.getCharacterElement(clickpos);
                AttributeSet a = tag.getAttributes();
                String server = (String) a.getAttribute(gopherServer);
                String dir = (String) a.getAttribute(gopherDir);
                String port = (String) a.getAttribute(gopherPort);
                String type = (String) a.getAttribute(gopherType);
               
                if (server != null && dir != null && port != null && type != null) {

                    if (type.equals("1")) {
                        view.setText("");
                        
                        Thread render = new Thread(() -> {
                            try {
                                renderGopher(server, dir, port);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(JGopherView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            url = "gopher://" + server + "/" + type + dir;
                            runUpdateUrlMethods();
                            view.setCaretPosition(0);
                        });
                        render.start();
                    } else {
                        Thread render = new Thread(() -> {
                            view.setText("");
                            try {
                                renderFile(server, dir, port);
                            } catch (IOException | BadLocationException ex) {
                                Logger.getLogger(JGopherView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            view.setCaretPosition(0);
                            url = "gopher://" + server + "/" + type + dir;
                            runUpdateUrlMethods();
                        });
                        render.start();
                        
                    }
                    validate();

                }
    
            }
        });
        addMouseMotionListener(new MouseAdapter(){
        @Override
        public void mouseMoved(MouseEvent e){
                JTextPane view = (JTextPane) e.getSource();
                Point pt = new Point(e.getX(), e.getY());
                
                int pos = view.viewToModel(pt);
                if(pos >= 0){
                    Document page = view.getDocument();
                    DefaultStyledDocument spage = (DefaultStyledDocument) page;
                    Element tag = spage.getCharacterElement(pos);
                    AttributeSet a = tag.getAttributes();
                    String server = (String) a.getAttribute(gopherServer);
                    String dir = (String) a.getAttribute(gopherDir);
                    String port = (String) a.getAttribute(gopherPort);
                    String type = (String) a.getAttribute(gopherType);
               
                    if (server != null && dir != null && port != null && type != null) {
                        
                        if(getCursor() != pointer){
                            setCursor(pointer);
                        }
                    }
                    else if (server == null && dir == null && port == null && type == null){
                        if(getCursor() == pointer){
                            setCursor(normal);
                        }
                    }
                }
             }
        });
    }
    
    public void addUrlUpdateMethod(Object classobj, String method_name){
        try{
            Method method = classobj.getClass().getMethod(method_name);
            methods.add(method);
            classobjs.add(classobj);
        }
        catch( NoSuchMethodException | SecurityException e){
            Logger.getLogger(JGopherView.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public String getURL(){
        return url;
    }
    
    public void renderGopher(String url, String dir, String port) throws BadLocationException{
        
            addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                setEditable(true);
                getCaret().setVisible(false);
            }

            @Override
            public void focusGained(FocusEvent e) {
                setEditable(false);
                getCaret().setVisible(false);
            }
        });
        setCursor(load);
        
        GopherConnection gc = new GopherConnection(url, dir, Integer.parseInt(port));
        try {
            gc.ExecuteConnection();
        } catch (IOException ex) {
            Logger.getLogger(JGopherView.class.getName()).log(Level.SEVERE, null, ex);
        }
        GopherSelector[] sels = gc.ReadToGopherSelectorArray();
        
        // History related functions are coming later, I have been on and off
        // testing them for a while so take these as maybe a preview for the 
        // future.
        
        //history.add(new GopherURL(url, dir, Integer.parseInt(port)));
        //placeInHistory = history.size() - 1;
        
        doc = getDocument();
        for(GopherSelector sel : sels){

            StyleContext context = new StyleContext();
            Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
            JLabel label;
            if(sel.GetType() == 'i'){
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attrs, "Monospace");
                StyleConstants.setFontSize(attrs, fontsize);
 
                label = new JLabel(new ImageIcon(blank));
                StyleConstants.setComponent(labelStyle, label);
                doc.insertString(doc.getLength(), "Icon", labelStyle);
                doc.insertString(doc.getLength(), sel.GetMessage() + "\n", attrs);    
            }
            else if(sel.GetType() != '.'){
                switch(sel.GetType()){
                    case '1':
                        label = new JLabel(new ImageIcon(folder));

                    break;
                    default:
                        label = new JLabel(new ImageIcon(file));
                    break;
                }
                StyleConstants.setComponent(labelStyle, label);
                doc.insertString(doc.getLength(), "Icon", labelStyle);

                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attrs, "Monospace");
                StyleConstants.setFontSize(attrs, fontsize);
                StyleConstants.setUnderline(attrs, true);
                StyleConstants.setForeground(attrs, blue);
                attrs.addAttribute(gopherDir, sel.GetDirectory());
                attrs.addAttribute(gopherServer, sel.GetServer());
                attrs.addAttribute(gopherPort, sel.GetPort());
                attrs.addAttribute(gopherType, "" + sel.GetType());
                doc.insertString(doc.getLength(), sel.GetMessage() + "\n", attrs);
            }
        }
        if(getCursor() == load){
            setCursor(normal);
        }
    }
    
    private void loadImages() throws IOException{
        Image tmp = ImageIO.read(getClass().getClassLoader().getResource("icons/folder.png"));
        folder = tmp.getScaledInstance(fontsize, fontsize, 0);
        tmp = ImageIO.read(getClass().getClassLoader().getResource("icons/blank.png"));
        blank = tmp.getScaledInstance(fontsize, fontsize, 0);
        tmp = ImageIO.read(getClass().getClassLoader().getResource("icons/text.png"));
        file = tmp.getScaledInstance(fontsize, fontsize, 0);
    }
    
    public void renderFile(String url, String dir, String port) throws IOException, BadLocationException{
        setCursor(load);
        GopherConnection gc = new GopherConnection(url, dir, Integer.parseInt(port));
        try {
            gc.ExecuteConnection();
        } catch (IOException ex) {
            Logger.getLogger(JGopherView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // History related functions are coming later, I have been on and off
        // testing them for a while so take these as maybe a preview for the 
        // future.
        
        //history.add(placeInHistory, new GopherURL(url, dir, Integer.parseInt(port)));
        //placeInHistory = history.size() - 1;
        
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, "Monospace");
        StyleConstants.setFontSize(attrs, fontsize);
        doc.insertString(doc.getLength(), gc.ReadToString() + "\n", attrs);
        if(getCursor() == load){
            setCursor(normal);
        }
    }
}
