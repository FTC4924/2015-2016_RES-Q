package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/19/2015.
 */
public class BlueCornerBeacon extends DeviBeaconBase {

    public BlueCornerBeacon() {

        objectivePath = new DrivePathSegment[] {

                new DrivePathSegment(-10.0f, 0.7f),
                new DrivePathSegment(26.0f, 27.0f, 0.9f),
                new DrivePathSegment(359.0f, 0.7f),
                new DrivePathSegment(26.0f, 27.0f, 0.9f),
                new DrivePathSegment(359.0f, 0.7f),
                new DrivePathSegment(26.0f, 27.0f, 0.9f),
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
