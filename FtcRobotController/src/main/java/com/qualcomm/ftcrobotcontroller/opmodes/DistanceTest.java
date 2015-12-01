package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;
import com.qualcomm.ftcrobotcontroller.EncoderTargets;
import com.qualcomm.ftcrobotcontroller.FourWheelDrivePowerLevels;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by 4924_Users on 12/1/2015.
 */
public class DistanceTest extends OpMode {

    final DrivePathSegment[] testPath = {

            new DrivePathSegment(36.0f, 36.0f, 0.9f),
    };

    private EncoderTargets zeroEncoderTargets = new EncoderTargets(0, 0);
    private int currentPathSegmentIndex = 0;
    private DrivePathSegment[] currentPath = testPath;
    DrivePathSegment segment = currentPath[currentPathSegmentIndex];
    private FourWheelDrivePowerLevels zeroPowerLevels = new FourWheelDrivePowerLevels(0.0f, 0.0f);
    double countsPerInch;

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;

    EncoderTargets currentEncoderTargets = zeroEncoderTargets;


    @Override
    public void init() {

        frontRightMotor = hardwareMap.dcMotor.get("frontrightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontleftMotor");
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {

        if (pathComplete()) {

            TurnOffAllDriveMotors();
        }
    }

    private boolean pathComplete() {
        // Wait for this Segement to end and then see what's next.
        if (moveComplete()) {
            // Start next Segement if there is one.
            if (currentPathSegmentIndex < currentPath.length) {

                TurnOffAllDriveMotors();
                startSeg();

            } else {

                currentPath = null;
                currentPathSegmentIndex = 0;
                TurnOffAllDriveMotors();
                return true;
            }
        }

        return false;
    }

    private boolean moveComplete() {

        return ((Math.abs(getLeftPosition() - currentEncoderTargets.LeftTarget) < 5) &&
                (Math.abs(getRightPosition() - currentEncoderTargets.RightTarget) < 5));
    }

    private int getRightPosition() {

        return frontRightMotor.getCurrentPosition();
    }

    private int getLeftPosition() {

        return frontLeftMotor.getCurrentPosition();
    }

    private void startSeg() {

        segment = currentPath[currentPathSegmentIndex];

        int Left;
        int Right;

        if (currentPath != null) {

            Left = (int) (segment.LeftSideDistance * countsPerInch);
            Right = (int) (segment.RightSideDistance * countsPerInch);
            addEncoderTarget(Left, Right);
            FourWheelDrivePowerLevels powerLevels =
                    new FourWheelDrivePowerLevels(segment.Power, segment.Power);
            SetDriveMotorPowerLevels(powerLevels);

            currentPathSegmentIndex++;
        }
    }

    void addEncoderTarget(int leftEncoderAdder, int rightEncoderAdder) {

        currentEncoderTargets.LeftTarget += leftEncoderAdder;
        currentEncoderTargets.RightTarget += rightEncoderAdder;
    }

    private void SetDriveMotorPowerLevels(FourWheelDrivePowerLevels levels) {

        frontRightMotor.setPower(levels.frontLeft);
        frontLeftMotor.setPower(levels.backRight);
    }

    private void TurnOffAllDriveMotors() {
        SetDriveMotorPowerLevels(zeroPowerLevels);
    }
}
