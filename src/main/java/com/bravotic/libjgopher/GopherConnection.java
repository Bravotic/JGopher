package com.bravotic.libjgopher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GopherConnection {
    private final String url;
    private final String dir;
    private final int port;
    
    public PrintWriter out;
    public BufferedReader in;
    
    public GopherConnection(String input_url, String input_directory, int input_port){
        url = input_url;
        dir = input_directory;
        port = input_port;
    }
    public GopherConnection(GopherURL gourl){
        url = gourl.getURL();
        dir = gourl.getDir();
        port = gourl.getPort();
    }
    public GopherConnection(String input_url, String input_directory){
        url = input_url;
        dir = input_directory;
        port = 70;
    }
    public GopherConnection(String input_url){
        url = input_url;
        dir = "/";
        port = 70;
    }
    
    public void ExecuteConnection() throws IOException{
        Socket s = new Socket(url, port);
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader( new InputStreamReader(s.getInputStream()));
        
        out.println(dir);
    }
    
    public String ReadToString() throws IOException{
        String retValue = "";
        String line;
        while((line = in.readLine()) != null){
            retValue += line + "\n";
        }
        return retValue;
    }
    
    public GopherSelector[] ReadToGopherSelectorArray(){
        String raw = "";
        try {
            raw = ReadToString();
        } catch (IOException ex) {
            Logger.getLogger(GopherConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String[] lines = raw.split("\n");
        
        GopherSelector[] retValue = new GopherSelector[lines.length];
        
        for(int i = 0; i < lines.length; i++){
            retValue[i] = new GopherSelector(lines[i]);
        }
        return retValue;
    }
    
    public String ParseToHTML(GopherSelector[] sels){
        String retVal = "";
        for(GopherSelector sel : sels){
            switch(sel.GetType()){
                case '.': retVal += ""; break;
                case 'i': retVal += "<span>" + sel.GetMessage() + "</span><br>"; break;
                case '1': retVal += "<a href='#'>" + sel.GetMessage() + "</a><br>"; break;
                
            }
        }
        return retVal;
    }
    public String ParseToHTML(GopherSelector sel){
        String retVal = "";
        
            switch(sel.GetType()){
                case '.': retVal += ""; break;
                case 'i': retVal += "<span>" + sel.GetMessage() + "</span><br>"; break;
                case '1': retVal += "<a href='#'>" + sel.GetMessage() + "</a><br>"; break;
                
            }
        
        return retVal;
    }
    public String ParseToString(GopherSelector sel){
        String retVal = "";
        
            switch(sel.GetType()){
                case '.': retVal += ""; break;
                case 'i': retVal += "" + sel.GetMessage() + "\n"; break;
                case '1': retVal += "" + sel.GetMessage() + "\n"; break;
                
            }
        
        return retVal;
    }
}
