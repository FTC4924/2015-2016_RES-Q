package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 10/3/2015.
 */
public class ColorConclusion {
    public static boolean isRed(ColorValue colorValue){
         if(colorValue.red_team>colorValue.blue_team){
        return true;
    }
    return false;
}
    public static boolean isBlue(ColorValue colorValue){
        if (colorValue.blue_team>colorValue.red_team){
            return true;
        }
        return false;
    }
}
