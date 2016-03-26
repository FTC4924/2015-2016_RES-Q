package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 3/26/2016.
 */
public class RedWallBeacon extends DeviBeaconBaseTest {

    public RedWallBeacon() {

        beaconPath = new DrivePathSegment[] {

                new DrivePathSegment(50.0f, 50.0f, 0.9f),
                new DrivePathSegment(270.0f, 0.9f)
        };
    }
}
