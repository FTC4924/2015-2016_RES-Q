package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 9/17/2015.
 */
public class PowerCal {
    public static MotorPwerLvl CalcPwerLvls(DriverInputs inputs){
       MotorPwerLvl levels=new MotorPwerLvl();
        float right = inputs.throttle - inputs.direction;
        float left = inputs.throttle + inputs.direction;

        levels.backLeft = left;
        levels.backRight = right;
        levels.frontLeft = left;
        levels.frontRight = right;
        return levels;
    }

}
