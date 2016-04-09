package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by William on 4/8/2016.
 */
public class BlueWallBeaconDecisionTest extends DeviBeaconBaseDecisionTest {

    public BlueWallBeaconDecisionTest() {

        objectivePath = new DrivePathSegment[] {

                new DrivePathSegment(25.0f, 25.0f, 0.9f),
                new DrivePathSegment(45.0f, 0.9f),
                new DrivePathSegment(35.0f, 35.0f, 0.9f),
                new DrivePathSegment(92.0f, 0.9f)
        };
    }

    @Override
    boolean isRobotOnRedAlliance() {

        return false;
    }

    @Override
    boolean isStartingOnWall() {

        return true;
    }
}
