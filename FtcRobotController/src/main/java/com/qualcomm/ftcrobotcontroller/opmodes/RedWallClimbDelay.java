package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/3/2015.
 */
public class RedWallClimbDelay extends DeviClimbBase {

    public RedWallClimbDelay() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(5.0f),
                new DrivePathSegment(10.0f, 10.0f, 1.0f),
                new DrivePathSegment(315.0f, 0.7f),
                new DrivePathSegment(61.5f, 61.5f, 1.0f), //Because of similar triangles, this distance is 1.5 times larger than the wall version.
                new DrivePathSegment(-45.0f, 0.7f)
        };

        climbingTime = 10.0f;
    }
}
