package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 3/5/2016.
 */
public class RedPushAndClimb extends DeviClimbBase {

    public RedPushAndClimb() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(40.0f, 40.0f, 1.0f),
                new DrivePathSegment(-6.0f, -6.0f, 1.0f),
                new DrivePathSegment(267.0f, 0.7f)
        };

        climbingTime = 5.0f;
    }
}
