package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 2/20/2016.
 */
public class BeaconPathTest extends DeviBeaconBaseTest {

    public BeaconPathTest() {

        beaconPath = new DrivePathSegment[] {

                new DrivePathSegment(80.0f, 80.0f, 0.9f),
                new DrivePathSegment(5.0f),
                new DrivePathSegment(50.0f, 0.7f),
                new DrivePathSegment(-20.0f, -20.0f, 0.9f)
        };
    }
}
