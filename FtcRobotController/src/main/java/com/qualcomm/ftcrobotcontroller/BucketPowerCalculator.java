package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class BucketPowerCalculator {
    public static BucketArmPowerLevel Calculate(BucketArmMotorInputs inputs) {
        BucketArmPowerLevel levels = new BucketArmPowerLevel();
        float power;
        power = inputs.throttle;

        power = Range.clip(power, -1, 1);
        power = (float)scaleInput(power);

        levels.power = power;

        return levels;
    }

    /*
   * This method scales the joystick input so for low joystick values, the
   * scaled value is less than linear.  This is to make it easier to drive
   * the arm more precisely at slower speeds.
   */
    static double scaleInput(double dVal)  {
        //double[] raiseArmScale = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
        //        0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        //double[] lowerArmScale = { 0.0, 0.05, 0.09, 0.10, 0.10, 0.10, 0.15, 0.15,
        //        0.15, 0.15, 0.2, 0.2, 0.2, 0.2, 0.25, 0.25, 0.25 };
        double[] raiseArmScale = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.50, 0.50, 0.60, 0.7 , 0.7 };
        double[] lowerArmScale = { 0.0, 0.05, 0.09, 0.10, 0.10, 0.10, 0.15, 0.15,
                0.15, 0.15, 0.2, 0.2, 0.2, 0.2, 0.25, 0.25, 0.25 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);
        if (index < 0) {
            index = -index;
        } else if (index > 16) {
            index = 16;
        }

        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -lowerArmScale[index];
        } else {
            dScale = raiseArmScale[index];
        }

        return dScale;
    }
}
