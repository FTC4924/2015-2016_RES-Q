package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/2/2015.
 */
public class RedCornerClimb extends DeviClimbBase {

    public RedCornerClimb() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(10.0f, 10.0f, 0.9f),
                new DrivePathSegment(315.0f, 0.7f),
                new DrivePathSegment(61.5f, 61.5f, 0.9f), //Because of similar triangles, this distance is 1.5 times larger than the wall version.
                new DrivePathSegment(-45.0f, 0.7f)
        };
    }
}
