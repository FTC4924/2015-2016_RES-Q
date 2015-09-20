package com.qualcomm.ftcrobotcontroller;

/**
 * Created by 4924_Users on 9/20/2015.
 */
public class ServoInputs {
    public enum PinStates { PinUp, PinDown };
    public enum FlapStates { FlapOpen, FlapClosed };

    public PinStates CurrentPinState;
    public FlapStates CurrentFlapState;

}
