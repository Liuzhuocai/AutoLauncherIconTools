package com.auto.liuzuo.autoicon;

/**
 * Created by liuzuo on 17-6-29.
 */

public class IconBean {
    public String packageName;
    public String className;
    public String title;
    public  int  screen;
    public  int  container;
    public  int  cellX;
    public  int  cellY;
    public  int  rank = -1;
    public  int  id = -1;

    @Override
    public String toString() {
        return "["+"packageName:"+packageName+", className:"+className+",screen:"+screen+",cellX:"+cellX+",cellY:"+cellY+"]";
    }
}
