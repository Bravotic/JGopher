package com.bravotic.libjgopher;

import java.util.Arrays;

public class GopherSelector {
    private final char type;
    private final String[] data;
    
    public GopherSelector(String rawSelector){
        type = rawSelector.charAt(0);
        if(rawSelector.equals(".")){
            data = new String[4];
            data[0] = ".";
        }
        else{
            data = rawSelector.split("\t");
        }
        if(data.length < 4){
            throw new java.lang.Error("Gopher Data is unparsable, Data is of size " + data.length + "\n" + Arrays.toString(data));
        }
        data[0] = data[0].substring(1);
    }
    
    public char GetType(){
        return type;
    }
    public String GetMessage(){
        return data[0];
    }
    public String GetDirectory(){
        return data[1];
    }
    public String GetServer(){
        return data[2];
    }
    public String GetPort(){
        if(data[3] != null){
            return data[3];
        }
        else{
            return "0";
        }
    }
    
    public String ToString(){
        return "[ type = \"" + type + "\", Message = \"" + GetMessage() + "\", Directory = \"" + GetDirectory() + "\", Server = \"" + GetServer() + "\", Port = \"" + GetPort() + "\" ]";
    }
}
