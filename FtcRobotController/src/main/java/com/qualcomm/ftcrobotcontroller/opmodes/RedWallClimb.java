package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/2/2015.
 */
public class RedWallClimb extends DeviClimbBase {

    public RedWallClimb() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(20.0f, 20.0f, 0.9f),
                new DrivePathSegment(315.0f, 0.7f),
                new DrivePathSegment(35.0f, 35.0f, 0.9f),
                new DrivePathSegment(-50.0f, 0.7f)
        };
    }
}
