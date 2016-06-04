package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 3/26/2016.
 */
public class RedWallBeacon extends DeviBeaconBase {

    public RedWallBeacon() {

        objectivePath = new DrivePathSegment[] {

                new DrivePathSegment(25.0f, 25.0f, 0.9f),
                new DrivePathSegment(315.0f, 0.9f),
                new DrivePathSegment(35.0f, 35.0f, 0.9f),
                new DrivePathSegment(272.0f, 0.9f)
        };
    }

    @Override
    boolean isRobotOnRedAlliance() {

        return true;
    }

    @Override
    boolean isStartingOnWall() {

        return true;
    }
}
