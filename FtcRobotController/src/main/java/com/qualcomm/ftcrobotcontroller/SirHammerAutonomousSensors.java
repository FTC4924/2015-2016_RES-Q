package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 9/27/2015.
 */
public class SirHammerAutonomousSensors {
    ElapsedTime elapsedTime;

    public SirHammerAutonomousSensors() {
        elapsedTime = new ElapsedTime();
    }

    public void ResetTime() {
        elapsedTime.reset();
    }
}
