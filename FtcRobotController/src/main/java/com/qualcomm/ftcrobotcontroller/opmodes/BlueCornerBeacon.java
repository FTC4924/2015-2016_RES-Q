package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/19/2015.
 */
public class BlueCornerBeacon extends DeviBeaconBase {

    public BlueCornerBeacon() {

        beaconPath = new DrivePathSegment[] {

                new DrivePathSegment(96.0f, 96.0f, 0.9f),
                new DrivePathSegment(-45.0f, 0.7f),
                new DrivePathSegment(8.0f, 8.0f, 0.9f)
        };
    }
}
