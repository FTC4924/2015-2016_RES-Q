package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/3/2015.
 */
public class BlueMountainBaseClimbDelay extends DeviClimbBase {

    public BlueMountainBaseClimbDelay() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(5.0f),
                new DrivePathSegment(20.0f, 20.0f, 1.0f),
                new DrivePathSegment(45.0f, 0.7f),
                new DrivePathSegment(35.0f, 35.0f, 1.0f),
                new DrivePathSegment(-45.0f, 0.7f)
        };

        climbingTime = 8.0f;
    }
}
