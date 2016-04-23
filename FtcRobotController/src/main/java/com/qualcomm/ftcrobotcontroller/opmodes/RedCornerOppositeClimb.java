package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 3/26/2016.
 */
public class RedCornerOppositeClimb extends DeviClimbBase {

    public RedCornerOppositeClimb() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(5.0f),
                new DrivePathSegment(20.0f, 20.0f, 1.0f),
                new DrivePathSegment(0.0f, 0.7f),
                new DrivePathSegment(20.0f, 20.0f, 1.0f),
                new DrivePathSegment(92.0f, 0.7f),
                new DrivePathSegment(20.0f, 20.0f, 1.0f),
                new DrivePathSegment(92.0f, 0.7f),
                new DrivePathSegment(20.0f, 20.0f, 1.0f),
                new DrivePathSegment(92.0f, 0.7f)
        };

        climbingTime = 5.0f;
    }
}
