package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.DrivePathSegment;

/**
 * Created by 4924_Users on 12/5/2015.
 */
public class DoNothingAutonomous extends DeviClimbBase {

    public DoNothingAutonomous() {

        mountainPath = new DrivePathSegment[] {

                new DrivePathSegment(40.0f)
        };
    }
}
