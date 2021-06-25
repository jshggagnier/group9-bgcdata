package com.example;

public class Square{
    private String boxname;
    private int height;
    private int width;
    private String boxcolor;
    private boolean outlined;

    public String getboxname() {
        return this.boxname;
    }

    public int getheight() {
        return this.height;
    }

    public int getwidth()
    {
        return this.width;
    }

    public String getboxcolor()
    {
        return this.boxcolor;
    }
    
    public boolean getoutlined()
    {
        return this.outlined;
    }
    
    public void setboxname(String target) {
        this.boxname = target;
    }

    public void setheight(int target) {
        this.height = target;
    }

    public void setwidth(int target)
    {
        this.width = target;
    }

    public void setboxcolor(String target)
    {
        this.boxcolor = target;
    }
    
    public void setoutlined(boolean target)
    {
        this.outlined = target;
    }
}