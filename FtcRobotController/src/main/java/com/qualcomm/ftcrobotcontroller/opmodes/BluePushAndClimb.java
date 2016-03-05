package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 3/5/2016.
 */
public class BluePushAndClimb extends DeviClimbBaseTest {

    public BluePushAndClimb() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(40.0f, 40.0f, 1.0f),
                new DrivePathSegment(-6.0f, -6.0f, 1.0f),
                new DrivePathSegment(90.0f, 0.7f)
        };

        climbingTime = 30.0f;
    }
}
