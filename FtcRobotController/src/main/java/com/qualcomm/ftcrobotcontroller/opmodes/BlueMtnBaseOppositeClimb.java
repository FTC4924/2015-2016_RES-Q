package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 3/25/2016.
 */
public class BlueMtnBaseOppositeClimb extends DeviClimbBase {

    public BlueMtnBaseOppositeClimb() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(5.0f),
                new DrivePathSegment(15.0f, 15.0f, 1.0f),
                new DrivePathSegment(317.0f, 0.7f),
                new DrivePathSegment(25.0f, 35.0f, 1.0f),
                new DrivePathSegment(317.0f, 0.7f),
                new DrivePathSegment(25.0f, 35.0f, 1.0f),
                new DrivePathSegment(317.0f, 0.7f)
        };

        climbingTime = 5.0f;
    }
}
