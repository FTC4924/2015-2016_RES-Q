package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/2/2015.
 */

//This uses the DeviClimbBase as the main class.
//This overrides the mountainPath in the base class with its own, allowing easy changes to said path.
public class RedMountainBaseClimb extends DeviClimbBase {

    public RedMountainBaseClimb() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(20.0f, 20.0f, 1.0f),
                new DrivePathSegment(315.0f, 0.7f),
                new DrivePathSegment(35.0f, 35.0f, 1.0f),
                new DrivePathSegment(-50.0f, 0.7f)
        };

        climbingTime = 8.0f;
    }
}
