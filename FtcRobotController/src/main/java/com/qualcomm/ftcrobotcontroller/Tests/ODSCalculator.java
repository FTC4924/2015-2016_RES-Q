package com.qualcomm.ftcrobotcontroller.Tests;

import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

/**
 * Created by 4924_Users on 10/25/2015.
 */
public class ODSCalculator {

    final static float WHITE_THRESHOLD = 0.07f; //Find actual value needed

    public static boolean isOnWhiteLine(OpticalDistanceSensor lineDetector) {

        if (lineDetector.getLightDetected() > WHITE_THRESHOLD) {

            return true;

        } else {

            return false;
        }
    }
}
