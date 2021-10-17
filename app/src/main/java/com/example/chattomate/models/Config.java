package com.example.chattomate.models;

public class Config {
    private String label;
    private String  value;
    private int icon;

    public Config(String label, String value, int icon){
        this.label = label;
        this.value = value;
        this.icon = icon;
    }

    public String getLabel(){
        return this.label;
    }

    public String getValue(){
        return this.value;
    }

    public int getIcon(){
        return this.icon;
    }

}
