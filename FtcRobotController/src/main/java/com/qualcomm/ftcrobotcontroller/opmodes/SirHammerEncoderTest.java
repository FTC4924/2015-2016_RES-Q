package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by 4924_Users on 10/10/2015.
 */
public class SirHammerEncoderTest extends OpMode {

    private DcMotor frontRightMotor;
    private DcMotor frontLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor backLeftMotor;
    int targetPosition = 0;
    ElapsedTime time;

    static final int FIRST_POSITION = 2500;
    static final int SECOND_POSITION = 5000;
    static final float DELAY = 1.0f;

    @Override
    public void init() {
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        frontRightMotor.setTargetPosition(0);
        frontRightMotor.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        frontRightMotor.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontLeftMotor.setTargetPosition(0);
        frontLeftMotor.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        frontLeftMotor.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        backRightMotor.setTargetPosition(0);
        backRightMotor.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        backRightMotor.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backLeftMotor.setTargetPosition(0);
        backLeftMotor.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        backLeftMotor.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
        time = new ElapsedTime();
        time.reset();
    }

    @Override
    public void loop() {


        if (gamepad1.a && (time.time() > DELAY)) {
            targetPosition  += FIRST_POSITION;
            setAllMotorPowers();
        }
        if (gamepad1.b && (time.time() > DELAY)) {
            targetPosition += SECOND_POSITION;
            setAllMotorPowers();
        }

        if (gamepad1.x) {
            resetAllMotors();
        }

        telemetry.addData("target:", targetPosition);
    }

    private void setAllMotorPowers() {
        setMotorPower(frontLeftMotor);
        setMotorPower(frontRightMotor);
        setMotorPower(backLeftMotor);
        setMotorPower(backRightMotor);
        time.reset();
    }

    private void setMotorPower(DcMotor motor) {
        motor.setChannelMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(targetPosition);
        motor.setPower(0.5f);
    }

    private void resetAllMotors() {
        resetMotor(frontLeftMotor);
        resetMotor(frontRightMotor);
        resetMotor(backLeftMotor);
        resetMotor(backRightMotor);
    }

    private void resetMotor(DcMotor motor) {
        motor.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        motor.setTargetPosition(0);
    }
}
