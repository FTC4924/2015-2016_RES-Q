package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/19/2015.
 */
public class BlueCornerBeacon extends DeviBeaconBase {

    public BlueCornerBeacon() {

        beaconPath = new DrivePathSegment[] {

                new DrivePathSegment(110.0f, 110.0f, 1.0f),
                new DrivePathSegment(-45.0f, 0.7f),
        };
    }
}
