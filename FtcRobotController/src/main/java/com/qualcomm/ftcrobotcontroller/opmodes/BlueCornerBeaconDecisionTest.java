package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by William on 4/8/2016.
 */
public class BlueCornerBeaconDecisionTest extends DeviBeaconBaseDecisionTest {

    public BlueCornerBeaconDecisionTest() {

        objectivePath = new DrivePathSegment[] {

                new DrivePathSegment(-10.0f, 0.7f),
                new DrivePathSegment(26.0f, 26.0f, 0.9f),
                new DrivePathSegment(359.0f, 0.7f),
                new DrivePathSegment(26.0f, 26.0f, 0.9f),
                new DrivePathSegment(359.0f, 0.7f),
                new DrivePathSegment(26.0f, 26.0f, 0.9f),
                new DrivePathSegment(-50.0f, 0.7f)
        };
    }

    @Override
    boolean isRobotOnRedAlliance() {

        return false;
    }

    @Override
    boolean isStartingOnWall() {

        return false;
    }
}
