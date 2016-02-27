package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/19/2015.
 */
public class RedCornerBeacon extends DeviBeaconBase {

    public RedCornerBeacon() {

        beaconPath = new DrivePathSegment[] {

                new DrivePathSegment(49.0f, 49.0f, 0.9f),
                new DrivePathSegment(-359.0f, 0.7f),
                new DrivePathSegment(49.0f, 49.0f, 0.9f),
                new DrivePathSegment(313.0f, 0.7f)
        };
    }
}