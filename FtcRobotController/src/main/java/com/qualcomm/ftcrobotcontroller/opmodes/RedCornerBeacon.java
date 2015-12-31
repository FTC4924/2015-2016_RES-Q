package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/19/2015.
 */
public class RedCornerBeacon extends DeviBeaconBase {

    public RedCornerBeacon() {

        beaconPath = new DrivePathSegment[] {

                new DrivePathSegment(100.0f, 100.0f, 0.9f),
                new DrivePathSegment(315.0f, 0.7f),
        };
    }
}
