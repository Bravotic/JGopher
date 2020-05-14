package com.bravotic.libjgopher;

public class GopherURL{
    private final String url;
    private final int port;
    private final String dir;
    private final char type;
    
    public GopherURL(String input_url, String input_directory, int input_port){
        url = input_url;
        dir = input_directory;
        port = input_port;
        type = '1';
    }
    
    public GopherURL(String input_url, String input_directory, int input_port, char input_type){
        url = input_url;
        dir = input_directory;
        port = input_port;
        type = input_type;
    }
    
    public String getURL(){
        return url;
    }
    
    public int getPort(){
        return port;
    }
    
    public String getDir(){
        return dir;
    }
    public char getType(){
        return type;
    }
}  
