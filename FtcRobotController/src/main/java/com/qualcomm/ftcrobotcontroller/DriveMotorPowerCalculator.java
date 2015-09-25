package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 9/17/2015.
 */
public class DriveMotorPowerCalculator {
    public static DriveMotorPowerLevels Calculate(DriverInputs inputs) {

        DriveMotorPowerLevels levels = new DriveMotorPowerLevels();
        float right = inputs.throttle - inputs.direction;
        float left = inputs.throttle + inputs.direction;

        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);

        // scale the joystick value to make it easier to control
        // the robot more precisely at slower speeds.
        right = (float)scaleInput(right);
        left =  (float)scaleInput(left);

        levels.backLeft = left;
        levels.backRight = right;
        levels.frontLeft = left;
        levels.frontRight = right;
        return levels;
    }

    /*
	 * This method scales the joystick input so for low joystick values, the
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
    static double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);
        if (index < 0) {
            index = -index;
        } else if (index > 16) {
            index = 16;
        }

        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        return dScale;
    }

}
