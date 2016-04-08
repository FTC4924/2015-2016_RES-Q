package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by William on 4/7/2016.
 */
public class RedCornerBeaconDecisionTest extends DeviBeaconBaseDecisionTest {

    public RedCornerBeaconDecisionTest() {

        objectivePath = new DrivePathSegment[] {

                new DrivePathSegment(40.0f, 40.0f, 0.9f),
                new DrivePathSegment(-359.0f, 0.7f),
                new DrivePathSegment(40.0f, 40.0f, 0.9f),
                new DrivePathSegment(313.0f, 0.7f)
        };
    }

    @Override
    boolean isRobotOnRedAlliance() {

        return true;
    }
}
