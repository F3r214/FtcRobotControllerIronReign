package org.firstinspires.ftc.teamcode.robots.conceptTrikeBot.vision.dummy;

import org.firstinspires.ftc.teamcode.robots.conceptTrikeBot.vision.StackHeight;

public class ZeroDummyVisionIntegration extends AbstractDummyVisionIntegration {

    @Override
    public StackHeight detect() {
        return StackHeight.ZERO;
    }

}