package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 3/5/2016.
 */
public class RedPushAndClimb extends DeviClimbBaseTest {

    public RedPushAndClimb() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(70.0f, 70.0f, 1.0f),
                new DrivePathSegment(-15.5f, -15.5f, 1.0f),
                new DrivePathSegment(45.0f, 0.7f)
        };

        climbingTime = 6.0f;
    }
}
