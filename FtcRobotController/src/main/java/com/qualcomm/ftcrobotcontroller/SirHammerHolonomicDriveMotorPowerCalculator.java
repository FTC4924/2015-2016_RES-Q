package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 9/17/2015.
 */
public class SirHammerHolonomicDriveMotorPowerCalculator {
    public static FourWheelDrivePowerLevels CalculatePowerForArcadeInputs(SirHammerDriverHolonomicInputs inputs) {

        FourWheelDrivePowerLevels levels = new FourWheelDrivePowerLevels();
        float right = inputs.throttle - inputs.direction;
        float left = inputs.throttle + inputs.direction;

        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);

        // scale the joystick value to make it easier to control
        // the robot more precisely at slower speeds.
        right = (float)scaleInput(right);
        left =  (float)scaleInput(left);

        if (inputs.leftHolonomic || inputs.rightHolonomic) {
            levels.backLeft = -0.8f;
            levels.backRight = -0.8f;
            levels.frontLeft = 0.8f;
            levels.frontRight = 0.8f;
            if (inputs.rightHolonomic) {
                levels.backLeft *= -1.0f;
                levels.backRight *= -1.0f;
                levels.frontLeft *= -1.0f;
                levels.frontRight *= -1.0f;
            }
        } else {
            levels.backLeft = left;
            levels.backRight = right;
            levels.frontLeft = left;
            levels.frontRight = right;
        }
        return levels;
    }

    public static FourWheelDrivePowerLevels CalculatePowerForTankInputs(DriverTankDriveInputs inputs) {
        FourWheelDrivePowerLevels levels = new FourWheelDrivePowerLevels();

        float left = inputs.left;
        float right = inputs.right;
        float accelerator = inputs.accelerator;

        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);
        accelerator = Range.clip(accelerator, 0, 1);

        // scale the joystick value to make it easier to control
        // the robot more precisely at slower speeds.
        right = right * accelerator;
        left =  left * accelerator;

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
