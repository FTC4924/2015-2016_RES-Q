package com.qualcomm.ftcrobotcontroller.opmodes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by 4924_Users on 6/3/2016.
 */
public class PracticeRobotTeleOp extends OpMode implements SensorEventListener {

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor backRightMotor;
    DcMotor frontRightMotor;
    GyroSensor Gyro;
    PracticeRobotPowerLevels PowerLevels = new PracticeRobotPowerLevels();
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float pitch = 0.0f;
    private float[] mGravity;
    private float[] mGeomagnetic;

    final float GYRO_ANGLE_MARGIN = 1.0f;
    final float BASE_HOLONOMIC_DRIVE_POWER = 0.5f;
    final int LOOP_SAMPLING_RATE = 20;

    float angleSample = 0.0f;
    float curvePowerAdjustment = 1.0f;
    float steadyAngle = 0.0f;
    boolean isStrafingLeft = false;
    boolean isStrafingRight = false;
    int loopCount = 0;

    @Override
    public void init() {

        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        Gyro = hardwareMap.gyroSensor.get("Gyro");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);

        mSensorManager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pitch = 0.0f;

        Gyro.calibrate();
    }

    @Override
    public void start() {

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public void loop() {

        if (!Gyro.isCalibrating()) {

            isStrafingLeft = gamepad1.left_bumper;
            isStrafingRight = gamepad1.right_bumper;

            if (isStrafingLeft || isStrafingRight) {

                setPowerForMecanumStrafe();

                if (loopCount >= LOOP_SAMPLING_RATE) {

                    if (Gyro.getHeading() - steadyAngle > 1.0f) {

                        curvePowerAdjustment += 0.05f;
                    }

                    if (Gyro.getHeading() - steadyAngle < -1.0f) {

                        curvePowerAdjustment -= 0.05f;
                    }

                    loopCount = 0;
                }

                loopCount++;

            } else {

                setPowerForTankDrive();
                steadyAngle = Gyro.getHeading();
                angleSample = Gyro.getHeading();
            }

            clipPowerLevels();
            setMotorPowerLevels(PowerLevels);
        }

        //telemetry.addData("pitch", Math.round(Math.toDegrees(pitch)));
    }

    @Override
    public void stop() {

        mSensorManager.unregisterListener(this);
    }

    public void setMotorPowerLevels(PracticeRobotPowerLevels PowerLevels) {

        frontLeftMotor.setPower(PowerLevels.frontLeftPower);
        backLeftMotor.setPower(PowerLevels.backLeftPower);
        backRightMotor.setPower(PowerLevels.backRightPower);
        frontRightMotor.setPower(PowerLevels.frontRightPower);
    }

    public void setPowerForTankDrive() {

        PowerLevels.frontLeftPower = gamepad1.left_stick_y;
        PowerLevels.backLeftPower = gamepad1.left_stick_y;
        PowerLevels.backRightPower = gamepad1.right_stick_y;
        PowerLevels.frontRightPower = gamepad1.right_stick_y;
    }

    public void setPowerForMecanumStrafe() {

        if (isStrafingLeft) {

            PowerLevels.frontLeftPower = BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.backLeftPower = -BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.backRightPower = BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.frontRightPower = -BASE_HOLONOMIC_DRIVE_POWER;

            adjustHolonomicPower();
        }

        if (isStrafingRight) {

            PowerLevels.frontLeftPower = -BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.backLeftPower = BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.backRightPower = -BASE_HOLONOMIC_DRIVE_POWER;
            PowerLevels.frontRightPower = BASE_HOLONOMIC_DRIVE_POWER;

            adjustHolonomicPower();
        }
    }

    public void adjustHolonomicPower() {

        if (isStrafingLeft) {

            if (Gyro.getHeading() < steadyAngle - GYRO_ANGLE_MARGIN) {

                PowerLevels.backLeftPower -= curvePowerAdjustment;
            }

            if (Gyro.getHeading() > steadyAngle + GYRO_ANGLE_MARGIN) {

                PowerLevels.frontLeftPower += curvePowerAdjustment;
            }
        }

        if (isStrafingRight) {

            if (Gyro.getHeading() < steadyAngle - GYRO_ANGLE_MARGIN) {

                PowerLevels.frontRightPower += curvePowerAdjustment;
            }

            if (Gyro.getHeading() > steadyAngle + GYRO_ANGLE_MARGIN) {

                PowerLevels.backRightPower -= curvePowerAdjustment;
            }
        }
    }

    public void clipPowerLevels() {

        PowerLevels.backRightPower = Range.clip(PowerLevels.backRightPower, -1.0f, 1.0f);
        PowerLevels.backLeftPower = Range.clip(PowerLevels.backLeftPower, -1.0f, 1.0f);
        PowerLevels.frontRightPower = Range.clip(PowerLevels.frontRightPower, -1.0f, 1.0f);
        PowerLevels.frontLeftPower = Range.clip(PowerLevels.frontLeftPower, -1.0f, 1.0f);
    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            mGravity = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

            mGeomagnetic = event.values;
        }

        if (mGravity != null && mGeomagnetic != null) {

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {

                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                pitch = orientation[1];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}